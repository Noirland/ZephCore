package nz.co.noirland.zephcore.database.mysql;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
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

    private BoneCP pool;

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
        BoneCPConfig conf = new BoneCPConfig();
        conf.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?rewriteBatchedStatements=true&useSSL=false", getHost(), getPort(), getDatabase()));
        conf.setUser(getUsername());
        conf.setPassword(getPassword());
        conf.setMaxConnectionsPerPartition(5);

        try {
            pool = new BoneCP(conf);
        } catch (SQLException e) {
            debug().disable("Unable to connect to database!", e);
        }
    }

    @Override
    public void close() {
        pool.shutdown();
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
