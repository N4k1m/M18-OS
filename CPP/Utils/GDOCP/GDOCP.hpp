#ifndef __GDOCP_HPP__
#define __GDOCP_HPP__

#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <sstream>
#include <algorithm>
#include <iterator>

class GDOCP
{
    public:

        enum GDOCPCommand
        {
            LOGIN,
            GETPLAIN,
            GETCIPHER,
            CLOSE,
            FAIL,
            UNKNOWN
        };

        explicit GDOCP(const std::string& commandDelimiter,
                       const std::string& headerDelimiter,
                       const std::string& endDelimiter);

        int parseQuery(const std::string& query);
        std::string generateQuery(void) const;

        void setCommandDelimiter(const std::string& commandDelimiter);
        void setHeaderDelimiter(const std::string& headerDelimiter);
        void setEndDelimiter(const std::string& endDelimiter);

        std::string commandDelimiter(void) const;
        std::string headerDelimiter(void) const;
        std::string endDelimiter(void) const;

        void setCommand(GDOCPCommand command);
        GDOCPCommand command(void) const;

        std::string getHeaderValue(const std::string header);
        void setHeaderValue(const std::string& header,
                            const std::string& value);

        void clearHeaders(void);

    protected:

        std::string _commandDelimiter;
        std::string _headerDelimiter;
        std::string _endDelimiter;

        GDOCPCommand _command;
        std::map<std::string, std::string> _headers;
};

#endif // GDOCP_HPP
