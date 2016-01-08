/**
 * 
 */
package in.com.v2kart.dataimport.converters;

import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.model.ModelService;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class BaseConverter.
 * 
 * @author gaurav2007
 */
public abstract class BaseConverter<SOURCE, TARGET> extends AbstractConverter<SOURCE, TARGET> {

    @Autowired
    private ModelService modelService;

    @Autowired
    private Mapper dozerBeanMapper;

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

    /**
     * Gets the dozer bean mapper.
     * 
     * @return the dozerBeanMapper
     */
    public Mapper getDozerBeanMapper() {
        return dozerBeanMapper;
    }

    /**
     * Sets the dozer bean mapper.
     * 
     * @param dozerBeanMapper
     *        the dozerBeanMapper to set
     */
    public void setDozerBeanMapper(final Mapper dozerBeanMapper) {
        this.dozerBeanMapper = dozerBeanMapper;
    }

}
