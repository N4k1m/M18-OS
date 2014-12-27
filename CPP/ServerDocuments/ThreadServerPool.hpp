#ifndef __THREADSERVERPOOL_HPP__
#define __THREADSERVERPOOL_HPP__

#include <QThread>
#include <QWaitCondition>
#include <QMutex>
#include <QMutexLocker>
#include <QList>

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"
#include "../Utils/Sockets/TCPSocketServer.hpp"

// Protocol
#include "../Utils/GDOCP/GDOCP.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

// Threads
#include "ThreadClient.hpp"

// The wait condition that represents the state of a new client available
extern QWaitCondition clientsIsNotEmpty;
// The mutex that synchronizes the usage of the wait conditions
extern QMutex conditionMutex;
extern QList<TCPSocketClient*> clients;
extern int clientAvailable;

class ThreadServerPool : public QThread
{
        Q_OBJECT

    public:

        explicit ThreadServerPool(int port,
                                  int threadsClientCount,
                                  QObject* parent = NULL);

        virtual ~ThreadServerPool(void);

    public slots:

        void requestStop(void);

    private slots:

        void threadClientStarted(void);
        void threadClientFinished(void);
        void threadClientClientAccepted(void);
        void threadClientClientDisconnected(void);

    signals:

        void message(const QString& message);
        void clientsCountChanged(int clientCount);

    protected:

        void run(void);

    private:

        void sendFAILMessage(const QString& cause);
        bool stopRequested(void);

    private:

        int _port;
        int _threadsClientCount;
        QList<ThreadClient*> _threadsClient;

        TCPSocketServer* _socketServer;
        TCPSocketClient* _socketClient;

        GDOCP _protocolManager;

        QMutex _mutex;
        bool   _stopRequested; // Protected by _mutex
};

#endif /* __THREADSERVERPOOL_HPP__ */
