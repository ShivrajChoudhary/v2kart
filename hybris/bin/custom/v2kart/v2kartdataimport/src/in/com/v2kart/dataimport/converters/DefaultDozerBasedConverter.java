/**
 * 
 */
package in.com.v2kart.dataimport.converters;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.dataimport.dto.BaseDto;

/**
 * Implementation of Generic Dozer based converter
 * 
 */
public class DefaultDozerBasedConverter<SOURCE extends BaseDto, TARGET extends ItemModel> extends BaseConverter<SOURCE, TARGET> implements
        GenericDozerBasedConverter<SOURCE, TARGET> {

    /**
     * Function to create model instance.
     * 
     * @param claz
     *        the claz
     * @return the target
     */
    protected TARGET createTarget(final Class<TARGET> claz) {
        return getModelService().create(claz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#convert(java.lang.Object)
     */
    @Override
    public TARGET convert(final SOURCE source, final Class<TARGET> claz) throws ConversionException {
        final TARGET target = createTarget(claz);
        populate(source, target);
        return target;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final SOURCE source, final TARGET target) {
        getDozerBeanMapper().map(source, target);
    }

}
