package ro.deiutzblaxo.cloud.nus.prefab;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import ro.deiutzblaxo.cloud.data.mysql.MySQLConnection;
import ro.deiutzblaxo.cloud.data.mysql.MySQLManager;
import ro.deiutzblaxo.cloud.expcetions.NoFoundException;
import ro.deiutzblaxo.cloud.nus.NameUUIDStorage;
import ro.deiutzblaxo.cloud.nus.NusType;

import java.util.Locale;
import java.util.UUID;

@Getter(AccessLevel.PROTECTED)
public class NameUUIDStorageMySQL implements NameUUIDStorage {

    private MySQLManager mySQLManager;
    private String table;

    public NameUUIDStorageMySQL(MySQLConnection connection, int threads, String tablePrefix) {
        mySQLManager = new MySQLManager(connection, threads);
        table = tablePrefix + "_NameUUIDStorage";
        createTable();
    }

    public NameUUIDStorageMySQL(MySQLManager manager, String tablePrefix) {
        mySQLManager = manager;
        createTable();
    }

    public boolean exists(@NonNull String value, @NonNull NusType type) {
        switch (type) {
            case NAME:
                String name = value.toLowerCase(Locale.ROOT);
                return mySQLManager.exists(table, "name", name);
            case UUID:
                return mySQLManager.exists(table, "uuid", value);
            default:
                return false;
        }

    }

    public void createTable() {
        mySQLManager.createTable(table, new String[]{"name varchar(255)", "uuid varchar(255)"});
    }


    public String getUUIDByName(String name) {
        name = name.toLowerCase(Locale.ROOT);

        try {
            return mySQLManager.get(table, "uuid", "name", name, String.class);
        } catch (NoFoundException e) {
            return null;
        }

    }

    public String getNameByUUID(UUID uuid) {

        try {
            return mySQLManager.get(table, "name", "uuid", uuid.toString(), String.class);
        } catch (NoFoundException e) {
            return null;
        }

    }

    @SneakyThrows
    public void add(String name, UUID uuid) {
        if (!mySQLManager.exists(table, "uuid", uuid.toString())) {
            mySQLManager.insert(table, new String[]{"uuid", "name"}, new Object[]{uuid.toString(), name});
        }
    }
}
