package in.com.v2kart.facades.product.converters.populator;

import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.converters.populator.ProductClassificationPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * V2Kart Classification populator.
 * 
 * @author vikrant2480
 * 
 */
public class V2ProductClassificationPopulator extends ProductClassificationPopulator<ProductModel, ProductData> {

    @Override
    public void populate(ProductModel productModel, ProductData productData) throws ConversionException {

        FeatureList featureList = null;


        if (productModel instanceof V2kartSizeVariantProductModel) {
            // if product is size variant product, get classification features from size product
     //comment by Shivraj
        //   final ProductModel product = ((VariantProductModel) productModel).getBaseProduct();
         // if (product instanceof V2kartSizeVariantProductModel) {
               featureList = getClassificationService().getFeatures(productModel);
         //  }
       } else {
            // if product is style variant product
            featureList = getClassificationService().getFeatures(productModel);
        }
        if (featureList != null && !featureList.getFeatures().isEmpty())
        {
            getProductFeatureListPopulator().populate(featureList, productData);
        }
    }
}
