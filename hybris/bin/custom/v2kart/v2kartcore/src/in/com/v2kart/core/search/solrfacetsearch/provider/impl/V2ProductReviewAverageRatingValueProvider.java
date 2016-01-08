/**
 * 
 */
package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import java.util.Iterator;

import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ProductReviewAverageRatingValueProvider;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * @author shubhammaheshwari ProductReviewAverageRatingValueProvider to override the product rating calculation function. Calculate the
 *         product rating for Style + Size Variant
 */
public class V2ProductReviewAverageRatingValueProvider extends ProductReviewAverageRatingValueProvider {

    public V2ProductReviewAverageRatingValueProvider() {

    }
    
    @Override
    protected Double getProductRating(ProductModel product, LanguageModel language) {
        double sum = 0.0D;
        int count = 0;
        
        // first call super to calculate the product rating for style vairant.
        Double styleVaraintProductRatingSum = super.getProductRating(product, language);
        if(null != styleVaraintProductRatingSum){
            sum = styleVaraintProductRatingSum.doubleValue();
        }
        
        // get all the variant of the product and calculate rating for each
        for(Iterator<VariantProductModel> sizeVariantProductIterator = product.getVariants().iterator();sizeVariantProductIterator.hasNext();){
            VariantProductModel variantProduct = sizeVariantProductIterator.next();
            for (Iterator<CustomerReviewModel> iterator = getCustomerReviewService().getReviewsForProduct(variantProduct).iterator(); iterator.hasNext();) {
                CustomerReviewModel review = (CustomerReviewModel) iterator.next();
                if ((language == null || language.equals(review.getLanguage())) && review.getRating() != null
                        && review.getApprovalStatus() == CustomerReviewApprovalType.APPROVED) {
                    count++;
                    sum += review.getRating().doubleValue();
                }
            }
        }

        if (count > 0)
            return Double.valueOf(sum / (double) count);
        else
            return null;
    }
}
