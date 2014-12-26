#include "GDOCP.hpp"

using namespace std;

GDOCP::GDOCP(const string &commandDelimiter,
             const string &headerDelimiter,
             const string &endDelimiter)
    : _commandDelimiter(commandDelimiter),
      _headerDelimiter(headerDelimiter),
      _endDelimiter(endDelimiter),
      _command(UNKNOWN)
{

}

GDOCP::GDOCP(GDOCP const& other)
    : _commandDelimiter(other._commandDelimiter),
      _headerDelimiter(other._headerDelimiter),
      _endDelimiter(other._endDelimiter),
      _command(UNKNOWN)
{

}

int GDOCP::parseQuery(const string &query)
{
    // Free map
    this->clearHeaders();

    if (query.empty())
    {
        this->_command = FAIL;
        this->setHeaderValue("cause", "empty query");
        return 0;
    }

    vector<string> tokens;

    std::size_t prev_pos = 0, pos;
    std::string delimiters = _commandDelimiter + _headerDelimiter + _endDelimiter;
    while ((pos = query.find_first_of(delimiters, prev_pos)) != std::string::npos)
    {
        if (pos > prev_pos)
            tokens.push_back(query.substr(prev_pos, pos-prev_pos));
        prev_pos = pos + 1;
    }

    if (prev_pos < query.length())
        tokens.push_back(query.substr(prev_pos, std::string::npos));

    // First token = command
    string command = tokens.front();
    tokens.erase(tokens.begin());

    // Upper text to be case insensitive
    transform(command.begin(), command.end(),command.begin(), ::toupper);

    if (command == "LOGIN")
        this->_command = LOGIN;
    else if (command == "GETPLAIN")
        this->_command = GETPLAIN;
    else if (command == "GETCIPHER")
        this->_command = GETCIPHER;
    else if (command == "CLOSE")
        this->_command = CLOSE;
    else if (command == "FAIL")
        this->_command = FAIL;
    else
        this->_command = UNKNOWN;

    vector<string>::iterator it;
    for(it = tokens.begin(); it != tokens.end(); ++it)
    {
        // Header in capital letters, case unsensitive
        string header = *it;
        transform(header.begin(), header.end(),header.begin(), ::toupper);

        string value = *(++it);

        this->_headers.insert(pair<string, string>(header, value));
    }

    return (int)this->_headers.size();
}

string GDOCP::generateQuery(void) const
{
    string query("");

    // Add command
    switch (this->_command)
    {
        case GDOCP::LOGIN:
            query += "LOGIN";
            break;
        case GDOCP::GETPLAIN:
            query += "GETPLAIN";
            break;
        case GDOCP::GETCIPHER:
            query += "GETCIPHER";
            break;
        case GDOCP::CLOSE:
            query += "CLOSE";
            break;
        case GDOCP::FAIL:
            query += "FAIL";
            break;
        default:
            break;
    }

    if (this->_command == CLOSE)
        return query.append(this->_endDelimiter);

    // Add headers
    std::map<std::string, std::string>::const_iterator iter;
    for (iter = _headers.begin(); iter != _headers.end(); ++iter)
    {
        query.append(this->_commandDelimiter);
        query.append(iter->first);
        query.append(this->_headerDelimiter);
        query.append(iter->second);
    }

    return query.append(this->_endDelimiter);
}

void GDOCP::setCommandDelimiter(const string& commandDelimiter)
{
    if (!commandDelimiter.empty())
        this->_commandDelimiter = commandDelimiter;
}

void GDOCP::setHeaderDelimiter(const string& headerDelimiter)
{
    if (!headerDelimiter.empty())
        this->_headerDelimiter = headerDelimiter;
}

void GDOCP::setEndDelimiter(const string& endDelimiter)
{
    if (!endDelimiter.empty())
        this->_endDelimiter = endDelimiter;
}

string GDOCP::commandDelimiter(void) const
{
    return this->_commandDelimiter;
}

string GDOCP::headerDelimiter(void) const
{
    return this->_headerDelimiter;
}

string GDOCP::endDelimiter(void) const
{
    return this->_endDelimiter;
}

void GDOCP::setCommand(GDOCPCommand type)
{
    this->_command = type;
}

GDOCP::GDOCPCommand GDOCP::command(void) const
{
    return this->_command;
}

string GDOCP::getHeaderValue(const string header)
{
    string existingHeader = header;
    transform(existingHeader.begin(), existingHeader.end(), existingHeader.begin(), ::toupper);

    return this->_headers[existingHeader];
}

void GDOCP::setHeaderValue(const string &header, const string &value)
{
    string newHeader(header);
    transform(newHeader.begin(), newHeader.end(),newHeader.begin(), ::toupper);

    this->_headers[newHeader] = value;
}

void GDOCP::clearHeaders(void)
{
    this->_headers.clear();
}
