package nz.co.noirland.zephcore.database.mysql;

import nz.co.noirland.zephcore.database.AsyncDatabaseUpdateTask;
import nz.co.noirland.zephcore.database.Query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class MySQLQuery implements Query {

    private Object[] values;
    private String query;

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

    public String getQuery() {
        return query;
    }

    public void execute() throws SQLException {
        PreparedStatement stmt = getStatement();
        stmt.execute();
        stmt.getConnection().close();
    }

    public void executeAsync() {
        AsyncDatabaseUpdateTask.addQuery(this);
    }

    public List<Map<String, Object>> executeQuery() throws SQLException {
        PreparedStatement stmt = getStatement();
        List<Map<String, Object>> ret = MySQLDatabase.toMapList(stmt.executeQuery());
        stmt.getConnection().close();
        return ret;
    }

    private PreparedStatement getStatement() {
        String q = getQuery().replaceAll("\\{PREFIX\\}", getDB().getPrefix());

        try {
            PreparedStatement statement;
            statement = getDB().getRawConnection().prepareStatement(q);

            for(int i = 0; i < values.length; i++) {
                statement.setObject(i+1, values[i]);
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
