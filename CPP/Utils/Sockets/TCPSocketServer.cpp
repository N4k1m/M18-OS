//
//  TCPSocketServer.cpp
//  Sockets
//
//  Created by Xavier Mawet on 9/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#include "TCPSocketServer.hpp"

TCPSocketServer::TCPSocketServer(int port, int max_pending_connections)
    : SocketServer(socket_type::TCP, port),
     _maxPendingConnections(max_pending_connections)
{
    // Listen
    this->setMaxPendingConnections(max_pending_connections);
}

TCPSocketServer::TCPSocketServer(const TCPSocketServer& copy_tcpsocketserver)
    : SocketServer(copy_tcpsocketserver),
      _maxPendingConnections(copy_tcpsocketserver._maxPendingConnections)
{

}

TCPSocketServer& TCPSocketServer::operator=(const TCPSocketServer& copy_tcpsocketserver)
{
    if (this != &copy_tcpsocketserver)
    {
        SocketServer::operator=(copy_tcpsocketserver);
        this->_maxPendingConnections = copy_tcpsocketserver._maxPendingConnections;
    }
    
    return *this;
}

TCPSocketClient* TCPSocketServer::nextPendingConnection(void) const
{
    TCPSocketClient* new_tcpclient = new TCPSocketClient(INVALID_SOCKET);
    
    if (!this->accept(*new_tcpclient))
        throw SocketException("Could not accept new client", SOCKET_ERRNO);
    
    new_tcpclient->_connected = true;
    
    return new_tcpclient;
}

int TCPSocketServer::maxPendingConnections(void) const
{
    return this->_maxPendingConnections;
}

bool TCPSocketServer::setMaxPendingConnections(int max_pending_connections)
{
    if (max_pending_connections < 0)
        throw SocketException("Number of pending connections must be greater than zero");
    
    if (!this->listen(max_pending_connections))
        throw SocketException("Could not set socket in passive mode", SOCKET_ERRNO);
    
    this->_maxPendingConnections = max_pending_connections;
    
    return true;
}

TCPSocketServer::~TCPSocketServer(void)
{

}

bool TCPSocketServer::listen(const int backlog) const
{
    if (!this->isValid())
        return false;
    
    return ::listen(this->_sock, backlog) != SOCKET_ERROR;
}

bool TCPSocketServer::accept(TCPSocketClient& new_client) const
{
    socklen_t addr_length = (socklen_t) sizeof new_client._addr;
    
    new_client._sock = ::accept(this->_sock, (SOCKADDR*)&new_client._addr, &addr_length);
    
    return new_client.isValid();
}
