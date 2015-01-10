package DOCSAP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author Nakim
 */
public class DOCSAPRequest
{
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DOCSAPRequest()
    {
        this(NO_COMMAND);
    }

    public DOCSAPRequest(String command)
    {
        this.args = new ArrayList<>();
        this.command = command;
    }

    private DOCSAPRequest(String[] tokens)
    {
        this.args = new ArrayList<>();

        if (tokens.length < 1)
            this.command = DOCSAPRequest.NO_COMMAND;
        else
        {
            this.command = tokens[0];

            for (int i = 1; i < tokens.length; i++)
                this.addArg(tokens[i]);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public void setCommand(String command)
    {
        this.command = command;
        this.clearArgs();
    }

    public String getCommand()
    {
        return this.command;
    }

    public boolean is(String command)
    {
        return this.command.equalsIgnoreCase(command);
    }

    public final void addArg(String argument)
    {
        this.args.add(argument);
    }

    public String getArg(int index)
    {
        return this.args.get(index);
    }

    public ArrayList<String> getArgs()
    {
        return this.args;
    }

    public int getArgsCount()
    {
        return this.args.size();
    }

    public void clearArgs()
    {
        this.args.clear();
    }

    public void reset()
    {
        this.clearArgs();
        this.command = DOCSAPRequest.NO_COMMAND;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Utils methods">
    public boolean send(Socket socketClient)
    {
        try
        {
            DataOutputStream dos = new DataOutputStream(socketClient.getOutputStream());

            // Build query string
            String query = this.command;

            for (String arg : this.args)
                query += DOCSAPRequest.SEP_TRAME + arg;
            query += DOCSAPRequest.END_TRAME;

            dos.write(query.getBytes());

            return true;
        }
        catch (IOException ex)
        {
            return false;
        }
    }

    public static DOCSAPRequest recv(Socket socketClient)
    {
        if (socketClient == null || !socketClient.isConnected())
            return new DOCSAPRequest(); // command == NO_COMMAND

        String[] tokens;
        byte b;
        char end = DOCSAPRequest.END_TRAME.charAt(0);
        StringBuilder buffer = new StringBuilder();

        try
        {
            DataInputStream dis = new DataInputStream(socketClient.getInputStream());

            // Receiving loop
            while ((b = dis.readByte()) != (byte)end)
                buffer.append((char)b);

            String reply = buffer.toString();
            tokens = reply.split("\\" + DOCSAPRequest.SEP_TRAME
                + "|\\" + DOCSAPRequest.END_TRAME);

            if (tokens.length == 0)
                throw new Exception();

            return new DOCSAPRequest(tokens);
        }
        catch (Exception e)
        {
            return new DOCSAPRequest(); // command == NO_COMMAND
        }
    }

    public DOCSAPRequest sendAndRecv(Socket socketclient)
    {
        if (this.send(socketclient))
            return DOCSAPRequest.recv(socketclient);
        return new DOCSAPRequest(); // command == NO_COMMAND
    }

    public static boolean quickSend(String command, Socket socketClient)
    {
        DOCSAPRequest request = new DOCSAPRequest(command);
        return request.send(socketClient);
    }

    public static boolean quickSend(String command,
                                    String argument,
                                    Socket socketClient)
    {
        DOCSAPRequest request = new DOCSAPRequest(command);
        request.addArg(argument);
        return request.send(socketClient);
    }
    //</editor-fold>

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

    // Network management
    public final static String SOCK_ERROR = "SOCK_ERROR";
    public final static String NO_COMMAND = "NO_COMMAND";

    private static String SEP_TRAME;
    private static String END_TRAME;

    private static final String DEFAULT_SEP_TRAME = "$";
    private static final String DEFAULT_END_TRAME = "#";

    static
    {
        try
        {
            // Get protocol parameters from properties file
            Properties properties = new Properties();
            InputStream input = DOCSAPRequest.class.getResourceAsStream("config.properties");
            properties.load(input);
            SEP_TRAME = properties.getProperty("sep_trame", DEFAULT_SEP_TRAME);
            END_TRAME = properties.getProperty("end_trame", DEFAULT_END_TRAME);

        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] DOCSAP : settings file not found. "
                + "Default settings applied");

            SEP_TRAME = DEFAULT_SEP_TRAME;
            END_TRAME = DEFAULT_END_TRAME;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private String command;
    private ArrayList<String> args;
    //</editor-fold>
}
