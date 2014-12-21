#include <iostream>
#include "GDOCP.hpp"

using namespace std;

int main()
{
    GDOCP query("#", "=", "!");
    query.parseQuery("LOGIN#nom=Mawet#prenom=Xavier!");


    //query.setCommand(GDOCP::LOGIN);

    switch (query.command())
    {
        case GDOCP::LOGIN:
            cout << "login" << endl;
            break;
        case GDOCP::GETPLAIN:
            cout << "GetPlain" << endl;
            break;
        case GDOCP::GETCIPHER:
            cout << "GetCipher" << endl;
            break;
        case GDOCP::CLOSE:
            cout << "close" << endl;
            break;
        case GDOCP::UNKNOWN:
            cout << "Erreur ... commande non reconnue" << endl;
            break;
        default:
            break;
    }

    cout << "Nom : " << query.getHeaderValue("Nom") << endl;
    cout << "prenom : " << query.getHeaderValue("prenom") << endl;
    cout << "Commande = " << query.generateQuery() << endl;

    return 0;
}

