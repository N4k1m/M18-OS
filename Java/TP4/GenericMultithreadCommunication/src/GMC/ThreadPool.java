package GMC;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nakim
 */

// WARNING : n'est pas un thread mais contient et gère le pool de threads client génériques
public class ThreadPool
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadPool(int noOfThreads, TaskQueue taskQueue)
    {
        this.taskQueue = taskQueue;

        this.threads = null;
        this.threadsCount = noOfThreads;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public void start()
    {
        this.isStopped = false;
        this.taskQueue.clear();
        this.threads = new ArrayList<>();

        // Instantiate threads
        for (int i = 0; i < this.threadsCount; i++)
            this.threads.add(new ThreadClient("Th" + i, this.taskQueue));

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

    public synchronized boolean isStopped()
    {
        return this.isStopped;
    }

    public synchronized void execute(Runnable task) throws InterruptedException,
                                                           TaskQueueException
    {
        if (this.isStopped)
            throw new TaskQueueException("Thread pool is stopped");

        this.taskQueue.enqueue(task);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private final TaskQueue taskQueue;

    private List<ThreadClient> threads;
    private final int threadsCount;

    private boolean isStopped;
    //</editor-fold>
}
