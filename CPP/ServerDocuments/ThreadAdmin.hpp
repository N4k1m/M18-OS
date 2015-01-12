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
#include "../Utils/AGDOCP/AGDOCProtocol.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

// Timer
#include "../Utils/Timers/QTimerOneShot.hpp"

#define DEFAULT_PORT_ADMIN_CLIENT 8001

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

    public slots:

        void manageSHUTDOWN_NOW(void);

    signals:

        void message(const QString& message);
        void administratorAccepted(const QString& adminLogin);
        void administratorDisconnected(void);
        void suspendServer(bool suspend);
        void shutdownServer(void);

    protected:

        void run(void);

    private:

        void manageLOGINA(void);
        void manageLCLIENTS(void);
        void managePAUSE(void);
        void manageRESUME(void);
        void manageSTOP(void);
        void manageQUIT(void);
        void manageFAIL(void);

        void sendFAILMessage(QString const& cause);
        void informAllClients(const AGDOCProtocol& request);

        bool stopRequested(void);

    private:

        int _port;
        int _portAdminClient;
        TCPSocketServer* _serverSocket;
        TCPSocketClient* _clientSocket;

        QTimerOneShot* _timer;

        DOCSAP _protocolManager;

        bool _clientLoggedIn;

        QMutex _mutex;
        bool _stopRequested; // Protected by _mutex
};

#endif /* __THREADADMIN_HPP__ */
