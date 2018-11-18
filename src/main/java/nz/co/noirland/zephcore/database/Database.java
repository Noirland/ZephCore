package nz.co.noirland.zephcore.database;

import nz.co.noirland.zephcore.Debug;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Abstract database implementation, that will work for both SQL and NoSQL databases.
 */
public abstract class Database {

    /**
     * All databases that were created under this implementation.
     * Used by ZephCore to close all open databases on shutdown.
     */
    private static Set<Database> databases = new HashSet<Database>();

    /**
     * A Map of Schema definitions.
     */
    protected SortedMap<Integer, Schema> schemas = new TreeMap<Integer, Schema>();

    public Database() {
        databases.add(this);
        init();
    }

    /**
     * Get the debugger for this database/plugin.
     * @return
     */
    public abstract Debug debug();

    protected abstract String getHost();

    protected abstract int getPort();

    protected abstract String getDatabase();

    protected abstract String getUsername();

    protected abstract String getPassword();

    public abstract String getPrefix();

    protected abstract int getCurrentSchema();

    public abstract void close();

    protected abstract void init();

    /**
     * Get the current database's schema, and update if it's out of date.
     */
    public void checkSchema() {
        int version = getCurrentSchema();
        int latest = getLatestSchema();
        if(version == latest) {
            return;
        }
        if(version > latest) {
            debug().disable("Database schema is newer than this plugin version!");
            return;
        }

        for(Schema schema : schemas.tailMap(version + 1).values()) {
            schema.run();
        }

    }

    /**
     * Get the latest schema defined by this plugin.
     * @return Schema version
     */
    protected int getLatestSchema() {
        return schemas.lastKey();
    }

    /**
     * Get the Schema definition associated with the integer version.
     * @param schema version to fetch
     * @return Schema definition, or null if there is not one defined.
     */
    protected Schema getSchemaDef(int schema) {
        return schemas.get(schema);
    }

    /**
     * Get all databases implementations currently instantiated.
     * @return A Set of databases.
     */
    public static Set<Database> getDatabases() {
        return databases;
    }
}
