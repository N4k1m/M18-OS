#include "ThreadAdmin.hpp"

ThreadAdmin::ThreadAdmin(int port, QObject* parent) :
    QThread(parent), _port(port), _serverSocket(NULL), _clientSocket(NULL),
    _clientLoggedIn(false), _clientStop(false), _stopRequested(false)
{
    // TODO : récupérer les paramètres de délimiters

    // TODO : ouvrir les users admin
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
            this->_clientStop = false;
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

            while(!this->_clientStop)
            {
                ssize_t ret = this->_clientSocket->recv(msg, "#");

                // Client close the connection
                if (ret == SOCKET_CLOSED)
                    break;

                emit message("[ADMIN] Receive query : " + QString::fromStdString(msg));

                // TODO : créer un parser pour le protocole DOCSAP

                if (msg == "QUIT#")
                    this->_clientStop = true;
                else
                {
                    std::string message = "ACK#";
                    this->_clientSocket->send(message);
                }

                // TODO Switch case sur la commande de la requete
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

bool ThreadAdmin::stopRequested(void)
{
    QMutexLocker locker(&this->_mutex);
    return this->_stopRequested;
}
