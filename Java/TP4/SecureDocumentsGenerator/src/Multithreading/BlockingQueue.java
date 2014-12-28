package Multithreading;

import GMC.TaskQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Nakim
 */
public class BlockingQueue implements TaskQueue
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public BlockingQueue(int limit)
    {
        this.queue = Collections.synchronizedList(new LinkedList<Runnable>());
        this.limit = limit;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public synchronized void enqueue(Runnable task) throws InterruptedException
    {
        while(this.isFull())
            wait();

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
    public synchronized boolean isFull()
    {
        return this.queue.size() >= this.limit;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private List<Runnable> queue;
    private int limit;
    //</editor-fold>
}
