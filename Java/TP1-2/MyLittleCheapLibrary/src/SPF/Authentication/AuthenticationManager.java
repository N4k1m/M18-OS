/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SPF.Authentication;

import DB.BeanDBAccessMySQL;
import java.beans.Beans;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nakim
 */

/* Classe non instanciable régissant l'enregistrement 
 * des providers et les accès au service
 */
public class AuthenticationManager
{
    // <editor-fold defaultstate="collapsed" desc="Constructeur Privé">
    
    // Constructeur privé pour empêcher l'instantiation
    private AuthenticationManager()
    {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fonctions statiques">
    public static Authentication getAuthentication(String providerName)
    {
        AuthenticationProvider provider = providers.get(providerName);
        
        if (provider == null)
            throw new IllegalArgumentException("Provider " + providerName + " not found");
        
        return provider.newAuthentication();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Variables statiques et initialisation">
    // Association nom du provider - Provider implémenté
    private static final Map<String, AuthenticationProvider> providers;
    
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
            ResultSet rs = dbaMySQL.selectAll("authentication_provider");
            for (int i = 0; rs.next(); i++)
            {
                String providerName      = rs.getString("name");
                String providerClassName = rs.getString("class_name");
                
                // Instanciation du provider
                Class classProvider = Class.forName(providerClassName);
                AuthenticationProvider provider = (AuthenticationProvider) classProvider.newInstance();
                
                providers.put(providerName, provider);
            }
        }
        catch (IOException | ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException ex)
        {
            Logger.getLogger(AuthenticationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // </editor-fold>
}
