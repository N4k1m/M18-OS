package Admin;

import DOCSAP.DOCSAPRequest;
import Utils.ReturnValue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Nakim
 */
public class ThreadAdmin extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadAdmin(int port)
    {
        this.port = port;
        this.socketServer = null;
        this.socketClient = null;

        this.isStopped = true;
        this.clientStop = true;
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
                    case DOCSAPRequest.QUIT:
                        this.clientStop = true;
                        System.out.println("[ADMI] Administrator quit");
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
    //</editor-fold>
}
