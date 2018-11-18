package nz.co.noirland.zephcore.database;

import nz.co.noirland.zephcore.ZephCore;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncDatabaseUpdateTask extends Thread {

    private final static LinkedBlockingQueue<Query> queries = new LinkedBlockingQueue<Query>();
    private static AsyncDatabaseUpdateTask inst;

    private AsyncDatabaseUpdateTask() {
        super("AsyncDatabaseUpdateTask");
    }

    public static AsyncDatabaseUpdateTask inst() {
        if(inst == null) {
            inst = new AsyncDatabaseUpdateTask();
        }
        return inst;
    }

    /**
     * Causes the task to complete, by stopping the loop and completing all remaining
     * queries.
     */
    public void finish() {
        interrupt();
        LinkedList<Query> drain = new LinkedList<Query>();
        queries.drainTo(drain);
        for(Query query : drain) {
            execute(query);
        }
    }

    /**
     * Main loop for executing threads added to queue.
     */
    @Override
    public void run() {
        try {
            while(true) {
                execute(queries.take());
            }
        } catch (InterruptedException ignored) {}

    }

    private void execute(Query query) {
        try {
            query.execute();
            ZephCore.debug().debug("Executed db update statement " + query.toString());
        } catch (Exception e) {
            ZephCore.debug().warning("Failed to execute update statement " + query.toString(), e);
        }
    }

    /**
     * Add a query to the queue to be executed later.
     * @param query Query to execute
     */
    public static void addQuery(Query query) {
        queries.add(query);
    }
}
