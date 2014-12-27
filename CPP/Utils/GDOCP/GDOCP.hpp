#ifndef __GDOCP_HPP__
#define __GDOCP_HPP__

#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <sstream>
#include <algorithm>
#include <iterator>

#define DEFAULT_COMMAND_DELIMITER "#"
#define DEFAULT_HEADER_DELIMITER  "="
#define DEFAULT_END_DELIMITER     "!"

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

        explicit GDOCP(
                const std::string& commandDelimiter = DEFAULT_COMMAND_DELIMITER,
                const std::string& headerDelimiter  = DEFAULT_HEADER_DELIMITER,
                const std::string& endDelimiter     = DEFAULT_END_DELIMITER);
        GDOCP(GDOCP const& other);

        int parseQuery(const std::string& query);
        std::string generateQuery(void) const;

        void setCommandDelimiter(const std::string& commandDelimiter);
        void setHeaderDelimiter(const std::string& headerDelimiter);
        void setEndDelimiter(const std::string& endDelimiter);

        std::string commandDelimiter(void) const;
        std::string headerDelimiter(void) const;
        std::string endDelimiter(void) const;

        void setCommand(GDOCPCommand command);
        void setNewCommand(GDOCPCommand command);
        GDOCPCommand command(void) const;

        std::string getHeaderValue(const std::string header);
        void setHeaderValue(const std::string& header,
                            const std::string& value);

        bool is(GDOCPCommand command);
        void clearHeaders(void);

    protected:

        std::string _commandDelimiter;
        std::string _headerDelimiter;
        std::string _endDelimiter;

        GDOCPCommand _command;
        std::map<std::string, std::string> _headers;
};

#endif /* __GDOCP_HPP__ */
