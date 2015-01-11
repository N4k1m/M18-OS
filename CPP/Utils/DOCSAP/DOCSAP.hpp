#ifndef __DOCSAP_HPP__
#define __DOCSAP_HPP__

#include <iostream>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>
#include <iterator>

#define DEFAULT_FIELD_DELIMITER "$"
#define DEFAULT_END_DELIMITER   "#"

class DOCSAP
{
    public:

        enum DOCSAPCommand
        {
            LOGINA,
            LCLIENTS,
            PAUSE,
            RESUME,
            STOP,
            QUIT,
            ACK,
            FAIL,
            UNKNOWN
        };

        explicit DOCSAP(
                const std::string& fieldDelimiter = DEFAULT_FIELD_DELIMITER,
                const std::string& endDelimiter = DEFAULT_END_DELIMITER);
        DOCSAP(DOCSAP const& other);

        int parseQuery(const std::string& query);
        std::string generateQuery(void) const;

        void setFieldDelimiter(const std::string& fieldDelimiter);
        void setEndDelimiter(const std::string& endDelimiter);

        std::string fieldDelimiter(void) const;
        std::string endDelimiter(void) const;

        void setCommand(DOCSAPCommand command);
        void setNewCommand(DOCSAPCommand command);
        DOCSAPCommand command(void) const;

        void addArg(std::string const& argument);
        std::string getArg(int index) const;
        int getArgCount(void) const;

        bool is(DOCSAPCommand command) const;
        void clearFields(void);

        virtual ~DOCSAP(void);

    protected:

        std::string _fieldDelimiter;
        std::string _endDelimiter;

        DOCSAPCommand _command;
        std::vector<std::string> _fields;
};

#endif /* __DOCSAP_HPP__ */
