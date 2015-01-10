package Multithreading;

import DOCSAP.DOCSAPRequest;
import GUI.MainFrame;
import Utils.ReturnValue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Nakim
 */
public class ThreadAdmin extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadAdmin(int port, OutputStream out, InputStream in, MainFrame parent)
    {
        this.port = port;
        this.socketServer = null;
        this.socketClient = null;

        this.isStopped = true;
        this.clientStop = true;

        this.parent = parent;

        // Pipe
        this.out = new DataOutputStream(out);
        this.in  = new DataInputStream(in);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        try
        {
            this.socketServer = new ServerSocket(this.port);
            System.out.println("[ADMI] Thread administrator started on port " + this.port);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            System.exit(ReturnValue.FAILURE.getReturnCode());
        }

        this.isStopped = false;

        // Main loop
        while (!this.isStopped())
        {
            try
            {
                // Accept client
                System.out.println("[ADMI] Waiting administrator");
                this.socketClient = this.socketServer.accept();
                this.clientStop = false;
                System.out.println("[ADMI] New administrator connected");
            }
            catch (IOException ex)
            {
                System.out.println("[ADMI] Interrupted. Stop waiting administrator");
                continue;
            }

            // Client loop
            while(!this.clientStop)
            {
                System.out.println("[ADMI] Waiting instruction");
                this.query = DOCSAPRequest.recv(this.socketClient);
                System.out.println("[ADMI] Query received : "
                    + this.query.getCommand());

                switch(this.query.getCommand())
                {
                    case DOCSAPRequest.SOCK_ERROR:
                    case DOCSAPRequest.NO_COMMAND:
                        this.clientStop = true;
                        System.out.println("[ OK ] Interrupted. Stop receiving message");
                        break;
                    case DOCSAPRequest.LCLIENTS:
                        this.manageLCLIENTS();
                        break;
                    case DOCSAPRequest.PAUSE:
                        this.managePAUSE();
                        break;
                    case DOCSAPRequest.RESUME:
                        this.manageRESUME();
                        break;
                    case DOCSAPRequest.QUIT:
                        this.clientStop = true;
                        System.out.println("[ADMI] Administrator quit");
                        DOCSAPRequest.quickSend(DOCSAPRequest.ACK, this.socketClient);
                        break;
                    default:
                        System.out.println("[ADMI] command : " + this.query.getCommand());
                        System.out.println("[ADMI] Params  : " + this.query.getArgs());
                        DOCSAPRequest.quickSend(DOCSAPRequest.ACK, this.socketClient);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void manageLCLIENTS()
    {
        try
        {
            // Send LCLIENTS request to thread urgence
            this.out.writeUTF("LCLIENTS");

            // Prepare reply
            DOCSAPRequest reply = new DOCSAPRequest(DOCSAPRequest.ACK);

            // Get number of clients
            int clientsCount = this.in.readInt();

            // Get all clients IP address
            for (int i = 0; i < clientsCount; i++)
                reply.addArg(this.in.readUTF());

            reply.send(this.socketClient);
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] enable to list clients : " +
                ex.getMessage());

            DOCSAPRequest.quickSend(
                DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }

    private void managePAUSE()
    {
        try
        {
            // Send PAUSE request to thread urgence
            this.out.writeUTF("PAUSE");

            // Get ACK from thread urgence
            //String ack = this.in.readUTF();
            //if (!ack.equalsIgnoreCase("ACK"))
              //  throw new Exception(this.in.readUTF());

            // Update GUI status
            this.parent.setServerSuspended(true);

            // Send ACK to admin client
            DOCSAPRequest.quickSend(DOCSAPRequest.ACK, this.socketClient);
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] enable to suspend server : " +
                ex.getMessage());
            DOCSAPRequest.quickSend(DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }

    private void manageRESUME()
    {
        try
        {
            // Send PAUSE request to thread urgence
            this.out.writeUTF("RESUME");

            // Update GUI status
            this.parent.setServerSuspended(false);

            // Send ACK to admin client
            DOCSAPRequest.quickSend(DOCSAPRequest.ACK, this.socketClient);
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] enable to resume server : " +
                ex.getMessage());
            DOCSAPRequest.quickSend(DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public synchronized void requestStop() throws IOException
    {
        this.isStopped = true;

        // Close socket client
        if (this.socketClient != null && this.socketClient.isConnected())
            this.socketClient.close();

        // Close socket server
        if (this.socketServer != null)
            this.socketServer.close();
    }

    public synchronized boolean isStopped()
    {
        return this.isStopped;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private Variables">
    int port;
    private ServerSocket socketServer;
    private Socket socketClient;

    private boolean isStopped;
    private boolean clientStop;

    private DOCSAPRequest query;

    private MainFrame parent;

    // Pipe
    private DataOutputStream out;
    private DataInputStream in;
    //</editor-fold>
}
