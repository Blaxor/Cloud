package ro.deiutzblaxo.cloud.data.mysql;

import java.sql.Connection;
import java.sql.SQLException;

public interface MySQLConnection {
    static final String DEFAULT_PREFIX = "jdbc:mysql://";

    public void connect(String host, int port, String database, String username, String password, String params);

    public void connect(String host, int port, String username, String password, String params);

    public void connect(String host, int port, String database, String username, String password, String params, String prefixPoolName);

    public Connection getConnection() throws SQLException;

    public int getPoolSize();

    public void setPoolSize(int n);

    public int getIdleMin();

    public void setIdleMin(int n);


    public void close();
}
