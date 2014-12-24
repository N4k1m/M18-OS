package Threads;

import Utils.PropertyLoader;
import Utils.Request;
import Utils.ReturnValue;
import Utils.SymmetricCrypter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

/**
 *
 * @author Nakim
 */
public class ThreadServerV2 extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadServerV2(int port)
    {
        this.port_server = port;
        this.stopRequested = false;
        this.socketServer = null;
        this.socketClient = null;
        this.rand = new Random();

        this.openBible();
        this.openIterpreter();
        this.createCiphers();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        try
        {
            this.socketServer = new ServerSocket(this.port_server);
            System.out.println("[ V2 ] Server started on port " + this.port_server);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            System.exit(ReturnValue.FAILURE.getReturnCode());
        }

        // Main loop
        while(!this.stopRequested)
        {
            try
            {
                this.clientStop = false;
                this.symmetricCrypter.invalidate();
                System.out.println("[ V2 ] Waiting client");
                this.socketClient = this.socketServer.accept();
                System.out.println("[ V2 ] New client connected");
            }
            catch (IOException ex)
            {
                System.out.println("[ V2 ] Interrupted. Stop waiting client");
                continue;
            }

            while (!this.clientStop)
            {
                this.query = Request.recv(this.socketClient);
                System.out.println("[ V2 ] Query received : " +
                    this.query.getCommand());

                switch(this.query.getCommand())
                {
                    case Request.SOCK_ERROR:
                    case Request.NO_COMMAND:
                        this.clientStop = true;
                        System.out.println("[ V2 ] Interrupted. Stop receiving message");
                        break;
                    case "MESSAGE":
                        this.manageMessage();
                        break;
                    case "GENERATE_KEY":
                        this.generateSecretKey();
                        break;
                    default:
                        System.out.println("[ V2 ] Invalid query : "
                            + this.query.getCommand());
                        Request.quickSend(this.query.getCommand() + "_FAIL",
                                          "Invalid query",
                                          this.socketClient);
                        break;
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void openBible()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                + "src"
                + System.getProperty("file.separator")
                + "GUI"
                + System.getProperty("file.separator")
                + "bible.properties";

            this.bibleProperties = PropertyLoader.load(path);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void openIterpreter()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "GUI"
                    + System.getProperty("file.separator")
                    + "interpreter.properties";

            this.interpreterProperties = PropertyLoader.load(path);

            this.codedSentences = new ArrayList<>();
            this.interpreterProperties.keySet().stream().forEach((sentence) ->
            {
                this.codedSentences.add((String)sentence);
            });

        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void createCiphers()
    {
        try
        {
            this.symmetricCrypter = new SymmetricCrypter(
                algorithm + "/" + cipherMode + "/" + padding);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException ex)
        {
            this.symmetricCrypter = null;
            System.out.println("[FAIL] Unable to create ciphers : " + ex.getMessage());
        }
    }

     private void manageMessage()
    {
        System.out.println("[ V2 ] Encrypted message received");

        try
        {
            if (this.symmetricCrypter == null)
                throw new Exception("No cipher objects available");

            if (!this.symmetricCrypter.isValid())
                throw new Exception("A new secret key must be generated");

            String codedMessage = this.symmetricCrypter.decryptString(this.query.getArg(0));
            System.out.println("[ V2 ] Decrypted message : " + codedMessage);

            System.out.println("[ V2 ] Real message is : " +
                this.interpreterProperties.getProperty(codedMessage, "UNKNOW"));

            // Reply : logic should be here instead of random
            int random = this.rand.nextInt(this.codedSentences.size());
            codedMessage = this.codedSentences.get(random);

            // Build reply query
            Request reply = new Request("MESSAGE_ACK");
            reply.addArg(this.symmetricCrypter.encrypt(codedMessage));
            reply.send(this.socketClient);

            System.out.println("[ V2 ] Encrypted message sent (reply)");
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] " + ex.getMessage());
            Request.quickSend("MESSAGE_FAIL", ex.getMessage(), this.socketClient);
        }
    }

     private void generateSecretKey()
    {
        System.out.println("[ V2 ] Generate new secret key");

        try
        {
            if (this.symmetricCrypter == null)
                throw new Exception("No cipher objects available");
/*
            // Create the parameter generator for a 1024-bit DH key pair
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);

            System.out.println("[ V2 ] Generating params");

            // Generate the parameters
            AlgorithmParameters params = paramGen.generateParameters();
            //Specify parameters to use for the algorithm
            DHParameterSpec dhSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

            System.out.println("[ V2 ] Params generated");
*/

            // Get public key from client
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
                this.query.getArg(0));
            KeyFactory keyFact = KeyFactory.getInstance("DH");
            PublicKey publicKey = keyFact.generatePublic(x509KeySpec);

            /* Gets the DH parameters associated with the client public key.
             * The server must use the same parameters when he generates his
             * own key pair.
             */
            DHParameterSpec dhSpec = ((DHPublicKey)publicKey).getParams();

            // Creates his own DH key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            keyGen.initialize(dhSpec);

            KeyPair keypair = keyGen.generateKeyPair();

            /* Prepare to generate the secret key with the private key and
             * public key of the server
             */
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(keypair.getPrivate());
            ka.doPhase(publicKey, true);

            // Generate new secret key and initialize crypter
            this.symmetricCrypter.init(ka.generateSecret("DES"));

            Request reply = new Request("GENERATE_KEY_ACK");
            reply.addArg(keypair.getPublic().getEncoded());
            reply.send(this.socketClient);

            System.out.println("[ V2 ] New secret key generated");
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.out.println("[ V2 ] " + e.getMessage());
            Request.quickSend("GENERATE_KEY_FAIL", e.getMessage(), this.socketClient);
        }
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
    private final Random rand;

    private List<String> codedSentences;
    private Properties interpreterProperties;
    private Properties bibleProperties;

    private SymmetricCrypter symmetricCrypter;

    private Request query;

    private ServerSocket socketServer;
    private Socket socketClient;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String algorithm;
    private static final String cipherMode;
    private static final String padding;

    static
    {
        algorithm  = "DES";
        cipherMode = "ECB";
        padding    = "PKCS5Padding";
    }
    // </editor-fold>
}
