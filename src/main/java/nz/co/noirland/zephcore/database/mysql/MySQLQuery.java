package nz.co.noirland.zephcore.database.mysql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import nz.co.noirland.zephcore.ZephCore;
import nz.co.noirland.zephcore.database.AsyncDatabaseUpdateTask;
import nz.co.noirland.zephcore.database.Query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class MySQLQuery implements Query {

    private Object[] values;
    private String query;

    private List<Object[]> batches = new ArrayList<>();

    private ListenableFutureTask task;

    protected abstract MySQLDatabase getDB();

    /**
     * An abstract MySQL query representation.
     * @param nargs Number of args in query
     * @param query SQL PreparedStatement query to be executed.
     */
    public MySQLQuery(int nargs, String query) {
        this(new Object[nargs], query);
    }

    public MySQLQuery(String query) {
        this(0, query);
    }

    public MySQLQuery(Object[] values, String query) {
        this.values = values;
        this.query = query;
    }

    /**
     * Sets the (natural) index in the query to the given value.
     * @param index index of value, starting at 1
     * @param value The value
     * @throws IllegalArgumentException if index is out of range
     */
    public void setValue(int index, Object value) throws IllegalArgumentException {
        if(index == 0 || index > values.length) {
            throw new IllegalArgumentException();
        }
        values[index - 1] = value;
    }

    public Object[] getValues() {
        return values;
    }

    /**
     * Saves the values to a batch, and starts a new one.
     * These will be executed in one go on the SQL server.
     */
    public void batch() {
        batches.add(values);
        values = new Object[values.length];

    }

    public String getQuery() {
        return query;
    }

    @Override
    public void execute() throws SQLException {
        PreparedStatement stmt = getStatement();
        stmt.executeBatch();
        stmt.getConnection().close();
    }

    @Override
    public ListenableFuture<Void> executeAsync() {
        task = ListenableFutureTask.create(() -> {
            try {
                execute();
                ZephCore.debug().debug("Executed db update statement " + toString());
            } catch (Exception e) {
                ZephCore.debug().warning("Failed to execute update statement " + toString(), e);
            }
        }, null);
        AsyncDatabaseUpdateTask.addQuery(this);
        return task;
    }

    public List<Map<String, Object>> executeQuery() throws SQLException {
        PreparedStatement stmt = getStatement();
        List<Map<String, Object>> ret = MySQLDatabase.toMapList(stmt.executeQuery());
        stmt.getConnection().close();
        return ret;
    }

    public ListenableFuture<List<Map<String, Object>>> executeQueryAsync() {
        task =  ListenableFutureTask.create(() -> {
            try {
                ZephCore.debug().debug("Executing db statement " + toString());
                return executeQuery();
            } catch (Exception e) {
                ZephCore.debug().warning("Failed to execute statement " + toString(), e);
                return Collections.emptyList();
            }
        });
        AsyncDatabaseUpdateTask.addQuery(this);
        return task;
    }

    @Override
    public ListenableFutureTask getTask() {
        return task;
    }

    private PreparedStatement getStatement() {
        String q = getQuery().replaceAll("\\{PREFIX\\}", getDB().getPrefix());

        if(batches.isEmpty()) {
            batch();
        }

        try {
            PreparedStatement statement;
            statement = getDB().getRawConnection().prepareStatement(q);
            for(Object[] batch : batches) {
                for(int i = 0; i < batch.length; i++) {
                    statement.setObject(i+1, batch[i]);
                }
                statement.addBatch();
            }

            return statement;
        } catch (SQLException e) {
            getDB().debug().disable("Could not create statement for database!", e);
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("{%s}: %s", this.query, Arrays.toString(this.values));
    }
}
