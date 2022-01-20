package ro.deiutzblaxo.cloud.data.mysql;

import org.checkerframework.checker.nullness.qual.NonNull;
import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.expcetions.ToManyArgs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class MySQLManager {

    private final MySQLConnection connection;
    private final ExecutorService pool;

    public MySQLManager(@NonNull MySQLConnection connection, int nthreads) {
        this.connection = connection;
        pool = Executors.newFixedThreadPool(nthreads, new MySQLThreadFactory());
    }


    public <T> void insert(@NonNull String table, @NonNull String[] columns, @NonNull T[] values) throws ToManyArgs {

        if (columns.length != values.length)
            throw new ToManyArgs("Too many/less arguments!");
        CompletableFuture.runAsync(() -> {
            try {
                StringBuilder builder = new StringBuilder("INSERT INTO " + table + " (");
                for (int i = 0; i < columns.length; i++) {
                    builder.append(columns[i]).append(i < columns.length - 1 ? "," : ") VALUES (");
                }
                for (int i = 0; i < values.length; i++) {
                    builder.append("?").append(i < values.length - 1 ? "," : ");");
                }
                PreparedStatement statement = getPreparedStatement(builder.toString());
                for (int i = 1; i <= values.length; i++) {
                    statement.setObject(i, values[i - 1]);
                }
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }, pool);
    }


    public PreparedStatement getPreparedStatement(@NonNull String sql) {
        try {
            return connection.getConnection().prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


    public boolean execute(@NonNull String sql) {
        CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() ->
        {
            try {
                return getPreparedStatement(sql).execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return false;
            }
        }, pool);
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public int executeUpdate(@NonNull String sql) {
        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() ->
        {
            try {
                return getPreparedStatement(sql).executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return 0;
            }
        }, pool);
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ResultSet executeQuery(@NonNull String sql) {
        CompletableFuture<ResultSet> result = CompletableFuture.supplyAsync(() -> {

            try {
                return getPreparedStatement(sql).executeQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }

        }, pool);
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> boolean exists(@NonNull String table, @NonNull String collum, @NonNull T value) {
        try {
            ResultSet set = executeQuery("SELECT * FROM " + table + " WHERE " + collum + "='" + value + "';");
            return set.next();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }


    public <T> void update(@NonNull String table, @NonNull String keyCollum, @NonNull T key, @NonNull String[] valuesCollum, @NonNull T[] values) throws
            ToManyArgs {

        if (valuesCollum.length != values.length)
            throw new ToManyArgs("Too many/less arguments!");
        CompletableFuture.runAsync(() -> {
            try {
                StringBuilder builder = new StringBuilder("UPDATE " + table + " SET ");
                for (int i = 0; i < valuesCollum.length; i++) {
                    builder.append(valuesCollum[i]).append(" = ? ").append(i < valuesCollum.length - 1 ? "," : " ");
                }
                builder.append("WHERE ").append(keyCollum).append(" = ? ;");
                PreparedStatement statement = getPreparedStatement(builder.toString());
                int i = 1;
                for (; i <= valuesCollum.length; i++) {
                    statement.setObject(i, values[i - 1]);
                }
                statement.setObject(i, key);
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, pool);
    }

    public <T> T get(@NonNull String table, @NonNull String valueColumn, @NonNull String keyColumn, @NonNull Object key, @NonNull Class<T> type) throws NoFoundException {
        String query = "SELECT * FROM " + table + " WHERE " + keyColumn + " = ? ;";
        PreparedStatement statement = getPreparedStatement(query);
        try {
            statement.setObject(1, key);

            ResultSet r = statement.executeQuery();
            if (!r.next())
                throw new NoFoundException("Data not found in table " + table + ", keyColumn:" + keyColumn + ", key:" + key + ", valueColumn: " + valueColumn);
            return r.getObject(valueColumn, type);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getString(@NonNull String table, @NonNull String valueColumn, @NonNull String keyColumn, @NonNull Object key) throws NoFoundException {
        return get(table, valueColumn, keyColumn, key, String.class);

    }

    public void createTable(@NonNull String table, @NonNull String... columns) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table + " (");
        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i]).append(i == columns.length - 1 ? ");" : ",");
        }
        execute(builder.toString());
    }

    public void createDataBase(@NonNull String database) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement statement = getPreparedStatement("CREATE DATABASE IF NOT EXISTS ?");
                statement.setString(1, database);
                statement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, pool);
    }

    public void deleteRow(String table, String ByField, String field) {
        CompletableFuture.runAsync(() -> executeUpdate("DELETE FROM " + table + " WHERE " + ByField + " = '" + field + "';"), pool);

    }

    public String getLikeString(String table, String byField, String field, String get) throws NoFoundException {

        PreparedStatement statement = getPreparedStatement("SELECT * FROM " + table + " WHERE " + byField + " LIKE ? ;");
        try {
            statement.setObject(1, field);
            ResultSet set = statement.executeQuery();

            if (!(set.next())) {
                throw new NoFoundException("No data found");
            }
            return set.getString(get);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }


    }

    public void setNull(String Table, String ByField, String search, String fieldToSet) {

        try {
            PreparedStatement statement = getPreparedStatement("UPDATE " + Table + " SET " + fieldToSet + " = NULL WHERE " + ByField + "= ? ");
            statement.setObject(1, search);
            statement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void delete(String table, String collum, String value) {
        try {
            PreparedStatement statement = getPreparedStatement("DELETE FROM " + table + " WHERE " + collum + "=?;");
            statement.setObject(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MySQLConnection getConnection() {
        return connection;
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
