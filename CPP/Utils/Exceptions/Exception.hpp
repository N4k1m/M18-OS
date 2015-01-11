#ifndef __EXCEPTION_HPP__
#define __EXCEPTION_HPP__

#include <exception>
#include <string>

class Exception : public std::exception
{
    public:

        Exception(std::string const& message);
        Exception(std::string const& message, int errorCode);
        Exception(const Exception& exceptionToCopy);
        virtual Exception& operator=(const Exception& exceptionToCopy);
        virtual ~Exception(void) throw();

        virtual const char* what(void) const throw();
        virtual int code(void) const throw();

    protected:

        std::string _message;
        int _code;
};

#endif /* __EXCEPTION_HPP__ */
