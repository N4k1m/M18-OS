#ifndef __THREADADMIN_HPP__
#define __THREADADMIN_HPP__

#include <QThread>
#include <QMutex>
#include <QMutexLocker>

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"
#include "../Utils/Sockets/TCPSocketServer.hpp"

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

    protected:

        void run(void);

    private:

        bool stopRequested(void);

    private:

        int _port;
        TCPSocketServer* _serverSocket;
        TCPSocketClient* _clientSocket;

        bool _clientLoggedIn;
        bool _clientStop;

        QMutex _mutex;
        bool _stopRequested; // Protected by _mutex
};

#endif /* __THREADADMIN_HPP__ */
