#include "ThreadAdmin.hpp"

ThreadAdmin::ThreadAdmin(int port, QObject* parent) :
    QThread(parent), _port(port), _serverSocket(NULL), _clientSocket(NULL),
    _protocolManager(), _clientLoggedIn(false), _stopRequested(false)
{
    // Get DOCSAP delimiters
    IniParser parser("server_documents.conf");

    if (parser.keyExists("DOCSAP_fieldDelimiter"))
        this->_protocolManager.setFieldDelimiter(parser.value("DOCSAP_fieldDelimiter"));
    if (parser.keyExists("DOCSAP_endDelimiter"))
        this->_protocolManager.setEndDelimiter(parser.value("DOCSAP_endDelimiter"));
}

ThreadAdmin::~ThreadAdmin(void)
{
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
        this->_clientSocket->close();

    // Or interrupt server blocking function
    if (this->_serverSocket != NULL && this->_serverSocket->isValid())
        this->_serverSocket->close();
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
    }
    catch(Exception const& exception)
    {
        this->sendFAILMessage(exception.what());
        emit message("[ADMIN] " + QString::fromStdString(exception.what()));
    }
}

void ThreadAdmin::manageQUIT(void)
{
    emit message("[ADMIN] Administrator requested to quit");

    // Nothing else to do ...
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
}

bool ThreadAdmin::stopRequested(void)
{
    QMutexLocker locker(&this->_mutex);
    return this->_stopRequested;
}
