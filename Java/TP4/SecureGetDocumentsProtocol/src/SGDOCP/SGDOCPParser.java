package SGDOCP;

import Utils.BytesConverter;

/**
 *
 * @author Nakim
 */
public class SGDOCPParser
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public SGDOCPParser(SGDOCPRequest request)
    {
        // At lease 4 args are require
        // 1 : File name
        // 2 : Flag "Chiffrement" requested
        // 3 : Flag Authentication requested
        // 4 : Flag Integrity control requested
        if (request.getArgsCount() < 4)
        {
            this.command = SGDOCPCommand.NO_COMMAND;
            return;
        }

        // Get command
        this.command = request.getCommand();

        // If a network occured a NO_COMMAND request is created
        if (request.is(SGDOCPCommand.NO_COMMAND))
            return;

        int currnentIndexArg = 0;

        // Get file name
        this.fileName = request.getStringArg(currnentIndexArg);

        // Get flags
        boolean chiffrementRequested, authenticationRequested, integrityRequested;

        chiffrementRequested = (boolean)BytesConverter.fromByteArray(
            request.getArg(++currnentIndexArg));
        authenticationRequested = (boolean)BytesConverter.fromByteArray(
            request.getArg(++currnentIndexArg));
        integrityRequested = (boolean)BytesConverter.fromByteArray(
            request.getArg(++currnentIndexArg));

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
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Getters et Setters ">
    public SGDOCPCommand getCommand()
    {
        return this.command;
    }

    public void setCommand(SGDOCPCommand command)
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

    // <editor-fold defaultstate="collapsed" desc="Privates variables">
    // Requires
    private SGDOCPCommand command;
    private String fileName;

    // Optional
    private String providerChiffrement;
    private String cleChiffrement;

    private String providerAuthentication;
    private String cleAuthentication;

    private String providerIntegrity;
    // </editor-fold>
}
