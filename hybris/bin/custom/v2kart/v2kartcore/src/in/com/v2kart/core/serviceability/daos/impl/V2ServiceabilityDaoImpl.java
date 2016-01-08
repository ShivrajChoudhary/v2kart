package in.com.v2kart.core.serviceability.daos.impl;

import in.com.v2kart.core.model.ServiceabilityAreaModel;
import in.com.v2kart.core.model.V2kartPostalCodeModel;
import in.com.v2kart.core.serviceability.daos.V2ServiceabilityDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.store.BaseStoreModel;

public class V2ServiceabilityDaoImpl extends AbstractItemDao implements V2ServiceabilityDao {

    private static final Logger LOG = Logger.getLogger(V2ServiceabilityDaoImpl.class);


    public boolean checkDeliveryOptionForPinCode(String pinCode, boolean isNet, CountryModel countryModel, CurrencyModel currencyModel,
            BaseStoreModel baseStoreModel) {

        StringBuilder query = new StringBuilder("SELECT DISTINCT {zdm:").append("pk").append("}");
        query.append(" FROM { ").append("ZoneDeliveryModeValue").append(" AS val");
        query.append(" JOIN ").append("ZoneDeliveryMode").append(" AS zdm");
        query.append(" ON {val:").append("deliveryMode").append("}={zdm:").append("pk").append('}');
        query.append(" JOIN ").append("ZoneCountryRelation").append(" AS z2c");
        query.append(" ON {val:").append("zone").append("}={z2c:").append("source").append('}');
        query.append(" JOIN ").append("BaseStore2DeliveryModeRel").append(" AS s2d");
        query.append(" ON {val:").append("deliveryMode").append("}={s2d:").append("target").append('}');

        // Additional check for getting delivery modes through relation "V2PostalCode2ZoneDeliveryModeRel"
        query.append(" JOIN ").append("V2PostalCode2ZoneDeliveryModeRel").append(" AS postalCode2Mode");
        query.append(" ON {postalCode2Mode:").append("target").append("}={zdm:").append("pk").append('}');
        query.append(" JOIN ").append("V2kartPostalCode").append(" AS postalCode");
        query.append(" ON {postalCode2Mode:").append("source").append("}={postalCode:").append("pk").append('}');

        query.append(" } WHERE {val:").append("currency").append("}=?currency");
        query.append(" AND {z2c:").append("target").append("}=?deliveryCountry");
        query.append(" AND {s2d:").append("source").append("}=?store");
        query.append(" AND {zdm:").append("net").append("}=?net");
        query.append(" AND {zdm:").append("active").append("}=?active");
        query.append(" AND {postalCode:").append("pinCode").append("}=?pinCode");

        Map params = new HashMap();
        params.put("deliveryCountry", countryModel);
        params.put("currency", currencyModel);
        params.put("net", isNet);
        params.put("active", Boolean.TRUE);
        params.put("store", baseStoreModel);
        params.put("pinCode", pinCode);

        FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
        fQuery.addQueryParameters(params);
        fQuery.setResultClassList(Collections.singletonList(ZoneDeliveryModeModel.class));
        return CollectionUtils.isNotEmpty(search(fQuery).getResult());

    }
    
    @Override
    public boolean checkDeliveryOptionForPinCode(String pinCode) {
        final String query = "SELECT {pk} FROM {" + V2kartPostalCodeModel._TYPECODE + "} WHERE {" + V2kartPostalCodeModel.PINCODE
                + "} = ?pinCode";

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(V2kartPostalCodeModel.PINCODE, pinCode);
        final SearchResult<V2kartPostalCodeModel> result = getFlexibleSearchService().search(query, params);
        final List<V2kartPostalCodeModel> pinCodes = result.getResult();

        return CollectionUtils.isNotEmpty(pinCodes);
    }
    
    
    
    @Override
    public ServiceabilityAreaModel getServiceabilityAreaForPincode(final String pincode)
    {
        ServiceabilityAreaModel serviceabilityAreaModel = null;

        final String query = "SELECT {pk} FROM {" + ServiceabilityAreaModel._TYPECODE + "}" + "WHERE {"
                + ServiceabilityAreaModel.PINCODE + "} = ?pincode";
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("pincode", pincode);

        final FlexibleSearchQuery flexiblesearchquery = new FlexibleSearchQuery(query, params);
        try {
            serviceabilityAreaModel = getFlexibleSearchService().searchUnique(flexiblesearchquery);
        } catch (ModelNotFoundException e) {
            LOG.debug("No pin code found with code" + pincode);
        }

        return serviceabilityAreaModel;
    }



}
