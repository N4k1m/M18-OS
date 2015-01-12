#include "Widget.hpp"
#include "ui_Widget.h"

Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget),
    _threadAdmin(NULL),
    _clientSocket(NULL),
    protocolManager(),
    primeGenerator(),
    _serverSuspended(false)

{
    this->ui->setupUi(this);

    // Config file
    IniParser parser("client_documents.conf");

    // Get delimiters
    if (parser.keyExists("commandDelimiter"))
        protocolManager.setCommandDelimiter(parser.value("commandDelimiter"));
    if (parser.keyExists("headerDelimiter"))
        protocolManager.setHeaderDelimiter(parser.value("headerDelimiter"));
    if (parser.keyExists("endDelimiter"))
        protocolManager.setEndDelimiter(parser.value("endDelimiter"));

    // Get other parameters
    if (parser.keyExists("default_ip"))
        this->ui->lineEditIPServer->setText(
                QString::fromStdString(parser.value("default_ip")));
    if (parser.keyExists("default_port"))
        this->ui->spinBoxPortServer->setValue(
                QString::fromStdString(parser.value("default_port")).toInt());
    if(parser.keyExists("default_username"))
        this->ui->lineEditUsername->setText(
                QString::fromStdString(parser.value("default_username")));
    if(parser.keyExists("default_password"))
        this->ui->lineEditPassword->setText(
                QString::fromStdString(parser.value("default_password")));

    // Generate prime numbers up to 1000000
    primeGenerator.init_fast(1000000);

    this->showStatus();
}

Widget::~Widget(void)
{
    delete this->_threadAdmin;
    this->_threadAdmin = NULL;

    delete this->_clientSocket;
    this->_clientSocket = NULL;

    delete this->ui;
}

void Widget::setWidgetsEnable(bool client_connected)
{
    this->ui->lineEditIPServer->setEnabled(!client_connected);
    this->ui->spinBoxPortServer->setEnabled(!client_connected);
    this->ui->lineEditUsername->setEnabled(!client_connected);
    this->ui->lineEditPassword->setEnabled(!client_connected);
    this->ui->pushButtonConnect->setEnabled(!client_connected);
    this->ui->pushButtonDisconnect->setEnabled(client_connected);
    this->ui->pushButtonPlainText->setEnabled(client_connected);
    this->ui->pushButtonCipherText->setEnabled(client_connected);
}

void Widget::showStatus(void)
{
    bool isConnected = this->_clientSocket != NULL &&
                       this->_clientSocket->isValid();

    this->setWidgetsEnable(isConnected);

    // If client is connected to server
    if (isConnected)
    {
        // If server is suspended
        if (this->_serverSuspended)
        {
            this->ui->labelStatus->setStyleSheet("QLabel { color : orange; }");
            this->ui->labelStatus->setText("Connected - Server suspended");

            // Desable request buttons
            this->ui->pushButtonPlainText->setEnabled(false);
            this->ui->pushButtonCipherText->setEnabled(false);
        }
        else
        {
            this->ui->labelStatus->setStyleSheet("QLabel { color : green; }");
            this->ui->labelStatus->setText("Connected");
        }
    }
    else
    {
        this->ui->labelStatus->setStyleSheet("QLabel { color : red; }");
        this->ui->labelStatus->setText("Disconnected");
    }
}

void Widget::displayMessage(const QString &message)
{
    this->ui->plainTextEditConsole->appendPlainText(message);
}

void Widget::threadAdminStarted(void)
{
    // Display message
    this->displayMessage("Thread admin started");
}

void Widget::threadAdminFinished(void)
{
    // Delete thread admin
    delete this->_threadAdmin;
    this->_threadAdmin = NULL;

    // Display message
    this->displayMessage("Thread admin ended");
}

void Widget::serverSuspended(bool suspended)
{
    this->_serverSuspended = suspended;
    this->showStatus();
}

void Widget::on_pushButtonConnect_clicked(void)
{
    // Some checks
    try
    {
        if (this->ui->lineEditIPServer->text().isEmpty())
            throw Exception("Invalid IP address");

        if (this->ui->spinBoxPortServer->value() <= 0)
            throw Exception("Invalid port number");

        if (this->ui->lineEditUsername->text().isEmpty())
            throw Exception("Invalid username");

        if (this->ui->lineEditPassword->text().isEmpty())
            throw Exception("Invalid password");
    }
    catch(Exception const& ex)
    {
        this->displayMessage("Error : " + QString(ex.what()));
        QMessageBox::information(this, "Error", ex.what());
        return;
    }

    try
    {
        // Close connection is already connected
        this->closeConnection();

        // Create client TCP socket
        this->_clientSocket = new TCPSocketClient;

        // Console display
        std::string host = this->ui->lineEditIPServer->text().toStdString();
        int port = this->ui->spinBoxPortServer->value();
        this->displayMessage("Try to connect to "
                             + this->ui->lineEditIPServer->text()
                             + " on port " + QString::number(port));

        // Connect to host
        this->_clientSocket->connectToHost(host, port);
        this->displayMessage("Connected !");

        // Login procedure
        this->loginProcedure();

        // Create and start thread admin
        this->_threadAdmin = new ThreadAdmin;

        connect(this->_threadAdmin, SIGNAL(message(QString)),
                this, SLOT(displayMessage(QString)));
        connect(this->_threadAdmin, SIGNAL(started()),
                this, SLOT(threadAdminStarted()));
        connect(this->_threadAdmin, SIGNAL(finished()),
                this, SLOT(threadAdminFinished()));
        connect(this->_threadAdmin, SIGNAL(serverSuspended(bool)),
                this, SLOT(serverSuspended(bool)));

        this->_threadAdmin->start();

        // Connection succeed, server isn't suspended
        this->_serverSuspended = false;

        // Update status and widgets
        this->showStatus();
    }
    catch(SocketException const& ex)
    {
        this->closeConnection();

        this->displayMessage("Network Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Login failed", ex.what());
    }
    catch(Exception const& ex)
    {
        this->closeConnection();

        this->displayMessage("Login Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Login failed", ex.what());
    }
}

void Widget::on_pushButtonDisconnect_clicked(void)
{
    // Already disconnected
    if (this->_clientSocket == NULL || !this->_clientSocket->isValid())
        return;

    try
    {
        this->displayMessage("Send CLOSE command");

        this->protocolManager.setNewCommand(GDOCP::CLOSE);
        this->_clientSocket->send(this->protocolManager.generateQuery());

        this->closeConnection();
        this->displayMessage("Disconnected !");

        this->showStatus();
    }
    catch(const SocketException& ex)
    {
        QMessageBox::critical(this, "Critical error", ex.what());
    }
}

void Widget::on_pushButtonPlainText_clicked(void)
{
    std::string tmp_str;
    ssize_t ret;

    try
    {
        // Check if the user is connected to the server
        if (this->_clientSocket == NULL || !this->_clientSocket->isValid())
            throw Exception("Disconnected from server", 1);

        QString filename = QInputDialog::getText(
                    this, "Plain text file name", "Enter file name");

        // User canceled
        if(filename.isEmpty())
            return;

        // Create query
        protocolManager.setNewCommand(GDOCP::GETPLAIN);
        protocolManager.setHeaderValue("filename", filename.toStdString());

        tmp_str = this->protocolManager.generateQuery();
        this->displayMessage("Send : " + QString::fromStdString(tmp_str));

        // Send GETPLAIN query
        ret = this->_clientSocket->send(tmp_str);
        if (ret == SOCKET_CLOSED)
            throw Exception("Connection to server closed", 1);

        // Receive GETPLAIN
        ret = this->_clientSocket->recv(tmp_str, this->protocolManager.endDelimiter());
        if (ret == SOCKET_CLOSED)
            throw Exception("Connection to server closed", 1);

        this->displayMessage("Received : " + QString::fromStdString(tmp_str));

        // Create query object
        this->protocolManager.parseQuery(tmp_str);

        // If request failed
        if (this->protocolManager.is(GDOCP::FAIL))
            throw Exception(protocolManager.getHeaderValue("cause"));

        // If received invalid query
        if (!this->protocolManager.is(GDOCP::GETPLAIN))
            throw Exception("Received invalid reply");

        this->displayMessage("Plain text : " + QString::fromStdString(
                              this->protocolManager.getHeaderValue("content")));
    }
    catch(SocketException const& ex)
    {
        this->closeConnection();
        this->showStatus();

        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
    catch(Exception const& ex)
    {
        // Ask to close the connection if the exception code is set to 1
        if (ex.code() > 0)
        {
            this->closeConnection();
            this->showStatus();
        }

        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
}

void Widget::on_pushButtonCipherText_clicked(void)
{
    std::string tmp_str;
    ssize_t ret;

    try
    {
        // Check if the user is connected to the server
        if (this->_clientSocket == NULL || !this->_clientSocket->isValid())
            throw Exception("Disconnected from server", 1);

        QString filename = QInputDialog::getText(
                    this, "Cipher text file name", "Enter file name");

        // User canceled
        if(filename.isEmpty())
            return;

        // Create query
        protocolManager.setNewCommand(GDOCP::GETCIPHER);
        protocolManager.setHeaderValue("filename", filename.toStdString());

        tmp_str = this->protocolManager.generateQuery();
        this->displayMessage("Send : " + QString::fromStdString(tmp_str));

        // Send GETCIPHER request
        ret = this->_clientSocket->send(tmp_str);
        if (ret == SOCKET_CLOSED)
            throw Exception("Connection to server closed", 1);

        // Receive GETCIPHER
        ret = this->_clientSocket->recv(tmp_str, this->protocolManager.endDelimiter());
        if (ret == SOCKET_CLOSED)
            throw Exception("Connection to server closed", 1);

        this->displayMessage("Received : " + QString::fromStdString(tmp_str));

        // Create query object
        this->protocolManager.parseQuery(tmp_str);

        // If request failed
        if (this->protocolManager.is(GDOCP::FAIL))
            throw Exception(this->protocolManager.getHeaderValue("cause"));

        // If received invalid query
        if (!this->protocolManager.is(GDOCP::GETCIPHER))
            throw Exception("Received invalid reply");

        this->displayMessage("Cipher text : " + QString::fromStdString(
                              this->protocolManager.getHeaderValue("content")));
    }
    catch(SocketException const& ex)
    {
        this->closeConnection();
        this->setWidgetsEnable(false);

        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
    catch(Exception const& ex)
    {
        // Ask to close the connection if the exception code is set to 1
        if (ex.code() > 0)
        {
            this->closeConnection();
            this->setWidgetsEnable(false);
        }

        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
}

void Widget::on_pushButtonClear_clicked(void)
{
    this->ui->plainTextEditConsole->clear();
}

void Widget::loginProcedure(void) // Throws SocketException and Exception
{
    std::string tmp_str;
    ssize_t ret;

    // Create LOGIN request with username
    this->protocolManager.setNewCommand(GDOCP::LOGIN);
    this->protocolManager.setHeaderValue(
                "username", this->ui->lineEditUsername->text().toStdString());

    tmp_str = this->protocolManager.generateQuery();
    this->displayMessage("Send : " + QString::fromStdString(tmp_str));

    // Send LOGIN request with username
    ret = this->_clientSocket->send(tmp_str);
    if (ret == SOCKET_CLOSED)
        throw Exception("Connection to server closed");

    // Receive LOGIN ack and nonce
    ret = this->_clientSocket->recv(tmp_str, this->protocolManager.endDelimiter());
    if (ret == SOCKET_CLOSED)
        throw Exception("Connection to server closed");

    this->displayMessage("Received : " + QString::fromStdString(tmp_str));

    // Create query object
    this->protocolManager.parseQuery(tmp_str);

    // If request failed
    if (this->protocolManager.is(GDOCP::FAIL))
        throw Exception(protocolManager.getHeaderValue("cause"));

    // If received invalid query
    if (!this->protocolManager.is(GDOCP::LOGIN))
        throw Exception("Received invalid reply");

    // LOGIN ACK : Get nonce
    unsigned int nonce = std::stoul(protocolManager.getHeaderValue("nonce"));
    this->displayMessage("nonce recieved = " + QString::number(nonce));

    // Generate cnonce (prime number)
    unsigned int cnonce = primeGenerator.get();
    this->displayMessage("cnonce generated = " + QString::number(cnonce));

    // Hash password
    tmp_str = this->ui->lineEditPassword->text().toStdString();
    unsigned int hash_passwd = Hash::hash_str(tmp_str, nonce, cnonce);
    this->displayMessage("Hash password = " + QString::number(hash_passwd));

    // Build query
    protocolManager.setNewCommand(GDOCP::LOGIN);
    protocolManager.setHeaderValue("cnonce", std::to_string(cnonce));
    protocolManager.setHeaderValue("hashpassword", std::to_string(hash_passwd));

    tmp_str = protocolManager.generateQuery();
    displayMessage("Send : " + QString::fromStdString(tmp_str));

    // Send LOGIN request with cnonce and hashpassword
    ret = this->_clientSocket->send(tmp_str);
    if (ret == SOCKET_CLOSED)
        throw Exception("Connection to server closed");

    // Receive LOGIN or FAIL
    ret = this->_clientSocket->recv(tmp_str, this->protocolManager.endDelimiter());
    if (ret == SOCKET_CLOSED)
        throw Exception("Connection to server closed");

    this->displayMessage("Received : " + QString::fromStdString(tmp_str));

    // Create query object
    this->protocolManager.parseQuery(tmp_str);

    // If request failed
    if (this->protocolManager.is(GDOCP::FAIL))
        throw Exception(protocolManager.getHeaderValue("cause"));

    // If received invalid query
    if (!this->protocolManager.is(GDOCP::LOGIN))
        throw Exception("Received invalid reply");
}

void Widget::closeConnection(void)
{
    // Stop thread client
    if (this->_threadAdmin != NULL && this->_threadAdmin->isRunning())
    {
        this->_threadAdmin->requestStop();
        this->_threadAdmin->wait();
    }

    // close client connection
    delete this->_clientSocket;
    this->_clientSocket = NULL;
}
