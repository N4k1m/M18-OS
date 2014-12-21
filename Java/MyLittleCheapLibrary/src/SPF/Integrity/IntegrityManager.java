package SPF.Integrity;

import DB.BeanDBAccessMySQL;
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

/* Classe non instanciable régissant l'enregistrement 
 * des providers et les accès au service
 */
public class IntegrityManager
{
    // <editor-fold defaultstate="collapsed" desc="Constructeur Privé">
    
    // Constructeur privé pour empêcher l'instantiation
    private IntegrityManager()
    {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fonctions statiques">
    public static Integrity getIntegrity(String providerName)
    {
        IntegrityProvider provider = providers.get(providerName);
        
        if (provider == null)
            throw new IllegalArgumentException("Provider " + providerName + " not found");
        
        return provider.newIntegrity();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Variables statiques et initialisation">
    // Association nom du provider - Provider implémenté
    private static final Map<String, IntegrityProvider> providers;
    
    static
    {
        providers = new ConcurrentHashMap<>();
        
        try
        {
            // Initialisation du Bean pour accéder à la base de données
            BeanDBAccessMySQL dbaMySQL;
            dbaMySQL = (BeanDBAccessMySQL)Beans.instantiate(
                null, "DB.BeanDBAccessMySQL");
            dbaMySQL.init();
            
            // Récupération de toutes les données sur les providers
            ResultSet rs = dbaMySQL.selectAll("integrity_provider");
            for (int i = 0; rs.next(); i++)
            {
                String providerName      = rs.getString("name");
                String providerClassName = rs.getString("class_name");
                
                // Instanciation du provider
                Class classProvider = Class.forName(providerClassName);
                IntegrityProvider provider = (IntegrityProvider) classProvider.newInstance();
                
                providers.put(providerName, provider);
            }
        }
        catch (IOException | ClassNotFoundException | SQLException |
                InstantiationException | IllegalAccessException ex)
        {
            System.err.println(ex);
        }
    }
    
    // </editor-fold>
}
