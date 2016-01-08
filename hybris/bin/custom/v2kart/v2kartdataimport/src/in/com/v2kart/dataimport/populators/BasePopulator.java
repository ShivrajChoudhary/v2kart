/**
 * 
 */
package in.com.v2kart.dataimport.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.dozer.Mapper;

/**
 * The Class BasePopulator.
 * 
 * @author gaurav2007
 */
public class BasePopulator<SOURCE, TARGET> implements Populator<SOURCE, TARGET> {

    private Mapper dozerBeanMapper;

    private ModelService modelService;

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
    public final ModelService getModelService() {
        return modelService;
    }

    /**
     * Sets the model service.
     * 
     * @param modelService
     *        the modelService to set
     */
    public final void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        getDozerBeanMapper().map(source, target);
    }

}
