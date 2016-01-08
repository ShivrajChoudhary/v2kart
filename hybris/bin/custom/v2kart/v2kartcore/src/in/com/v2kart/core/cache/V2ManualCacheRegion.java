/**
 *
 */
package in.com.v2kart.core.cache;

/**
 * V2ManualCacheRegion - Manual Cache region
 * 
 */
public interface V2ManualCacheRegion {

    /**
     * Function to put key value pair in cache
     * 
     * @param key
     *        - key element
     * @param value
     *        - value for the corresponding cache key
     */
    void put(Object key, Object value);

    /**
     * Function to retrieve cached value based upon the passed key
     * 
     * @param key
     *        - cache key
     */
    Object get(Object key);

    /**
     * Function to invalidate/remove cache element
     * 
     * @param key
     */
    void invalidate(Object key);

    /**
     * Function to clear complete cache
     */
    void clear();

}
