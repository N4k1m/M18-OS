package SPF.Crypto.AES;

import SPF.Cle;
import java.security.SecureRandom;
import javax.crypto.SecretKey;

/**
 *
 * @author nakim
 */
public class CleAES implements Cle
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public CleAES(SecretKey secretKey, int keysize)
    {
        this.secretKey = secretKey;
        this.keysize = keysize;
        
        // Generate random byteq for the IV
        this.IV = new byte[blockSize];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(IV);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters et setters ">

    public SecretKey getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey)
    {
        this.secretKey = secretKey;
    }

    public int getKeysize()
    {
        return keysize;
    }

    public void setKeysize(int keysize)
    {
        this.keysize = keysize;
    }

    public byte[] getIV()
    {
        return IV;
    }

    public void setIV(byte[] IV)
    {
        this.IV = IV;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public int getLongueur()
    {
        return this.getKeysize();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables membres ">
    
    private SecretKey secretKey = null;
    private int keysize;
    
    // The initialization vector needed by the CBC mode
    private byte[] IV =  null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Varibles statiques ">
    
    // The default block size
    public static int blockSize = 16;
    
    private static final long serialVersionUID = 1L;
    
    // </editor-fold>
}
