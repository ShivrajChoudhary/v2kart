/**
 *
 */
package in.com.v2kart.core.cache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.log4j.Logger;

/**
 * DefaultV2ManualCacheRegion - Default implemention of V2ManualCacheRegion
 * 
 * This internally uses the EHCahce repository to maintain cached value
 * 
 */
public class DefaultV2ManualCacheRegion extends InvalidationBasedV2ManualCacheRegion {

    private static final Logger LOG = Logger.getLogger(DefaultV2ManualCacheRegion.class);

    /**
     * EHCache Manager
     */
    private CacheManager cacheManager;
    /**
     * Cache instance
     */
    private Cache cacheMap;

    /**
     * @param cacheName
     */
    public DefaultV2ManualCacheRegion(final String cacheName) {
        super(cacheName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.cache.impl.InvalidationBasedV2ManualCacheRegion#initCacheProvider()
     */
    @Override
    protected void initCacheProvider() {
        this.cacheManager = CacheManager.getInstance();
        if (!this.cacheManager.cacheExists(this.cacheName)) {
            final Cache cacheMap = new Cache(this.createCacheConfiguration());
            this.cacheManager.addCache(cacheMap);
            this.cacheMap = this.cacheManager.getCache(this.cacheName);
        }
    }

    /**
     * Creates EHCache configuration
     */
    private CacheConfiguration createCacheConfiguration() {
        final CacheConfiguration config = new CacheConfiguration();
        config.setStatistics(false);
        LOG.info("V2ManualCacheRegion " + this.cacheName + ", eviction policy " + this.evictionPolicy + ", size " + this.maxEntries);
        config.setMemoryStoreEvictionPolicy(this.evictionPolicy.toString());
        config.setMaxElementsOnDisk(1);
        config.setMaxEntriesLocalHeap(this.maxEntries.longValue());
        config.setName(this.cacheName);
        config.overflowToDisk(false);
        if (this.ttlSeconds == null) {
            config.setEternal(true);
        } else {
            config.setEternal(false);
            config.setTimeToLiveSeconds(this.ttlSeconds.longValue());
        }

        return config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.cache.V2ManualCacheRegion#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(final Object key, final Object value) {
        this.cacheMap.put(new Element(key, value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.fantasticfurniture.core.cache.FFManualCacheRegion#get(java.lang.Object)
     */
    @Override
    public Object get(final Object key) {
        final Element element = this.cacheMap.get(key);
        Object returnedValue = null;
        if (element != null) {
            returnedValue = element.getObjectValue();
        }
        return returnedValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.cache.V2ManualCacheRegion#invalidate(java.lang.Object)
     */
    @Override
    public void invalidate(final Object key) {
        this.invalidate(key, INVALIDATIONTYPE_MODIFIED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.cache.V2ManualCacheRegion#clear()
     */
    @Override
    public void clear() {
        for (final Object key : this.cacheMap.getKeys()) {
            this.invalidate(key, INVALIDATIONTYPE_CLEARALL);
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.cache.impl.InvalidationBasedV2ManualCacheRegion#removeCacheElement(java.lang.Object)
     */
    @Override
    protected void removeCacheElement(final Object key) {
        this.cacheMap.remove(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.cache.impl.InvalidationBasedV2ManualCacheRegion#removeAllCacheElements()
     */
    @Override
    protected void removeAllCacheElements() {
        this.cacheMap.removeAll();
    }

}
