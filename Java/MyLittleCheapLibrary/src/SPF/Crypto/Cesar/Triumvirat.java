package SPF.Crypto.Cesar;

import SPF.Crypto.Chiffrement;
import SPF.Crypto.CryptoProvider;

/**
 *
 * @author nakim
 */
public class Triumvirat implements CryptoProvider
{
    // <editor-fold defaultstate="collapsed" desc="Constructeur">
    public Triumvirat() 
    {
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public Chiffrement newChiffrement() 
    {
        return new CryptoCaesar();
    }
    // </editor-fold>
}
