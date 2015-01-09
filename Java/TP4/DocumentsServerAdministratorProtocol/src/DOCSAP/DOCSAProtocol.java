package DOCSAP;

import Utils.PropertyLoader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Nakim
 */
public class DOCSAProtocol
{
    //<editor-fold defaultstate="collapsed" desc="static variables">
    // DOCSAP commands
    public final static String LOGINA   = "LOGINA";
    public final static String LCLIENTS = "LCLIENTS";
    public final static String PAUSE    = "PAUSE";
    public final static String RESUME   = "RESUME";
    public final static String STOP     = "STOP";

    // DOCSAP extended commands
    public final static String QUIT     = "QUIT";
    public final static String ACK      = "ACK";
    public final static String FAIL     = "FAIL";

    public static String sep_trame;
    public static String end_trame;

    private static final String DEFAULT_SEP_TRAME = "$";
    private static final String DEFAULT_END_TRAME = "#";

    static
    {
        try
        {
            // Get server parameters from properties file
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                  + "src"
                  + System.getProperty("file.separator")
                  + "DOCSAP"
                  + System.getProperty("file.separator")
                  + "config.properties";

            Properties prop = PropertyLoader.load(path);
            sep_trame = prop.getProperty("sep_trame", DEFAULT_SEP_TRAME);
            end_trame = prop.getProperty("end_trame", DEFAULT_END_TRAME);

        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] DOCSAP : settings file not found. "
                + "Default settings applied");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public static functions">
    public static String getLOGINATrame(String username, String password)
    {
        String request = DOCSAProtocol.LOGINA
                       + DOCSAProtocol.sep_trame
                       + username
                       + DOCSAProtocol.sep_trame
                       + password
                       + DOCSAProtocol.end_trame;
        return request;
    }

    public static String getLCLIENTSTrame()
    {
        String request = DOCSAProtocol.LCLIENTS + DOCSAProtocol.end_trame;
        return request;
    }

    public static String getPAUSETrame()
    {
        String request = DOCSAProtocol.PAUSE + DOCSAProtocol.end_trame;
        return request;
    }

    public static String getRESUMETrame()
    {
        String request = DOCSAProtocol.RESUME + DOCSAProtocol.end_trame;
        return request;
    }

    public static String getSTOPTrame(int delay)
    {
        String request = DOCSAProtocol.STOP
                       + DOCSAProtocol.sep_trame
                       + String.valueOf(delay)
                       + DOCSAProtocol.end_trame;
        return request;
    }

    public static String getQUITTrame()
    {
        String request = DOCSAProtocol.QUIT + DOCSAProtocol.end_trame;
        return request;
    }

    public static String getACKTrame()
    {
        String request = DOCSAProtocol.ACK + DOCSAProtocol.end_trame;
        return request;
    }

    public static String getFAILTrame(String cause)
    {
        String request = DOCSAProtocol.FAIL
                       + DOCSAProtocol.sep_trame
                       + cause
                       + DOCSAProtocol.end_trame;
        return request;
    }
    //</editor-fold>
}
