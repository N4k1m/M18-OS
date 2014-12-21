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
    {
        this->_threadServeur->quit();
    }

    delete this->_threadServeur;
    delete ui;
}

void MainWindow::displayMessage(const QString& msg)
{
    this->ui->plainTextEditConsole->appendPlainText(msg);
}

void MainWindow::on_pushButtonStart_clicked()
{
    // Server is running
    if (this->_threadServeur != NULL)
    {
        this->ui->plainTextEditConsole->appendPlainText("Server is already running\n");
        return;
    }

    this->_threadServeur = new ThreadServer(this->ui->spinBoxPort->value(), 0);

    connect(this->_threadServeur, SIGNAL(message(QString)), this, SLOT(displayMessage(QString)));
    connect(this->_threadServeur, SIGNAL(serverRunning(bool)), this, SLOT(setWidgetsEnable(bool)));

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
    // TODO ... fermer le serveur ...

    this->setWidgetsEnable(false);
}
