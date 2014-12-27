package Threads;

import GDOCP.GDOCP;
import GUI.MainFrame;
import MyLittleCheapLibrary.CIAManager;
import SPF.Authentication.Authentication;
import SPF.Cle;
import SPF.Crypto.Chiffrement;
import SPF.Integrity.Integrity;
import Utils.Request;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author nakim
 */
public class ThServer extends Thread
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public ThServer(MainFrame parent, int port)
    {
        this.port_server   = port;
        this.stopRequested = false;
        this.socketServer  = null;
        this.parent        = parent;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overrided Methods ">
    @Override
    public void run()
    {
        try
        {
            this.socketServer = new ServerSocket(this.port_server);
            System.out.println("[ OK ] Serveur demarre sur le port " + this.port_server);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            System.exit(1);
        }
        
        // Boucle principale
        while (!stopRequested)
        {
            try
            {
                System.out.println("[ OK ] Serveur en attente de connexion");
                this.socketClient = this.socketServer.accept();
                this.clientStop = false;
                System.out.println("[ OK ] Connexion d'un client");
                
                while(!clientStop)
                {
                    // Réception de la requete
                    GDOCP query = new GDOCP(Request.recv(socketClient));
                    System.out.println("[ RQ ] Requete recue : " + query.getCommand());
                    
                    Request reply = new Request();
                    
                    // Requete de demande de document
                    if(query.getCommand().compareToIgnoreCase("GET_DOCUMENT") == 0)
                    {
                        System.out.println("[ RQ ] Demande du document : " + query.getFileName());
                        
                        // Vérifie si le docuement demandé existe
                        if (!isFileExists(textsFolderPath + query.getFileName()))
                        {
                            reply.setCommand("GET_DOCUMENT_FAIL");
                            reply.addArg("Document " + query.getFileName() + " inexistant");
                            reply.send(socketClient);
                            continue;
                        }
                        
                        // Vérifie si la clé de chiffrement existe
                        if (query.isChiffrementRequested())
                        {
                            System.out.println(
                                "[ RQ ] Demande de chiffrement (cle = " 
                                + query.getCleChiffrement() + ", Provider = " 
                                + query.getProviderChiffrement() + ")");
                            
                            // Si la clé de chiffrement n'existe pas
                            if (!isKeyExists(query.getCleChiffrement()))
                            {
                                System.out.println("[ RQ ] La cle " + query.getCleChiffrement() + " est inexistante");
                                
                                reply.setCommand("GET_DOCUMENT_FAIL");
                                reply.addArg("Cle de chiffrement " + query.getCleChiffrement() + " inexistante");
                                reply.send(socketClient);
                                continue;
                            }
                        }
                        
                        // Vérifie si la clé d'authentification existe
                        if (query.isAuthenticationRequested())
                        {
                            System.out.println(
                                "[ RQ ] Demande d'authentification (cle = " 
                                + query.getCleAuthentication() + ", Provider = " 
                                + query.getProviderAuthentication() + ")");
                            
                            // Si la clé d'authentification n'existe pas
                            if (!isKeyExists(query.getCleAuthentication()))
                            {
                                System.out.println("[ RQ ] La cle " + query.getCleAuthentication() + " est inexistante");
                                
                                reply.setCommand("GET_DOCUMENT_FAIL");
                                reply.addArg("Cle d'authentification " + query.getCleAuthentication() + " inexistante");
                                reply.send(socketClient);
                                continue;
                            }
                        }
                        
                        if (query.isIntegrityRequested())
                            System.out.println("[ RQ ] Demande de controle d'integrite (Provider = " + query.getProviderIntegrity() + ")");
                        
                        // Construction de la réponse
                        reply.setCommand("GET_DOCUMENT_ACK");
                        reply.clearArgs();
                        
                        // Récupérer le text
                        String content = this.getFileContent(textsFolderPath + query.getFileName());
                        
                        // Ajout du text clair/chiffré selon la demande
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
                        
                        // Ajout de l'authentification
                        if (query.isAuthenticationRequested())
                        {
                            this.authentication = CIAManager.getAuthentication(
                                query.getProviderAuthentication());
                            this.getCleFromFile(query.getCleAuthentication());
                            this.authentication.init(this.cle);
                            
                            reply.addArg(this.authentication.makeAuthenticate(content));
                        }
                        
                        // Ajout de l'intégrité
                        if (query.isIntegrityRequested())
                        {
                            this.integrity = CIAManager.getIntegrity(
                                query.getProviderIntegrity());
                            
                            reply.addArg(this.integrity.makeCheck(content));
                        }
                        
                        // Envoyer requete
                        reply.send(socketClient);
                    }
                    else if (query.getCommand().compareToIgnoreCase("NO_COMMAND") == 0)
                    {
                        this.clientStop = true;
                    }
                }
                
                System.out.println("[ OK ] Fin de connexion avec le client");
                this.socketClient.close();
                this.socketClient = null;
            }
            catch (IOException ex)
            {
                System.out.println("[ OK ] Interruption recue");
            }
        }
        
        // Free resources
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" méthodes privées ">
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
            return "";
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
    
    // </editor-fold>
    
    public synchronized void requestStop() throws IOException
    {
        this.stopRequested = true;
        
        if (this.socketClient != null && this.socketClient.isConnected())
            this.socketClient.close();
        
        this.socketServer.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Variables privées ">
    private final int port_server;
    private boolean stopRequested;
    private boolean clientStop;
    
    private ServerSocket socketServer;
    private Socket socketClient;
    
    private Chiffrement chiffrement;
    private Cle cle;
    private Authentication authentication;
    private Integrity integrity;
    
    private final MainFrame parent;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables statiques ">
    private static final String keysFolderPath;
    private static final String textsFolderPath;
    
    static
    {
        keysFolderPath = "KEYS" + System.getProperty("file.separator");
        textsFolderPath = "PLAIN" + System.getProperty("file.separator");
    }
    // </editor-fold>
}
