#include "ThreadAdmin.hpp"

ThreadAdmin::ThreadAdmin(int port, QObject* parent) :
    QThread(parent), _port(port), _portAdminClient(DEFAULT_PORT_ADMIN_CLIENT),
    _serverSocket(NULL), _clientSocket(NULL), _timer(NULL), _protocolManager(),
    _clientLoggedIn(false), _stopRequested(false)
{
    IniParser parser("server_documents.conf");

    // Get DOCSAP delimiters
    if (parser.keyExists("DOCSAP_fieldDelimiter"))
        this->_protocolManager.setFieldDelimiter(parser.value("DOCSAP_fieldDelimiter"));
    if (parser.keyExists("DOCSAP_endDelimiter"))
        this->_protocolManager.setEndDelimiter(parser.value("DOCSAP_endDelimiter"));

    // Get get port admin on client side
    if (parser.keyExists("client_admin_port"))
        this->_portAdminClient = std::stoi(parser.value("client_admin_port"));

    // Create a single shot timer
    this->_timer = new QTimerOneShot(this);
    connect(this->_timer, SIGNAL(timeout()), this, SLOT(manageSHUTDOWN_NOW()));
}

ThreadAdmin::~ThreadAdmin(void)
{
    delete this->_timer;
    this->_timer = NULL;

    delete this->_clientSocket;
    this->_clientSocket = NULL;

    delete this->_serverSocket;
    this->_serverSocket = NULL;
}

void ThreadAdmin::requestStop(void)
{
    QMutexLocker locker(&this->_mutex);
    this->_stopRequested = true;

    // Interrupt client blocking function
    if (this->_clientSocket != NULL && this->_clientSocket->isValid())
    {
        delete this->_clientSocket; // Shutdown and close
        this->_clientSocket = NULL;
    }

    // Or interrupt server blocking function
    if (this->_serverSocket != NULL && this->_serverSocket->isValid())
    {
        delete this->_serverSocket; // Shutdown and close
        this->_serverSocket = NULL;
    }
}

void ThreadAdmin::manageSHUTDOWN_NOW(void)
{
    // Create query PAUSE
    AGDOCProtocol query;
    query.command = AGDOCProtocol::SHUTDOWN_NOW;

    // Inform all clients
    this->informAllClients(query);

    emit shutdownServer();
}

void ThreadAdmin::run(void)
{
    try
    {
        // Create serveur socket
        this->_serverSocket = new TCPSocketServer(this->_port);
    }
    catch(const SocketException& exception)
    {
        // Send message
        QString msg("Admin starting thread error : ");
        msg.append(exception.what());
        emit message(msg);

        return;
    }

    // Main loop
    while (!stopRequested())
    {
        // Waiting client
        try
        {
            emit message("[ADMIN] Waiting client");
            this->_clientSocket = this->_serverSocket->nextPendingConnection();
            emit message("[ADMIN] Client connected");
        }
        catch(const SocketException& exception)
        {
            delete this->_serverSocket;
            this->_serverSocket = NULL;

            // Emit message to GUI
            QString msg("[ADMIN] Stop waiting client : ");
            msg.append(exception.what());
            emit message(msg);

            continue;
        }

        // Admin client management
        try
        {
            std::string msg;

            // While client don't ask to quit
            while(!this->_protocolManager.is(DOCSAP::QUIT))
            {
                // Receive query
                ssize_t ret = this->_clientSocket->recv(
                                  msg, this->_protocolManager.endDelimiter());

                // Client close the connection
                if (ret == SOCKET_CLOSED)
                {
                    emit message("[ADMIN] Admin close connection");
                    break;
                }

                emit message("[ADMIN] Receive query : "
                             + QString::fromStdString(msg));

                // Create query object. FAIL query is created if msg is empty
                this->_protocolManager.parseQuery(msg);

                switch (this->_protocolManager.command())
                {
                    case DOCSAP::LOGINA:
                        this->manageLOGINA();
                        break;
                    case DOCSAP::LCLIENTS:
                        this->manageLCLIENTS();
                        break;
                    case DOCSAP::PAUSE:
                        this->managePAUSE();
                        break;
                    case DOCSAP::RESUME:
                        this->manageRESUME();
                        break;
                    case DOCSAP::STOP:
                        this->manageSTOP();
                        break;
                    case DOCSAP::QUIT:
                        this->manageQUIT();
                        break;
                    case DOCSAP::FAIL:
                        this->manageFAIL();
                        break;
                    default:
                        break;
                }
            }
        }
        catch(const SocketException& exception)
        {
            // Emit message to GUI
            QString msg("[ADMIN] Stop receiving message : ");
            msg.append(exception.what());
            emit message(msg);
        }

        // Free client
        delete this->_clientSocket;
        this->_clientSocket = NULL;
        this->_clientLoggedIn = false;
        this->_protocolManager.setCommand(DOCSAP::UNKNOWN);
        emit message("[ADMIN] Client disconnected");
    }
}

void ThreadAdmin::manageLOGINA(void)
{
    try
    {
        // Get all admin login - password
        IniParser adminParser("DB/admin.conf");
        std::string logina = this->_protocolManager.getArg(0);

        // Check if administrator exists
        if (!adminParser.keyExists(logina))
            throw Exception("Administrator " + logina + " not allowed");

        std::string passworda = this->_protocolManager.getArg(1);
        std::string password  = adminParser.value(logina);

        // Accept or not the client
        if (passworda != password)
            throw Exception("Invalid password");

        // Adminitrator logged in
        this->_clientLoggedIn = true;
        this->_protocolManager.setNewCommand(DOCSAP::ACK);
        this->_clientSocket->send(this->_protocolManager.generateQuery());

        emit message("[ADMIN] " + QString::fromStdString(logina) + " logged in");
        emit administratorAccepted(QString::fromStdString(logina));
    }
    catch(Exception const& exception)
    {
        this->sendFAILMessage(exception.what());
    }
}

void ThreadAdmin::manageLCLIENTS(void)
{
    // Check if administrator is logged in
    if (!this->_clientLoggedIn)
    {
        this->sendFAILMessage("Unable to list clients. You must be logged in");
        return;
    }

    // Administrator is logged in
    this->_protocolManager.setNewCommand(DOCSAP::ACK);

    // Get all client's IPv4
    conditionMutex.lock();
    for (int i(0); i < clients.count(); ++i)
        this->_protocolManager.addArg(clients.at(i)->getIPv4());
    conditionMutex.unlock();

    // Send reply
    this->_clientSocket->send(this->_protocolManager.generateQuery());
}

void ThreadAdmin::managePAUSE(void)
{
    // Check if administrator is logged in
    if (!this->_clientLoggedIn)
    {
        this->sendFAILMessage("Unable to suspend server. You must be logged in");
        return;
    }

    // Emit a signal to GUI and thread server
    emit suspendServer(true);

    // Create query PAUSE
    AGDOCProtocol query;
    query.command = AGDOCProtocol::PAUSE;

    // Inform all clients
    this->informAllClients(query);
}

void ThreadAdmin::manageRESUME(void)
{
    // Check if administrator is logged in
    if (!this->_clientLoggedIn)
    {
        this->sendFAILMessage("Unable to resume server. You must be logged in");
        return;
    }

    // Emit a signal to GUI and thread server
    emit suspendServer(false);

    // Create query PAUSE
    AGDOCProtocol query;
    query.command = AGDOCProtocol::RESUME;

    // Inform all clients
    this->informAllClients(query);
}

void ThreadAdmin::manageSTOP(void)
{
    try
    {
        // Check if administrator is logged in
        if (!this->_clientLoggedIn)
            throw Exception("Unable to stop server. You must be logged in");

        // Check if timer has already started
        if (this->_timer->isRunning())
            throw Exception("Shutdown already in progress");

        // Check if delay exists
        if (this->_protocolManager.getArgCount() < 1)
            throw Exception("Delay is missing");

        // Get delay
        int delay = std::stoi(this->_protocolManager.getArg(0));
        emit message("[ADMIN] Server shutdown in " + QString::number(delay) + " second(s)");

        // Create query STOP
        AGDOCProtocol query;
        query.command = AGDOCProtocol::STOP;
        query.content.stop.delay = delay;

        // Inform all clients
        this->informAllClients(query);

        // Start timer
        this->_timer->setInterval(delay);
        this->_timer->start();
    }
    catch(const Exception& exception)
    {
        this->sendFAILMessage(exception.what());
    }
}

void ThreadAdmin::manageQUIT(void)
{
    emit message("[ADMIN] Administrator requested to quit");
    emit administratorDisconnected();
}

void ThreadAdmin::manageFAIL(void)
{
    // Fail query is created if no data are received --> disconnect the admin
    this->_protocolManager.setCommand(DOCSAP::QUIT);
}

void ThreadAdmin::sendFAILMessage(const QString& cause)
{
    this->_protocolManager.setNewCommand(DOCSAP::FAIL);
    this->_protocolManager.addArg(cause.toStdString());
    this->_clientSocket->send(this->_protocolManager.generateQuery());
    emit message("[ADMIN] " + cause);
}

void ThreadAdmin::informAllClients(const AGDOCProtocol& request)
{
    // Prepare reply
    this->_protocolManager.setNewCommand(DOCSAP::ACK);

    // Get all IPv4 addresses
    QList<std::string> ips;
    conditionMutex.lock();
    for (int i(0); i < clients.size(); ++i)
        ips.append(clients.at(i)->getIPv4());
    conditionMutex.unlock();

    if (!ips.isEmpty())
    {
        try
        {
            emit message("[ADMIN] Infoms all clients");

            // Send query to all clients
            TCPSocketClient* sockClient;
            for (int i(0); i < ips.count(); ++i)
            {
                sockClient = new TCPSocketClient();
                sockClient->connectToHost(ips.at(i), this->_portAdminClient);
                sockClient->send<AGDOCProtocol>(&request);
                delete sockClient;
            }
        }
        catch(const SocketException& exception)
        {
            // Reply FAIL
            this->_protocolManager.setNewCommand(DOCSAP::FAIL);
            this->_protocolManager.addArg(exception.what());
        }
    }

    // Send Reply
    this->_clientSocket->send(this->_protocolManager.generateQuery());
}

bool ThreadAdmin::stopRequested(void)
{
    QMutexLocker locker(&this->_mutex);
    return this->_stopRequested;
}
