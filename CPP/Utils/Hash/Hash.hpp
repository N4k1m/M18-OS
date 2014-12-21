#ifndef __HASH_HPP__
#define __HASH_HPP__

#include <iostream>

class Hash
{
    public:

        static unsigned hash_str(const std::string& text,
                                 unsigned nonce,
                                 unsigned cnonce);

        static bool isPrime(unsigned int number);

    protected:

        unsigned _nonce;
        unsigned _cnonce;
};

#endif // HASH_HPP
