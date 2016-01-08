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

import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import in.com.v2kart.core.enums.SwatchColorEnum;
import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

public class ColorFacetValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private FieldNameProvider fieldNameProvider;

    @Override
    public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty, final Object model)
            throws FieldValueProviderException {
        final V2kartStyleVariantProductModel v2KartStyleModel = getV2KartStyleProductModel(model);
        if (v2KartStyleModel == null) {
            return Collections.emptyList();
        }

        // to get color facet from primary color value of v2kartStyleVariantProduct
        final List<FieldValue> fieldValues = new ArrayList<>(0);
        final SwatchColorEnum color = v2KartStyleModel.getPrimaryColor();
        if (null != color) {
            fieldValues.addAll(createFieldValue(color, indexedProperty));
        }

        return fieldValues;

        // uncomment below code to get facet from swatch color enum (Default OOTB implementation)
        /*
         * final Set<SwatchColorEnum> colors = v2KartStyleModel.getSwatchColors();
         * 
         * if (colors != null && !colors.isEmpty()) { final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>(); for (final
         * SwatchColorEnum color : colors) { fieldValues.addAll(createFieldValue(color, indexedProperty)); } return fieldValues; } else {
         * return Collections.emptyList(); }
         */
    }

    protected List<FieldValue> createFieldValue(final SwatchColorEnum color, final IndexedProperty indexedProperty) {
        final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        final Object value = color.getCode();
        final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
        for (final String fieldName : fieldNames) {
            fieldValues.add(new FieldValue(fieldName, value));
        }
        return fieldValues;
    }

    protected V2kartStyleVariantProductModel getV2KartStyleProductModel(Object model) {
        if (model instanceof V2kartSizeVariantProductModel) {
            final V2kartSizeVariantProductModel sizeModel = (V2kartSizeVariantProductModel) model;
            model = sizeModel.getBaseProduct();
        }

        if (model instanceof V2kartStyleVariantProductModel) {
            return (V2kartStyleVariantProductModel) model;
        } else {
            return null;
        }
    }

    @Required
    public void setFieldNameProvider(final FieldNameProvider fieldNameProvider) {
        this.fieldNameProvider = fieldNameProvider;
    }

}
