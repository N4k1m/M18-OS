package SecureDocumentsGeneratorMain;

import GUI.MainFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author nakim
 */
public class Main
{
    public static void main(String[] args)
    {
        //<editor-fold defaultstate="collapsed" desc="Change look and feel">
        try
        {
            String className = getLookAndFeelClassName("Nimbus");
            UIManager.setLookAndFeel(className);
        }
        catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            System.err.println(ex);
        }
        //</editor-fold>

        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static String getLookAndFeelClassName(String nameSnippet)
    {
        UIManager.LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        for(UIManager.LookAndFeelInfo info : plafs)
            if (info.getName().contains(nameSnippet))
                return info.getClassName();

        return null;
    }
}
