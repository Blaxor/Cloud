package ro.deiutzblaxo.cloud.data.mysql;

import ro.deiutzblaxo.cloud.utils.CloudLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;


public class MySQLConnection {
    private static String DEFAULT_PREFIX = "jdbc:mysql://";
    private Connection connection;
    private String hostname;
    private String username;
    private String password;
    private int port;
    private String database;
    private String params;

//USE VERSION 5.1.49 OF MYSQL
    public MySQLConnection(String host, int port, String database, String username, String password, String params) {
        this.hostname = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.database = database;
        this.params = params;
        connect(host, port, database, username, password, params);
    }

    public void connect(String host, int port, String database, String username, String password, String params) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (connection != null && !(this.connection.isClosed())) {
                CloudLogger.getLogger().log(Level.WARNING, "A connection is already existing. This may produce errors");
            }
            this.connection = DriverManager.getConnection(DEFAULT_PREFIX + host + ":" + port + "/" + database + (params == "" ? "" : "?" + params), username, password);
            CloudLogger.getLogger().log(Level.INFO, "Connected with success at database " + database + " with user: " + username + " and password " + password);
        } catch (SQLException throwables) {
            CloudLogger.getLogger().log(Level.INFO, "Failed connecting at database " + database + " with user: " + username + " and password " + password);
            throwables.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
