#ifndef __COMMON_HPP__
#define __COMMON_HPP__

#include <sstream>

// std::to_string() like for C++ < 11
#define SSTR( x ) dynamic_cast< std::ostringstream & >( \
        ( std::ostringstream() << std::dec << x ) ).str()

#endif // __COMMON_HPP__
