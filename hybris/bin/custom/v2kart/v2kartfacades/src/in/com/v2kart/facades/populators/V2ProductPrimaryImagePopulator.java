package in.com.v2kart.facades.populators;

import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * @author arunkumar
 * 
 */
public class V2ProductPrimaryImagePopulator extends ProductPrimaryImagePopulator<ProductModel, ProductData> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator#getPrimaryImageMediaContainer(de.hybris
     * .platform.core.model.product.ProductModel)
     */
    @Override
    protected MediaContainerModel getPrimaryImageMediaContainer(final ProductModel productModel) {

        MediaContainerModel mediaContainer = null;

        if (productModel instanceof V2kartSizeVariantProductModel) {
            // if product is size variant product
            final ProductModel product = ((VariantProductModel) productModel).getBaseProduct();
            if (product instanceof V2kartStyleVariantProductModel) {
                final List<MediaContainerModel> mediaContainers = product.getGalleryImages();
                if (CollectionUtils.isNotEmpty(mediaContainers)) {
                    mediaContainer = mediaContainers.get(0);
                }
            }
        } else {
            // if product is style variant product
            final List<MediaContainerModel> mediaContainers = productModel.getGalleryImages();
            if (CollectionUtils.isNotEmpty(mediaContainers)) {
                mediaContainer = mediaContainers.get(0);
            }
        }
        return mediaContainer;
    }
}
