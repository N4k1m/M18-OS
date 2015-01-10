package Multithreading;

import GMC.ThreadPool;
import GMC.EventTracker;
import MyLittleCheapLibrary.CIAManager;
import SGDOCP.SGDOCPCommand;
import SGDOCP.SGDOCPRequest;
import SPF.Authentication.Authentication;
import SPF.Authentication.SymmetricKey;
import Utils.PropertyLoader;
import Utils.ReturnValue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

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
        // client management
        this.port = port;
        this.socketServer = null;
        this.socketClient = null;
        this.isStopped = true;
        this.parent = parent;

        // Urgence management
        this.socketServerUrgence = null;
        this.socketClientUrgence = null;
        this.threadUrgence = new ThreadUrgence();

        this.keyGen = null;
        this.authentication = null;
        this.prepareAuthentication();

        // Create task queue
        this.limitedBlockingTaskQueue =
            new LimitedBlockingTaskQueue(limitQueue, false);
        // Create thread pool
        this.pool =
            new ThreadPool(threadsClientCount, this.limitedBlockingTaskQueue);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        try
        {
            this.socketServer  = new ServerSocket(this.port);
            this.socketServerUrgence = new ServerSocket(this.port + 1);
            System.out.println("[ OK ] Server started on port " + this.port +
                " and urgence on port : " + this.port + 1);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            System.exit(ReturnValue.FAILURE.getReturnCode());
        }

        // Start threads client
        this.pool.start();
        this.isStopped = false;

        // Start thread urgence
        this.threadUrgence.start();

        // Main loop
        while(!this.isStopped())
        {
            try
            {
                parent.manageEvent("[ OK ] Waiting client");
                this.socketClient = this.socketServer.accept();
                this.socketClientUrgence = this.socketServerUrgence.accept();
                parent.manageEvent("[ OK ] New client connected");
            }
            catch (IOException ex)
            {
                parent.manageEvent("[ OK ] Thread server Interrupted."
                    + " Stop waiting client");
                continue;
            }

            // TODO vérifier que l'in peut accepter des clients (serveur en pause)

            // Get LOGIN request from protocol SGDOCP
            try
            {
                // New client connected --> get request
                this.query = SGDOCPRequest.recv(this.socketClient);
            }
            catch (ClassCastException ex)
            {
                parent.manageEvent("[FAIL] Thread server : client try to use an other protocol");
                try
                {
                    this.socketClient.close();
                }
                catch (IOException ex1)
                {
                    parent.manageEvent("[FAIL] Thread server failed to send fail query : "
                               + ex.getMessage());
                }
                finally
                {
                    continue;
                }
            }

            // If the client use the right protocol
            switch(this.query.getCommand())
            {
                // Connection with the client is closed
                case SOCK_ERROR:
                case NO_COMMAND:
                    parent.manageEvent("[ OK ] Thread server : connection with the client is closed");
                    break;
                // Client want to log in
                case LOGIN:
                    parent.manageEvent("[ OK ] Thread server : login requested");
                    this.manageLogin();
                    break;
                // Client try to send query before login
                default:
                    parent.manageEvent("[FAIL] Invalid query");
                    this.sendFailReply("Login required");
                    break;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void prepareAuthentication()
    {
        String usersFilePath = System.getProperty("user.dir")
            + System.getProperty("file.separator") + "src"
            + System.getProperty("file.separator") + "GUI"
            + System.getProperty("file.separator") + "users.properties";

        try
        {
            // Get users properties
            this.usersProperties = PropertyLoader.load(usersFilePath);

            // Get Authentification
            this.authentication =
                CIAManager.getAuthentication(AUTHENTICATION_PROVIDER);

            // Create the parameter generator for a 1024-bit DH key pair
            AlgorithmParameterGenerator paramGen =
                AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);

            parent.manageEvent("[ OK ] Thread server : Generating params");

            // Generate the parameters
            AlgorithmParameters params = paramGen.generateParameters();
            //Specify parameters to use for the algorithm
            DHParameterSpec dhSpec = (DHParameterSpec)params.getParameterSpec(
                DHParameterSpec.class);

            parent.manageEvent("[ OK ] Thread server : Params generated");

            this.keyGen = KeyPairGenerator.getInstance("DH");
            this.keyGen.initialize(dhSpec);
        }
        catch (IOException |
               NoSuchAlgorithmException |
               InvalidParameterSpecException |
               InvalidAlgorithmParameterException ex)
        {
            System.err.println(ex);
            System.exit(ReturnValue.FAILURE.getReturnCode());
        }
    }

    private void sendFailReply(String cause)
    {
        SGDOCPRequest.quickSend(SGDOCPCommand.FAIL, cause, this.socketClient);

        try
        {
            // Close the two client sockets
            this.socketClient.close();
            this.socketClientUrgence.close();
        }
        catch (IOException ex)
        {
            parent.manageEvent("[FAIL] Thread server failed to send fail query : "
                               + ex.getMessage());
        }
    }

    private void manageLogin()
    {
        try
        {
            SGDOCPRequest reply;
            String password;

            // Get client password
            password = this.usersProperties.getProperty(
                this.query.getStringArg(0));

            // Check if client exists
            if (password == null)
                throw new Exception("Unauthorized user");

            parent.manageEvent("[ OK ] Thread server : valid user");

            // Generate key pair
            KeyPair keypair = keyGen.generateKeyPair();

            // Send LOGIN_ACK + Public key + Authentication provider
            reply = new SGDOCPRequest(SGDOCPCommand.LOGIN_ACK);
            reply.addArg(keypair.getPublic().getEncoded());
            reply.addArg(AUTHENTICATION_PROVIDER);

            parent.manageEvent("[ OK ] Thread server : send public key");
            this.query = reply.sendAndRecv(this.socketClient);

            // Check client reply
            if (this.query.is(SGDOCPCommand.NO_COMMAND) ||
                this.query.is(SGDOCPCommand.SOCK_ERROR))
                throw new Exception("Client has been disconnected");

            if (!this.query.is(SGDOCPCommand.LOGIN))
                throw new Exception("Invalid query. LOGIN required");

            // Get client public key
             X509EncodedKeySpec x509KeySpec =
                new X509EncodedKeySpec(this.query.getArg(0));
            KeyFactory keyFact = KeyFactory.getInstance("DH");
            PublicKey publicKey = keyFact.generatePublic(x509KeySpec);

            /* Prepare the "secret key generator" with the private key
             * and the client public key */
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(keypair.getPrivate());
            ka.doPhase(publicKey, true);

            // Generate new secret key for Authentication
            SymmetricKey symmetricKey = new SymmetricKey(ka.generateSecret("DES"), 1024);
            this.authentication.init(symmetricKey);

            // Check authentification
            if (!this.authentication.verifyAuthenticate(password, this.query.getArg(1)))
                throw new Exception("Invalid password");

            parent.manageEvent("[ OK ] Thread server : client authenticated");
            SGDOCPRequest.quickSend(SGDOCPCommand.LOGIN_ACK, this.socketClient);

            // Add Runnable to task queue
            this.pool.execute(this.query.createRunnable(
                this.socketClient, this.parent));

            // Transfert socket urgent to thread urgence
            this.threadUrgence.addClientUrgentSocket(this.socketClientUrgence);
        }
        catch (Exception ex)
        {
            parent.manageEvent("[FAIL] Thread server : " + ex.getMessage());
            this.sendFailReply(ex.getMessage());
        }
        finally
        {
            this.socketClient = null;
            this.socketClientUrgence = null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public synchronized void requestStop() throws IOException, InterruptedException
    {
        this.isStopped = true;

        // Stop threads client in pool
        this.pool.requestStop();

        // Stop thread urgence
        this.threadUrgence.requestStop();
        this.threadUrgence.join();

        // Close socket client
        if (this.socketClient != null && this.socketClient.isConnected())
            this.socketClient.close();

        // Close client socket urgence
        if (this.socketClientUrgence != null && this.socketClientUrgence.isConnected())
            this.socketClientUrgence.close();

        // Close socket server
        if (this.socketServer != null)
            this.socketServer.close();

        // Close server socket urgence
        if (this.socketServerUrgence != null)
            this.socketServerUrgence.close();
    }

    public synchronized boolean isStopped()
    {
        return this.isStopped;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    // Network
    private final int port;
    private ServerSocket socketServer;
    private Socket socketClient;

    private ServerSocket socketServerUrgence;
    private Socket socketClientUrgence;

    private boolean isStopped;

    // Multithreading
    private ThreadPool pool;
    private ThreadUrgence threadUrgence;
    private LimitedBlockingTaskQueue limitedBlockingTaskQueue;

    // Protocol
    private SGDOCPRequest query;

    // Authentication
    private Properties usersProperties;
    private KeyPairGenerator keyGen;
    private Authentication authentication;

    // GUI
    private EventTracker parent;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String AUTHENTICATION_PROVIDER;

    static
    {
        AUTHENTICATION_PROVIDER = "HMACSHA1MawetProvider";
    }
    //</editor-fold>
}
