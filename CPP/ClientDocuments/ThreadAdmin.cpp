#include "ThreadAdmin.hpp"

ThreadAdmin::ThreadAdmin(QObject* parent) :
    QThread(parent), _port(DEFAULT_ADMIN_PORT), _serverSocket(NULL),
    _clientSocket(NULL), _request(), _stopRequested(false)
{
    // Get admin port number
    IniParser parser("client_documents.conf");

    if (parser.keyExists("admin_port"))
        this->_port = std::stoi(parser.value("admin_port"));
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

void ThreadAdmin::run(void)
{
    try
    {
        // Create server socket
        this->_serverSocket = new TCPSocketServer(this->_port);
    }
    catch(const SocketException& exception)
    {
        // Send message
        QString msg("Thread admin starting error : ");
        msg.append(exception.what());
        emit message(msg);

        return;
    }

    // Main loop
    while(!this->stopRequested())
    {
        // Waiting client
        try
        {
            emit message("[ADMIN] Waiting administrator");
            this->_clientSocket = this->_serverSocket->nextPendingConnection();
            emit message("[ADMIN] Administrator connected");
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

        // Get request
        this->_clientSocket->recv<AGDOCProtocol>(&this->_request);
        emit message("[ADMIN] Receive query");

        // Close the connection
        delete this->_clientSocket;
        this->_clientSocket = NULL;

        switch (this->_request.command)
        {
            case AGDOCProtocol::PAUSE:
                emit serverSuspended(true);
                break;
            case AGDOCProtocol::RESUME:
                emit serverSuspended(false);
                break;
            case AGDOCProtocol::STOP:
                emit serverShutdown(this->_request.content.stop.delay);
                break;
            case AGDOCProtocol::SHUTDOWN_NOW:
                emit serverShutdownNow();
                break;
            default:
                emit message("[ADMIN] Invalid query");
                break;
        }
    }
}

bool ThreadAdmin::stopRequested(void)
{
    QMutexLocker locker(&this->_mutex);
    return this->_stopRequested;
}

