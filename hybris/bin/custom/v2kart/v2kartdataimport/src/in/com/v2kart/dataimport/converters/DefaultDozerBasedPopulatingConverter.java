/**
 * 
 */
package in.com.v2kart.dataimport.converters;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.dataimport.dto.BaseDto;

import org.dozer.Mapper;

/**
 * Implementation of Generic Dozer based converter.
 * 
 * @param <SOURCE>
 *        the generic type extending BaseDto
 * @param <TARGET>
 *        the generic type extending ItemModel
 */
public class DefaultDozerBasedPopulatingConverter<SOURCE extends BaseDto, TARGET extends ItemModel> extends
        AbstractPopulatingConverter<SOURCE, TARGET> implements GenericDozerBasedConverter<SOURCE, TARGET> {

    /**
     * ModelService instance
     */
    protected ModelService modelService;

    /**
     * Mapper isntance
     */
    private Mapper dozerBeanMapper;

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
     * @see de.hybris.platform.converters.impl.AbstractConverter#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // do nothing
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
     * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final SOURCE source, final TARGET target) {
        dozerBeanMapper.map(source, target);
        super.populate(source, target);
    }

    /**
     * Gets the dozer bean mapper.
     * 
     * @return the dozerBeanMapper
     */
    public final Mapper getDozerBeanMapper() {
        return dozerBeanMapper;
    }

    /**
     * Sets the dozer bean mapper.
     * 
     * @param dozerBeanMapper
     *        the dozerBeanMapper to set
     */
    public final void setDozerBeanMapper(final Mapper dozerBeanMapper) {
        this.dozerBeanMapper = dozerBeanMapper;
    }

    /**
     * Gets the model service.
     * 
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Sets the model service.
     * 
     * @param modelService
     *        the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
