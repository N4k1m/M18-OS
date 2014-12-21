#include <iostream>
#include "Hash.hpp"
#include "RandomPrimeGenerator.hpp"

using namespace std;

int main()
{
    string msg("Hello World");

    RandomPrimeGenerator rpg;
    rpg.init_fast(1000000);

    cout << "Entre du text : ";
    while (msg != "FIN")
    {
        cin >> msg;
        cout << msg << " = " << Hash::hash_str(msg, A, B) << endl;
        cout << "Nombre premier aleatoir = " << rpg.get();
    }

    return 0;
}

