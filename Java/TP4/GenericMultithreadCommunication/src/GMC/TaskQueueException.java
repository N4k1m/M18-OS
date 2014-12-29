
package GMC;

/**
 *
 * @author Nakim
 */
public class TaskQueueException extends Exception
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public TaskQueueException(String cause)
    {
        super(cause);
    }

    public TaskQueueException(String cause, String baseException)
    {
        this(cause);
        this.baseException = baseException;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public String getBaseException()
    {
        return this.baseException;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private String baseException;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Static variables">
    private static final long serialVersionUID = 1L;
    //</editor-fold>
}
