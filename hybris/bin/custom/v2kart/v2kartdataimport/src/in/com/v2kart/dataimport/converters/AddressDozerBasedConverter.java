/**
 * 
 */
package in.com.v2kart.dataimport.converters;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import in.com.v2kart.dataimport.dto.AddressDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * The Class AddressDozerBasedConverter.
 * 
 * @author gaurav2007
 */
public class AddressDozerBasedConverter extends BaseConverter<AddressDto, AddressModel> {

    @Resource(name = "regionModelDao")
    private GenericDao<RegionModel> regionModelDao;

    @Resource(name = "countryModelDao")
    private GenericDao<CountryModel> countryModelDao;

    private static final Logger LOG = Logger.getLogger(AddressDozerBasedConverter.class);

    private String countryParamName = CountryModel.ISOCODE;

    private String regionParamName = RegionModel.ISOCODE;

    /**
     * Function to create model instance.
     * 
     * @return the address model
     * @deprecated
     */
    @Deprecated
    @Override
    protected final AddressModel createTarget() {
        return getModelService().create(AddressModel.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public final void populate(final AddressDto source, final AddressModel target) {
        getDozerBeanMapper().map(source, target);

        // attach region
        final RegionModel regionModel = processModel(regionModelDao, regionParamName, source.getState());
        if (regionModel == null) {
            LOG.warn("Cannot find Region model for:" + source.getState());
        } else {
            target.setRegion(regionModel);
        }

        // attach country
        final CountryModel countryModel = processModel(countryModelDao, countryParamName, source.getCountry());
        if (countryModel == null) {
            LOG.warn("Cannot find country model for:" + source.getCountry());
        } else {
            target.setCountry(countryModel);
        }
    }

    /**
     * Process model.
     * 
     * @param <T>
     *        the generic type
     * @param dao
     *        the dao
     * @param paramName
     *        the param name
     * @param paramValue
     *        the param value
     * @return the t
     */
    private <T extends ItemModel> T processModel(final GenericDao<T> dao, final String paramName, final String paramValue) {
        T model = null;
        if (paramName != null && paramValue != null) {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put(paramName, paramValue);
            final List<T> models = dao.find(params);
            if (CollectionUtils.isNotEmpty(models)) {
                model = models.get(0);
            }
        }
        return model;
    }

    /**
     * Sets the country param name.
     * 
     * @param countryParamName
     *        the countryParamName to set
     */
    public void setCountryParamName(final String countryParamName) {
        this.countryParamName = countryParamName;
    }

    /**
     * Sets the region param name.
     * 
     * @param regionParamName
     *        the regionParamName to set
     */
    public void setRegionParamName(final String regionParamName) {
        this.regionParamName = regionParamName;
    }

}
