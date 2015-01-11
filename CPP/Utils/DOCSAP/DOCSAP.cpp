#include "DOCSAP.hpp"

using namespace std;

DOCSAP::DOCSAP(const string& fieldDelimiter,
               const string& endDelimiter) :
    _fieldDelimiter(fieldDelimiter),
    _endDelimiter(endDelimiter),
    _command(UNKNOWN),
    _fields()
{
    // Nothing to do here ...
}

DOCSAP::DOCSAP(DOCSAP const& other) :
    _fieldDelimiter(other._fieldDelimiter),
    _endDelimiter(other._endDelimiter),
    _command(other._command),
    _fields(other._fields)
{
    // Nothing to do here ...
}

int DOCSAP::parseQuery(const string& query)
{
    // Free old fiels
    this->clearFields();

    if (query.empty())
    {
        this->_command = FAIL;
        this->addArg("Empty query");
        return 1;
    }

    vector<string> tokens;

    size_t prev_pos = 0, pos;
    string delimiters = this->_fieldDelimiter + this->_endDelimiter;
    while ((pos = query.find_first_of(delimiters, prev_pos)) != string::npos)
    {
        if (pos > prev_pos)
            tokens.push_back(query.substr(prev_pos, pos-prev_pos));
        prev_pos = pos + 1;
    }

    if (prev_pos < query.length())
        tokens.push_back(query.substr(prev_pos, string::npos));

    // First token = command
    string command = tokens.front();
    tokens.erase(tokens.begin());

    // Upper text to be case insensitive
    transform(command.begin(), command.end(),command.begin(), ::toupper);

    if (command == "LOGINA")
        this->_command = LOGINA;
    else if (command == "LCLIENTS")
        this->_command = LCLIENTS;
    else if (command == "PAUSE")
        this->_command = PAUSE;
    else if (command == "RESUME")
        this->_command = RESUME;
    else if (command == "STOP")
        this->_command = STOP;
    else if (command == "QUIT")
        this->_command = QUIT;
    else if (command == "ACK")
        this->_command = ACK;
    else if (command == "FAIL")
        this->_command = FAIL;
    else
        this->_command = UNKNOWN;

    this->_fields = tokens;

    return (int)this->_fields.size();
}

std::string DOCSAP::generateQuery(void) const
{
    string query("");

    // Add command
    switch (this->_command)
    {
        case DOCSAP::LOGINA:
            query += "LOGINA";
            break;
        case DOCSAP::LCLIENTS:
            query += "LCLIENTS";
            break;
        case DOCSAP::PAUSE:
            query += "PAUSE";
            break;
        case DOCSAP::RESUME:
            query += "RESUME";
            break;
        case DOCSAP::STOP:
            query += "STOP";
            break;
        case DOCSAP::QUIT:
            query += "QUIT";
            break;
        case DOCSAP::ACK:
            query += "ACK";
            break;
        case DOCSAP::FAIL:
            query += "FAIL";
            break;
        default:
            query += "UNKNOWN";
            break;
    }

    // Add all fields
    std::vector<string>::const_iterator iter;
    for (iter = this->_fields.begin(); iter != this->_fields.end(); ++iter)
    {
        query.append(this->_fieldDelimiter);
        query.append(*iter);
    }

    return query.append(this->_endDelimiter);
}

void DOCSAP::setFieldDelimiter(const string& fieldDelimiter)
{
    if (!fieldDelimiter.empty())
        this->_fieldDelimiter = fieldDelimiter;
}

void DOCSAP::setEndDelimiter(const string& endDelimiter)
{
    if (!endDelimiter.empty())
        this->_endDelimiter = endDelimiter;
}

string DOCSAP::fieldDelimiter(void) const
{
    return this->_fieldDelimiter;
}

string DOCSAP::endDelimiter(void) const
{
    return this->_endDelimiter;
}

void DOCSAP::setCommand(DOCSAPCommand command)
{
    this->_command = command;
}

void DOCSAP::setNewCommand(DOCSAPCommand command)
{
    this->_command = command;

    // If command change, previous fields are invalide
    this->clearFields();
}

DOCSAP::DOCSAPCommand DOCSAP::command(void) const
{
    return this->_command;
}

void DOCSAP::addArg(const string& argument)
{
    this->_fields.push_back(argument);
}

string DOCSAP::getArg(int index) const
{
    if (index > (int)(this->_fields.size() - 1))
        return "";

    return this->_fields[index];
}

int DOCSAP::getArgCount(void) const
{
    return (int)this->_fields.size();
}

bool DOCSAP::is(DOCSAPCommand command) const
{
    return this->_command == command;
}

void DOCSAP::clearFields(void)
{
    this->_fields.clear();
}

DOCSAP::~DOCSAP(void)
{
    // Nothing to do here ...
}
