#ifndef __RANDOMPRIMEGENERATOR_HPP__
#define __RANDOMPRIMEGENERATOR_HPP__

#include <cstdlib>
#include <iostream>
#include <vector>

class RandomPrimeGenerator
{
    public:

        explicit RandomPrimeGenerator(void);
        virtual ~RandomPrimeGenerator(void);

        void init_fast(unsigned int _max_number);
        void init_cheap(unsigned int _max_prime_count);

        unsigned int get(void);

    private:

        void find_primes(void);

    private:

        enum
        {
            NONE = 0,
            FAST = 1,
            CHEAP = 2
        } mode;

        unsigned int max_number;
        unsigned int max_prime_count;

        std::vector<unsigned int> prime_list;
};

#endif /* __RANDOMPRIMEGENERATOR_HPP__ */
