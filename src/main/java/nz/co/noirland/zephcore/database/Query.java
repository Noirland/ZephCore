package nz.co.noirland.zephcore.database;

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
    void executeAsync();
}
