#include "Exception.hpp"

using namespace std;

Exception::Exception(string const& message)
    : _message(message), _code(-1)
{
}

Exception::Exception(const string &message, int errorCode)
    : _message(message), _code(errorCode)
{
}

Exception::Exception(const Exception& exceptionToCopy)
    : _message(exceptionToCopy._message), _code(exceptionToCopy._code)
{
}

Exception& Exception::operator=(const Exception& exceptionToCopy)
{
    if(this != &exceptionToCopy)
        this->_message = exceptionToCopy._message;

    return *this;
}

Exception::~Exception(void) throw()
{
}

const char* Exception::what() const throw()
{
    return this->_message.c_str();
}

int Exception::code(void) const throw()
{
    return this->_code;
}
