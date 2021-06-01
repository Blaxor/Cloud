package ro.deiutzblaxo.cloud.data.redis;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisConnection {

    final JedisPoolConfig poolConfig = buildPoolConfig();
    @Getter
    public JedisPool jedisPool;
    private String hostname;
    private int port;
    private String password;
    private String user;


    public RedisConnection(String hostname, int port, String user, String password) {
        this.hostname = hostname;
        this.password = password;
        this.port = port;
        this.user = user;
        connect(hostname, port, user, password);
    }

    public void connect(String hostname, int port, String user, String password) {
        if (password == null || password == "")
            jedisPool = new JedisPool(poolConfig, hostname, port);
        else if (user == null || user == "")
            jedisPool = new JedisPool(poolConfig, hostname, port, 1000, password);
        else
            jedisPool = new JedisPool(poolConfig, hostname, port, user, password);
    }

    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);

        }
    }

    public String get(String key) {
        String value;
        try (Jedis jedis = jedisPool.getResource()) {
            value = jedis.get(key);

        }

        return value == "nil" ? null : value;
    }


    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(64);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }


}
