package SPF.Crypto;

/**
 *
 * @author nakim
 */

// Interface provider de service (Chiffrement)
public interface CryptoProvider 
{
    Chiffrement newChiffrement();
}
