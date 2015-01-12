#ifndef __THREADADMIN_HPP__
#define __THREADADMIN_HPP__

#include <QThread>
#include <QMutex>
#include <QMutexLocker>

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"
#include "../Utils/Sockets/TCPSocketServer.hpp"

// Protocol
#include "../Utils/AGDOCP/AGDOCProtocol.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

#define DEFAULT_ADMIN_PORT 8001

class ThreadAdmin : public QThread
{
    Q_OBJECT

    public:

        explicit ThreadAdmin(QObject* parent = NULL);
        virtual ~ThreadAdmin(void);

    public slots:

        void requestStop(void);

    signals:

        void message(QString const& message);
        void serverSuspended(bool suspended);
        void serverShutdown(int delay);
        void serverShutdownNow(void);

    protected:

        void run(void);

    private:

        bool stopRequested(void);

    private:

        int _port;
        TCPSocketServer* _serverSocket;
        TCPSocketClient* _clientSocket;

        AGDOCProtocol _request;

        QMutex _mutex;
        bool _stopRequested; // Protected by _mutex
};

#endif /* __THREADADMIN_HPP__ */
