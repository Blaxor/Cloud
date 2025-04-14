package ro.deiutzblaxo.cloud.data.cache.template;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.deiutzblaxo.cloud.data.cache.interfaces.CacheManager;
import ro.deiutzblaxo.cloud.data.cache.objects.Value;
import ro.deiutzblaxo.cloud.utils.TimeUtils;

import java.util.concurrent.ConcurrentHashMap;


public abstract class CacheManagerTemplate<V> implements CacheManager<String, V> {

    private final static Logger logger = LogManager.getLogger(CacheManagerTemplate.class);
    private final int cacheRetentionSeconds;
    ConcurrentHashMap<String, Value<V>> cache = new ConcurrentHashMap<>();

    public CacheManagerTemplate(int cacheRetentionSeconds) {

        this.cacheRetentionSeconds = cacheRetentionSeconds;


    }

    @Override
    public void putCache(String key, V value) {
        cache.put(key, new Value<>(value, TimeUtils.getFutureEpochSecondsInSeconds(cacheRetentionSeconds, "UTC")));
    }

    @Override
    public void evictCache(String key) {
        cache.remove(key);
    }


    /**
     * @param key the identifier for the cache
     * @return the cached value or null if not found.
     */
    @Override
    public V getCache(String key) {
        Value<V> result = cache.get(key);
        if (result == null) {
            return null;
        }
        return result.getValue();
    }

    @Override
    public void resetCache() {
        cache.clear();
    }

    @Override
    public void evictPeriodically() {
        logger.debug("Working on evicting..." + cache.size());
        for (String key : cache.keySet()) {
            Value<V> value = cache.get(key);
            if (value.getExpirationEpoch() < TimeUtils.getCurrentEpochSeconds("UTC")) {
                logger.debug("Evict cache key " + key);
                evictCache(key);
            }
        }

    }

    @Override
    public int size() {
        return cache.size();
    }
}
