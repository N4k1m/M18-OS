//
//  TCPSocketClient.h
//  Sockets
//
//  Created by Xavier Mawet on 9/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#ifndef __Sockets__TCPSocketClient__
#define __Sockets__TCPSocketClient__

#include "Socket.hpp"

#define MAX_RECV 500

class TCPSocketClient : public Socket
{
    friend class TCPSocketServer;
    
    public:
    
        explicit TCPSocketClient(void);
        explicit TCPSocketClient(const TCPSocketClient& copy_tcpsocketclient);
        TCPSocketClient& operator=(const TCPSocketClient& copy_tcpsocketclient);
    
        bool connectToHost(const std::string& ip, const int port);
        bool connectToHostname(const std::string& hostname, const int port);
    
        ssize_t send(const std::string& message) const;
        ssize_t recv(std::string& message) const;
        ssize_t recv(std::string& message, const std::string& endDelimiter) const;
    
        // Send unique data
        template <class T>
        ssize_t send(const T *data) const
        {
            return this->send<T>(data, 1);
        }
    
        // send data vector
        template <class T>
        ssize_t send(const void* data, size_t length) const
        {
            std::cout << "send ..." << std::endl;
            ssize_t total = 0;
            while (total < (ssize_t) (sizeof (T) * length))
            {
                ssize_t ret = this->send((const char*) data + total,
                                         length * sizeof(T) - total, 0);
            
                if (ret == -1)
                    throw SocketException("Incomplete data stream sent", SOCKET_ERRNO);
            
                total += ret;
            }
        
            return total;
        }
    
        // Reçoit une donnée
        template <class T>
        ssize_t recv(T *data) const
        {
            return this->recv<T>(data, 1);
        }
    
        // Reçoit un vecteur de données
        template <class T>
        ssize_t recv(void *data, size_t length) const
        {
            ssize_t total = 0;
            while (total < (ssize_t) (sizeof (T) * length))
            {
                ssize_t ret = this->recv((char *) data + total,
                                        sizeof (T) * length - total, 0);
            
                if (ret == -1)
                    throw SocketException("Incomplete data stream received", SOCKET_ERRNO);
            
                total += ret;
            }
        
            return total;
        }
    
        virtual ~TCPSocketClient(void);
    
    protected:
    
        explicit TCPSocketClient(SOCKET sock);
    
        bool gethostbyname(const std::string& hostname);
        bool connect(const int port, const std::string& host = "");
    
        ssize_t send(const void *buffer, size_t length, int flags) const;
        ssize_t recv(void *buffer, size_t length, int flags) const;
    
        bool _connected;
};

#endif /* defined(__Sockets__TCPSocketClient__) */
