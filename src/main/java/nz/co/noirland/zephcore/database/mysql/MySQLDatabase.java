package nz.co.noirland.zephcore.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nz.co.noirland.zephcore.database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MySQLDatabase extends Database {

    private HikariDataSource pool;

    @Override
    protected int getCurrentSchema() {
        try {
            List<Map<String, Object>> res = new GetSchemaQuery(this).executeQuery();
            return (Integer) res.get(0).get("version");
        } catch (SQLException e) {
            return 0; // Could not get schema, assume that database is not set up
        }
    }



    @Override
    protected void init() {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", getHost(), getPort(), getDatabase()));
        conf.setUsername(getUsername());
        conf.setPassword(getPassword());
        conf.addDataSourceProperty("rewriteBatchedStatements", true);
        conf.addDataSourceProperty("useSSL", false);
        conf.setMaximumPoolSize(20);

        pool = new HikariDataSource(conf);
    }

    @Override
    public void close() {
        pool.close();
    }

    public static List<Map<String, Object>> toMapList(ResultSet res) throws SQLException {
        ResultSetMetaData md = res.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (res.next()){
            Map<String, Object> row = new HashMap<String, Object>(columns);
            for(int i=1; i<=columns; ++i){
                row.put(md.getColumnName(i),res.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

    public Connection getRawConnection() throws SQLException {
        return pool.getConnection();
    }
}
