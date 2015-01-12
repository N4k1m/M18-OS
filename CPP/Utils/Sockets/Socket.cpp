//
//  Socket.cpp
//  Sockets
//
//  Created by Xavier Mawet on 8/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#include "Socket.hpp"

Socket::Socket(socket_type sock_type)
    : _sock(INVALID_SOCKET)
{
    // Create socket file descriptor
    this->_sock = ::socket(AF_INET, static_cast<int>(sock_type), 0);
    if (!this->isValid())
        throw SocketException("Unable to create socket", SOCKET_ERRNO);
    
    /* Specifies that the rules used in validating addresses supplied to bind()
     * should allow reuse of local addresses
     */
    int on = 1;
    if (::setsockopt(this->_sock, SOL_SOCKET, SO_REUSEADDR,(const char*)&on,
                     sizeof(on)) == SOCKET_ERROR)
        throw SocketException("Unable to reuse address", SOCKET_ERRNO);
    
    // Initialize struct SOCKADDR_IN
    memset(&this->_addr, 0, sizeof(this->_addr));
}

Socket::Socket(const Socket& copy_socket)
    : _sock(copy_socket._sock)
{
    memcpy((void*)&this->_addr, (void*)&copy_socket._addr, sizeof(SOCKADDR_IN));
}

Socket& Socket::operator=(const Socket& copy_socket)
{
    if (this != &copy_socket)
    {
        // Close current connection if needed
        if (this->isValid())
        {
            this->shutdown(SHUT_RDWR);
            this->close();
        }
        
        this->_sock = copy_socket._sock;
        memcpy((void*)&this->_addr, (void*)&copy_socket._addr, sizeof(SOCKADDR_IN));
    }
    
    return *this;
}

SOCKET Socket::socketDescriptor(void) const
{
    return this->_sock;
}

bool Socket::isValid(void) const
{
    return this->_sock != INVALID_SOCKET;
}

bool Socket::setBlockingEnable(bool enable)
{
    if (!this->isValid())
        return false;
 
/* Detect operating system */
#if defined(WIN32) || defined(_WIN32)
    
    unsigned long mode = enable ? 0 : 1;
    return (ioctlsocket(this->_sock, FIONBIO, &mode) == 0) ? true : false;
    
#elif defined(__unix__) || defined (__gnu_linux__) || \
(defined(__APPLE__) && defined(__MACH__))
    
    int opts = ::fcntl(this->_sock, F_GETFL, 0);
    
    if (opts < 0)
        return false;
    
    opts = enable ? (opts & ~O_NONBLOCK) : (opts | O_NONBLOCK);
    
    return (fcntl(this->_sock, F_SETFL, opts) == 0) ? true : false;
    
#else
    
    return false;

#endif /* Detect operating system */
}

std::string Socket::getIPv4(void) const
{
    return inet_ntoa(this->_addr.sin_addr);
}

bool Socket::shutdown(int how)
{
    // More control over how the socket closes
    return ::shutdown(this->_sock, how) != SOCKET_ERROR;
    
    // WARNING : It doesn't actually close the file descriptor — it just changes its usability
}

bool Socket::close(void)
{
    if (closesocket(this->_sock) == SOCKET_ERROR)
        return false;
    
    this->_sock = INVALID_SOCKET;
    return true;
}

Socket::~Socket(void)
{
    if (this->isValid())
    {
        this->shutdown(SHUT_RDWR);
        this->close();
    }
}

Socket::Socket(SOCKET sock)
    : _sock(sock)
{
    // Initialize struct SOCKADDR_IN
    memset(&this->_addr, 0, sizeof(this->_addr));
}
