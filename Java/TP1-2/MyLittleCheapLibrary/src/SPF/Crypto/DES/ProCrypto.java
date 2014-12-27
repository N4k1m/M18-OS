package SPF.Crypto.DES;

import SPF.Crypto.Chiffrement;
import SPF.Crypto.CryptoProvider;

/**
 *
 * @author nakim
 */
public class ProCrypto implements CryptoProvider
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public ProCrypto()
    {
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public Chiffrement newChiffrement()
    {
        return new CryptoDES();
    }
    // </editor-fold>
}
