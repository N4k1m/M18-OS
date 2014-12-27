package SPF.Authentication;

import SPF.ByteArrayList;
import SPF.Cle;

/**
 *
 * @author nakim
 */

// Interface de service
public interface Authentication
{
    void init(Cle k);
    Cle generateKey(ByteArrayList secret, String algorithm);
    byte[] makeAuthenticate(String plainText);
    boolean verifyAuthenticate(String plainText, byte[] hmac);
    String getProvider();
}
