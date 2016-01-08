package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.enums.BoostProductEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercesearch.model.SolrBoostRuleModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.impl.SpELValueProvider;

/**
 * @author arunkumar
 * 
 */
public class V2ProductBoostingValueProvider extends SpELValueProvider {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private FieldNameProvider v2fieldNameProvider;
    
    @Override
    public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
            final Object model) throws FieldValueProviderException
    {
        final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        final ProductModel product = (ProductModel) model;
        final BoostProductEnum boostingFactor = product.getBoostingFactor();
        
        final List<SolrBoostRuleModel> boostRules = new ArrayList<SolrBoostRuleModel>(indexedProperty.getBoostRules());
        for (final SolrBoostRuleModel boostRuleModel : boostRules) {
            if((null != boostingFactor) && (boostingFactor.getCode().equalsIgnoreCase(boostRuleModel.getPropertyValue()))){
                createFieldValue(fieldValues, indexedProperty, Integer.valueOf(boostRuleModel.getBoostFactor()));
            }
        }
        return fieldValues;
    }

    protected void createFieldValue(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty, final Object value)
    {
        final Collection<String> fieldNames = getV2fieldNameProvider().getFieldNames(indexedProperty, null);
        for (final String fieldName : fieldNames)
        {
            fieldValues.add(new FieldValue(fieldName, value));
        }
    }

    public FieldNameProvider getV2fieldNameProvider() {
        return v2fieldNameProvider;
    }

    @Required
    public void setV2fieldNameProvider(FieldNameProvider v2fieldNameProvider) {
        this.v2fieldNameProvider = v2fieldNameProvider;
    }
  

   
    
}
