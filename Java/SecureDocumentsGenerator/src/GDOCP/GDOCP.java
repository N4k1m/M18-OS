package GDOCP;

import Utils.BytesConverter;
import Utils.Request;

/**
 *
 * @author nakim
 */
public class GDOCP
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    
    public GDOCP(Request request)
    {
        // At lease 4 args are require
        if (request.getArgsCount() < 4)
        {
            this.command = "NO_COMMAND";
            return;
        }
        
        // Get command
        this.command = request.getCommand();
        
        if (this.command.equalsIgnoreCase("NO_COMMAND"))
            return;
        
        // Get file name
        this.fileName = request.getStringArg(0);
        
        // Get flags
        boolean chiffrementRequested, authenticationRequested, integrityRequested;
        
        chiffrementRequested = (boolean)BytesConverter.fromByteArray(
            request.getArg(1));
        authenticationRequested = (boolean) BytesConverter.fromByteArray(
            request.getArg(2));
        integrityRequested = (boolean)BytesConverter.fromByteArray(
            request.getArg(3));
        
        int currnentIndexArg = 3;
        
        if (chiffrementRequested)
        {
            this.providerChiffrement = request.getStringArg(++currnentIndexArg);
            this.cleChiffrement = request.getStringArg(++currnentIndexArg);
        }
        
        if (authenticationRequested)
        {
            this.providerAuthentication = request.getStringArg(++currnentIndexArg);
            this.cleAuthentication = request.getStringArg(++currnentIndexArg);
        }
        
        if (integrityRequested)
        {
            this.providerIntegrity = request.getStringArg(++currnentIndexArg);
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters et Setters ">

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public boolean isChiffrementRequested()
    {
        return this.providerChiffrement!= null && !this.providerChiffrement.isEmpty()
            && this.cleChiffrement != null && !this.cleChiffrement.isEmpty();
    }

    public String getProviderChiffrement()
    {
        return providerChiffrement;
    }

    public void setProviderChiffrement(String providerChiffrement)
    {
        this.providerChiffrement = providerChiffrement;
    }

    public String getCleChiffrement()
    {
        return cleChiffrement;
    }

    public void setCleChiffrement(String cleChiffrement)
    {
        this.cleChiffrement = cleChiffrement;
    }
    
    public boolean isAuthenticationRequested()
    {
        return this.providerAuthentication != null && !this.providerAuthentication.isEmpty()
            && this.cleAuthentication != null && !this.cleAuthentication.isEmpty();
    }

    public String getProviderAuthentication()
    {
        return providerAuthentication;
    }

    public void setProviderAuthentication(String providerAuthentication)
    {
        this.providerAuthentication = providerAuthentication;
    }

    public String getCleAuthentication()
    {
        return cleAuthentication;
    }

    public void setCleAuthentication(String cleAuthentication)
    {
        this.cleAuthentication = cleAuthentication;
    }
    
    public boolean isIntegrityRequested()
    {
        return this.providerIntegrity != null && !this.providerIntegrity.isEmpty();
    }

    public String getProviderIntegrity()
    {
        return providerIntegrity;
    }

    public void setProviderIntegrity(String providerIntegrity)
    {
        this.providerIntegrity = providerIntegrity;
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Variables membres ">
    
    // Requires
    private String command;
    private String fileName;
    
    // Optional
    private String providerChiffrement;
    private String cleChiffrement;
    
    private String providerAuthentication;
    private String cleAuthentication;
    
    private String providerIntegrity;
    
    // </editor-fold>
}
