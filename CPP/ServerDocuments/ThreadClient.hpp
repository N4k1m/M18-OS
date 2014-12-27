#ifndef __THREADCLIENT_HPP__
#define __THREADCLIENT_HPP__

#include <QThread>
#include <QWaitCondition>
#include <QList>

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"

// Protocol
#include "../Utils/GDOCP/GDOCP.hpp"

// Hash
#include "../Utils/Hash/RandomPrimeGenerator.hpp"
#include "../Utils/Hash/Hash.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

// The wait condition that represents the state of a new client available
extern QWaitCondition clientsIsNotEmpty;
// The mutex that synchronizes the usage of the wait conditions
extern QMutex conditionMutex;
extern QList<TCPSocketClient*> clients;
extern int clientAvailable;

class ThreadClient : public QThread
{
        Q_OBJECT

    public:

        explicit ThreadClient(GDOCP const& protocolManager);
        virtual ~ThreadClient(void);

    public slots:

        void requestStop(void);

    signals:

        void message(const QString& message);
        void clientAccepted(void);
        void clientDisconnected(void);

    protected:

        void run(void);

    private:

        void manageLOGIN(void);
        void manageGETPLAIN(void);
        void manageGETCIPHER(void);
        void sendFAILMessage(const std::string& cause);

        bool stopRequested(void);

    private:

        GDOCP _protocolManager;
        RandomPrimeGenerator _primeGenerator;

        TCPSocketClient* _socketClient;
        bool _clientConnected;

        QMutex _mutex;
        bool   _stopRequested; // Protected by _mutex
};

#endif /* __THREADCLIENT_HPP__ */
