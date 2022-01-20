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
import ro.deiutzblaxo.cloud.nus.PriorityNUS;

import java.util.Locale;
import java.util.UUID;

@Getter(AccessLevel.PROTECTED)
public class NameUUIDStorageMySQL implements NameUUIDStorage {

    private MySQLManager mySQLManager;
    private String table;
    private PriorityNUS priority;

    public NameUUIDStorageMySQL(MySQLConnection connection, int threads, String tablePrefix,PriorityNUS priority) {
        mySQLManager = new MySQLManager(connection, threads);
        table = tablePrefix + "_NameUUIDStorage";
        createTable();
        this.priority = priority;
    }

    public NameUUIDStorageMySQL(MySQLManager manager, String tablePrefix) {
        mySQLManager = manager;
        priority = PriorityNUS.LOW;
        this.table = tablePrefix + "_NameUUIDStorage";
        createTable();
    }

    public boolean exists(@NonNull String value, @NonNull NusType type) {
        switch (type) {
            case NAME:
                String name = value.toLowerCase(Locale.ROOT);
                return mySQLManager.exists(table, "NAME", name);
            case UUID:
                return mySQLManager.exists(table, "UUID", value);
            default:
                return false;
        }

    }

    public void createTable() {
        mySQLManager.createTable(table, new String[]{"NAME varchar(255)", "UUID varchar(255)"});
    }


    public String getUUIDByName(String name) {
        name = name.toLowerCase(Locale.ROOT);

        try {
            return mySQLManager.getLikeString(table, "NAME", name, "UUID");
        } catch (NoFoundException e) {
            return null;
        }

    }

    @Override
    public String getRealName(String fakename) {
        try {
            return mySQLManager.getLikeString(table, "NAME", fakename, "NAME");
        } catch (NoFoundException e) {
            return null;
        }
    }

    @Override
    public PriorityNUS getPriority() {
        return priority;
    }

    public String getNameByUUID(UUID uuid) {

        try {
            return mySQLManager.getLikeString(table, "UUID", uuid.toString(), "NAME");
        } catch (NoFoundException e) {
            return null;
        }

    }

    @SneakyThrows
    public void add(String name, UUID uuid) {
        if (!mySQLManager.exists(table, "UUID", uuid.toString())) {
            mySQLManager.insert(table, new String[]{"UUID", "NAME"}, new Object[]{uuid.toString(), name});
        }
    }
}
