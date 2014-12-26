#include "ThreadServer.hpp"

ThreadServer::ThreadServer(int port, QObject* parent) :
    QThread(parent), _protocolManager(), _primeGenerator(), _port(port),
    _serverSocket(NULL), _clientSocket(NULL), _clientConnected(false),
    _stopRequested(false)
{
    // Get delimiters
    IniParser parser("server_documents.conf");

    if (parser.keyExists("commandDelimiter"))
        _protocolManager.setCommandDelimiter(parser.value("commandDelimiter"));
    if (parser.keyExists("headerDelimiter"))
        _protocolManager.setHeaderDelimiter(parser.value("headerDelimiter"));
    if (parser.keyExists("endDelimiter"))
        _protocolManager.setEndDelimiter(parser.value("endDelimiter"));

    // Initialize the prime generator that generate prime numbers up to 1000000
    _primeGenerator.init_fast(1000000);
}

ThreadServer::~ThreadServer(void)
{
    delete this->_clientSocket;
    this->_clientSocket = NULL;
    delete this->_serverSocket;
    this->_serverSocket = NULL;
}

void ThreadServer::requestStop(void)
{
    QMutexLocker locker(&_mutex);
    this->_stopRequested = true;

    // Interrupt client blocking function
    if (this->_clientSocket != NULL && this->_clientSocket->isValid())
        this->_clientSocket->close();

    // Or interrupt server blocking function
    if (this->_serverSocket != NULL && this->_serverSocket->isValid())
        this->_serverSocket->close();
}

void ThreadServer::run(void)
{
    try
    {
        // Create serveur socket
        this->_serverSocket = new TCPSocketServer(_port);
    }
    catch(const SocketException& exception)
    {
        // Send message
        QString msg("Starting server error : ");
        msg.append(exception.what());
        emit message(msg);

        return;
    }

    // Main loop
    while(true)
    {
        if (stopRequested())
            break;

        // Waiting client
        try
        {
            emit message("Wainting client");
            _clientSocket = _serverSocket->nextPendingConnection();
            emit message("Client connected");
        }
        catch(const SocketException& exception)
        {
            delete this->_serverSocket;
            this->_serverSocket = NULL;

            // Send message
            QString msg("Server stop waiting client : ");
            msg.append(exception.what());
            emit message(msg);

            continue;
        }

        // Manage client
        try
        {
            std::string msg;

            // while client don't ask to close
            while(_protocolManager.command() != GDOCP::CLOSE)
            {
                // Receive query
                ssize_t ret = _clientSocket->recv(msg, _protocolManager.endDelimiter());

                // Client close the connection
                if (ret == SOCKET_CLOSED)
                    break;

                emit message("Message received : " + QString::fromStdString(msg));

                // create query object. FAIL query is created if msg is empty
                _protocolManager.parseQuery(msg);

                switch (_protocolManager.command())
                {
                    case GDOCP::LOGIN:
                        manageLOGIN();
                        break;
                    case GDOCP::GETPLAIN:
                        manageGETPLAIN();
                        break;
                    case GDOCP::GETCIPHER:
                        manageGETCIPHER();
                        break;
                    default:
                        emit message("Invalid query ");
                        break;
                }
            }
        }
        catch(const SocketException& exception)
        {
            // Send message
            QString msg("Server stop receiving message : ");
            msg.append(exception.what());
            emit message(msg);
        }

        // Free client socket
        delete this->_clientSocket;
        _clientSocket = NULL;
        _clientConnected = false;
        _protocolManager.setCommand(GDOCP::UNKNOWN);
        emit message("Client disconnected");
    }
}

void ThreadServer::manageLOGIN(void)
{
    // Get all users login - password
    IniParser usersParser("DB/users.conf");
    std::string username = _protocolManager.getHeaderValue("username");

    // Check if user exists
    if(!usersParser.keyExists(username))
    {
        sendFAILMessage("User doesn't exist");
        emit message("User " + QString::fromStdString(username) + " doesn't exist");
        return;
    }

    // Generate nonce (prime number)
    unsigned int nonce = _primeGenerator.get();
    emit message("Valid user : nonce generated = " + QString::number(nonce));

    // Create query object (LOGIN ACK)
    _protocolManager.setCommand(GDOCP::LOGIN);
    _protocolManager.clearHeaders();
    std::string tmp_str = std::to_string(nonce);
    _protocolManager.setHeaderValue("nonce", tmp_str);

    // Send query (LOGIN ACK)
    _clientSocket->send(_protocolManager.generateQuery());

    // Receive LOGIN ACK with cnonce and hashed password
    _clientSocket->recv(tmp_str, _protocolManager.endDelimiter());
    emit message("LOGIN ACK received : " + QString::fromStdString(tmp_str));

    // Create query objet
    _protocolManager.parseQuery(tmp_str);

    // Check if we recieved a LOGIN request
    if (_protocolManager.command() != GDOCP::LOGIN)
    {
        sendFAILMessage("Invalide request received");
        emit message("Invalide request received");
        return;
    }

    // Get cnonce
    unsigned int cnonce;
    tmp_str = _protocolManager.getHeaderValue("cnonce");
    std::istringstream(tmp_str) >> cnonce;
    emit message("cnonce recieved = " + QString::number(cnonce));

    // Get hash passwd
    unsigned int hash_passwd_recieved;
    tmp_str = _protocolManager.getHeaderValue("hashpassword");
    std::istringstream(tmp_str) >> hash_passwd_recieved;
    emit message("Hash password recieved = " + QString::number(hash_passwd_recieved));

    // Hash password
    std::string password = usersParser.value(username);
    unsigned int hash_passwd = Hash::hash_str(password, nonce, cnonce);
    emit message("Hash password = " + QString::number(hash_passwd));

    _protocolManager.clearHeaders();

    // Accept or not the client
    if (hash_passwd == hash_passwd_recieved)
    {
        _clientConnected = true;
        _protocolManager.setCommand(GDOCP::LOGIN);
        emit message("Client logged");
    }
    else
    {
        _clientConnected = false;
        _protocolManager.setCommand(GDOCP::FAIL);
        _protocolManager.setHeaderValue("cause", "Invalid password");
        emit message("Client refused");
    }

    // Send query
    _clientSocket->send(_protocolManager.generateQuery());
}

void ThreadServer::manageGETPLAIN(void)
{
    // Check if client is successfully connected
    if (!_clientConnected)
    {
        sendFAILMessage("You must be logged in to acces files");
        emit message("Unidentified client try to get plain document");
        return;
    }

    // Get filename from query object
    std::string filename = "PLAIN/" + _protocolManager.getHeaderValue("filename");

    // Check if file name is valid
    if (filename.empty())
    {
        sendFAILMessage("Invalid file name");
        emit message("Invalid file name");
        return;
    }

    // Check if file exist
    std::ifstream infile(filename.c_str());
    if(!infile.good())
    {
        infile.close();
        sendFAILMessage("File \"" + filename + "\" doesn't exist");
        emit message("File \"" + QString::fromStdString(filename) + "\" doesn't exist");
        return;
    }

    emit message("File exists");

    // Get file content into string
    std::string content((std::istreambuf_iterator<char>(infile)),
                           (std::istreambuf_iterator<char>()));

    infile.close();

    // Create query
    _protocolManager.setCommand(GDOCP::GETPLAIN);
    _protocolManager.clearHeaders();
    _protocolManager.setHeaderValue("content", content);

    // Send GETPLAIN reply
    _clientSocket->send(_protocolManager.generateQuery());
}

void ThreadServer::manageGETCIPHER(void)
{
    // Check if client is successfully connected
    if (!_clientConnected)
    {
        sendFAILMessage("You must be logged in to acces files");
        emit message("Unidentified client try to get cipher document");
        return;
    }

    // Get filename from query object
    std::string filename = "CIPHER/" + _protocolManager.getHeaderValue("filename");

    // Check if file name is valid
    if (filename.empty())
    {
        sendFAILMessage("Invalid file name");
        emit message("Invalid file name");
        return;
    }

    // Check if file exist
    std::ifstream infile(filename.c_str());
    if(!infile.good())
    {
        infile.close();
        sendFAILMessage("File \"" + filename + "\" doesn't exist");
        emit message("File \"" + QString::fromStdString(filename) + "\" doesn't exist");
        return;
    }

    emit message("File exists");

    // Get file content into string
    std::string content((std::istreambuf_iterator<char>(infile)),
                           (std::istreambuf_iterator<char>()));

    infile.close();

    // Create query
    _protocolManager.setCommand(GDOCP::GETCIPHER);
    _protocolManager.clearHeaders();
    _protocolManager.setHeaderValue("content", content);

    // Send GETCIPHER reply
    _clientSocket->send(_protocolManager.generateQuery());
}

void ThreadServer::sendFAILMessage(const std::string& cause)
{
    _protocolManager.clearHeaders();
    _protocolManager.setCommand(GDOCP::FAIL);
    _protocolManager.setHeaderValue("cause", cause);
    _clientSocket->send(_protocolManager.generateQuery());
}

bool ThreadServer::stopRequested(void)
{
    QMutexLocker locker(&_mutex);
    return this->_stopRequested;
}
