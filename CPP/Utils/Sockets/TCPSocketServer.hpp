//
//  TCPSocketServer.h
//  Sockets
//
//  Created by Xavier Mawet on 9/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#ifndef __Sockets__TCPSocketServer__
#define __Sockets__TCPSocketServer__

#include "SocketServer.hpp"
#include "TCPSocketClient.hpp"

#define DEFAULT_MAX_CON  5

class TCPSocketServer : public SocketServer
{
    public:
    
        explicit TCPSocketServer(int port, int max_pending_connections = DEFAULT_MAX_CON);
        explicit TCPSocketServer(const TCPSocketServer& copy_tcpsocketserver);
        TCPSocketServer& operator=(const TCPSocketServer& copy_tcpsocketserver);
    
        TCPSocketClient* nextPendingConnection(void) const;
    
        int maxPendingConnections(void) const;
        bool setMaxPendingConnections(int max_pending_connections);
    
        virtual ~TCPSocketServer(void);
    
    protected:
    
        bool listen(const int backlog) const;
        bool accept(TCPSocketClient& new_client) const;
    
        int _maxPendingConnections;
};

#endif /* defined(__Sockets__TCPSocketServer__) */
