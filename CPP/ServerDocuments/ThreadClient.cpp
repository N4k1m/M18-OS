#include "ThreadClient.hpp"

ThreadClient::ThreadClient(const GDOCP &protocolManager)
    : _protocolManager(protocolManager), _primeGenerator(), _socketClient(NULL),
      _clientConnected(false), _stopRequested(false)
{
    // Initialize the prime generator that generate prime numbers up to 1000000
    this->_primeGenerator.init_fast(1000000);
}

ThreadClient::~ThreadClient(void)
{
    delete this->_socketClient;
    this->_socketClient = NULL;
}

void ThreadClient::requestStop(void)
{
    QMutexLocker locker(&_mutex);
    _stopRequested = true;

    // Interrupt client blocking function
    if (this->_socketClient != NULL && this->_socketClient->isValid())
        this->_socketClient->close();
}

void ThreadClient::run(void)
{
    // Main loop
    while(true)
    {
        conditionMutex.lock();
        while(clientAvailable <= 0 && !stopRequested())
            clientsIsNotEmpty.wait(&conditionMutex);

        // Stop if requested
        if (stopRequested())
        {
            conditionMutex.unlock();
            break;
        }

        // Get the new client
        this->_socketClient = clients.last();
        --clientAvailable;
        conditionMutex.unlock();

        emit clientAccepted();

        // Manage client
        try
        {
            std::string msg;

            // While client don't ask to close
            while(!this->_protocolManager.is(GDOCP::CLOSE))
            {
                // Receive query
                ssize_t ret = this->_socketClient->recv(
                                  msg, this->_protocolManager.endDelimiter());

                // Client close the connection
                if (ret == SOCKET_CLOSED)
                {
                    emit message("Thread client : client close the connection");
                    break;
                }

                emit message("Thread client : message received : "
                             + QString::fromStdString(msg));

                // create query object. FAIL query is created if msg is empty
                this->_protocolManager.parseQuery(msg);

                switch (this->_protocolManager.command())
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
                        emit message("Thread client : received invalid query");
                        break;
                }
            }
        }
        catch(const SocketException& exception)
        {
            emit message("Thread client : stop receiving message : " +
                         QString::fromStdString(exception.what()));
        }

        // Remove client from list
        conditionMutex.lock();
        clients.removeOne(this->_socketClient);
        conditionMutex.unlock();

        // Free client socket
        delete this->_socketClient;
        this->_socketClient = NULL;

        // Reset client settings
        this->_clientConnected = false;
        this->_protocolManager.setNewCommand(GDOCP::UNKNOWN);

        emit clientDisconnected();
    }
}

void ThreadClient::manageLOGIN(void)
{
    // Get all users login - password
    IniParser usersParser("DB/users.conf");
    std::string username = this->_protocolManager.getHeaderValue("username");

    // Check if user exists
    if(!usersParser.keyExists(username))
    {
        sendFAILMessage("User doesn't exist");
        emit message("Thread client : user " + QString::fromStdString(username)
                     + " doesn't exist");
        return;
    }

    // Generate nonce (prime number)
    unsigned int nonce = this->_primeGenerator.get();
    emit message("Thread client : valid user : nonce generated = "
                 + QString::number(nonce));

    // Create query object (LOGIN ACK)
    this->_protocolManager.setNewCommand(GDOCP::LOGIN);
    std::string tmp_str = std::to_string(nonce);
    this->_protocolManager.setHeaderValue("nonce", tmp_str);

    // Send query (LOGIN ACK)
    this->_socketClient->send(this->_protocolManager.generateQuery());

    // Receive LOGIN ACK with cnonce and hashed password
    this->_socketClient->recv(tmp_str, this->_protocolManager.endDelimiter());
    emit message("Thread client : LOGIN ACK received : "
                 + QString::fromStdString(tmp_str));

    // Create query objet
    this->_protocolManager.parseQuery(tmp_str);

    // Check if we recieved a LOGIN request
    if (!this->_protocolManager.is(GDOCP::LOGIN))
    {
        sendFAILMessage("Invalide request received");
        emit message("Thread client : invalide request received");
        return;
    }

    // Get cnonce
    unsigned int cnonce;
    tmp_str = this->_protocolManager.getHeaderValue("cnonce");
    std::istringstream(tmp_str) >> cnonce;
    emit message("Thread client : cnonce recieved = "
                 + QString::number(cnonce));

    // Get hash passwd
    unsigned int hash_passwd_recieved;
    tmp_str = this->_protocolManager.getHeaderValue("hashpassword");
    std::istringstream(tmp_str) >> hash_passwd_recieved;
    emit message("Thread client : hash password recieved = "
                 + QString::number(hash_passwd_recieved));

    // Hash password
    std::string password = usersParser.value(username);
    unsigned int hash_passwd = Hash::hash_str(password, nonce, cnonce);
    emit message("Thread client : hash password = "
                 + QString::number(hash_passwd));

    // Accept or not the client
    if (hash_passwd == hash_passwd_recieved)
    {
        this->_clientConnected = true;
        this->_protocolManager.setNewCommand(GDOCP::LOGIN);
        emit message("Thread client : client logged");
    }
    else
    {
        this->_clientConnected = false;
        this->_protocolManager.setNewCommand(GDOCP::FAIL);
        this->_protocolManager.setHeaderValue("cause", "Invalid password");
        emit message("Thread client : client refused");
    }

    // Send query
    this->_socketClient->send(this->_protocolManager.generateQuery());
}

void ThreadClient::manageGETPLAIN(void)
{
    // Check if client is successfully connected
    if (!this->_clientConnected)
    {
        sendFAILMessage("You must be logged in to acces files");
        emit message("Thread client : unidentified client try to get plain document");
        return;
    }

    // Get filename from query object
    std::string filename = "PLAIN/" +
                           this->_protocolManager.getHeaderValue("filename");

    // Check if file name is valid
    if (filename.empty())
    {
        sendFAILMessage("Invalid file name");
        emit message("Thread client : invalid file name");
        return;
    }

    // Check if file exist
    std::ifstream infile(filename.c_str());
    if(!infile.good())
    {
        infile.close();
        sendFAILMessage("File \"" + filename + "\" doesn't exist");
        emit message("Thread client : file \""
                     + QString::fromStdString(filename) + "\" doesn't exist");
        return;
    }

    emit message("Thread client : file exists");

    // Get file content into string
    std::string content((std::istreambuf_iterator<char>(infile)),
                           (std::istreambuf_iterator<char>()));
    infile.close();

    // Create query
    this->_protocolManager.setNewCommand(GDOCP::GETPLAIN);
    this->_protocolManager.setHeaderValue("content", content);

    // Send GETPLAIN reply
    this->_socketClient->send(this->_protocolManager.generateQuery());
}

void ThreadClient::manageGETCIPHER(void)
{
    // Check if client is successfully connected
    if (!this->_clientConnected)
    {
        sendFAILMessage("You must be logged in to acces files");
        emit message("Thread client : unidentified client try to get cipher document");
        return;
    }

    // Get filename from query object
    std::string filename = "CIPHER/" +
                           this->_protocolManager.getHeaderValue("filename");

    // Check if file name is valid
    if (filename.empty())
    {
        sendFAILMessage("Invalid file name");
        emit message("Thread client : invalid file name");
        return;
    }

    // Check if file exist
    std::ifstream infile(filename.c_str());
    if(!infile.good())
    {
        infile.close();
        sendFAILMessage("File \"" + filename + "\" doesn't exist");
        emit message("Thread client : file \""
                     + QString::fromStdString(filename) + "\" doesn't exist");
        return;
    }

    emit message("Thread client : file exists");

    // Get file content into string
    std::string content((std::istreambuf_iterator<char>(infile)),
                           (std::istreambuf_iterator<char>()));
    infile.close();

    // Create query
    this->_protocolManager.setNewCommand(GDOCP::GETCIPHER);
    this->_protocolManager.setHeaderValue("content", content);

    // Send GETCIPHER reply
    this->_socketClient->send(this->_protocolManager.generateQuery());
}

void ThreadClient::sendFAILMessage(const std::string& cause)
{
    this->_protocolManager.setNewCommand(GDOCP::FAIL);
    this->_protocolManager.setHeaderValue("cause", cause);
    this->_socketClient->send(this->_protocolManager.generateQuery());
}

bool ThreadClient::stopRequested(void)
{
    QMutexLocker locker(&_mutex);
    return _stopRequested;
}

