package nz.co.noirland.zephcore.database;

/**
 * Abstract Schema. Used for versioning of a database.
 */
public interface Schema {

    /**
     * Update the database to this schema.
     */
    void run();

}
