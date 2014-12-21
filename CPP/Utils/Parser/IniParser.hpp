#ifndef __INIPARSER_HPP__
#define __INIPARSER_HPP__

#include <iostream>
#include <fstream>
#include <map>
#include <string>

#include "../Exceptions/Exception.hpp"

class IniParser
{
    public:

        IniParser(void);
        IniParser(std::string const& filepath);
        IniParser(IniParser const& other);
        virtual IniParser& operator=(IniParser const& other);
        virtual ~IniParser(void);

        int parse(std::string const& filepath);

        bool keyExists(const std::string& key) const;
        int count(void) const; // Number of keys

        std::string value(std::string key) const;
        std::map<std::string, std::string> get_assoc(void) const;

    protected:

        std::string _filename;
        std::map<std::string, std::string> _assoc;
};

#endif /* __INIPARSER_HPP__ */
