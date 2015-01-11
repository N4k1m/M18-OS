#include <iostream>
#include "DOCSAP.hpp"

using namespace std;

int main(void)
{
    DOCSAP protocolManager("$", "#");

    protocolManager.parseQuery("LOGINA$Xavier$password#");

    switch (protocolManager.command())
    {
        case DOCSAP::LOGINA:
            cout << "Command = LOGINA" << endl;
            break;
        case DOCSAP::LCLIENTS:
            cout << "Command = LCLIENTS" << endl;
            break;
        case DOCSAP::PAUSE:
            cout << "Command = PAUSE" << endl;
            break;
        case DOCSAP::RESUME:
            cout << "Command = RESUME" << endl;
            break;
        case DOCSAP::STOP:
            cout << "Command = STOP" << endl;
            break;
        case DOCSAP::QUIT:
            cout << "Command = QUIT" << endl;
            break;
        case DOCSAP::ACK:
            cout << "Command = ACK" << endl;
            break;
        case DOCSAP::FAIL:
            cout << "Command = FAIL" << endl;
            break;
        case DOCSAP::UNKNOWN:
            cout << "Command = UNKNOWN" << endl;
            break;
    }

    cout << "Nombre d'arguments = " << protocolManager.getArgCount() << endl;

    cout << "Affichage de tous les argurments" << endl;
    cout << "-------------------------------------" << endl;
    for (int i(0); i < protocolManager.getArgCount(); ++i)
        cout << "Arg " << i << " = " << protocolManager.getArg(i) << endl;

    return 0;
}

