package ro.deiutzblaxo.cloud.nus.prefab;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import ro.deiutzblaxo.cloud.data.redis.RedisConnection;
import ro.deiutzblaxo.cloud.nus.NameUUIDStorage;

import java.util.Locale;
import java.util.UUID;

@Getter(AccessLevel.PROTECTED)
public class NameUUIDStorageRedis implements NameUUIDStorage {

    private RedisConnection redisConnection;
    private String table;

    public NameUUIDStorageRedis(RedisConnection redisConnection, String tablePrefix) {
        this.redisConnection = redisConnection;
        table = tablePrefix + "_NameUUIDStorage";

    }


    public String getUUIDByName(String name) {
        name = name.toLowerCase(Locale.ROOT);
        String value = redisConnection.get(table + name);
        return value == null ? null : value;


    }

    public String getNameByUUID(UUID uuid) {

        String value = redisConnection.get(table + uuid.toString());
        return value == null ? null : value;


    }

    @SneakyThrows
    public void add(String name, UUID uuid) {
        redisConnection.set(table + name, uuid.toString());
        redisConnection.set(table + uuid, name);
    }
}
