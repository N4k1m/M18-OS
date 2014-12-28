package Multithreading;

import GMC.TaskQueue;

/**
 *
 * @author Nakim
 */
public class ThreadClient extends Thread
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ThreadClient(String name, TaskQueue tasks)
    {
        super(name);

        this.tasks = tasks;
        this.currentTask = null;
        this.isStopped = true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrided methods">
    @Override
    public void run()
    {
        this.isStopped = false;

        System.out.println("[ OK ] " + this.getName() + " started");
        while(!this.isStopped())
        {
            try
            {
                this.currentTask = tasks.dequeue();
                System.out.println("[ OK ] " + this.getName() + " run task");
                this.currentTask.run();
            }
            catch (InterruptedException ex)
            {
                //log or otherwise report exception,
                System.out.println("[ OK ] " + this.getName() + " : Interrupted. Stop wating task");

                //but keep pool thread alive.
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    public synchronized void requestStop()
    {
        this.isStopped = true;
        this.interrupt(); // Break client thread out of dequeue() call
    }

    public synchronized boolean isStopped()
    {
        return this.isStopped;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private final TaskQueue tasks;
    private Runnable currentTask;

    private boolean isStopped;
    //</editor-fold>
}
