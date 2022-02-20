package ro.deiutzblaxo.cloud.data.mysql.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ro.deiutzblaxo.cloud.data.mysql.MySQLConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnectionHikari implements MySQLConnection {
    private String host, database, username, password, params;
    private int port, poolSize, idleMin;
    private HikariDataSource hikariPool;


    public MySQLConnectionHikari(String host, int port, String database, String username, String password, String params, int poolSize, int idleMin) {
        this.port = port;
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.params = params;
        this.idleMin = idleMin;
        this.poolSize = poolSize;
        connect(host, port, database, username, password, params);
    }

    public MySQLConnectionHikari(String host, int port, String username, String password, String params, int poolSize, int idleMin) {
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
        this.params = params;
        this.idleMin = idleMin;
        this.poolSize = poolSize;
        connect(host, port, username, password, params);
    }

    @Override
    public void connect(String host, int port, String database, String username, String password, String params) {
        hikariPool = new HikariDataSource(getConfig(true, poolSize, idleMin));
    }

    @Override
    public void connect(String host, int port, String username, String password, String params) {
        hikariPool = new HikariDataSource(getConfig(false, poolSize, idleMin));
    }

    private HikariConfig getConfig(boolean databaseSpecified, int poolSize, int minIdle) {

        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(minIdle);
        if (databaseSpecified) {
            config.setJdbcUrl(DEFAULT_PREFIX + host + ":" + port + "/" + database + (params == "" ? "" : "?" + params));
        } else {
            config.setJdbcUrl(DEFAULT_PREFIX + host + ":" + port + "/" + (params == "" ? "" : "?" + params));
        }
        config.setMinimumIdle(minIdle);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return config;

    }

    @Override
    public Connection getConnection() throws SQLException {
        return hikariPool.getConnection();
    }

    @Override
    public void close() {
        try {
            hikariPool.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getIdleMin() {
        return idleMin;
    }

    public void setIdleMin(int idleMin) {
        this.idleMin = idleMin;
    }

    @Override
    public int getPoolSize() {
        return poolSize;
    }

    @Override
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
