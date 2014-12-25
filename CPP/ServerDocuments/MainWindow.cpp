#include "MainWindow.hpp"
#include "ui_MainWindow.h"

MainWindow::MainWindow(QWidget *parent) :
    QWidget(parent), ui(new Ui::MainWindow), _threadServeur(NULL)
{
    ui->setupUi(this);

    IniParser configFileParser("server_documents.conf");

    // Read port number from config file
    if (configFileParser.keyExists("port"))
    {
        std::istringstream iss(configFileParser.value("port"));
        int port;
        iss >> port;
        this->ui->spinBoxPort->setValue(port);
    }

    connect(this->ui->pushButtonClear, SIGNAL(clicked()),
            this->ui->plainTextEditConsole, SLOT(clear()));
}

MainWindow::~MainWindow()
{
    if (this->_threadServeur != NULL && this->_threadServeur->isRunning())
        this->_threadServeur->quit();

    delete this->_threadServeur;
    delete this->ui;
}

void MainWindow::displayMessage(const QString& msg)
{
    this->ui->plainTextEditConsole->appendPlainText(msg);
}

void MainWindow::threadServerStarted()
{
    // Enable widgets
    this->setWidgetsEnable(true);

    // Display message
    this->displayMessage("Server started on port " +
                         QString::number(this->ui->spinBoxPort->value()));
}

void MainWindow::threadServerFinished()
{
    // Desable widgets
    this->setWidgetsEnable(false);

    // Suppression du thread
    delete this->_threadServeur;
    this->_threadServeur = NULL;

    this->displayMessage("Thread server ended");
}

void MainWindow::on_pushButtonStart_clicked()
{
    // Server is running
    if (this->_threadServeur != NULL && this->_threadServeur->isRunning())
    {
        this->_threadServeur->requestStop();
        this->_threadServeur->wait();
    }

    this->_threadServeur = new ThreadServer(this->ui->spinBoxPort->value(), 0);

    connect(this->_threadServeur, SIGNAL(message(QString)),
            this, SLOT(displayMessage(QString)));
    connect(this->_threadServeur, SIGNAL(started()),
            this, SLOT(threadServerStarted()));
    connect(this->_threadServeur, SIGNAL(finished()),
            this, SLOT(threadServerFinished()));

    this->_threadServeur->start();
}

void MainWindow::setWidgetsEnable(bool serverRunning)
{
    this->ui->spinBoxPort->setEnabled(!serverRunning);
    this->ui->pushButtonStart->setEnabled(!serverRunning);
    this->ui->pushButtonStop->setEnabled(serverRunning);
    this->ui->plainTextEditConsole->setEnabled(serverRunning);
}

void MainWindow::on_pushButtonStop_clicked()
{
    this->_threadServeur->requestStop();
    this->_threadServeur->wait();
}
