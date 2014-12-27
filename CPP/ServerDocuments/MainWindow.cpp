#include "MainWindow.hpp"
#include "ui_MainWindow.h"

MainWindow::MainWindow(QWidget *parent) :
    QWidget(parent), ui(new Ui::MainWindow), _threadServeur(NULL)
{
    ui->setupUi(this);

    IniParser parser("server_documents.conf");

    // Read port number from config file
    if (parser.keyExists("port"))
        this->ui->spinBoxPort->setValue(std::stoi(parser.value("port")));

    if (parser.keyExists("threads_client"))
        this->ui->spinBoxThreadsPool->setValue(std::stoi(parser.value("threads_client")));

    connect(this->ui->pushButtonClear, SIGNAL(clicked()),
            this->ui->plainTextEditConsole, SLOT(clear()));
}

MainWindow::~MainWindow(void)
{
    if (this->_threadServeur != NULL && this->_threadServeur->isRunning())
        this->stopServer();

    delete this->_threadServeur;
    delete this->ui;
}

void MainWindow::stopServer(void)
{
    this->_threadServeur->requestStop();
    this->_threadServeur->wait();
}

void MainWindow::displayMessage(const QString& msg)
{
    this->ui->plainTextEditConsole->appendPlainText(msg);
}

void MainWindow::updateClientsCount(int clientsCount)
{
    this->ui->spinBoxClientsCount->setValue(clientsCount);
}

void MainWindow::threadServerStarted(void)
{
    // Enable widgets
    this->setWidgetsEnable(true);

    // Display message
    this->displayMessage("Server started on port " +
                         QString::number(this->ui->spinBoxPort->value()));
}

void MainWindow::threadServerFinished(void)
{
    // Desable widgets
    this->setWidgetsEnable(false);

    // Suppression du thread
    delete this->_threadServeur;
    this->_threadServeur = NULL;

    this->displayMessage("Thread server ended");
}

void MainWindow::on_pushButtonStart_clicked(void)
{
    // Server is running
    if (this->_threadServeur != NULL && this->_threadServeur->isRunning())
        this->stopServer();

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
}

void MainWindow::setWidgetsEnable(bool serverRunning)
{
    this->ui->spinBoxPort->setEnabled(!serverRunning);
    this->ui->spinBoxThreadsPool->setEnabled(!serverRunning);
    this->ui->pushButtonStart->setEnabled(!serverRunning);
    this->ui->pushButtonStop->setEnabled(serverRunning);
    this->ui->plainTextEditConsole->setEnabled(serverRunning);
}

void MainWindow::on_pushButtonStop_clicked(void)
{
    this->stopServer();
}
