package Threads;

import GUI.MainFrame;
import Utils.Request;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Nakim
 */
public class ThreadUrgence extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadUrgence(Socket socketUrgence, MainFrame parent)
    {
        this.sock = socketUrgence;
        this.parent = parent;

        this.isStopped = true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        this.isStopped = false;

        while(!this.isStopped())
        {
            System.out.println("[URG] Thread urgence waiting instruction");
            this.request = Request.recv(this.sock);
            System.out.println("[URG] Received : " + this.request.getCommand());

            switch(this.request.getCommand())
            {
                case Request.SOCK_ERROR:
                case Request.NO_COMMAND:
                    System.out.println("[URG] Disconnected from server");
                    this.isStopped = true;
                    break;
                case "PAUSE":
                    this.parent.setServerSuspended(true);
                    break;
                case "RESUME":
                    this.parent.setServerSuspended(false);
                    break;
                case "STOP":
                    this.parent.showMessage("Server shutdown",
                                            "Server will be down in "    +
                                            this.request.getStringArg(0) +
                                            " second(s)");
                    break;
                default:
                    break;
            }
        }

        System.out.println("[URG] Thread urgence ended");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public synchronized void requestStop() throws IOException
    {
        this.isStopped = true;

        // Close socket
        if (this.sock != null && this.sock.isConnected())
            this.sock.close();
    }

    public synchronized boolean isStopped()
    {
        return this.isStopped;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private final Socket sock;
    private final MainFrame parent;

    private Request request;

    private boolean isStopped;
    //</editor-fold>
}
