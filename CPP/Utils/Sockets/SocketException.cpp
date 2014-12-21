//
//  SocketException.cpp
//  Sockets
//
//  Created by Xavier Mawet on 8/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#include "SocketException.hpp"

using namespace std;

SocketException::SocketException(const string& what_arg, int error_code) throw()
    : runtime_error(what_arg), _errno(error_code)
{
    
}

SocketException::SocketException(const SocketException& copy_exception) throw()
    : runtime_error(copy_exception.what()), _errno(copy_exception._errno)
{
    
}

SocketException& SocketException::operator=(const SocketException& copy_exception) throw()
{
    if (this != &copy_exception)
    {
        runtime_error::operator=(copy_exception);
        this->_errno = copy_exception._errno;
    }
    
    return *this;
}

SocketException::~SocketException(void)
{
    
}

int SocketException::errorCode(void) const throw()
{
    return this->_errno;
}
