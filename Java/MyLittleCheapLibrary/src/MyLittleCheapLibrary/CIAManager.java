package MyLittleCheapLibrary;

import DB.BeanDBAccessMySQL;
import SPF.Authentication.Authentication;
import SPF.Authentication.AuthenticationProvider;
import SPF.Crypto.Chiffrement;
import SPF.Crypto.CryptoProvider;
import SPF.Integrity.Integrity;
import SPF.Integrity.IntegrityProvider;
import java.beans.Beans;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author nakim
 */
public class CIAManager
{
    // <editor-fold defaultstate="collapsed" desc="Constructeur Privé">
    
    // Constructeur privé pour empêcher l'instantiation
    private CIAManager()
    {
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fonctions statiques">
    public static Chiffrement getChiffrement(String providerName)
    {
        if (!cryptoProviders.containsKey(providerName))
        {
            ResultSet rs = dbaMySQL.selectAll(
                "crypto_provider", "name LIKE " + "\"" + providerName + "\"");
            
            try
            {
                while(rs.next())
                {
                    String provider_name = rs.getString("name");
                    String provider_class_name = rs.getString("class_name");
                    
                    // Instanciation du provider
                    Class classProvider = Class.forName(provider_class_name);
                    CryptoProvider provider = 
                        (CryptoProvider) classProvider.newInstance();
                    
                    cryptoProviders.put(provider_name, provider);
                }
            }
            catch (SQLException | ClassNotFoundException |
                    InstantiationException | IllegalAccessException ex)
            {
                System.err.println(ex);
            }
        }
        
        CryptoProvider provider = cryptoProviders.get(providerName);
        
        if (provider == null)
            throw new IllegalArgumentException("Provider " + providerName + " not found");
        
        return provider.newChiffrement();
    }
    
    public static Integrity getIntegrity(String providerName)
    {
        // If provider not loaded
        if (!integrityProviders.containsKey(providerName))
        {
            ResultSet rs = dbaMySQL.selectAll(
                "integrity_provider", "name LIKE " + "\"" + providerName + "\"");
            
            try
            {
                while(rs.next())
                {
                    String provider_name = rs.getString("name");
                    String provider_class_name = rs.getString("class_name");
                    
                    // Instanciation du provider
                    Class classProvider = Class.forName(provider_class_name);
                    IntegrityProvider provider = 
                        (IntegrityProvider) classProvider.newInstance();
                    
                    integrityProviders.put(provider_name, provider);
                }
            }
            catch (SQLException | ClassNotFoundException |
                    InstantiationException | IllegalAccessException ex)
            {
                System.err.println(ex);
            }
        }
        
        IntegrityProvider provider = integrityProviders.get(providerName);
        
        if (provider == null)
            throw new IllegalArgumentException("Provider " + providerName + " not found");
        
        return provider.newIntegrity();
    }
    
    public static Authentication getAuthentication(String providerName)
    {
        // If provider not loaded
        if(!authenticationProviders.containsKey(providerName))
        {
            ResultSet rs = dbaMySQL.selectAll(
                "authentication_provider", "name LIKE " + "\"" + providerName + "\"");
            
            try
            {
                while(rs.next())
                {
                    String provider_name = rs.getString("name");
                    String provider_class_name = rs.getString("class_name");
                    
                    // Instanciation du provider
                    Class classProvider = Class.forName(provider_class_name);
                    AuthenticationProvider provider = 
                        (AuthenticationProvider) classProvider.newInstance();
                    
                    authenticationProviders.put(provider_name, provider);
                }
            }
            catch (SQLException | ClassNotFoundException |
                    InstantiationException | IllegalAccessException ex)
            {
                System.err.println(ex);
            }
        }
        
        AuthenticationProvider provider = authenticationProviders.get(providerName);
        
        if (provider == null)
            throw new IllegalArgumentException("Provider " + providerName + " not found");
        
        return provider.newAuthentication();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Variables statiques et initialisation">
    private static Map<String, CryptoProvider> cryptoProviders;
    private static Map<String, IntegrityProvider> integrityProviders;
    private static Map<String, AuthenticationProvider> authenticationProviders;
    
    private static BeanDBAccessMySQL dbaMySQL;
    
    static
    {
        cryptoProviders = new ConcurrentHashMap<>();
        integrityProviders = new ConcurrentHashMap<>();
        authenticationProviders = new ConcurrentHashMap<>();
        
        try
        {
            dbaMySQL = (BeanDBAccessMySQL)Beans.instantiate(
                null, "DB.BeanDBAccessMySQL");
            dbaMySQL.init();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.err.println(ex);
        }
    }
    
    // </editor-fold>
}
