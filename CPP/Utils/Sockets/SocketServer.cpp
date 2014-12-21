//
//  SocketServer.cpp
//  Sockets
//
//  Created by Xavier Mawet on 9/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#include "SocketServer.hpp"

SocketServer::SocketServer(socket_type sock_type, int port)
    : Socket(sock_type)
{
    if (!this->bind(port))
        throw SocketException("Could not bind to port", SOCKET_ERRNO);
}

SocketServer::SocketServer(const SocketServer& copy_socketserver)
    : Socket(copy_socketserver)
{
    
}

SocketServer& SocketServer::operator=(const SocketServer &copy_socketserver)
{
    if (this != &copy_socketserver)
    {
        Socket::operator=(copy_socketserver);
    }
    
    return *this;
}

SocketServer::~SocketServer(void)
{
    
}

bool SocketServer::bind(const int port)
{
    if (!this->isValid())
        return false;
    
    // Accept all IPs
    this->_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    this->_addr.sin_family      = AF_INET;
    this->_addr.sin_port        = htons(port);

    return ::bind(this->_sock, (SOCKADDR*)&this->_addr, sizeof this->_addr) != SOCKET_ERROR;
}
