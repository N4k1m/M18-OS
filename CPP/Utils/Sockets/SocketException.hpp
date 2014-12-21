//
//  SocketException.h
//  Sockets
//
//  Created by Xavier Mawet on 8/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#ifndef __Sockets__SocketException__
#define __Sockets__SocketException__

#include <iostream>
#include <stdexcept>
#include <string>

class SocketException : public std::runtime_error
{
    public:
    
        SocketException(const std::string& what_arg, int error_code = -1) throw();
        SocketException(const SocketException& copy_exception) throw();
        SocketException& operator=(const SocketException& copy_exception) throw();
    
        virtual ~SocketException(void);
    
        int errorCode(void) const throw();
    
    protected:
    
        int _errno;
};

#endif /* defined(__Sockets__SocketException__) */
