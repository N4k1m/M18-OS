package Main;

import MyLittleCheapLibrary.CIAManager;
import SPF.Authentication.Authentication;
import SPF.ByteArrayList;
import SPF.Cle;
import SPF.Crypto.Chiffrement;
import SPF.Integrity.Integrity;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Date;

/**
 *
 * @author nakim
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("-------------------------------------------------");
        System.out.println("|              Service Chiffrement              |");
        System.out.println("-------------------------------------------------");
        System.out.println("");
        
        /* Alberti */
        System.out.println("---------------------Alberti---------------------");
        
        //Chiffrement chiffrement = CryptoManager.getChiffrement("AlbertiFamily");
        //Chiffrement chiffrement = CIAManager2.getChiffrement("AlbertiFamily");
        Chiffrement chiffrement = CIAManager.getChiffrement("AlbertiFamily");
        Cle cle = chiffrement.genererCle(5);
        
        chiffrement.init(cle);
        String msg = "Hello World";
        System.out.println("Message clair    : " + msg);
        msg = chiffrement.crypte(msg);
        System.out.println("Message crypté   : " + msg);
        msg = chiffrement.decrypte(msg);
        System.out.println("Message décrypté : " + msg);
        
        /* Cesar */
        System.out.println("----------------------Cesar----------------------");
        
        //chiffrement = CryptoManager.getChiffrement("Triumvirat");
        //chiffrement = CIAManager2.getChiffrement("Triumvirat");
        chiffrement = CIAManager.getChiffrement("Triumvirat");
        cle = chiffrement.genererCle(3);
        
        chiffrement.init(cle);
        msg = "Hello World";
        System.out.println("Message clair    : " + msg);
        msg = chiffrement.crypte(msg);
        System.out.println("Message crypté   : " + msg);
        msg = chiffrement.decrypte(msg);
        System.out.println("Message décrypté : " + msg);
        
        /* DES */
        System.out.println("-----------------------DES-----------------------");
        
        //chiffrement = CryptoManager.getChiffrement("ProCrypto");
        //chiffrement = CIAManager2.getChiffrement("ProCrypto");
        chiffrement = CIAManager.getChiffrement("ProCrypto");
        cle = chiffrement.genererCle(64);
        
        chiffrement.init(cle);
        msg = "Hello World";
        System.out.println("Message clair    : " + msg);
        msg = chiffrement.crypte(msg);
        System.out.println("Message crypté   : " + msg);
        msg = chiffrement.decrypte(msg);
        System.out.println("Message décrypté : " + msg);
        
        /* AES */
        System.out.println("-----------------------AES-----------------------");
        
        chiffrement = CIAManager.getChiffrement("CryptoCBCAESProvider");
        cle = chiffrement.genererCle(128);
        
        chiffrement.init(cle);
        msg = "Hello World Hello World Hello World";
        System.out.println("Message clair    : " + msg);
        msg = chiffrement.crypte(msg);
        System.out.println("Message crypté   : " + msg);
        msg = chiffrement.decrypte(msg);
        System.out.println("Message décrypté : " + msg);
        
        System.out.println("");
        System.out.println("-------------------------------------------------");
        System.out.println("|               Service Integrity               |");
        System.out.println("-------------------------------------------------");
        System.out.println("");
        
        // Digest without salt
        //Integrity integrity = IntegrityManager.getIntegrity("SHA1MawetProvider");
        //Integrity integrity = CIAManager2.getIntegrity("SHA1MawetProvider");
        Integrity integrity = CIAManager.getIntegrity("SHA1MawetProvider");
        msg = "Hello World";
        byte[] hash = integrity.makeCheck(msg);
        System.out.println("Hash : " + new String(hash));
        boolean valid = integrity.verifyCheck(msg, hash);
        System.out.println("hash valid for " + msg + " : " + valid);
        
        // Salt
        ByteArrayList salt = new ByteArrayList();
        salt.add("RandomString".getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        long time = (new Date()).getTime();
        dos.writeLong(time);
        salt.add(baos.toByteArray());
        
        // Digest with salt
        hash = integrity.makeCheck(msg, salt);
        System.out.println("Hash : " + new String(hash));
        valid = integrity.verifyCheck(msg, salt, hash);
        System.out.println("hash valid for " + msg + " : " + valid);
        
        System.out.println("");
        System.out.println("-------------------------------------------------");
        System.out.println("|             Service Authentication            |");
        System.out.println("-------------------------------------------------");
        System.out.println("");
        
        //Authentication authentication = AuthenticationManager.getAuthentication("HMACSHA1MawetProvider");
        //Authentication authentication = CIAManager2.getAuthentication("HMACSHA1MawetProvider");
        Authentication authentication = CIAManager.getAuthentication("HMACSHA1MawetProvider");
        
        // Key generator
        String secret = "Phrase secrète pour genérer clé";
        ByteArrayList secretBytes = new ByteArrayList();
        secretBytes.add(secret.getBytes());
        cle = authentication.generateKey(secretBytes, "DES");
        
        authentication.init(cle);
        byte[] hmac = authentication.makeAuthenticate(msg);
        
        System.out.println("HMAC : " + new String(hmac));
        valid = authentication.verifyAuthenticate(msg, hmac);
        System.out.println("HMAC valid for " + msg + " : " + valid);
    }
}
