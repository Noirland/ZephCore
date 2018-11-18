package nz.co.noirland.zephcore.database.mongo;

import com.mongodb.*;
import nz.co.noirland.zephcore.database.Database;

import java.net.UnknownHostException;
import java.util.Arrays;

public abstract class MongoDatabase extends Database {

    private MongoClient client;
    private DB db;

    @Override
    public void close() {
        client.close();
    }

    @Override
    protected void init() {
        try {
            ServerAddress addr = new ServerAddress(getHost(), getPort());
            MongoCredential auth = MongoCredential.createPlainCredential(getUsername(), "admin", getPassword().toCharArray());
            client = new MongoClient(addr, Arrays.asList(auth));
            db = client.getDB(getDatabase());
        } catch (UnknownHostException e) {

        }
    }

    @Override
    protected int getCurrentSchema() {
        DBObject res = new GetSchemaQuery(this).doOne();
        if(res == null) return 0;
        return (Integer) res.get("version");
    }

    public DBCollection getCollection() {
        return db.getCollection(getPrefix());
    }
}
