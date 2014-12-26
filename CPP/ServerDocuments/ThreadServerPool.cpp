#include "ThreadServerPool.hpp"

ThreadServerPool::ThreadServerPool(int port, int threadsClient, QObject* parent)
    : QThread(parent), _port(port), _threadsClient(threadsClient),
      _socketServer(NULL), _socketClient(NULL), _stopRequested(false)
{
    // TODO : récupérer les délimiters
}

ThreadServerPool::~ThreadServerPool(void)
{
    qDebug("ThreadServerPool ~ThreadServerPool");

    delete this->_socketClient;
    this->_socketClient = NULL;

    delete this->_socketServer;
    this->_socketServer = NULL;
}

void ThreadServerPool::requestStop(void)
{
    qDebug("ThreadServerPool requestStop");

    QMutexLocker locker(&this->_mutex);
    this->_stopRequested = true;

    // TODO : Faire un requestStop sur tous les threads client

    // Interrupt server blocking function
    if (this->_socketServer != NULL && this->_socketServer->isValid())
        this->_socketServer->close();
}

void ThreadServerPool::run(void)
{
    try
    {
        // Create server socket
        this->_socketServer = new TCPSocketServer(this->_port);
    }
    catch(const SocketException& exception)
    {
        qDebug("SocketException create server socket");

        // Send message
        QString msg("Starting server error : ");
        msg.append(exception.what());
        emit message(msg);

        return;
    }

    // TODO : lancer tous les threads client

    // Main loop
    while(true)
    {
        if (this->stopRequested())
        {
            qDebug("Main loop stopRequested");
            break;
        }

        // Waiting client
        try
        {
            emit message("Wainting client");
            this->_socketClient = this->_socketServer->nextPendingConnection();
            emit message("Client connected");
        }
        catch(const SocketException& exception)
        {
            qDebug("SocketException Waiting client");

            delete this->_socketServer;
            this->server_socket = NULL;

            // Send message
            QString msg("Server stop waiting client : ");
            msg.append(exception.what());
            emit message(msg);

            continue;
        }

        // TODO : regarder s'il y a une place de disponible

        // TODO : attribuer le client à un des threads client
    }
}
