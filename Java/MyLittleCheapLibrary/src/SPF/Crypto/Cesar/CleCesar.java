package SPF.Crypto.Cesar;

import SPF.Cle;

/**
 *
 * @author nakim
 */
public class CleCesar implements Cle
{
    // <editor-fold defaultstate="collapsed" desc="Constructeur">
    
    public CleCesar(int decalage)
    {
        this.decalage = decalage;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Getters et setters">

    public int getDecalage() 
    {
        return decalage;
    }

    public void setDecalage(int decalage) 
    {
        this.decalage = decalage;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public int getLongueur()
    {
        // return this.decalage;
        return this.decalage / 10;
    }

    @Override
    public String toString() 
    {
        return "CleCesar{" + "decalage = " + decalage 
                           + ", longueur clé = " + this.getLongueur() + '}';
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Variables membres privées">
    
    // Nombre de caractères que l'on peut coder avec le même calage
    private int decalage;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Varibles statiques ">
    
    private static final long serialVersionUID = 1L;
    
    // </editor-fold>
}
