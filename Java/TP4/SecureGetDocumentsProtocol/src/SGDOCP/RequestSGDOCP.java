package SGDOCP;

import Utils.BytesConverter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Nakim
 */
public class RequestSGDOCP implements Serializable
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public RequestSGDOCP()
    {
        this(SGDOCPCommand.NO_COMMAND);
    }

    public RequestSGDOCP(SGDOCPCommand command)
    {
        this.command = command;
        this.args = new ArrayList<>();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">

    // Command
    public void setCommand(SGDOCPCommand command)
    {
        this.command = command;
    }

    public SGDOCPCommand getCommand()
    {
        return this.command;
    }

    public boolean is(SGDOCPCommand command)
    {
        return this.command == command;
    }

    // Arguments
    public void addArg(byte[] argument)
    {
        this.args.add(argument);
    }

    public void addArg(String argument)
    {
        this.args.add(argument.getBytes());
    }

    public byte [] getArg(int index)
    {
        if (this.args.size() <= index)
            throw new IllegalAccessError("Invalid index");
        return this.args.get(index);
    }

    public String getStringArg(int index)
    {
        return BytesConverter.byteArrayToString(this.getArg(index));
    }

    public ArrayList<byte[]> getArgs()
    {
        return this.args;
    }

    public int getArgsCount()
    {
        return this.args.size();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Utils methods">
    public boolean send(Socket socketClient)
    {
        if(socketClient == null || !socketClient.isConnected())
            return false;

        ObjectOutputStream out = null;

        try
        {
            out = new ObjectOutputStream(socketClient.getOutputStream());

            out.writeObject(this);
            out.flush();

            return true;
        }
        catch (IOException ex)
        {
            //System.err.println("sending error : " + ex);
            return false;
        }
    }

    public static RequestSGDOCP recv(Socket socketClient)
    {
        if (socketClient == null || !socketClient.isConnected())
            return new RequestSGDOCP(); // SGDOCPCommand == NO_COMMAND

        try
        {
            ObjectInputStream in = new ObjectInputStream(socketClient.getInputStream());
            RequestSGDOCP request = (RequestSGDOCP)in.readObject();

            return request;
        }
        catch (IOException | ClassNotFoundException ex)
        {
            //System.err.println("Receipt error : " + ex);
            return new RequestSGDOCP(); // SGDOCPCommand == NO_COMMAND
        }
    }

    public RequestSGDOCP sendAndRecv(Socket socketClient)
    {
        if (this.send(socketClient))
            return RequestSGDOCP.recv(socketClient);
        return new RequestSGDOCP(); // SGDOCPCommand == NO_COMMAND
    }

    public static boolean quickSend(SGDOCPCommand command, Socket socketClient)
    {
        RequestSGDOCP request = new RequestSGDOCP(command);
        return request.send(socketClient);
    }

    public static boolean quickSend(SGDOCPCommand command,
                                    String argument,
                                    Socket socketClient)
    {
        RequestSGDOCP request = new RequestSGDOCP(command);
        request.addArg(argument);
        return request.send(socketClient);
    }

    public void clearArgs()
    {
        this.args.clear();
    }

    public void reset()
    {
        this.clearArgs();
        this.command = SGDOCPCommand.NO_COMMAND;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private SGDOCPCommand command;
    private ArrayList<byte[]> args;
    //</editor-fold>
}
