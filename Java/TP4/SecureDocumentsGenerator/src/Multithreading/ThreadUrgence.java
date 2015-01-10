package Multithreading;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nakim
 */
public class ThreadUrgence extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadUrgence()
    {
        this.clientUrgentSockets = new ArrayList<>();
        this.isStopped = true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        this.isStopped = false;

        // TODO : communiquer avec le thread admin
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public synchronized void addClientUrgentSocket(Socket socketClient)
    {
        System.out.println("[URG] Add new client urgent socket");
        this.clientUrgentSockets.add(socketClient);
        System.out.println("[URG] Number of client = " + this.clientUrgentSockets.size());
    }

    public synchronized void requestStop() throws IOException
    {
        this.isStopped = true;

        // close all client sockets
        for(Socket clientSocket : this.clientUrgentSockets)
            if(clientSocket != null && clientSocket.isConnected())
                clientSocket.close();
    }

    public synchronized boolean isStopped()
    {
        return this.isStopped;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    List<Socket> clientUrgentSockets;

    private boolean isStopped;
    //</editor-fold>
}
