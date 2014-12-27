package SPF.Crypto.DES;

import SPF.Cle;
import javax.crypto.SecretKey;

/**
 *
 * @author nakim
 */
public class CleDES implements Cle
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public CleDES(SecretKey secretKey, int keySize)    
    {
        this.secretKey = secretKey;
        this.keysize = keySize;
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
    private int keysize = -1;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Varibles statiques ">
    
    private static final long serialVersionUID = 1L;
    
    // </editor-fold>
}
