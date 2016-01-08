/**
 * 
 */
package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import java.util.Collection;
import java.util.Collections;

import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.util.Feature;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.impl.ClassificationPropertyValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * @author shubhammaheshwari
 * Custom V2CommerceClassificationPropertyValueProvider created to load data from the Base Product of a Child Products for CSCockpit
 */
public class V2CSCommerceClassificationPropertyValueProvider extends ClassificationPropertyValueProvider {

    public Collection<FieldValue> getFieldValues(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model)
            throws FieldValueProviderException {
    	
        if ((null != model) && model instanceof ProductModel) {
            ClassAttributeAssignmentModel classAttributeAssignmentModel = indexedProperty.getClassAttributeAssignment();
            ClassAttributeAssignment classAttributeAssignment = (ClassAttributeAssignment) this.modelService
                    .getSource(classAttributeAssignmentModel);
            
            // load the data from the Style Variant (Base Product) of Size Variant
            VariantProductModel productModel = (VariantProductModel)model;
            if(null != productModel.getBaseProduct()){
                Product product = (Product) this.modelService.getSource(productModel.getBaseProduct());
                FeatureContainer cont = FeatureContainer.loadTyped(product, new ClassAttributeAssignment[] { classAttributeAssignment });
                if (cont.hasFeature(classAttributeAssignment)) {
                    Feature feature = cont.getFeature(classAttributeAssignment);
                    if ((feature == null) || (feature.isEmpty())) {
                        return Collections.emptyList();

                    }
                    return getFeaturesValues(indexConfig, feature, indexedProperty);
                }
                return Collections.emptyList();
            }
        }
        throw new FieldValueProviderException("Cannot provide classification property of non-product item");
    }
}
