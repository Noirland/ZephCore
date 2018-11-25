package nz.co.noirland.zephcore.database.mongo;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import nz.co.noirland.zephcore.ZephCore;
import nz.co.noirland.zephcore.database.AsyncDatabaseUpdateTask;
import nz.co.noirland.zephcore.database.Query;
import org.apache.commons.lang.Validate;

import java.util.List;

/**
 * Abstractified MongoDB query.
 */
public abstract class MongoQuery implements Query {

    /**
     * Name of the collection that the query alters.
     */
    protected String collection;

    /**
     * Type of query that is run.
     *
     * @see QueryType
     */
    private QueryType type;

    private ListenableFutureTask task;

    /**
     * Provides the database that this query is used in. This is required
     * for Async queries to be completed.
     */
    protected abstract MongoDatabase getDB();

    public MongoQuery(String collection, QueryType type) {
        Validate.notNull(type, "Must specify a QueryType");
        Validate.notEmpty(collection, "Must specify a collection");
        this.collection = collection;
        this.type = type;
    }

    /**
     * Execute the given query, by using the correct QueryType. This allows
     * Async queries to be executed regardless of the output.
     */
    @Override
    public void execute() {
        switch(type) {
            case ONE:
                doOne();
                break;
            case MULTIPLE:
                doMultiple();
                break;
            case RESULT:
                doResult();
                break;
        }
    }

    @Override
    public ListenableFuture<Void> executeAsync() {
        AsyncDatabaseUpdateTask.addQuery(this);

        ListenableFutureTask<Void> task = ListenableFutureTask.create(() -> {
            try {
                execute();
                ZephCore.debug().debug("Executed db update statement " + toString());
            } catch (Exception e) {
                ZephCore.debug().warning("Failed to execute update statement " + toString(), e);
            }
        }, null);
        this.task = task;
        return task;
    }

    public DBObject doOne() {
        return null;
    }

    public List<DBObject> doMultiple() {
        return null;
    }

    public WriteResult doResult() {
        return null;
    }

    protected DBCollection getCollection() {
        return getDB().getCollection().getCollection(collection);
    }

    @Override
    public ListenableFutureTask getTask() {
        return task;
    }

    protected enum QueryType {
        ONE,
        MULTIPLE,
        RESULT
    }
}
