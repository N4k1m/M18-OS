//
//  TCPSocketClient.cpp
//  Sockets
//
//  Created by Xavier Mawet on 9/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#include "TCPSocketClient.hpp"

TCPSocketClient::TCPSocketClient(void)
    : Socket(socket_type::TCP), _connected(false)
{
    
}

TCPSocketClient::TCPSocketClient(const TCPSocketClient& copy_tcpsocketclient)
    : Socket(copy_tcpsocketclient), _connected(copy_tcpsocketclient._connected)
{
    
}

TCPSocketClient& TCPSocketClient::operator=(const TCPSocketClient& copy_tcpsocketclient)
{
    if (this != &copy_tcpsocketclient)
    {
        Socket::operator=(copy_tcpsocketclient);
        this->_connected = copy_tcpsocketclient._connected;
    }
    
    return *this;
}

bool TCPSocketClient::connectToHost(const std::string& ip, const int port)
{
    this->_connected = this->connect(port, ip);
    
    if (!this->_connected)
        throw SocketException("Unable to connect to the host", SOCKET_ERRNO);
    
    return true;
}

bool TCPSocketClient::connectToHostname(const std::string& hostname, const int port)
{
    this->_connected = false;
    
    if (!this->gethostbyname(hostname))
        throw SocketException("Unable to identify host", SOCKET_ERRNO);
    
    if (!this->connect(port))
        throw SocketException("Unable to connect to the host", SOCKET_ERRNO);
    
    this->_connected = true;
    
    return true;
}

ssize_t TCPSocketClient::send(const std::string& message) const
{
    return this->send(message.c_str(), message.size(), 0);
}

ssize_t TCPSocketClient::recv(std::string& message) const
{
    message.clear();
    
    char buf[MAX_RECV + 1];
    memset(buf, 0, MAX_RECV + 1);
    
    ssize_t status = this->recv(buf, MAX_RECV, 0);
    
    if (status > 0)
    {
        buf[status] = '\0';
        message = buf;
    }
    
    return status;
}

ssize_t TCPSocketClient::recv(std::string& message, const std::string& endDelimiter) const
{
    char buffer[MAX_RECV];
    bool endingFound(false);

    // Clear previous message content
    message.clear();
    ssize_t bytes_received = 0;

    while(!endingFound)
    {
        // Clear previous message received
        memset(buffer, 0, MAX_RECV);

        bytes_received += this->recv(buffer, MAX_RECV, 0);
        message.append(buffer);

        // Check if all the message has been received
        endingFound = (message.length() > endDelimiter.length()) &&
                       (message.compare(message.length() - endDelimiter.length(),
                                     endDelimiter.length(), endDelimiter) == 0);
    }

    return bytes_received;
}

TCPSocketClient::~TCPSocketClient(void)
{
    
}

TCPSocketClient::TCPSocketClient(SOCKET sock)
    : Socket(sock), _connected(false)
{
    
}

bool TCPSocketClient::gethostbyname(const std::string& hostname)
{
    struct hostent* hostinfo = NULL;
    
    // WARNING : gethostbyname is obsolete
    hostinfo = ::gethostbyname(hostname.c_str());
    if (hostinfo == NULL)
        return false;
    
    this->_addr.sin_addr = *(IN_ADDR*)hostinfo->h_addr;
    
    return true;
}

bool TCPSocketClient::connect(const int port, const std::string& host)
{
    if (!this->isValid())
        return false;
    
    this->_addr.sin_family = AF_INET;
    this->_addr.sin_port   = htons(port);
    
    if (host != "")
    {
        inet_pton(AF_INET, host.c_str(), &this->_addr.sin_addr);
        
        if (SOCKET_ERRNO == EAFNOSUPPORT)
            return false;
    }
    
    return ::connect(this->_sock, (SOCKADDR*)&this->_addr, sizeof(SOCKADDR))
            != SOCKET_ERROR;
}

ssize_t TCPSocketClient::send(const void* buffer, size_t length, int flags) const
{
    ssize_t ret = ::send(this->_sock, buffer, length, flags);
    if (ret == SOCKET_ERROR)
        throw SocketException("Error sending data", SOCKET_ERRNO);
    return ret;
}

ssize_t TCPSocketClient::recv(void* buffer, size_t length, int flags) const
{
    ssize_t ret = ::recv(this->_sock, buffer, length, flags);
    if (ret == SOCKET_ERROR)
        throw SocketException("Error receiving data", SOCKET_ERRNO);
    return ret;
}
