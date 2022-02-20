package ro.deiutzblaxo.cloud.data.mysql.classic;

import org.checkerframework.checker.nullness.qual.NonNull;
import ro.deiutzblaxo.cloud.data.mysql.MySQLConnection;
import ro.deiutzblaxo.cloud.data.mysql.MySQLManager;
import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.expcetions.ToManyArgs;

import java.sql.*;
import java.util.HashMap;
import java.util.concurrent.*;

public class MySQLManagerNormal implements MySQLManager {

    private final MySQLConnection connection;
    private final ExecutorService pool;

    public MySQLManagerNormal(@NonNull MySQLConnection connection, int nthreads) {
        this.connection = connection;
        pool = Executors.newFixedThreadPool(nthreads, new MySQLThreadFactory());
    }

    public MySQLConnection getConnection() {
        return connection;
    }

    @Override
    public ExecutorService getPool() {
        return pool;
    }

    @Override
    public void close() {
        pool.shutdownNow();
        connection.close();
    }
}

class MySQLThreadFactory implements ThreadFactory {
    private int count = 0;

    public int getCount() {
        return count;
    }

    @Override
    public Thread newThread(Runnable r) {
        count += 1;
        return new Thread(r, count + "-Cloud-MySQL");

    }

}
