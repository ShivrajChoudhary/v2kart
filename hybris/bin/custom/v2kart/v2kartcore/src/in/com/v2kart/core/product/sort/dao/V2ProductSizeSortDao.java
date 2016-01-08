package in.com.v2kart.core.product.sort.dao;

import java.util.List;

import in.com.v2kart.core.model.V2ProductSizeModel;

/**
 * @author mandeepjolly
 *
 */
public interface V2ProductSizeSortDao {

    
    
    /**
     * @param key
     * @return v2ProductSizeModel
     */
    List<String> getProductSizeValue();
    
}
