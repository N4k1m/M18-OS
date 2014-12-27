package SPF.Crypto.Alberti;

import SPF.Cle;

/**
 *
 * @author nakim
 */
public class CleAlberti implements Cle
{
    // <editor-fold defaultstate="collapsed" desc="Constructeur">

    public CleAlberti(String key, char smallRingLetter, int periodLength)
    {
        this.key = key;
        this.smallRingLetter = smallRingLetter;
        this.periodLength = periodLength;
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters et setters">
    public String getKey()
    {
        return key;
    }

    public void setKey(String key) 
    {
        this.key = key;
    }

    public char getSmallRingLetter() 
    {
        return smallRingLetter;
    }

    public void setSmallRingLetter(char smallRingLetter)
    {
        this.smallRingLetter = smallRingLetter;
    }

    public int getPeriodLength()
    {
        return periodLength;
    }

    public void setPeriodLength(int periodLength)
    {
        this.periodLength = periodLength;
    }
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="Overrided methods">
    
    @Override
    public int getLongueur()
    {
        return key.length();
    }

    @Override
    public String toString()
    {
        return "CleAlberti{" + "key=" + key 
                             + ", longueur clé = " + this.getLongueur()
                             + ", smallRingLetter = " + smallRingLetter 
                             + ", periodLength = " + periodLength 
                             + '}';
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc="Variables membres privées">
    
    // Mot pour les calages successifs
    private String key;
    /* Lettre du disque intérieur sur laquelle
     *on cale la lettre du disque exterieur */
    private char smallRingLetter;
    // Nombre de caractères que l'on peut coder avec le même calage
    private int periodLength;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Varibles statiques ">
    
    private static final long serialVersionUID = 1L;
    
    // </editor-fold>
}
