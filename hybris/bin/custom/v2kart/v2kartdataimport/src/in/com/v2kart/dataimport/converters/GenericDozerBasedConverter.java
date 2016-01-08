/**
 * 
 */
package in.com.v2kart.dataimport.converters;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.dataimport.dto.BaseDto;

/**
 * GenericDozerBasedConverter interface for converting csv dtos to hybris models.
 * 
 * This is used for creating single generic converter where dozer mapping alone is enough for populating model instances.
 * 
 * @param <SOURCE>
 *        the generic type that extends BaseDto
 * @param <TARGET>
 *        the generic type that extends ItemModel
 */
public interface GenericDozerBasedConverter<SOURCE extends BaseDto, TARGET extends ItemModel> {

    /**
     * Function to convert from source dto to hybris model.
     * 
     * @param source
     *        - dto instance
     * @param claz
     *        - type of model class
     * @return model instance
     * @throws ConversionException
     *         the conversion exception
     */
    TARGET convert(final SOURCE source, final Class<TARGET> claz) throws ConversionException;

    /**
     * Function to populate existing model instance from source dto.
     * 
     * @param source
     *        the source
     * @param target
     *        the target
     */
    void populate(final SOURCE source, final TARGET target);

}
