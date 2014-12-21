#include "Widget.hpp"
#include "ui_Widget.h"

Widget::Widget(QWidget *parent) :
    QWidget(parent), ui(new Ui::Widget), client_sock(NULL),
    protocolManager("#", "=", "!"), primeGenerator()

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
}

Widget::~Widget(void)
{
    delete this->client_sock;
    delete this->ui;
}

void Widget::on_pushButtonConnect_clicked()
{
    if (this->ui->lineEditIPServer->text().isEmpty())
    {
        QMessageBox::warning(this, "Error", "Invalid IP address");
        return;
    }

    if (this->ui->spinBoxPortServer->value() <= 0)
    {
        QMessageBox::warning(this, "Error", "Invalid port");
        return;
    }

    if (this->ui->lineEditUsername->text().isEmpty())
    {
        QMessageBox::warning(this, "Error", "Invalid username");
        return;
    }

    if (this->ui->lineEditPassword->text().isEmpty())
    {
        QMessageBox::warning(this, "Error", "Invalid password");
        return;
    }

    try
    {
        // Close connection is already connected
        this->disconnectFromServer();

        // Create client TCP socket
        this->client_sock = new TCPSocketClient;

        // Console display
        std::string host = this->ui->lineEditIPServer->text().toStdString();
        int port = this->ui->spinBoxPortServer->value();
        displayMessage("Try to connect to " + this->ui->lineEditIPServer->text()
                       + " on port " + QString::number(port));

        // Connect to host
        this->client_sock->connectToHost(host, port);
        displayMessage("Connected !");

        // Login procedure
        if (this->login())
            this->setWidgetsEnable(true);
        else
        {
            this->disconnectFromServer();
            QMessageBox::critical(this, "Login failed",
                                  QString::fromStdString(protocolManager.getHeaderValue("cause")));
        }
    }
    catch(SocketException const& ex)
    {
        delete this->client_sock;
        client_sock = NULL;

        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
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

void Widget::displayMessage(const QString &message)
{
    this->ui->plainTextEdit->appendPlainText(message);
}

void Widget::on_pushButtonDisconnect_clicked()
{
    this->disconnectFromServer();
}

void Widget::on_pushButtonPlainText_clicked()
{
    try
    {
        // Check if the user is connected to the server
        if (this->client_sock == NULL || !this->client_sock->isValid())
            throw Exception("Disconnected from server");

        QString filename = QInputDialog::getText(
                    this, "Plain text file name", "Enter file name");

        // User canceled
        if(filename.isEmpty())
            return;

        // Create query
        protocolManager.setCommand(GDOCP::GETPLAIN);
        protocolManager.clearHeaders();
        protocolManager.setHeaderValue("filename", filename.toStdString());

        // Send query
        this->client_sock->send(protocolManager.generateQuery());

        // Receive replay
        std::string reply;
        client_sock->recv(reply, protocolManager.endDelimiter());

        // Create query object
        protocolManager.parseQuery(reply);

        // if request failed
        if (protocolManager.command() == GDOCP::FAIL)
            throw Exception(protocolManager.getHeaderValue("cause"));
        else if (protocolManager.command() == GDOCP::GETPLAIN)
            displayMessage("Plain Text : " + QString::fromStdString(
                               protocolManager.getHeaderValue("content")));
    }
    catch(SocketException const& ex)
    {
        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
    catch(Exception const& ex)
    {
        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
}

void Widget::on_pushButtonCipherText_clicked()
{
    try
    {
        // Check if the user is connected to the server
        if (this->client_sock == NULL || !this->client_sock->isValid())
            throw Exception("Disconnected from server");

        QString filename = QInputDialog::getText(
                    this, "Cipher text file name", "Enter file name");

        // User canceled
        if(filename.isEmpty())
            return;

        // Create query
        protocolManager.setCommand(GDOCP::GETCIPHER);
        protocolManager.clearHeaders();
        protocolManager.setHeaderValue("filename", filename.toStdString());

        // Send query
        this->client_sock->send(protocolManager.generateQuery());

        // Receive replay
        std::string reply;
        client_sock->recv(reply, protocolManager.endDelimiter());

        // Create query object
        protocolManager.parseQuery(reply);

        // if request failed
        if (protocolManager.command() == GDOCP::FAIL)
            throw Exception(protocolManager.getHeaderValue("cause"));
        else if (protocolManager.command() == GDOCP::GETCIPHER)
            displayMessage("Cipher Text : " + QString::fromStdString(
                               protocolManager.getHeaderValue("content")));
    }
    catch(SocketException const& ex)
    {
        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
    catch(Exception const& ex)
    {
        displayMessage("Error : " + QString(ex.what()));
        QMessageBox::critical(this, "Fatal Error", ex.what());
    }
}

void Widget::on_pushButtonClear_clicked()
{
    this->ui->plainTextEdit->clear();
}

void Widget::disconnectFromServer(void)
{
    // Already disconnected
    if (this->client_sock == NULL || !this->client_sock->isValid())
        return;

    try
    {
        displayMessage("Send CLOSE command");

        this->protocolManager.setCommand(GDOCP::CLOSE);
        client_sock->send(protocolManager.generateQuery());

        delete this->client_sock;
        this->client_sock = NULL;

        displayMessage("Disconnected !");

        this->setWidgetsEnable(false);
    }
    catch(const SocketException& ex)
    {
        QMessageBox::critical(this, "Critical error", ex.what());
    }
}

bool Widget::login(void)
{
    std::string tmp_str;

    // Create LOGIN request with username
    this->protocolManager.setCommand(GDOCP::LOGIN);
    this->protocolManager.clearHeaders();
    this->protocolManager.setHeaderValue("username", this->ui->lineEditUsername->text().toStdString());

    tmp_str = protocolManager.generateQuery();
    displayMessage("Send query : " + QString::fromStdString(tmp_str));

    // Send LOGIN request with username
    client_sock->send(tmp_str);

    // Receive LOGIN ack and nonce
    client_sock->recv(tmp_str, protocolManager.endDelimiter());
    displayMessage("LOGIN ACK received : " + QString::fromStdString(tmp_str));

    // Create query object
    protocolManager.parseQuery(tmp_str);

    // if request failed
    if (protocolManager.command() == GDOCP::FAIL)
    {
        displayMessage(QString::fromStdString(
                           protocolManager.getHeaderValue("cause")));
        return false;
    }

    if (protocolManager.command() != GDOCP::LOGIN)
        return false;

    // LOGIN ACK : Get nonce
    unsigned int nonce;
    tmp_str = protocolManager.getHeaderValue("nonce");
    std::istringstream(tmp_str) >> nonce;
    displayMessage("nonce recieved = " + QString::number(nonce));

    // Generate cnonce (prime number)
    unsigned int cnonce = primeGenerator.get();
    displayMessage("cnonce generated = " + QString::number(cnonce));

    // Hash password
    tmp_str = this->ui->lineEditPassword->text().toStdString();
    unsigned int hash_passwd = Hash::hash_str(tmp_str, nonce, cnonce);
    displayMessage("Hash password = " + QString::number(hash_passwd));

    // Build query
    protocolManager.setCommand(GDOCP::LOGIN);
    protocolManager.clearHeaders();
    tmp_str = static_cast<std::ostringstream*>(
                &(std::ostringstream() << cnonce))->str();
    protocolManager.setHeaderValue("cnonce", tmp_str);
    tmp_str = static_cast<std::ostringstream*>(
                &(std::ostringstream() << hash_passwd))->str();
    protocolManager.setHeaderValue("hashpassword", tmp_str);

    tmp_str = protocolManager.generateQuery();
    displayMessage("Send query : " + QString::fromStdString(tmp_str));

    // Send LOGIN request with cnonce and hashpassword
    client_sock->send(tmp_str);

    // Receive LOGIN or FAIL
    client_sock->recv(tmp_str, protocolManager.endDelimiter());
    displayMessage("LOGIN or FAIL received : " + QString::fromStdString(tmp_str));

    // Create query object
    protocolManager.parseQuery(tmp_str);

    // if request failed
    if (protocolManager.command() == GDOCP::FAIL)
    {
        displayMessage(QString::fromStdString(
                           protocolManager.getHeaderValue("cause")));
        return false;
    }
    else if (protocolManager.command() == GDOCP::LOGIN)
        return true;

    return false;
}
