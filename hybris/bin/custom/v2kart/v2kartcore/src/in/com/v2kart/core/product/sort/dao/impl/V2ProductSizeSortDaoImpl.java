package in.com.v2kart.core.product.sort.dao.impl;

import in.com.v2kart.core.model.V2ProductSizeModel;
import in.com.v2kart.core.product.sort.dao.V2ProductSizeSortDao;

import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * @author mandeepjolly
 *
 */
public class V2ProductSizeSortDaoImpl extends AbstractItemDao implements V2ProductSizeSortDao{

    /**
     * @param key 
     * 
     */
    @Override
    public List<String> getProductSizeValue() {
        final String query = "SELECT {pk} FROM {" + V2ProductSizeModel._TYPECODE + "} ORDER BY {"+ V2ProductSizeModel.VALUE+"} ASC";
        final SearchResult<V2ProductSizeModel> result = getFlexibleSearchService().search(query);
        final List<V2ProductSizeModel> v2ProductSizeList  = result.getResult();
        List<String> size= new ArrayList<String>();
        for(V2ProductSizeModel v2ProductSize: v2ProductSizeList)
        {
            size.add(v2ProductSize.getKey());
        }
        return size;
        
    }
}
