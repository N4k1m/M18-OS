#include "MainWindow.hpp"
#include "ui_MainWindow.h"

MainWindow::MainWindow(QWidget *parent) :
    QWidget(parent), ui(new Ui::MainWindow), _threadServeur(NULL),
    _threadAdmin(NULL), _threadServerStarted(false), _threadAdminStarted(false),
    _serverSuspended(false)
{
    ui->setupUi(this);

    IniParser parser("server_documents.conf");

    // Read client port number from config file
    if (parser.keyExists("clients_port"))
        this->ui->spinBoxPort->setValue(std::stoi(parser.value("clients_port")));

    // Read admin port number from config file
    if (parser.keyExists("admin_port"))
        this->ui->spinBoxPortAdmin->setValue(std::stoi(parser.value("admin_port")));

    // Read the number of threads in pool from config file
    if (parser.keyExists("threads_client"))
        this->ui->spinBoxThreadsPool->setValue(std::stoi(parser.value("threads_client")));

    connect(this->ui->pushButtonClear, SIGNAL(clicked()),
            this->ui->plainTextEditConsole, SLOT(clear()));

    this->showStatus();
}

MainWindow::~MainWindow(void)
{
    this->stopServer();

    delete this->_threadServeur;
    delete this->_threadAdmin;

    delete this->ui;
}

void MainWindow::stopServer(void)
{
    if (this->_threadServeur != NULL && this->_threadServeur->isRunning())
    {
        this->_threadServeur->requestStop();
        this->_threadServeur->wait();
    }

    if (this->_threadAdmin != NULL && this->_threadAdmin->isRunning())
    {
        this->_threadAdmin->requestStop();
        this->_threadAdmin->wait();
    }
}

void MainWindow::displayMessage(const QString& msg)
{
    this->ui->plainTextEditConsole->appendPlainText(msg);
}

void MainWindow::updateClientsCount(int clientsCount)
{
    this->ui->labelClientsCount->setText(QString::number(clientsCount));
}

void MainWindow::administratorConnected(const QString& adminLogin)
{
    this->ui->labelAdminConnectedLogin->setStyleSheet("QLabel { color : green; }");
    this->ui->labelAdminConnectedLogin->setText(adminLogin);
}

void MainWindow::administratorDisconnected(void)
{
    this->ui->labelAdminConnectedLogin->setStyleSheet("QLabel { color : red; }");
    this->ui->labelAdminConnectedLogin->setText("No one");
}

void MainWindow::setServerSuspended(bool suspended)
{
    // Change suspended state
    this->_serverSuspended = suspended;

    // Update label and widgets
    this->showStatus();
}

void MainWindow::threadServerStarted(void)
{
    this->_threadServerStarted = true;

    // Update status if both threads have started
    if (this->_threadServerStarted && this->_threadAdminStarted)
        this->showStatus();

    // Display message
    this->displayMessage("Thread server started on port " +
                         QString::number(this->ui->spinBoxPort->value()));
}

void MainWindow::threadServerFinished(void)
{
    this->_threadServerStarted = false;

    // Update status if both threads have started
    if (!this->_threadServerStarted && !this->_threadAdminStarted)
        this->showStatus();

    // Suppression du thread
    delete this->_threadServeur;
    this->_threadServeur = NULL;

    this->displayMessage("Thread server ended");
}

void MainWindow::threadAdminStarted(void)
{
    this->_threadAdminStarted = true;

    // Enable widgets if both threads have started
    if (this->_threadServerStarted && this->_threadAdminStarted)
        this->showStatus();

    // Display message
    this->displayMessage("Thread admin started on port " +
                         QString::number(this->ui->spinBoxPortAdmin->value()));
}

void MainWindow::threadAdminFinished(void)
{
    this->_threadAdminStarted = false;

    // Desable widgets if both threads have finished
    if (!this->_threadServerStarted && !this->_threadAdminStarted)
        this->showStatus();

    // Suppression du thread
    delete this->_threadAdmin;
    this->_threadAdmin = NULL;

    // Update administrator label
    this->administratorDisconnected();

    this->displayMessage("Thread admin ended");
}

void MainWindow::on_pushButtonStart_clicked(void)
{
    // Stop server if running
    this->stopServer();

    // Create and start thread server
    //this->_threadServeur = new ThreadServer(this->ui->spinBoxPort->value(), 0);
    this->_threadServeur = new ThreadServerPool(
                               this->ui->spinBoxPort->value(),
                               this->ui->spinBoxThreadsPool->value(), 0);

    connect(this->_threadServeur, SIGNAL(message(QString)),
            this, SLOT(displayMessage(QString)));
    connect(this->_threadServeur, SIGNAL(clientsCountChanged(int)),
            this, SLOT(updateClientsCount(int)));
    connect(this->_threadServeur, SIGNAL(started()),
            this, SLOT(threadServerStarted()));
    connect(this->_threadServeur, SIGNAL(finished()),
            this, SLOT(threadServerFinished()));

    this->_threadServeur->start();

    // Create and start thread admin
    this->_threadAdmin = new ThreadAdmin(this->ui->spinBoxPortAdmin->value(), 0);

    // Connect thread admin to GUI
    connect(this->_threadAdmin, SIGNAL(message(QString)),
            this, SLOT(displayMessage(QString)));
    connect(this->_threadAdmin, SIGNAL(administratorAccepted(QString)),
            this, SLOT(administratorConnected(QString)));
    connect(this->_threadAdmin, SIGNAL(administratorDisconnected()),
            this, SLOT(administratorDisconnected()));
    connect(this->_threadAdmin, SIGNAL(suspendServer(bool)),
            this, SLOT(setServerSuspended(bool)));
    connect(this->_threadAdmin, SIGNAL(started()),
            this, SLOT(threadAdminStarted()));
    connect(this->_threadAdmin, SIGNAL(finished()),
            this, SLOT(threadAdminFinished()));

    // Connect thread admin to thread server
    connect(this->_threadAdmin, SIGNAL(suspendServer(bool)),
            this->_threadServeur, SLOT(suspendServer(bool)));

    this->_threadAdmin->start();
}

void MainWindow::setWidgetsEnable(bool serverRunning)
{
    this->ui->spinBoxPort->setEnabled(!serverRunning);
    this->ui->spinBoxPortAdmin->setEnabled(!serverRunning);
    this->ui->spinBoxThreadsPool->setEnabled(!serverRunning);
    this->ui->pushButtonStart->setEnabled(!serverRunning);
    this->ui->pushButtonStop->setEnabled(serverRunning);
    this->ui->plainTextEditConsole->setEnabled(serverRunning);
}

void MainWindow::showStatus(void)
{
    bool isRunning = this->_threadAdminStarted && this->_threadServerStarted;

    this->setWidgetsEnable(isRunning);

    if (isRunning)
    {
        if (this->_serverSuspended)
        {
            this->ui->labelStatus->setStyleSheet("QLabel { color : orange; }");
            this->ui->labelStatus->setText("Server suspended");
        }
        else
        {
            this->ui->labelStatus->setStyleSheet("QLabel { color : green; }");
            this->ui->labelStatus->setText("Server is running");
        }
    }
    else
    {
        this->ui->labelStatus->setStyleSheet("QLabel { color : red; }");
        this->ui->labelStatus->setText("Server stopped");
    }
}

void MainWindow::on_pushButtonStop_clicked(void)
{
    this->stopServer();
}
