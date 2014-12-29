package SGDOCP;

import GMC.EventTracker;
import MyLittleCheapLibrary.CIAManager;
import SPF.Authentication.Authentication;
import SPF.Cle;
import SPF.Crypto.Chiffrement;
import SPF.Integrity.Integrity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Nakim
 */
public class SGDOCPWorker implements Runnable
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public SGDOCPWorker(Socket clientSocket, EventTracker eventTracker)
    {
        this.clientSocket = clientSocket;
        this.eventTracker = eventTracker;
        this.clientStop   = false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private boolean isKeyExists(String keyFilename)
    {
        return this.isFileExists(keysFolderPath + keyFilename);
    }

    private boolean isFileExists(String filname)
    {
        return new File(filname).exists();
    }

    private String getFileContent(String filepath)
    {
        try
        {
            return new String(Files.readAllBytes(Paths.get(filepath)));
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            return null;
        }
    }

    private void getCleFromFile(String keyname)
    {
        try
        {
            final FileInputStream fis = new FileInputStream(keysFolderPath + keyname);
            ObjectInputStream ois = new ObjectInputStream(fis);

            try
            {
                this.cle = (Cle) ois.readObject();
            }
            finally
            {
                // on ferme les flux
                try
                {
                    ois.close();
                }
                finally
                {
                    fis.close();
                }
            }
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.err.println(ex);
            this.cle = null;
        }
    }

    public void sendFailReply(String cause)
    {
        SGDOCPRequest.quickSend(SGDOCPCommand.FAIL, cause, this.clientSocket);
    }

    private void manage_GET_DOCULENT()
    {
        SGDOCPRequest reply = new SGDOCPRequest();

        try
        {
            eventTracker.manageEvent("[ RQ ] Requested document : "
                + query.getFileName());

            // Check if file exists
            if (!this.isFileExists(textsFolderPath + query.getFileName()))
                throw new Exception("Document " + query.getFileName() + " not found");

            // Check if "Chiffrement" key exists
            if (query.isChiffrementRequested())
            {
                eventTracker.manageEvent(
                    "[ RQ ] Encryption requested (key = "
                     + query.getCleChiffrement() + ", Provider = "
                     + query.getProviderChiffrement() + ")");

                if (!this.isKeyExists(query.getCleChiffrement()))
                    throw new Exception("Cipher key " + query.getCleChiffrement() + " not found");
            }

            // Check if Authentication key exists
            if (query.isAuthenticationRequested())
            {
                eventTracker.manageEvent(
                    "[ RQ ] Authentication requested (key = "
                    + query.getCleAuthentication() + ", Provider = "
                    + query.getProviderAuthentication() + ")");

                if (!this.isKeyExists(query.getCleAuthentication()))
                    throw new Exception("Authentication key " + query.getCleAuthentication() + " not found");
            }

            // Check if integrity is requested
            if (query.isIntegrityRequested())
                eventTracker.manageEvent(
                    "[ RQ ] Integrity checking requested (Provider = "
                    + query.getProviderIntegrity() + ")");

            // Get file content
            String content = this.getFileContent(textsFolderPath + query.getFileName());
            if (content == null)
                throw new Exception("Enable to get file content");

            // Add plain or encrypted file content
            if (query.isChiffrementRequested())
            {
                this.chiffrement = CIAManager.getChiffrement(
                    query.getProviderChiffrement());
                this.getCleFromFile(query.getCleChiffrement());
                this.chiffrement.init(this.cle);

                reply.addArg(this.chiffrement.crypte(content));
            }
            else
                reply.addArg(content);

            // Add Authentication
            if (query.isAuthenticationRequested())
            {
                this.authentication = CIAManager.getAuthentication(
                    query.getProviderAuthentication());
                this.getCleFromFile(query.getCleAuthentication());
                this.authentication.init(this.cle);

                reply.addArg(this.authentication.makeAuthenticate(content));
            }

            // Add Integrity control
            if (query.isIntegrityRequested())
            {
                this.integrity = CIAManager.getIntegrity(
                    query.getProviderIntegrity());

                reply.addArg(this.integrity.makeCheck(content));
            }

            reply.setCommand(SGDOCPCommand.GET_DOCUMENT_ACK);
        }
        catch (Exception ex)
        {
            eventTracker.manageEvent("[FAIL] " + ex.getMessage());

            // Send fail reply
            reply.setCommand(SGDOCPCommand.FAIL);
            reply.clearArgs(); // May be some arguments have already been added
            reply.addArg(ex.getMessage());
        }
        finally
        {
            reply.send(this.clientSocket);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        while(!clientStop)
        {
            // Get query
            query = new SGDOCPParser(SGDOCPRequest.recv(clientSocket));

            switch(query.getCommand())
            {
                case SOCK_ERROR:
                case NO_COMMAND:
                    eventTracker.manageEvent("[ OK ] connection with the client closed");
                    this.clientStop = true;
                case GET_DOCUMENT:
                    // TODO manage get document
                    this.manage_GET_DOCULENT();
                    break;
                default:
                    eventTracker.manageEvent("[FAIL] Invalid query");
                    this.sendFailReply("Invalid query");
                    this.clientStop = true;
                    break;
            }
        }

        // Disconnect client
        try
        {
            this.clientSocket.close();
            eventTracker.manageEvent("Client disconnected");
        }
        catch (IOException ex)
        {
            eventTracker.manageEvent("Fail to disconnect client");
        }
        finally
        {
            this.clientSocket = null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private Socket clientSocket;
    private EventTracker eventTracker;
    private boolean clientStop;

    SGDOCPParser query;

    private Chiffrement chiffrement;
    private Cle cle;
    private Authentication authentication;
    private Integrity integrity;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String keysFolderPath;
    private static final String textsFolderPath;

    static
    {
        keysFolderPath = "KEYS" + System.getProperty("file.separator");
        textsFolderPath = "PLAIN" + System.getProperty("file.separator");
    }
    // </editor-fold>
}
