package Multithreading;

import DB.BeanDBAccessMySQL;
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
import java.sql.ResultSet;

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
        this.adminConnected = false;

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
            // Connect to the data base
            this.db = new BeanDBAccessMySQL();
            if (!this.db.init())
                throw new Exception("Unable to connect to the database");

            // Creatre server socket
            this.socketServer = new ServerSocket(this.port);
            System.out.println("[ADMI] Thread administrator started on port " + this.port);
        }
        catch (Exception ex)
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
                this.adminConnected = false;
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
                        this.manageERROR();
                        break;
                    case DOCSAPRequest.LOGINA:
                        this.manageLOGINA();
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
                    case DOCSAPRequest.STOP:
                        this.manageSTOP();
                        break;
                    case DOCSAPRequest.QUIT:
                        this.manageQUIT();
                        break;
                    default:
                        System.out.println("[ADMI] Invalid query");
                        DOCSAPRequest.quickSend(DOCSAPRequest.FAIL,
                                                "Invalid query",
                                                this.socketClient);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void manageERROR()
    {
        this.clientStop = true;
        System.out.println("[ OK ] Interrupted. Stop receiving message");
    }

    private void manageLOGINA()
    {
        try
        {
            // Get admin password from DB
            ResultSet rsLogin = this.db.selectAll(
                "server_admin", "login LIKE \"" + this.query.getArg(0) + "\"");

            // Check if we have a result
            if (!rsLogin.next())
                throw new Exception("Administrator " + this.query.getArg(0) + " not allowed");

            String dbPassword = rsLogin.getString("password");
            String password = this.query.getArg(1);

            if (dbPassword == null || dbPassword.compareTo(password) != 0)
                throw new Exception("Invalid password");

            // Valid login - password. Send ACK
            this.adminConnected = true;
            DOCSAPRequest.quickSend(DOCSAPRequest.ACK, this.socketClient);
        }
        catch (Exception ex)
        {
            this.adminConnected = false;
            DOCSAPRequest.quickSend(
                DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }

    private void manageLCLIENTS()
    {
        try
        {
            if (!this.adminConnected)
                throw new Exception("you must be logged in");

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
            System.out.println("[FAIL] Unable to list clients : " +
                ex.getMessage());
            DOCSAPRequest.quickSend(
                DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }

    private void managePAUSE()
    {
        try
        {
            if (!this.adminConnected)
                throw new Exception("you must be logged in");

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
            System.out.println("[FAIL] Unable to suspend server : " +
                ex.getMessage());
            DOCSAPRequest.quickSend(
                DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }

    private void manageRESUME()
    {
        try
        {
            if (!this.adminConnected)
                throw new Exception("you must be logged in");

            // Send PAUSE request to thread urgence
            this.out.writeUTF("RESUME");

            // Update GUI status
            this.parent.setServerSuspended(false);

            // Send ACK to admin client
            DOCSAPRequest.quickSend(DOCSAPRequest.ACK, this.socketClient);
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] Unable to resume server : " +
                ex.getMessage());
            DOCSAPRequest.quickSend(
                DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }

    private void manageSTOP()
    {
        try
        {
            if (!this.adminConnected)
                throw new Exception("you must be logged in");

            // TODO
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] Unable to stop server : " +
                ex.getMessage());
            DOCSAPRequest.quickSend(
                DOCSAPRequest.FAIL, ex.getMessage(), this.socketClient);
        }
    }

    private void manageQUIT()
    {
        this.clientStop = true;

        System.out.println("[ADMI] Administrator quit");
        DOCSAPRequest.quickSend(DOCSAPRequest.ACK, this.socketClient);
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
    private int port;
    private ServerSocket socketServer;
    private Socket socketClient;

    private boolean isStopped;
    private boolean clientStop;
    private boolean adminConnected;

    private DOCSAPRequest query;

    private MainFrame parent;

    // Pipe
    private DataOutputStream out;
    private DataInputStream in;

    // DB access
    private BeanDBAccessMySQL db;
    //</editor-fold>
}
