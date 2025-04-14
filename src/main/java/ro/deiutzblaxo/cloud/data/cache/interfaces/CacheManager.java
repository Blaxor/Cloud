package ro.deiutzblaxo.cloud.data.cache.interfaces;

public interface CacheManager<K, V> {

    void putCache(K key, V value);

    void evictCache(K key);

    V getCache(K key);

    void resetCache();

    void evictPeriodically();

    int size();
}
