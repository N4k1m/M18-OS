package SPF.Crypto.Alberti;

import SPF.Crypto.Chiffrement;
import SPF.Crypto.CryptoProvider;

/**
 *
 * @author nakim
 */
public class AlbertiFamily implements CryptoProvider
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public AlbertiFamily()    
    {
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overrided methods ">
    @Override
    public Chiffrement newChiffrement() 
    {
        return new ChiffrePoly();
    }
    // </editor-fold>
}
