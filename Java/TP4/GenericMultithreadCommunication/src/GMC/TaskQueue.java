package GMC;

/**
 *
 * @author Nakim
 */
public interface TaskQueue
{
    public Runnable dequeue(Runnable task);
    public boolean isEmpty();
    public void enqueue();
}
