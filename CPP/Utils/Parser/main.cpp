#include <iostream>
#include "IniParser.hpp"

using namespace std;

int main(void)
{
    IniParser iniParser("users.ini");

    cout << "Nombre de clés = " << iniParser.count() << endl;

    if (iniParser.keyExists("xavier"))
        cout << "Mot de passe = " << iniParser.value("xavier") << endl;
    else
        cout << "clé pas trouvée ..." << endl;

    return 0;
}
