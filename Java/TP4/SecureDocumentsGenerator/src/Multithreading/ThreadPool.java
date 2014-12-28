package Multithreading;

import GMC.TaskQueue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nakim
 */

// WARNING : n'est pas un thread mais contient et gère le pool de threads client
public class ThreadPool
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadPool(int noOfThreads, int maxNoOfTasks)
    {
        this.taskQueue = null;
        this.taskQueueLimit = maxNoOfTasks;

        this.threads = null;
        this.threadsCount = noOfThreads;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public void start()
    {
        this.isStopped = false;
        this.taskQueue = new BlockingQueue(this.taskQueueLimit);
        this.threads = new ArrayList<>();

        // Instantiate threads
        for (int i = 0; i < this.threadsCount; i++)
            this.threads.add(new ThreadClient("Th" + threadsCount, this.taskQueue));

        // Start threads
        threads.stream().forEach((threadClient) ->
        {
            threadClient.start();
        });
    }

    public synchronized void requestStop()
    {
        this.isStopped = true;

        // Stop threads
        threads.stream().forEach((threadClient) ->
        {
            threadClient.requestStop();
        });
    }

    public synchronized void execute(Runnable task) throws InterruptedException
    {
        if (this.isStopped)
            throw new IllegalStateException("Thread pool is stopped");

        if (this.taskQueue.isFull())
            throw new IllegalStateException("Task queue is full");

        this.taskQueue.enqueue(task);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private TaskQueue taskQueue;
    private final int taskQueueLimit;

    private List<ThreadClient> threads;
    private final int threadsCount;

    private boolean isStopped;
    //</editor-fold>
}
