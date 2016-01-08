/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

public class GenderValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable {
    private FieldNameProvider fieldNameProvider;

    @Override
    public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty, final Object model)
            throws FieldValueProviderException {
        final ProductModel apparelModel = getProductModel(model);
        if (apparelModel == null) {
            return Collections.emptyList();
        }

        final List<Gender> genders = apparelModel.getGenders();

        if (genders != null && !genders.isEmpty()) {
            final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
            for (final Gender gender : genders) {
                fieldValues.addAll(createFieldValue(gender, indexedProperty));
            }
            return fieldValues;
        } else {
            return Collections.emptyList();
        }
    }

    protected List<FieldValue> createFieldValue(final Gender gender, final IndexedProperty indexedProperty) {
        final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        final Object value = gender.getCode();
        final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
        for (final String fieldName : fieldNames) {
            fieldValues.add(new FieldValue(fieldName, value));
        }
        return fieldValues;
    }

    protected ProductModel getProductModel(Object model) {

        if (model instanceof V2kartSizeVariantProductModel) {
            final V2kartSizeVariantProductModel sizeModel = (V2kartSizeVariantProductModel) model;
            model = sizeModel.getBaseProduct();
        }

        if (model instanceof V2kartStyleVariantProductModel) {
            final V2kartStyleVariantProductModel styleModel = (V2kartStyleVariantProductModel) model;
            model = styleModel.getBaseProduct();
        }

        if (model instanceof ProductModel) {
            return (ProductModel) model;
        } else {
            return null;
        }
    }

    @Required
    public void setFieldNameProvider(final FieldNameProvider fieldNameProvider) {
        this.fieldNameProvider = fieldNameProvider;
    }

}
