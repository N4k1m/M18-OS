//
//  main.cpp
//  Sockets
//
//  Created by Xavier Mawet on 8/11/14.
//  Copyright (c) 2014 Xavier Mawet. All rights reserved.
//

#include <iostream>
#include <cstdlib>

#include "TCPSocketClient.hpp"

#define PORT 8000

using namespace std;

int main(int argc, const char * argv[])
{
    string ip;
    string msg("");
    TCPSocketClient* client_sock = new TCPSocketClient;

    cout << "Ip du serveur : ";
    cin >> ip;

    client_sock->connectToHost(ip, PORT);

    while (msg != "FIN")
    {
        cout << "Message à envoyer : ";
        cin >> msg;

        client_sock->send(msg);
    }

    delete client_sock;
    
    return EXIT_SUCCESS;
}
