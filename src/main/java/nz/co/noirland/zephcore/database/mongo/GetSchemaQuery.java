package nz.co.noirland.zephcore.database.mongo;

import com.mongodb.DBObject;

public class GetSchemaQuery extends MongoQuery {

    private MongoDatabase db;

    public GetSchemaQuery(MongoDatabase db) {
        super("schema", QueryType.ONE);
        this.db = db;
    }

    @Override
    protected MongoDatabase getDB() {
        return db;
    }

    @Override
    public DBObject doOne() {
        return getCollection().findOne(); // Collection should never have more than 1 document
    }
}
