package Multithreading;

import GMC.EventTracker;
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

    private EventTracker parent; // GUI
    //</editor-fold>
}
