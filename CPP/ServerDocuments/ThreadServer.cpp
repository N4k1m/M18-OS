#include "ThreadServer.hpp"

ThreadServer::ThreadServer(int port, QObject *parent) :
    QThread(parent), protocolManager("#", "=", "!"), primeGenerator(),
    _port(port), server_socket(NULL), client_socket(NULL),
    client_connected(false), _stopRequested(false)
{
    // Get delimiters
    IniParser parser("server_documents.conf");

    if (parser.keyExists("commandDelimiter"))
        protocolManager.setCommandDelimiter(parser.value("commandDelimiter"));
    if (parser.keyExists("headerDelimiter"))
        protocolManager.setHeaderDelimiter(parser.value("headerDelimiter"));
    if (parser.keyExists("endDelimiter"))
        protocolManager.setEndDelimiter(parser.value("endDelimiter"));

    // Initialize the prime generator that generate prime numbers up to 1000000
    primeGenerator.init_fast(1000000);
}

ThreadServer::~ThreadServer(void)
{
    delete this->client_socket;
    this->client_socket = NULL;
    delete this->server_socket;
    this->server_socket = NULL;
}

void ThreadServer::requestStop()
{
    QMutexLocker locker(&mutex);
    this->_stopRequested = true;

    // Interrupt client blocking function
    if (this->client_socket != NULL && this->client_socket->isValid())
        this->client_socket->close();

    // Or interrupt server blocking function
    if (this->server_socket != NULL && this->server_socket->isValid())
        this->server_socket->close();
}

void ThreadServer::run()
{
    try
    {
        // Create serveur socket
        this->server_socket = new TCPSocketServer(_port);
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
            client_socket = server_socket->nextPendingConnection();
            emit message("Client connected");
        }
        catch(const SocketException& exception)
        {
            delete this->server_socket;
            this->server_socket = NULL;

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
            while(protocolManager.command() != GDOCP::CLOSE)
            {
                // Receive query
                ssize_t ret = client_socket->recv(msg, protocolManager.endDelimiter());

                // Client close the connection
                if (ret == SOCKET_CLOSED)
                    break;

                emit message("Message received : " + QString::fromStdString(msg));

                // create query object. FAIL query is created if msg is empty
                protocolManager.parseQuery(msg);

                switch (protocolManager.command())
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
        delete this->client_socket;
        client_socket = NULL;
        client_connected = false;
        protocolManager.setCommand(GDOCP::UNKNOWN);
        emit message("Client disconnected");
    }
}

void ThreadServer::manageLOGIN(void)
{
    // Get all users login - password
    IniParser usersParser("DB/users.conf");
    std::string username = protocolManager.getHeaderValue("username");

    // Check if user exists
    if(!usersParser.keyExists(username))
    {
        sendFAILMessage("User doesn't exist");
        emit message("User " + QString::fromStdString(username) + " doesn't exist");
        return;
    }

    // Generate nonce (prime number)
    unsigned int nonce = primeGenerator.get();
    emit message("Valid user : nonce generated = " + QString::number(nonce));

    // Create query object (LOGIN ACK)
    protocolManager.setCommand(GDOCP::LOGIN);
    protocolManager.clearHeaders();
    std::string tmp_str = std::to_string(nonce);
    protocolManager.setHeaderValue("nonce", tmp_str);

    // Send query (LOGIN ACK)
    client_socket->send(protocolManager.generateQuery());

    // Receive LOGIN ACK with cnonce and hashed password
    client_socket->recv(tmp_str, protocolManager.endDelimiter());
    emit message("LOGIN ACK received : " + QString::fromStdString(tmp_str));

    // Create query objet
    protocolManager.parseQuery(tmp_str);

    // Check if we recieved a LOGIN request
    if (protocolManager.command() != GDOCP::LOGIN)
    {
        sendFAILMessage("Invalide request received");
        emit message("Invalide request received");
        return;
    }

    // Get cnonce
    unsigned int cnonce;
    tmp_str = protocolManager.getHeaderValue("cnonce");
    std::istringstream(tmp_str) >> cnonce;
    emit message("cnonce recieved = " + QString::number(cnonce));

    // Get hash passwd
    unsigned int hash_passwd_recieved;
    tmp_str = protocolManager.getHeaderValue("hashpassword");
    std::istringstream(tmp_str) >> hash_passwd_recieved;
    emit message("Hash password recieved = " + QString::number(hash_passwd_recieved));

    // Hash password
    std::string password = usersParser.value(username);
    unsigned int hash_passwd = Hash::hash_str(password, nonce, cnonce);
    emit message("Hash password = " + QString::number(hash_passwd));

    protocolManager.clearHeaders();

    // Accept or not the client
    if (hash_passwd == hash_passwd_recieved)
    {
        client_connected = true;
        protocolManager.setCommand(GDOCP::LOGIN);
        emit message("Client logged");
    }
    else
    {
        client_connected = false;
        protocolManager.setCommand(GDOCP::FAIL);
        protocolManager.setHeaderValue("cause", "Invalid password");
        emit message("Client refused");
    }

    // Send query
    client_socket->send(protocolManager.generateQuery());
}

void ThreadServer::manageGETPLAIN(void)
{
    // Check if client is successfully connected
    if (!client_connected)
    {
        sendFAILMessage("You must be logged in to acces files");
        emit message("Unidentified client try to get plain document");
        return;
    }

    // Get filename from query object
    std::string filename = "PLAIN/" + protocolManager.getHeaderValue("filename");

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
    protocolManager.setCommand(GDOCP::GETPLAIN);
    protocolManager.clearHeaders();
    protocolManager.setHeaderValue("content", content);

    // Send GETPLAIN reply
    client_socket->send(protocolManager.generateQuery());
}

void ThreadServer::manageGETCIPHER(void)
{
    // Check if client is successfully connected
    if (!client_connected)
    {
        sendFAILMessage("You must be logged in to acces files");
        emit message("Unidentified client try to get cipher document");
        return;
    }

    // Get filename from query object
    std::string filename = "CIPHER/" + protocolManager.getHeaderValue("filename");

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
    protocolManager.setCommand(GDOCP::GETCIPHER);
    protocolManager.clearHeaders();
    protocolManager.setHeaderValue("content", content);

    // Send GETCIPHER reply
    client_socket->send(protocolManager.generateQuery());
}

void ThreadServer::sendFAILMessage(const std::string& cause)
{
    protocolManager.clearHeaders();
    protocolManager.setCommand(GDOCP::FAIL);
    protocolManager.setHeaderValue("cause", cause);
    client_socket->send(protocolManager.generateQuery());
}

bool ThreadServer::stopRequested()
{
    QMutexLocker locker(&mutex);
    return this->_stopRequested;
}
