package in.com.v2kart.core.product.sort.impl;

import java.util.List;

import in.com.v2kart.core.model.V2ProductSizeModel;
import in.com.v2kart.core.product.sort.V2ProductSizeSortService;
import in.com.v2kart.core.product.sort.dao.V2ProductSizeSortDao;


public class V2ProductSizeSortServiceImpl implements V2ProductSizeSortService{
    
    private V2ProductSizeSortDao productSizeSortDao;

    /**
     * @return the productSizeSortDao
     */
    public V2ProductSizeSortDao getProductSizeSortDao() {
        return productSizeSortDao;
    }

    /**
     * @param productSizeSortDao the productSizeSortDao to set
     */
    public void setProductSizeSortDao(V2ProductSizeSortDao productSizeSortDao) {
        this.productSizeSortDao = productSizeSortDao;
    }

    @Override
    public List<String> getProductSizeValue() {        
        List<String> v2ProductSize=productSizeSortDao.getProductSizeValue();
        return v2ProductSize;
    }
    
    

}
