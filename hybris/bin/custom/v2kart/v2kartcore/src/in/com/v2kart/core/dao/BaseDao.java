package in.com.v2kart.core.dao;

import java.util.List;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Base interface for all daos.
 * 
 */
public interface BaseDao<T extends ItemModel> extends GenericDao {

    /**
     * Function returning unique element by code (business key)
     * 
     * It will throw AmbiguousIdentifierException exception if there are multiple elements exist with this identifier Returns null if no
     * element found
     * 
     * @param code
     * @return model item
     */
    public T findUniqueModelByCode(String code);

    /**
     * Function returning list of item by code
     * 
     * @param code
     * @return list of model items
     */
    public List<T> findByCode(String code);

}
