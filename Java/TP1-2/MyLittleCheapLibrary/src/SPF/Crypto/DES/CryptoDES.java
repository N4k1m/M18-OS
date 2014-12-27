package SPF.Crypto.DES;

import SPF.Cle;
import SPF.Crypto.Chiffrement;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author nakim
 */
public class CryptoDES implements Chiffrement
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public CryptoDES()
    {
        try
        {
            // Get cipher Object
            this.cipher = Cipher.getInstance(
                algorithm + "/" + cipherMode + "/" + padding,
            provider);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException |
                NoSuchPaddingException ex)
        {
            System.err.println(ex);
            this.cipher = null;
        }
        finally
        {
            this.key    = null;
            this.opmode = -1;
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overrided methods ">
    @Override
    public void init(Cle k)
    {
        if (!(k instanceof CleDES))
            throw new IllegalArgumentException("Invalid key. CleDES expected");
        
        if(this.cipher == null)
            throw new NullPointerException("No cipher object available"); 
        
        this.key = (CleDES) k;
    }

    @Override
    public Cle genererCle(int longueur)
    {
        if (longueur < 0)
            throw new IllegalArgumentException("Invalid key size");
        
        try
        {
            // Get key generator
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm, provider);
            
            /* Initializes this key generator for a certain keysize (longueur) 
             * using a user-provided source of randomness (new SecureRandom())*/
            keyGenerator.init(longueur, new SecureRandom());
            
            // Get key
            SecretKey secretKey = keyGenerator.generateKey();
            
            return new CleDES(secretKey, longueur);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException ex)
        {
            System.err.println(ex);
        }
        
        return null;
    }

    @Override
    public String crypte(String plainText)
    {
        // Check if cipher and key exists
        if(this.cipher == null)
            throw new NullPointerException("No cipher object available");
        
        if(this.key == null)
            throw new NullPointerException("No key object available");
        
        try
        {
            if (this.opmode != Cipher.ENCRYPT_MODE)
            {
                this.opmode = Cipher.ENCRYPT_MODE;
                this.cipher.init(this.opmode, this.key.getSecretKey());
            }
            
            byte[] cipherTextByteArray = this.cipher.doFinal(plainText.getBytes());
            byte[] cipherTextByteArrayBase64 = Base64.getEncoder().encode(cipherTextByteArray);
            
            return new String(cipherTextByteArrayBase64);
        }
        catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex)
        {
            System.err.println(ex);
        }
        
        return null;
    }

    @Override
    public String decrypte(String cipherText)
    {
        // Check if cipher and key exists
        if(this.cipher == null)
            throw new NullPointerException("No cipher object available");
        
        if(this.key == null)
            throw new NullPointerException("No key object available");
        
        try
        {
            if (this.opmode != Cipher.DECRYPT_MODE)
            {
                this.opmode = Cipher.DECRYPT_MODE;
                this.cipher.init(this.opmode, this.key.getSecretKey());
            }
            
            byte[] encryptedByteArray = Base64.getDecoder().decode(cipherText.getBytes());
            byte[] decodedByte = this.cipher.doFinal(encryptedByteArray);
            
            return new String(decodedByte);
        }
        catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex)
        {
            System.err.println(ex);
        }
        
        return null;
    }

    @Override
    public String getProvider()
    {
        return "ProCrypto";
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables membres ">
    
    private Cipher cipher;
    private CleDES key = null;
    private int opmode;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables statiques ">
    
    private static final String algorithm;
    private static final String cipherMode;
    private static final String padding;
    private static final String provider;
    
    static
    {
        algorithm  = "DES";
        cipherMode = "ECB";
        padding    = "PKCS5Padding";
        provider   = "BC";
    }
    
    // </editor-fold>
}
