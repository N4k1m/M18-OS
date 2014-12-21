package SPF.Integrity.SHA1Simple;

import SPF.Integrity.Integrity;
import SPF.Integrity.IntegrityProvider;

/**
 *
 * @author nakim
 */
public class SHA1MawetProvider implements IntegrityProvider
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">

    public SHA1MawetProvider()
    {
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public Integrity newIntegrity()
    {
        return new SHA1Mawet();
    }
    // </editor-fold>
}
