package nz.co.noirland.zephcore.database;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

/**
 * Interface for a Query, regardless of database.
 */
public interface Query {

    /**
     * Executes this query on the database.
     * @throws Exception if query is unable to complete
     */
    void execute() throws Exception;


    /**
     * Schedules this query to be executed asynchronously.
     * Async queries are executed with {@link #execute()}
     */
    ListenableFuture<Void> executeAsync();

    /**
     * Gets the task to be run by the thread when executing the async query.
     * @return task created in {@link Query#executeAsync()}
     */
    ListenableFutureTask getTask();
}
