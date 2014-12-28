package GMC;

/**
 *
 * @author Nakim
 */
public interface TaskQueue
{
    public void enqueue(Runnable task);
    public Runnable dequeue();
    public boolean isEmpty();
}
