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
package in.com.v2kart.facades.populators;

import in.com.v2kart.facades.product.data.GenderData;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * Populates {@link ProductData} with genders
 */
public class V2ProductPopulator implements Populator<ProductModel, ProductData> {
    private Converter<Gender, GenderData> genderConverter;

    protected Converter<Gender, GenderData> getGenderConverter() {
        return genderConverter;
    }

    @Required
    public void setGenderConverter(final Converter<Gender, GenderData> genderConverter) {
        this.genderConverter = genderConverter;
    }

    @Override
    public void populate(final ProductModel source, final ProductData target) throws ConversionException {
        final ProductModel baseProduct = getBaseProduct(source);
    //added by shivraj
    
        if (baseProduct instanceof ProductModel) {
            final ProductModel v2KartProductModel = (ProductModel) baseProduct;
          if(v2KartProductModel.getWeight()!=null && ! (v2KartProductModel.getWeight().isEmpty())){
            target.setWeight(Double.valueOf(v2KartProductModel.getWeight())); 
          }else
          {
        	  target.setWeight(0.0D);
          }
            if (CollectionUtils.isNotEmpty(v2KartProductModel.getGenders())) {
                final List<GenderData> genders = new ArrayList<GenderData>();
                for (final Gender gender : v2KartProductModel.getGenders()) {
                    genders.add(getGenderConverter().convert(gender));
                }
                target.setGenders(genders);
            }
        }
    }

    protected ProductModel getBaseProduct(final ProductModel productModel) {
        ProductModel currentProduct = productModel;
        while (currentProduct instanceof VariantProductModel) {
            final VariantProductModel variant = (VariantProductModel) currentProduct;
            currentProduct = variant.getBaseProduct();
        }
        return currentProduct;
    }
}
