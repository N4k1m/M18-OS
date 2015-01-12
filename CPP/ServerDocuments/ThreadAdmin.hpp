#ifndef __THREADADMIN_HPP__
#define __THREADADMIN_HPP__

#include <QThread>
#include <QMutex>
#include <QMutexLocker>

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"
#include "../Utils/Sockets/TCPSocketServer.hpp"

// Protocol
#include "../Utils/DOCSAP/DOCSAP.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

// Global declaration
extern QMutex conditionMutex;
extern QList<TCPSocketClient*> clients;

class ThreadAdmin : public QThread
{
    Q_OBJECT

    public:

        explicit ThreadAdmin(int port, QObject* parent = NULL);
        virtual ~ThreadAdmin(void);

    public slots:

        void requestStop(void);

    signals:

        void message(const QString& message);
        void administratorAccepted(const QString& adminLogin);
        void administratorDisconnected(void);

    protected:

        void run(void);

    private:

        void manageLOGINA(void);
        void manageLCLIENTS(void);
        void manageQUIT(void);
        void manageFAIL(void);

        void sendFAILMessage(QString const& cause);

        // TODO à voir si j'utilise
//        void managePAUSE(void);
//        void manageRESUME(void);
//        void manageSTOP(void);
//        void manageACK(void);
//        void manageUNKNOWN(void);

        bool stopRequested(void);

    private:

        int _port;
        TCPSocketServer* _serverSocket;
        TCPSocketClient* _clientSocket;

        DOCSAP _protocolManager;

        bool _clientLoggedIn;

        QMutex _mutex;
        bool _stopRequested; // Protected by _mutex
};

#endif /* __THREADADMIN_HPP__ */
