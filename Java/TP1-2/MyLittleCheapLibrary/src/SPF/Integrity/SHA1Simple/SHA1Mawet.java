package SPF.Integrity.SHA1Simple;

import SPF.ByteArrayList;
import SPF.Integrity.Integrity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 *
 * @author nakim
 */
public class SHA1Mawet implements Integrity
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public SHA1Mawet()
    {
        try
        {
            this.messageDigest = MessageDigest.getInstance(algorithm, provider);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException ex)
        {
            System.err.println(ex);
            this.messageDigest = null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public byte[] makeCheck(String plainText)
    {
        this.reset();
        
        return this.messageDigest.digest(plainText.getBytes());
    }
    
    @Override
    public byte[] makeCheck(String plainText, ByteArrayList salt)
    {
        this.reset();
        
        if (salt != null && !salt.isEmpty())
            this.messageDigest.update(salt.getArray());
        
        return this.messageDigest.digest(plainText.getBytes());
    }

    @Override
    public boolean verifyCheck(String plainText, byte[] digest)
    {
        this.reset();
        
        return MessageDigest.isEqual(this.messageDigest.digest(
            plainText.getBytes()), digest);
    }
    
    @Override
    public boolean verifyCheck(String plainText, ByteArrayList salt, byte[] digest)
    {
        this.reset();
        
        if (salt != null && !salt.isEmpty())
            this.messageDigest.update(salt.getArray());
        
        return MessageDigest.isEqual(this.messageDigest.digest(
            plainText.getBytes()), digest);
    }

    @Override
    public String getProvider()
    {
        return "SHA1MawetProvider";
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Méthodes privées ">
    private void reset()
    {
        if (this.messageDigest == null)
            throw new NullPointerException("No messageDigest object available");
        
        // Resets the digest for further use
        this.messageDigest.reset();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables membres ">
    private MessageDigest messageDigest = null;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables statiques ">
    private static final String algorithm;
    private static final String provider;
    
    static
    {
        algorithm  = "SHA-1";
        provider   = "BC";
    }
    // </editor-fold>
}
