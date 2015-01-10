package Multithreading;

import Utils.Request;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    public ThreadUrgence(OutputStream out, InputStream in)
    {
        this.clientUrgentSockets = new ArrayList<>();
        this.isStopped = true;

        // Pipe
        this.out = new DataOutputStream(out);
        this.in  = new DataInputStream(in);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        this.isStopped = false;

        try
        {
            while (!this.isStopped())
            {
                System.out.println("[URG] Waiting instruction");
                String query = this.in.readUTF();
                System.out.println("[URG] Instruction received : " + query);

                switch(query)
                {
                    case "LCLIENTS":
                        this.manageLCLIENTS();
                        break;
                    case "PAUSE":
                        this.managePAUSE();
                        break;
                    case "RESUME":
                        this.manageRESUME();
                        break;
                    case "STOP":
                        break;
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("[URG] Interrupted. Stop waiting instruction");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void manageLCLIENTS()
    {
        try
        {
            // Check if clients are already connected
            for (int i = 0; i < this.clientUrgentSockets.size(); i++)
            {
                Socket clientSocket= this.clientUrgentSockets.get(i);
                boolean connected = Request.quickSend("KEEPALIVE", clientSocket);

                if (connected == false)
                {
                    System.out.println("[URG] disconnected client detected");
                    this.clientUrgentSockets.remove(i);
                    try
                    {
                        clientSocket.close();
                    }
                    catch (IOException ex){}
                    i--;
                }
            }

            // Write the number of clients
            this.out.writeInt(this.clientUrgentSockets.size());

            // Write IPv4 addresses of all clients
            for(Socket socketClient : this.clientUrgentSockets)
                this.out.writeUTF(socketClient.getRemoteSocketAddress().toString());
        }
        catch (IOException ex)
        {
            System.out.println("[URG] LCLIENTS management failed : " + ex.getMessage());
        }
    }

    private void managePAUSE()
    {
        for (int i = 0; i < this.clientUrgentSockets.size(); i++)
        {
            Socket clientSocket= this.clientUrgentSockets.get(i);
            boolean connected = Request.quickSend("PAUSE", clientSocket);

            if (connected == false)
            {
                System.out.println("[URG] disconnected client detected");
                this.clientUrgentSockets.remove(i);
                try
                {
                    clientSocket.close();
                }
                catch (IOException ex){}
                i--;
            }
        }
    }

    private void manageRESUME()
    {
        for (int i = 0; i < this.clientUrgentSockets.size(); i++)
        {
            Socket clientSocket= this.clientUrgentSockets.get(i);
            boolean connected = Request.quickSend("RESUME", clientSocket);

            if (connected == false)
            {
                System.out.println("[URG] disconnected client detected");
                this.clientUrgentSockets.remove(i);
                try
                {
                    clientSocket.close();
                }
                catch (IOException ex){}
                i--;
            }
        }
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

        this.interrupt();

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
    private List<Socket> clientUrgentSockets;

    private boolean isStopped;

    // Pipe
    private DataOutputStream out;
    private DataInputStream in;
    //</editor-fold>
}
