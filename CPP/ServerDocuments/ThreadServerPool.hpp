#ifndef __THREADSERVERPOOL_HPP__
#define __THREADSERVERPOOL_HPP__

#include <QThread>
#include <QString>

// Networking
#include "../Utils/Sockets/TCPSocketClient.hpp"
#include "../Utils/Sockets/TCPSocketServer.hpp"

// Protocol
#include "../Utils/GDOCP/GDOCP.hpp"

// Parser
#include "../Utils/Parser/IniParser.hpp"

class ThreadServerPool : public QThread
{
        Q_OBJECT

    public:

        explicit ThreadServerPool(int port,
                                  int threadsClient,
                                  QObject* parent = NULL);

        virtual ~ThreadServerPool(void);

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
        int _threadsClient;

        TCPSocketServer* _socketServer;
        TCPSocketClient* _socketClient;

        GDOCP _protocolManager;

        QMutex _mutex;
        bool   _stopRequested; // Protected by _mutex
};

#endif /* __THREADSERVERPOOL_HPP__ */
