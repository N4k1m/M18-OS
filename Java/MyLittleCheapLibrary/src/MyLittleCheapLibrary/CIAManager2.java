package MyLittleCheapLibrary;

import SPF.Authentication.Authentication;
import SPF.Authentication.AuthenticationManager;
import SPF.Crypto.Chiffrement;
import SPF.Crypto.CryptoManager;
import SPF.Integrity.Integrity;
import SPF.Integrity.IntegrityManager;

/**
 *
 * @author nakim
 */
public class CIAManager2
{
    // <editor-fold defaultstate="collapsed" desc="Constructeur Privé">
    
    // Constructeur privé pour empêcher l'instantiation
    private CIAManager2()
    {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fonctions statiques">
    
    public static Chiffrement getChiffrement(String nomProvider)
    {
        return CryptoManager.getChiffrement(nomProvider);
    }
    
    public static Integrity getIntegrity(String providerName)
    {
        return IntegrityManager.getIntegrity(providerName);
    }
    
    public static Authentication getAuthentication(String providerName)
    {
        return AuthenticationManager.getAuthentication(providerName);
    }
    
    // </editor-fold>
}
