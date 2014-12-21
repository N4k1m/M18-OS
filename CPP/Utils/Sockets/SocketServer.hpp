//
//  SocketServer.h
//  Sockets
//
//  Created by Xavier Mawet on 9/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#ifndef __Sockets__SocketServer__
#define __Sockets__SocketServer__

#include "Socket.hpp"

class SocketServer : public Socket
{
    public:
    
        explicit SocketServer(socket_type sock_type, int port);
        explicit SocketServer(const SocketServer& copy_socketserver);
        SocketServer& operator=(const SocketServer& copy_socketserver);
    
        virtual ~SocketServer(void);
    
    protected:
    
        bool bind(const int port);
};

#endif /* defined(__Sockets__SocketServer__) */
