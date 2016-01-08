package in.com.v2kart.core.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;

/**
 * Base interface for CatalogAwareModelDao.
 * 
 * @author arunkumar
 * 
 *         It provides the common APIs for the models which are catalog aware.
 * @param <T>
 */
public interface CatalogAwareModelDao<T> extends BaseDao {

    /**
     * Returns model based upon the code and catalogVersionModel. They together makes a business key here.
     * 
     * @param code
     * @param catalogVersionModel
     * @return model item
     */
    public T findByCodeAndCatalogVersion(String code, CatalogVersionModel catalogVersionModel);
}
