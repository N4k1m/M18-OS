#include "IniParser.hpp"

using namespace std;

IniParser::IniParser()
{

}

IniParser::IniParser(std::string const& filepath)
{
    this->parse(filepath);
}

IniParser::IniParser(const IniParser& other)
{
    this->_assoc = other.get_assoc();
}

IniParser& IniParser::operator=(const IniParser& other)
{
    if (this != &other)
    {
    }

    return *this;
}

IniParser::~IniParser(void)
{
}

// Charge le contenu du fichier .ini dans une hashtable
int IniParser::parse(const string &filepath)
{
    // Open file
    ifstream infile(filepath.c_str());
    if(!infile.good())
    {
        infile.close();
        return -1;
    }

    // File exist. Save name
    this->_filename = filepath;

    string line, key, value;

    // Read file line by line and save tokens
    this->_assoc.clear();
    while(getline(infile, line))
    {
        int delimiter_pos = line.find_first_of("=");
        key   = line.substr(0, delimiter_pos);
        value = line.substr(delimiter_pos + 1);
        this->_assoc.insert(pair<string, string>(string(key), string(value)));
    }

    infile.close();
    return (int)this->_assoc.size();
}

int IniParser::count(void) const
{
    return (int) this->_assoc.size();
}

bool IniParser::keyExists(const string& key) const
{
    map<string, string>::const_iterator key_it = this->_assoc.find(key);

    return key_it != this->_assoc.end();
}

string IniParser::value(const string key) const
{
    map<string, string>::const_iterator value = this->_assoc.find(key);

    if (value == this->_assoc.end())
        throw Exception("La clé n'existe pas");
    else
        return value->second;
}

map<string, string> IniParser::get_assoc(void) const
{
    return this->_assoc;
}
