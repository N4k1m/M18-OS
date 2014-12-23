package Threads;

import Utils.Request;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Nakim
 */
public class ThreadServerV1 extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadServerV1(int port)
    {
        this.port_server = port;
        this.stopRequested = false;
        this.socketServer = null;
        this.socketClient = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        try
        {
            this.socketServer = new ServerSocket(this.port_server);
            System.out.println("[ V1 ] Server started on port " + this.port_server);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            System.exit(1);
        }

        // Main loop
        while(!this.stopRequested)
        {
            try
            {
                System.out.println("[ V1 ] Waiting client");
                this.socketClient = this.socketServer.accept();
                this.clientStop = false;
                System.out.println("[ V1 ] New client connected");
            }
            catch (IOException ex)
            {
                System.out.println("[ V1 ] Interrupted. Stop waiting client");
                continue;
            }

            while (!this.clientStop)
            {
                this.query = Request.recv(this.socketClient);
                System.out.println("[ V1 ] Query received : " + query.getCommand());

                switch(this.query.getCommand())
                {
                    case Request.SOCK_ERROR:
                    case Request.NO_COMMAND:
                        this.clientStop = true;
                    case "MESSAGE":
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void manageMessage()
    {
        System.out.println("[ V1 ] manage message");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public synchronized void requestStop() throws IOException
    {
        this.stopRequested = true;

        if (this.socketClient != null && this.socketClient.isConnected())
            this.socketClient.close();

        this.socketServer.close();
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Variables privées ">
    private final int port_server;
    private boolean stopRequested;
    private boolean clientStop;

    private Request query;

    private ServerSocket socketServer;
    private Socket socketClient;
    // </editor-fold>
}
