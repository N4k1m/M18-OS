#include "ThreadServerPool.hpp"

ThreadServerPool::ThreadServerPool(int port, int threadsClient, QObject* parent)
    : QThread(parent), _port(port), _threadsClient(threadsClient),
      _socketServer(NULL), _socketClient(NULL), _stopRequested(false)
{
    // Get delimiters
    IniParser parser("server_documents.conf");

    if (parser.keyExists("commandDelimiter"))
        _protocolManager.setCommandDelimiter(parser.value("commandDelimiter"));
    if (parser.keyExists("headerDelimiter"))
        _protocolManager.setHeaderDelimiter(parser.value("headerDelimiter"));
    if (parser.keyExists("endDelimiter"))
        _protocolManager.setEndDelimiter(parser.value("endDelimiter"));
}

ThreadServerPool::~ThreadServerPool(void)
{
    qDebug("ThreadServerPool ~ThreadServerPool");

    delete _socketClient;
    _socketClient = NULL;

    delete _socketServer;
    _socketServer = NULL;
}

void ThreadServerPool::requestStop(void)
{
    qDebug("ThreadServerPool requestStop");

    QMutexLocker locker(&_mutex);
    _stopRequested = true;

    // TODO : Faire un requestStop sur tous les threads client

    // Interrupt server blocking function
    if (_socketServer != NULL && _socketServer->isValid())
        _socketServer->close();
}

void ThreadServerPool::run(void)
{
    try
    {
        // Create server socket
        _socketServer = new TCPSocketServer(_port);
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
        if (stopRequested())
        {
            qDebug("Main loop stopRequested");
            break;
        }

        // Waiting client
        try
        {
            emit message("Wainting client");
            _socketClient = _socketServer->nextPendingConnection();
            emit message("Client connected");
        }
        catch(const SocketException& exception)
        {
            qDebug("SocketException Waiting client");

            delete _socketServer;
            _socketServer = NULL;

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

bool ThreadServerPool::stopRequested(void)
{
    QMutexLocker locker(&_mutex);
    return _stopRequested;
}
