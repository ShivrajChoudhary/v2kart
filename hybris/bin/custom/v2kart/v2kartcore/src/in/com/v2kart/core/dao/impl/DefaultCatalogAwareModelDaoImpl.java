package in.com.v2kart.core.dao.impl;

import in.com.v2kart.core.dao.CatalogAwareModelDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;

/**
 * Default implementation of catalog aware model's daos
 * 
 */
public class DefaultCatalogAwareModelDaoImpl<T extends ItemModel> extends DefaultBaseDaoImpl implements CatalogAwareModelDao<T> {

    private final String catalogVersionPropertyName;

    /**
     * @param typecode
     */
    public DefaultCatalogAwareModelDaoImpl(final String typecode, final String codePropertyName, final String catalogVersionPropertyName) {
        super(typecode, codePropertyName);
        this.catalogVersionPropertyName = catalogVersionPropertyName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.dao.CatalogAwareModelDao#findByCodeAndCatalogVersion(java.lang.String,
     * de.hybris.platform.catalog.model.CatalogVersionModel)
     */
    @Override
    public T findByCodeAndCatalogVersion(final String code, final CatalogVersionModel catalogVersion) {
        T item = null;
        final Map<String, Object> params = new HashMap<>();
        params.put(getCodePropertyName(), code);
        params.put(getCatalogVersionPropertyName(), catalogVersion);
        final List<T> items = this.find(params);
        if (CollectionUtils.isNotEmpty(items)) {
            item = items.get(0);
        }
        return item;
    }

    /**
     * @return the configurationTypePropertyName
     */
    public String getCatalogVersionPropertyName() {
        return catalogVersionPropertyName;
    }

}
