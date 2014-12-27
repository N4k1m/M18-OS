package SPF.Crypto.AES;

import SPF.Crypto.Chiffrement;
import SPF.Crypto.CryptoProvider;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;

/**
 *
 * @author nakim
 */
public class CryptoCBCAESProvider implements CryptoProvider
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public CryptoCBCAESProvider()    
    {
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overrided methods ">
    @Override
    public Chiffrement newChiffrement()
    {
        /* The middle performance version uses only one 256 word table for each,
         * for a total of 2Kbytes, adding 12 rotate operations per round to
         * compute the values contained in the other tables from the contents
         * of the first. [Bouncy Castle API documentation]
         * AESEngine, that processes 128 bits (16 bytes) blocks 
         * and will use a 128, 192 or 256 bit key.
         */
        BlockCipher engine = new AESEngine();
        
        return new CryptoCBCAES(new CBCBlockCipher(engine));
    }
    // </editor-fold>
}
