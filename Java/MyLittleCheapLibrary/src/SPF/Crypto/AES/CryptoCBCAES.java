package SPF.Crypto.AES;

import SPF.ByteArrayList;
import SPF.Cle;
import SPF.Crypto.Chiffrement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 *
 * @author nakim
 */

/* In this class, the "PaddedBufferedBlockCipher" object is used for encryption
 * and decryption. Best practice could be to use two PaddedBufferedBlockCipher,
 * the first one for encryption and the second one for decryption
 * In this case, the init method inherited from Chiffrement will initialise both
 * of them, one for encryption and the other one for decryption
 */

/* Because, the files that are going to be encrypted may have (most of the
 * times) a dimension that is NOT multiple of block size (128 bits), we must use
 * padding for the last block. In this case we will use the 
 * PaddedBufferedBlockCipher class, which is “a wrapper class that allows block 
 * ciphers to be used to process data in a piecemeal fashion with padding
 * [Bouncy Castle API documentation]. The default padding mechanism used is the
 * one outlined in PKCS5/PKCS7 standard.
 */
public class CryptoCBCAES extends org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
                          implements Chiffrement
{

    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public CryptoCBCAES(BlockCipher bc)
    {
        // Create a buffered block cipher with PKCS7 padding
        super(bc);
        
        this.opmode = -1;
        this.key = null;
        
        this.inbuf = new byte[blockSize];
        this.outbuf = new byte[512];
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void init(Cle k)
    {
        if (!(k instanceof CleAES))
            throw new IllegalArgumentException("Invalid key. CleAES expected");
        
        this.key = (CleAES) k;
        
        //create the IV parameter
        this.parameterIV = new ParametersWithIV(
            new KeyParameter(this.key.getSecretKey().getEncoded()), this.key.getIV());
        
        // Force la réinitialisation de l'objet lui même lors du prochain (de)cryptage
        this.opmode = -1;
    }

    @Override
    public Cle genererCle(int longueur)
    {
        if (longueur != 128 && longueur != 192 && longueur != 256)
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
            
            return new CleAES(secretKey, longueur);
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
        // Check if key exists
        if (this.key == null)
            throw new NullPointerException("No key object available");
        
        // Initialise the cipher.
        if (this.opmode != Cipher.ENCRYPT_MODE)
        {   
            this.init(true, this.parameterIV);
            this.opmode = Cipher.ENCRYPT_MODE;
        }
        
        int noBytesRead      = 0;   //number of bytes read from input
        int noBytesProcessed = 0;   //number of bytes processed
        
        InputStream is = new ByteArrayInputStream(plainText.getBytes());
        ByteArrayList processedBytes = new ByteArrayList();
        
        try
        {
            while ((noBytesRead = is.read(this.inbuf)) >= 0)
            {
                //System.out.println(noBytesRead + " bytes read");
                noBytesProcessed = this.processBytes(
                    this.inbuf, 0, noBytesRead, this.outbuf, 0);
                //System.out.println(noBytesProcessed + " bytes processed");
                processedBytes.add(outbuf, 0, noBytesProcessed);
            }
            
            //System.out.println(noBytesRead +" bytes read");
            noBytesProcessed = this.doFinal(this.outbuf, 0);
            //System.out.println(noBytesProcessed + " final bytes processed");
            processedBytes.add(outbuf, 0, noBytesProcessed);
            
            // Return string
            byte[] cipherTextByteArrayBase64 = Base64.getEncoder().encode(
                processedBytes.getArray());
            return new String(cipherTextByteArrayBase64);
        }
        catch (IOException | DataLengthException |
                IllegalStateException | InvalidCipherTextException ex)
        {
            System.err.println(ex);
        }
        
        return null;
    }

    @Override
    public String decrypte(String cipherText)
    {
        // Check if key exists
        if (this.key == null)
            throw new NullPointerException("No key object available");
        
        // Initialise the cipher.
        if (this.opmode != Cipher.DECRYPT_MODE)
        {
            this.init(false, this.parameterIV);
            this.opmode = Cipher.DECRYPT_MODE;
        }
        
        int noBytesRead      = 0;   //number of bytes read from input
        int noBytesProcessed = 0;   //number of bytes processed
        
        InputStream is = new ByteArrayInputStream(
            Base64.getDecoder().decode(cipherText.getBytes()));
        ByteArrayList processedBytes = new ByteArrayList();
        
        try
        {
            while ((noBytesRead = is.read(this.inbuf)) >= 0)            
            {
                //System.out.println(noBytesRead +" bytes read");
                noBytesProcessed = this.processBytes(this.inbuf, 0, noBytesRead, this.outbuf, 0);
                //System.out.println(noBytesProcessed +" bytes processed");
                processedBytes.add(this.outbuf, 0, noBytesProcessed);
            }
            
            //System.out.println(noBytesRead +" bytes read");
            noBytesProcessed = this.doFinal(this.outbuf, 0);
            //System.out.println(noBytesProcessed +" bytes processed");
            processedBytes.add(outbuf, 0, noBytesProcessed);
            
            return new String(processedBytes.getArray());
        }
        catch (IOException | DataLengthException |
                IllegalStateException | InvalidCipherTextException ex)
        {
            System.err.println(ex);
        }
        
        return null;
    }

    @Override
    public String getProvider()
    {
        return "CryptoCBCAESProvider";
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private int opmode;
    private CleAES key = null;
    
    // IV parameter
    private ParametersWithIV parameterIV = null;
    
    // Buffer used to transport the bytes from one stream to another
    private byte[] inbuf;               //input buffer
    private byte[] outbuf;             //output buffer
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables statiques ">
    
    // The default block size
    public  static final int blockSize;
    private static final String algorithm;
    private static final String provider;
    
    static
    {
        blockSize = 16;
        algorithm = "Rijndael";
        provider  = "BC";
    }
    
    // </editor-fold>
}
