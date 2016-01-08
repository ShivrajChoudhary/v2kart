package in.com.v2kart.core.dao.impl;

import in.com.v2kart.core.dao.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * Default implementation for BaseDao
 * 
 */
public class DefaultBaseDaoImpl<T extends ItemModel> extends DefaultGenericDao implements BaseDao<T> {

    /**
     * attribute for code property name
     */
    protected String codePropertyName;

    /**
     * @param typecode
     */
    public DefaultBaseDaoImpl(final String typecode, final String codePropertyName) {
        super(typecode);
        this.codePropertyName = codePropertyName;
    }

    
    /**
     * @return the codePropertyName
     */
    public String getCodePropertyName() {
        return codePropertyName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.dao.BaseDao#findUniqueModelByCode(java.lang.String)
     */
    @Override
    public T findUniqueModelByCode(final String code) {
        final List<T> items = this.findByCode(code);
        T item = null;
        if (CollectionUtils.isNotEmpty(items)) {
            if (items.size() == 1) {
                item = items.get(0);
            } else {
                throw new AmbiguousIdentifierException("No unique element found. Ambiguous identifier passed : " + code);
            }
        }
        return item;
    }

   
    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.dao.BaseDao#findByCode(java.lang.String)
     */
    @Override
    public List<T> findByCode(final String code) {
        final Map<String, Object> params = new HashMap<>();
        params.put(getCodePropertyName(), code);
        return this.find(params);
    }

    

}
