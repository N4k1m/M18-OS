#ifndef THREADSERVEUR_HPP
#define THREADSERVEUR_HPP

#include <QThread>

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"
#include "../Utils/Sockets/TCPSocketServer.hpp"

// Protocol
#include "../Utils/GDOCP/GDOCP.hpp"

// Hash
#include "../Utils/Hash/RandomPrimeGenerator.hpp"
#include "../Utils/Hash/Hash.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

class ThreadServer : public QThread
{
        Q_OBJECT

    public:

        explicit ThreadServer(int port, QObject* parent = NULL);
        virtual ~ThreadServer(void);

    public slots:

        void requestStop(void);

    signals:

        void message(const QString& msg);

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

        int _port;
        TCPSocketServer* _serverSocket;
        TCPSocketClient* _clientSocket;

        bool _clientConnected;

        QMutex _mutex;
        bool   _stopRequested; // Protected by _mutex
};

#endif /* __THREADSERVEUR_HPP__ */
