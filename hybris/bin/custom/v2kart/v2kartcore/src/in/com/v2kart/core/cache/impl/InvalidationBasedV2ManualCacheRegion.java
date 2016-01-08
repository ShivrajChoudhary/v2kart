/**
 *
 */
package in.com.v2kart.core.cache.impl;

import de.hybris.platform.cache.InvalidationListener;
import de.hybris.platform.cache.InvalidationManager;
import de.hybris.platform.cache.InvalidationTarget;
import de.hybris.platform.cache.InvalidationTopic;
import de.hybris.platform.cache.RemoteInvalidationSource;
import de.hybris.platform.core.Registry;

import in.com.v2kart.core.cache.V2ManualCacheRegion;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

/**
 * InvalidationBasedV2ManualCacheRegion - Invalidation based abstract implement of V2ManualCacheRegion
 * 
 * This internally uses the Hybris invalidation framework to invalidate cache across cluster nodes
 * 
 * @see {@link InvalidationManager}, @see {@link InvalidationTopic} and @see {@link InvalidationListener}
 * 
 * 
 */
public abstract class InvalidationBasedV2ManualCacheRegion implements V2ManualCacheRegion, InvalidationListener {

    private static final Logger LOG = Logger.getLogger(InvalidationBasedV2ManualCacheRegion.class);

    protected static final int INVALIDATIONTYPE_MODIFIED = 1;
    protected static final int INVALIDATIONTYPE_CLEARALL = 2;

    /**
     * cache name
     */
    protected String cacheName;
    /**
     * Eviction policy - default set to LFU
     */
    protected String evictionPolicy = "LFU";
    /**
     * Max entries - default value 200
     */
    protected Long maxEntries = new Long(200);
    /**
     * Time to live seconds, default value is null i.e. cache element will live forever
     */
    protected Long ttlSeconds = null;

    /**
     * Invalidation topic
     */
    protected InvalidationTopic invalidationTopic;

    /**
     * @param cacheName
     */
    public InvalidationBasedV2ManualCacheRegion(final String cacheName) {
        this.cacheName = cacheName;

    }

    @PostConstruct
    public void init() {
        this.initCacheProvider();
        this.initInvalidationListener();
    }

    /**
     * Function to initialize underlying cache repository
     */
    protected abstract void initCacheProvider();

    /**
     * Function to remove cache element
     * 
     * @param key
     */
    protected abstract void removeCacheElement(Object key);

    /**
     * Function to remove all cache elements
     */
    protected abstract void removeAllCacheElements();

    /**
     * Function to initialize invalidation listener
     */
    protected void initInvalidationListener() {
        invalidationTopic = Registry.getCurrentTenant().getInvalidationManager().getOrCreateInvalidationTopic(this.generateTopicName());
        invalidationTopic.addInvalidationListener(this);
    }

    /**
     * Generates the topic name using alpha numeric characters of cache name
     */
    protected Object[] generateTopicName() {
        return new Object[] { this.cacheName.replaceAll("\\P{Alnum}", "") };
    }

    /**
     * @param key
     * @param invalidationType
     */
    public void invalidate(final Object key, final int invalidationType) {
        LOG.debug("Invalidating cache key  " + key + " , Invalidation Type : " + invalidationType);
        invalidationTopic.invalidate(this.createKey(key), invalidationType);
    }

    /**
     * @param key
     */
    private Object[] createKey(final Object key) {
        return new Object[] { key };
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.cache.InvalidationListener#keyInvalidated(java.lang.Object[], int,
     * de.hybris.platform.cache.InvalidationTarget, de.hybris.platform.cache.RemoteInvalidationSource)
     */
    @Override
    public void keyInvalidated(final Object[] paramArrayOfObject, final int paramInt, final InvalidationTarget paramInvalidationTarget,
            final RemoteInvalidationSource paramRemoteInvalidationSource) {
        if (paramInt == INVALIDATIONTYPE_CLEARALL) {
            LOG.info("Listener invoked for cache invalidation. Removing all cache elements");
            this.removeAllCacheElements();
        } else {
            LOG.info("Listener invoked for cache invalidation. Cache key : " + paramArrayOfObject[0]);
            this.removeCacheElement(paramArrayOfObject[0]);
        }
    }

    /**
     * @return the cacheName
     */
    public String getCacheName() {
        return cacheName;
    }

    /**
     * @return the evictionPolicy
     */
    public String getEvictionPolicy() {
        return evictionPolicy;
    }

    /**
     * @param evictionPolicy
     *        the evictionPolicy to set
     */
    public void setEvictionPolicy(final String evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    /**
     * @return the maxEntries
     */
    public Long getMaxEntries() {
        return maxEntries;
    }

    /**
     * @param maxEntries
     *        the maxEntries to set
     */
    public void setMaxEntries(final Long maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * @return the ttlSeconds
     */
    public Long getTtlSeconds() {
        return ttlSeconds;
    }

    /**
     * @param ttlSeconds
     *        the ttlSeconds to set
     */
    public void setTtlSeconds(final Long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

}
