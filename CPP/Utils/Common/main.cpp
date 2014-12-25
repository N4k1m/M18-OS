#include <iostream>

#include "Common.hpp"

using namespace std;

int main()
{
    int x = 42;
    string s = SSTR(x);

    cout << "Number is " << s << endl;

    return EXIT_SUCCESS;
}

