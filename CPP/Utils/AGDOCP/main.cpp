#include <iostream>

#include "AGDOCProtocol.hpp"

using namespace std;

int main(void)
{
    AGDOCProtocol query;
    query.command = AGDOCProtocol::STOP;
    query.content.stop.delay = 10;

    switch (query.command)
    {
        case AGDOCProtocol::PAUSE:
            cout << "Pause" << endl;
            break;
        case AGDOCProtocol::RESUME:
            cout << "Resume" << endl;
            break;
        case AGDOCProtocol::STOP:
            cout << "Stop" << endl;
            cout << "delay = " << query.content.stop.delay << endl;
            break;
    }

    return 0;
}

