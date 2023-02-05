package ro.deiutzblaxo.cloud.data.mysql;

import org.checkerframework.checker.nullness.qual.NonNull;
import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.expcetions.ToManyArgs;

import java.sql.*;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public interface MySQLManager {

    default <T> void insert(@NonNull String table, @NonNull String[] columns, @NonNull T[] values) throws ToManyArgs {

        if (columns.length != values.length)
            throw new ToManyArgs("Too many/less arguments!");

        try {
            StringBuilder builder = new StringBuilder("INSERT INTO " + table + " (");
            for (int i = 0; i < columns.length; i++) {
                builder.append(columns[i]).append(i < columns.length - 1 ? "," : ") VALUES (");
            }
            for (int i = 0; i < values.length; i++) {
                builder.append("?").append(i < values.length - 1 ? "," : ");");
            }
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(builder.toString());

                for (int i = 1; i <= values.length; i++) {
                    statement.setObject(i, values[i - 1]);
                }
                statement.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

 /*   default boolean execute(@NonNull String sql) {

        try {
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                return statement.execute();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }


    }

    default int executeUpdate(@NonNull String sql) {

        try {
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                return statement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    default ResultSet executeQuery(@NonNull String sql) {


        try {

                PreparedStatement statement = connection.prepareStatement(sql);
                return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }


    }*/

    default <T> boolean exists(@NonNull String table, @NonNull String collum, @NonNull T value) {
        try {
            try (Connection connection = getConnection().getConnection()) {
                ResultSet set = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + collum + "='" + value + "';").executeQuery();
                return set.next();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }


    default <T> void update(@NonNull String table, @NonNull String keyCollum, @NonNull T key, @NonNull String[] valuesCollum, @NonNull T[] values) throws
            ToManyArgs {

        if (valuesCollum.length != values.length)
            throw new ToManyArgs("Too many/less arguments!");

        try {
            StringBuilder builder = new StringBuilder("UPDATE " + table + " SET ");
            for (int i = 0; i < valuesCollum.length; i++) {
                builder.append(valuesCollum[i]).append(" = ? ").append(i < valuesCollum.length - 1 ? "," : " ");
            }
            builder.append("WHERE ").append(keyCollum).append(" = ? ;");
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(builder.toString());

                int i = 1;
                for (; i <= valuesCollum.length; i++) {
                    statement.setObject(i, values[i - 1]);
                }
                statement.setObject(i, key);
                statement.executeUpdate();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    default HashMap<String, Object> gets(@NonNull String table, @NonNull String[] valueColumns, @NonNull String keyColumn, @NonNull Object key, @NonNull Class[] types) {
        String query = "SELECT ";
        for (int i = 0; i < valueColumns.length; i++) {
            query += valueColumns[i];
            if (i < valueColumns.length - 1) {
                query += ",";
            }
        }
        query += " FROM " + table + " WHERE " + keyColumn + " = ? ;";
        try {
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query);


                statement.setObject(1, key);
                ResultSet r = statement.executeQuery();
                if (!r.next())
                    throw new NoFoundException("Data not found in table " + table + ", keyColumn:" + keyColumn + ", key:" + key + ", valueColumn: " + valueColumns);
                HashMap<String, Object> hashMap = new HashMap<>();
                for (int i = 0; i < valueColumns.length; i++) {
                    if (types[i] == Blob.class)
                        hashMap.put(valueColumns[i], r.getBlob(valueColumns[i]));
                    else
                        hashMap.put(valueColumns[i], r.getObject(valueColumns[i]));
                }
                return hashMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }


    }

    default <T> T get(@NonNull String table, @NonNull String valueColumn, @NonNull String keyColumn, @NonNull Object key, @NonNull Class<T> type) throws NoFoundException {
        String query = "SELECT " + valueColumn + " FROM " + table + " WHERE " + keyColumn + " = ? ;";

        try {
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setObject(1, key);

                ResultSet r = statement.executeQuery();
                if (!r.next())
                    throw new NoFoundException("Data not found in table " + table + ", keyColumn:" + keyColumn + ", key:" + key + ", valueColumn: " + valueColumn);
                if (type == Blob.class)
                    return (T) r.getBlob(valueColumn);
                return r.getObject(valueColumn, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    default String getString(@NonNull String table, @NonNull String valueColumn, @NonNull String keyColumn, @NonNull Object key) throws NoFoundException {
        return get(table, valueColumn, keyColumn, key, String.class);

    }

    default void createTable(@NonNull String table, @NonNull String... columns) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table + " (");
        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i]).append(i == columns.length - 1 ? ");" : ",");
        }
        try {
            try (Connection connection = getConnection().getConnection()) {
                connection.prepareStatement(builder.toString()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void createDataBase(@NonNull String database) {

        try {
            try (Connection connection = getConnection().getConnection()) {

                PreparedStatement statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS ?");
                statement.setString(1, database);
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    default void deleteRow(String table, String ByField, String field) {
        try {
            try (Connection con = getConnection().getConnection()) {
                con.prepareStatement("DELETE FROM " + table + " WHERE " + ByField + " = '" + field + "';").execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }

    default String getLikeString(String table, String byField, String field, String get) throws NoFoundException {

        try {
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + byField + " LIKE ? ;");


                statement.setObject(1, field);
                ResultSet set = statement.executeQuery();

                if (!(set.next())) {
                    throw new NoFoundException("No data found");
                }
                return set.getString(get);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }


    }

    default void setNull(String Table, String ByField, String search, String fieldToSet) {

        try {
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE " + Table + " SET " + fieldToSet + " = NULL WHERE " + ByField + "= ? ");
                statement.setObject(1, search);
                statement.executeUpdate();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    default void delete(String table, String collum, String value) {
        try {
            try (Connection connection = getConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM " + table + " WHERE " + collum + "=?;");
                statement.setObject(1, value);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    MySQLConnection getConnection();

    ExecutorService getPool();

    void close();

    default ResultSet getPreparedStatement(String string) throws SQLException {

        try (Connection connection = getConnection().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(string);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw e;
        }
    }

    default PreparedStatement getPrepareStatement(String string) throws SQLException {
        return getConnection().getConnection().prepareStatement(string);
    }
}
