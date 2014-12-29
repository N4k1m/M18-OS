package Multithreading;

import GMC.EventTracker;
import SGDOCP.SGDOCPCommand;
import SGDOCP.SGDOCPRequest;
import Utils.ReturnValue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Nakim
 */
public class ThreadServer extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadServer(int port,
                        int threadsClientCount,
                        int limitQueue,
                        EventTracker parent)
    {
        this.port = port;
        this.socketServer = null;
        this.socketClient = null;
        this.isStopped = true;

        // Create thread pool
        this.pool = new ThreadPool(threadsClientCount, limitQueue);

        this.parent = parent;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        try
        {
            this.socketServer = new ServerSocket(this.port);
            System.out.println("[ OK ] Server started on port " + this.port);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            System.exit(ReturnValue.FAILURE.getReturnCode());
        }

        // Start threads client
        this.pool.start();

        this.isStopped = false;

        // Main loop
        while(!this.isStopped())
        {
            try
            {
                parent.manageEvent("[ OK ] Waiting client");
                this.socketClient = this.socketServer.accept();
                parent.manageEvent("[ OK ] New client connected");
            }
            catch (IOException ex)
            {
                parent.manageEvent("[ OK ] Thread server Interrupted."
                    + " Stop waiting client");
                continue;
            }

            // New client connected --> get request
            this.query = SGDOCPRequest.recv(this.socketClient);

            switch(this.query.getCommand())
            {
                case SOCK_ERROR:
                case NO_COMMAND:
                    parent.manageEvent("[ OK ] Thread server : connection with the client closed");
                    break;
                case LOGIN:
                    parent.manageEvent("[ OK ] Thread server requete login");
                    this.manageLogin();
                    break;
                default:
                    parent.manageEvent("[FAIL] Invalid query");
                    this.sendFailReply("Login required");
                    break;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    public void sendFailReply(String cause)
    {
        SGDOCPRequest.quickSend(SGDOCPCommand.FAIL, cause, this.socketClient);

        try
        {
            this.socketClient.close();
        }
        catch (IOException ex)
        {
            parent.manageEvent("[FAIL] Thread server failed to send fail query : "
                               + ex.getMessage());
        }
    }

    public void manageLogin()
    {
        // Vérifier le login du client

        Runnable worker = this.query.createRunnable(this.socketClient, this.parent);

        try
        {
            this.pool.execute(worker);
        }
        // if enable to enqueue task because queue is full
        catch (InterruptedException | IllegalStateException ex)
        {
            parent.manageEvent("[FAIL] " + ex.getMessage());
            this.sendFailReply(ex.getMessage());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public synchronized void requestStop() throws IOException
    {
        this.isStopped = true;

        // Stop threads client in pool
        this.pool.requestStop();

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

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private final int port;
    private ServerSocket socketServer;
    private Socket socketClient;
    private boolean isStopped;

    private ThreadPool pool;

    private SGDOCPRequest query;

    private EventTracker parent; // GUI
    //</editor-fold>
}
