package GMC;

import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author Nakim
 */

public interface Request
    extends Serializable // Supposed to be sent through a network communication
{
    public Runnable createRunnable(
        Socket clientSocket,        // Client socket
        EventTracker eventTracker); // Tracker will manage all event that occured
}
