#include "ThreadServerPool.hpp"

// Global variables declaration
QWaitCondition clientsIsNotEmpty;
QMutex conditionMutex;
QList<TCPSocketClient*> clients;
int clientAvailable = 0;

ThreadServerPool::ThreadServerPool(int port, int threadsClientCount, QObject* parent)
    : QThread(parent), _port(port), _threadsClientCount(threadsClientCount),
      _socketServer(NULL), _socketClient(NULL), _stopRequested(false)
{
    // Open and parse settings file
    IniParser parser("server_documents.conf");

    // Get delimiters
    if (parser.keyExists("commandDelimiter"))
        _protocolManager.setCommandDelimiter(parser.value("commandDelimiter"));
    if (parser.keyExists("headerDelimiter"))
        _protocolManager.setHeaderDelimiter(parser.value("headerDelimiter"));
    if (parser.keyExists("endDelimiter"))
        _protocolManager.setEndDelimiter(parser.value("endDelimiter"));

    // Create threads client (not launch)
    for (int i(0); i < this->_threadsClientCount; ++i)
    {
        // Instanciate new thread client
        ThreadClient* threadClient = new ThreadClient(this->_protocolManager);

        // Connect signals
        connect(threadClient, SIGNAL(message(QString)),
                this, SIGNAL(message(QString)));
        connect(threadClient, SIGNAL(clientAccepted()),
                this, SLOT(threadClientClientAccepted()));
        connect(threadClient, SIGNAL(clientDisconnected()),
                this, SLOT(threadClientClientDisconnected()));
        connect(threadClient, SIGNAL(started()),
                this, SLOT(threadClientStarted()));
        connect(threadClient, SIGNAL(finished()),
                this, SLOT(threadClientFinished()));

        // Add newly created thread client to the list
        this->_threadsClient.append(threadClient);
    }
}

ThreadServerPool::~ThreadServerPool(void)
{
    // Delete threads client
    foreach (ThreadClient* threadClient, this->_threadsClient)
    {
        if (threadClient->isRunning())
        {
            threadClient->terminate();
            threadClient->wait();
        }

        delete threadClient;
    }

    delete _socketClient;
    _socketClient = NULL;

    delete _socketServer;
    _socketServer = NULL;
}

void ThreadServerPool::requestStop(void)
{
    QMutexLocker locker(&_mutex);
    _stopRequested = true;

    // Request stop for all threads client
    foreach (ThreadClient* threadClient, this->_threadsClient)
        threadClient->requestStop();

    // Wake all threads client
    clientsIsNotEmpty.wakeAll();

    // Wait until all threads client stop
    foreach (ThreadClient* threadClient, this->_threadsClient)
            threadClient->wait();

    // Interrupt server blocking function
    if (_socketServer != NULL && _socketServer->isValid())
        _socketServer->close();
}

void ThreadServerPool::threadClientStarted(void)
{
    emit message("Thread client : started");
}

void ThreadServerPool::threadClientFinished(void)
{
    emit message("Thread client : ended");
}

void ThreadServerPool::threadClientClientAccepted(void)
{
    emit message("Thread client : client accepted");
}

void ThreadServerPool::threadClientClientDisconnected(void)
{
    emit clientsCountChanged(clients.count());
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
        // Send message
        emit message("Starting server error : " +
                     QString::fromStdString(exception.what()));
        return;
    }

    // Launch all threads client
    foreach (ThreadClient* threadClient, this->_threadsClient)
        threadClient->start();

    // Main loop
    while(true)
    {
        if (stopRequested())
            break;

        // Waiting client
        try
        {
            emit message("Thread server : wainting client");
            this->_socketClient = _socketServer->nextPendingConnection();
            emit message("Thread server : client connected");
        }
        catch(const SocketException& exception)
        {
            delete this->_socketServer;
            this->_socketServer = NULL;

            // Send message
            emit message("Thread server : stop waiting client : " +
                         QString::fromStdString(exception.what()));

            continue;
        }

        // Check if a thread is available for the newly connected client
        if (clients.count() >= this->_threadsClientCount)
        {
            emit message("Thread server : no thread client available");

            // TODO : rejeter demande
            this->sendFAILMessage("Connection failure server full");

            // Free client socket
            delete this->_socketClient;
            this->_socketClient = NULL;
            emit message("Thread server : client disconnected");
            continue;
        }

        // Add newly connected client to the list
        conditionMutex.lock();
        clients.append(this->_socketClient);
        ++clientAvailable;
        conditionMutex.unlock();

        emit clientsCountChanged(clients.count());

        this->_socketClient = NULL; // Thread client free socket

        // Wakes one thread waiting a new client
        clientsIsNotEmpty.wakeOne();
    }
}

void ThreadServerPool::sendFAILMessage(const QString& cause)
{
    this->_protocolManager.setNewCommand(GDOCP::FAIL);
    this->_protocolManager.setHeaderValue("cause", cause.toStdString());
    this->_socketClient->send(this->_protocolManager.generateQuery());
}

bool ThreadServerPool::stopRequested(void)
{
    QMutexLocker locker(&_mutex);
    return _stopRequested;
}
