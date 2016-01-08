package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

public class V2PriceValueProvider extends V2AbstractPriceInfoValueProvider {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Double addFieldValueForProduct(ProductModel productModel) {
        Double priceValue = 0.0;
        if (null != productModel) {
            List<PriceInformation> priceInformations = this.getPriceService().getPriceInformationsForProduct(productModel);
            if (!CollectionUtils.isEmpty(priceInformations)) {
                PriceInformation price = priceInformations.get(0);
                priceValue = price.getPriceValue().getValue();
            }
        }
        return priceValue;
    }


}
