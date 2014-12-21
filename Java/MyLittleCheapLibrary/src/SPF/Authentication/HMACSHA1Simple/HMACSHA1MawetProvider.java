package SPF.Authentication.HMACSHA1Simple;

import SPF.Authentication.Authentication;
import SPF.Authentication.AuthenticationProvider;

/**
 *
 * @author nakim
 */
public class HMACSHA1MawetProvider implements AuthenticationProvider
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public HMACSHA1MawetProvider()
    {
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overrided methods ">
    @Override
    public Authentication newAuthentication()
    {
        return new HMACSHA1Mawet();
    }
    // </editor-fold>
}
