#ifndef EXCEPTION_HPP
#define EXCEPTION_HPP

#include <exception>
#include <string>

class Exception : public std::exception
{
    public:

        Exception(std::string const& message);
        Exception(const Exception& exceptionToCopy);
        virtual Exception& operator=(const Exception& exceptionToCopy);
        virtual ~Exception() throw();

        virtual const char* what() const throw();

    protected:

        std::string _message;
};

#endif // EXCEPTION_HPP
