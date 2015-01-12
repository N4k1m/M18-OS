#ifndef __WIDGET_HPP__
#define __WIDGET_HPP__

#include <QtWidgets>
#include <QString>

#include "ThreadAdmin.hpp"

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"

// Protocol
#include "../Utils/GDOCP/GDOCP.hpp"

// Hash
#include "../Utils/Hash/Hash.hpp"
#include "../Utils/Hash/RandomPrimeGenerator.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

// Exceptions
#include "../Utils/Exceptions/Exception.hpp"

namespace Ui {
    class Widget;
}

class Widget : public QWidget
{
        Q_OBJECT

    public:

        explicit Widget(QWidget *parent = NULL);
        virtual ~Widget(void);

    private slots:

        void setWidgetsEnable(bool client_connected);
        void showStatus(void);
        void displayMessage(const QString& message);

        // Thread admin signals management
        void threadAdminStarted(void);
        void threadAdminFinished(void);
        void serverSuspended(bool suspended);
        void serverWillShutdwon(int delay);
        void serverShutdown(void);

        // Auto-connect
        void on_pushButtonConnect_clicked(void);
        void on_pushButtonDisconnect_clicked(void);
        void on_pushButtonPlainText_clicked(void);
        void on_pushButtonCipherText_clicked(void);
        void on_pushButtonClear_clicked(void);

    protected:

        void loginProcedure(void); // Throws SocketException and Exception
        void closeConnection(void);

    protected:

        Ui::Widget* ui;
        ThreadAdmin* _threadAdmin;

        TCPSocketClient* _clientSocket;
        GDOCP protocolManager;
        RandomPrimeGenerator primeGenerator;

        bool _serverSuspended;
};

#endif /* __WIDGET_HPP__ */
