package SPF.Authentication.HMACSHA1Simple;

import SPF.Authentication.Authentication;
import SPF.Authentication.SymmetricKey;
import SPF.ByteArrayList;
import SPF.Cle;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author nakim
 */
public class HMACSHA1Mawet implements Authentication
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public HMACSHA1Mawet()
    {
        try
        {
            this.hmac = Mac.getInstance(algorithm, provider);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException ex)
        {
            System.err.println(ex);
            this.hmac = null;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Overrided methods ">
    @Override
    public void init(Cle k)
    {
        if (!(k instanceof SymmetricKey))
            throw new IllegalArgumentException("Invalid Key. SymmetricKey expected");
        
        if (this.hmac == null)
            throw new NullPointerException("No Mac available");
        
        SymmetricKey symmetricKey;
        symmetricKey = (SymmetricKey)k;
        
        try
        {
            this.hmac.init(symmetricKey.getSecretKey());
        }
        catch (InvalidKeyException ex)
        {
            System.err.println(ex);
        }
    }

    @Override
    public Cle generateKey(ByteArrayList secret, String algorithm)
    {
        SecretKey secretKey = new SecretKeySpec(secret.getArray(), algorithm);

        return new SymmetricKey(secretKey, secret.size());
    }

    @Override
    public byte[] makeAuthenticate(String plainText)
    {
        this.reset();
        
        return this.hmac.doFinal(plainText.getBytes());
    }

    @Override
    public boolean verifyAuthenticate(String plainText, byte[] hmac)
    {
        this.reset();
        
        return MessageDigest.isEqual(this.hmac.doFinal(
            plainText.getBytes()), hmac);
    }

    @Override
    public String getProvider()
    {
        return "HMACSHA1MawetProvider";
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Méthodes privées ">
    private void reset()
    {
        if (this.hmac == null)
            throw new NullPointerException("No HMAC object available");
        
        // Resets the Mac object for further use
        this.hmac.reset();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables membres ">
    private Mac hmac = null;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables statiques ">
    private static final String algorithm;
    private static final String provider;
    
    static
    {
        algorithm = "HMAC-SHA1";
        provider  = "BC";
    }
    // </editor-fold>
}
