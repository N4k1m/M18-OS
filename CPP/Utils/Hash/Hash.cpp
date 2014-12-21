#include "Hash.hpp"

unsigned Hash::hash_str(const std::string& text, unsigned nonce, unsigned cnonce)
{
    unsigned h = 31; /* also another prime */
    const char* c_string = text.c_str();

    while (*c_string)
    {
        h = (h * nonce) ^ (c_string[0] * cnonce);
        c_string++;
    }

    return h;
}

bool Hash::isPrime(unsigned int number)
{
    if (number <= 1)
        return false;

    for(unsigned int i = 2; i*i <= number; ++i)
        if (number % i == 0)
            return false;

    return true;
}
