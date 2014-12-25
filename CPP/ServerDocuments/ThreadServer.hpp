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

        explicit ThreadServer(int port, QObject *parent = 0);
        ~ThreadServer(void);

    public slots:

        void requestStop();

    signals:

        void message(const QString& msg);

    protected:

        void run();

    private:

        void manageLOGIN(void);
        void manageGETPLAIN(void);
        void manageGETCIPHER(void);

        void sendFAILMessage(const std::string& cause);

        bool stopRequested();

    private:

        GDOCP protocolManager;
        RandomPrimeGenerator primeGenerator;

        int _port;
        TCPSocketServer* server_socket;
        TCPSocketClient* client_socket;

        bool client_connected;

        QMutex mutex;
        bool _stopRequested; // Protected by mutex
};

#endif // __THREADSERVEUR_HPP__
