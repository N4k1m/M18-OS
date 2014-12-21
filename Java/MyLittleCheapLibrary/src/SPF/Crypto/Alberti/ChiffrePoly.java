package SPF.Crypto.Alberti;

import SPF.Cle;
import SPF.Crypto.Chiffrement;
import java.util.Random;

/**
 *
 * @author nakim
 */
public class ChiffrePoly implements Chiffrement
{
    // <editor-fold defaultstate="collapsed" desc=" Constructeur ">
    public ChiffrePoly()
    {
        this.ringLen = largeRing.length();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Overrided methods ">
    @Override
    public void init(Cle k) 
    {
        if (!(k instanceof CleAlberti))
            throw new IllegalArgumentException("Cle invalide");
        
        CleAlberti cle;
        cle = (CleAlberti) k;
        
        /* Normalement la clé est générée par ChiffrePoly mais par précaution
         * On vérifie que la lettre de calage figure bien dans l'alphabet crypté
         */
        if (this.smallRing.indexOf(cle.getSmallRingLetter()) == -1)
            throw new IllegalArgumentException("Clé non valide : lettre de calage inexistante");

        this.key = cle;
        this.indiceBindingLetterSmallRing = this.smallRing.indexOf(
                key.getSmallRingLetter());
        this.periode = this.key.getPeriodLength();
    }
    
    @Override
    public Cle genererCle(int longueur)
    {
        if (longueur < 1)
            throw new IllegalArgumentException("La longueur doit être supérieur à 0");
        
        // Génère un string aléatoire
        Random random = new Random();
        char[] randomString = new char[longueur];
        for (int i = 0; i < longueur; i++)
            randomString[i] = largeRing.charAt(random.nextInt(largeRing.length()));
        
        // Choisit aléatoirement la lettre de calage
        char smallRingLetter = smallRing.charAt(random.nextInt(smallRing.length()));
        
        return new CleAlberti(new String(randomString),
                             smallRingLetter, 
                             random.nextInt(9) + 1); // Nombre entre 
    }

    @Override
    public String crypte(String plainText) 
    {
        if (this.key == null)
            throw new IllegalStateException("Chiffrement non initialisé");
        
        String UCPlainText = plainText.toUpperCase();
        final int plainTextLen = plainText.length();
        
        int decalage  = -1;
        int indiceKey = -1;
        
        char[] cipherText = new char[plainTextLen];
        for (int i = 0; i < plainTextLen; ++i)
        {
            /* Choisir la lettre de référence du disque exterieur 
             * que l'on cale sur celle du disque intérieur */
            if (i % this.periode == 0)
            {   
                indiceKey = (indiceKey + 1) % this.key.getLongueur();
                int indiceBindingLetterLargeRing = this.largeRing.indexOf(
                        key.getKey().charAt(indiceKey));
        
                decalage = indiceBindingLetterSmallRing - 
                           indiceBindingLetterLargeRing;
                if (decalage < 0)
                    decalage += ringLen;
            }
            
            /* L'indice de la lettre cryptée = 
             * décalage + indice de la lettre non cryptée  */
            int indiceLettreCryptee = (decalage + largeRing.indexOf(
                    UCPlainText.charAt(i))) % ringLen;
            
            if (indiceLettreCryptee < 0)
                indiceLettreCryptee += ringLen;
            
            cipherText[i] = this.smallRing.charAt(indiceLettreCryptee);
        }
        
        return new String(cipherText);
    }

    @Override
    public String decrypte(String cipherText) 
    {
        if (this.key == null)
            throw new IllegalStateException("Chiffrement non initialisé");
        
        final int cipherTextLen = cipherText.length();
        
        int decalage  = -1;
        int indiceKey = -1;
        
        char[] plainText = new char[cipherTextLen];
        for (int i = 0; i < cipherTextLen; ++i)
        {
            /* Choisir la lettre de référence du disque exterieur 
             * que l'on cale sur celle du disque intérieur */
            if (i % periode == 0)
            {   
                indiceKey = (indiceKey + 1) % this.key.getLongueur();
                
                int indiceBindingLetterLargeRing = this.largeRing.indexOf(
                        key.getKey().charAt(indiceKey));
        
                decalage = indiceBindingLetterSmallRing - 
                           indiceBindingLetterLargeRing;
                if (decalage < 0)
                    decalage += ringLen;
            }
            
            /* L'indice de la lettre decryptée = 
             * indice de la lettre cryptée - décalage */
            int indiceLettreDecryptee = (smallRing.indexOf(cipherText.charAt(i))
                    - decalage) % ringLen;
            
            if (indiceLettreDecryptee < 0)
                indiceLettreDecryptee += ringLen;
            
            plainText[i] = this.largeRing.charAt(indiceLettreDecryptee);
        }
        
        return new String(plainText);
    }

    @Override
    public String getProvider() 
    {
        return "AlbertiFamily";
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables membres ">
    
    // Indépendant de la clé
    
    // Alphabet clair
    private final String largeRing = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
    // Alphabet crypté
    private final String smallRing = "c&bm+dgpf!zn=xyvto)sk,erl<ha*iq1-juw(";
    // Taille des disques
    private final int ringLen;
    
    // Dépendant de la clé
    
    // Clé utilisée pour chiffrer et/ou déchiffrer
    private CleAlberti key = null;
    
    /* Indice du disque interne de la lettre de référence
     * sur laquelle on cale le disque extérieur */
    private int indiceBindingLetterSmallRing;
    
    // Période durant laquelle on crypte avant de tourner le disque
    private int periode;
    
    // </editor-fold>
}
