package GMC;

/**
 *
 * @author Nakim
 */
public interface TaskQueue
{
    public void enqueue(Runnable task) throws InterruptedException, TaskQueueException;
    public Runnable dequeue()  throws InterruptedException, TaskQueueException;
    public boolean isEmpty();
    public void clear();

    /* Pas de méthode boolean isFull() car : 
     * Pas de notion de limitation de taille de la queue ou de notion de
     * file bloquante. Ces notions sont laissées à l'appréciation du du code
     * client, c'est à dire à la personne qui va utiliser notre pool de threads
     * C'est à lui que revient la charge de développer un tye de TaskQueue qui
     * à sa guise, sera soit limitée en taille, bloquante ou même les deux
     */

    /* Ne connaissant pas l'implémentation qui se cachera en dessous d'une
     * TaskQueue, je ne suis pas en mesure de définir quels types d'excpetion
     * pourront éventuellement être lancés. C'est la raison pour laquelle j'ai
     * défini une classe TaskQueueException qui sera la seule à pouvoir être
     * lancée (si besoin il y a).
     */
}
