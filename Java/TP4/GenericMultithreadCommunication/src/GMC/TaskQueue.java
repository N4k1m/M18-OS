package GMC;

/**
 *
 * @author Nakim
 */
public interface TaskQueue
{
    public void enqueue(Runnable task) throws InterruptedException;
    public Runnable dequeue()  throws InterruptedException;
    public boolean isEmpty();
    public boolean isFull();
}
