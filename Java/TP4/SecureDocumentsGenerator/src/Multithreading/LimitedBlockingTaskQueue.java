package Multithreading;

import GMC.TaskQueue;
import GMC.TaskQueueException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Nakim
 */

/* Il s'agit d'une file de tâches acceptant un nombre limité de tâches.
 * Par défaut, elle est bloquante pour l'acquisition ET l'insertion de tâches
 * Il est possible qu'elle ne soit pas bloquante pour l'insertion de tâches
 */
public class LimitedBlockingTaskQueue implements TaskQueue
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public LimitedBlockingTaskQueue(int limit)
    {
        this(limit, true);
    }

    public LimitedBlockingTaskQueue(int limit, boolean enqueueBlock)
    {
        this.queue = Collections.synchronizedList(new LinkedList<Runnable>());
        this.limit = limit;

        this.enqueueBlock = enqueueBlock;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public synchronized void enqueue(Runnable task) throws InterruptedException,
                                                           TaskQueueException
    {
        // Block or not in case of empty queue
        if (this.enqueueBlock)
        {
            while(this.isFull())
                wait();
        }
        else if(this.isFull())
        {
            throw new TaskQueueException("The queue is full");
        }

        if (this.isEmpty())
            notifyAll();

        this.queue.add(task);
    }

    @Override
    public synchronized Runnable dequeue() throws InterruptedException
    {
        while(this.isEmpty())
            wait();

        if (this.isFull())
            notifyAll();

        return this.queue.remove(0);
    }

    @Override
    public synchronized boolean isEmpty()
    {
        return this.queue.isEmpty();
    }

    @Override
    public void clear()
    {
        this.queue.clear();
    }

    public synchronized boolean isFull()
    {
        return this.queue.size() >= this.limit;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private List<Runnable> queue;
    private int limit;

    private boolean enqueueBlock;
    //</editor-fold>
}
