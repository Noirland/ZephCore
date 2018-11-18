package nz.co.noirland.zephcore.database.mongo;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
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
    public void executeAsync() {
        AsyncDatabaseUpdateTask.addQuery(this);
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

    protected enum QueryType {
        ONE,
        MULTIPLE,
        RESULT
    }
}
