package SPF.Crypto.Cesar;

import SPF.Cle;
import SPF.Crypto.Chiffrement;
import java.util.Random;

/**
 *
 * @author nakim
 */
public class CryptoCaesar implements Chiffrement
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public CryptoCaesar()    
    {
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overrided methods ">
    @Override
    public void init(Cle k)
    {
        if (!(k instanceof CleCesar))
            throw new IllegalArgumentException("Cle invalide");
        
        CleCesar cle;
        cle = (CleCesar) k;
        
        this.key = cle;
    }
    
    @Override
    public Cle genererCle(int longueur) 
    {
        Random random = new Random();
        int decalage = random.nextInt((longueur * 10) + 1 ) % 127;
        
        if (decalage == 0)
            ++decalage;
        
        return new CleCesar(decalage);
    }

    @Override
    public String crypte(String plainText) 
    {        
        int decalage = this.key.getDecalage();
        
        char cipherText[] = new char[plainText.length()];
        plainText.getChars(0, plainText.length(), cipherText, 0);
        
        for(int i = 0; i < plainText.length(); ++i)
            cipherText[i] += decalage;
        
        return new String(cipherText);
    }

    @Override
    public String decrypte(String cipherText) 
    {
        int decalage = this.key.getDecalage();
        
        char plainText[] = new char[cipherText.length()];
        cipherText.getChars(0, cipherText.length(), plainText, 0);
        
        for(int i = 0; i < cipherText.length(); ++i)
            plainText[i] -= decalage;
        
        return new String(plainText);
    }

    @Override
    public String getProvider() 
    {
        return "Triumvirat";
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables membres ">
    
    // Clé utilisée pour chiffrer et/ou déchiffrer
    private CleCesar key = null;
    
    // </editor-fold>
}
