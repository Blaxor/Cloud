package ro.deiutzblaxo.cloud.nus.prefab;

import ro.deiutzblaxo.cloud.data.redis.RedisConnection;
import ro.deiutzblaxo.cloud.nus.NameUUIDStorage;
import ro.deiutzblaxo.cloud.nus.PriorityNUS;

import java.util.Locale;
import java.util.UUID;

public class NameUUIDStorageRedis implements NameUUIDStorage {

    private RedisConnection redisConnection;
    private String table;
    private PriorityNUS priority;

    public NameUUIDStorageRedis(RedisConnection redisConnection, String tablePrefix,PriorityNUS priority) {
        this.redisConnection = redisConnection;
        table = tablePrefix + "NameUUIDStorage";
        this.priority = priority;


    }

    public NameUUIDStorageRedis(RedisConnection redisConnection, String tablePrefix) {
        this.redisConnection = redisConnection;
        table = tablePrefix + "NameUUIDStorage";
        this.priority = PriorityNUS.HIGH;

    }


    public String getUUIDByName(String name) {
        name = name.toLowerCase(Locale.ROOT);
        String value = redisConnection.get(table + name);
        return value == null ? null : value;


    }

    @Override
    public String getRealName(String fakename) {
        return null;
    }

    @Override
    public  PriorityNUS getPriority() {
        return priority;
    }

    public String getNameByUUID(UUID uuid) {

        String value = redisConnection.get(table + uuid.toString());
        return value == null ? null : value;


    }


    public void add(String name, UUID uuid) {
        redisConnection.set(table + name, uuid.toString());
        redisConnection.set(table + uuid, name);
    }
}
