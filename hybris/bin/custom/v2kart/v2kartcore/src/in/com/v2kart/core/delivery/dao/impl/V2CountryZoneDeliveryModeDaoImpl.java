package in.com.v2kart.core.delivery.dao.impl;

import in.com.v2kart.core.delivery.dao.V2CountryZoneDeliveryModeDao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.commerceservices.delivery.dao.impl.DefaultCountryZoneDeliveryModeDao;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.jalo.ZoneDeliveryModeValue;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.util.PriceValue;

@SuppressWarnings("all")
public class V2CountryZoneDeliveryModeDaoImpl extends DefaultCountryZoneDeliveryModeDao implements V2CountryZoneDeliveryModeDao {

    @Override
    public Collection<DeliveryModeModel> findDeliveryModes(final AbstractOrderModel abstractOrder) {
        Collection<DeliveryModeModel> result = Collections.EMPTY_LIST;
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

        AddressModel deliveryAddress = abstractOrder.getDeliveryAddress();
        if (deliveryAddress != null && StringUtils.isNotEmpty(deliveryAddress.getPostalcode().trim())) {
            Map params = new HashMap();
            params.put("deliveryCountry", abstractOrder.getDeliveryAddress().getCountry());
            params.put("currency", abstractOrder.getCurrency());
            params.put("net", abstractOrder.getNet());
            params.put("active", Boolean.TRUE);
            params.put("store", abstractOrder.getStore());
            params.put("pinCode", deliveryAddress.getPostalcode().trim());
            return doSearch(query.toString(), params, DeliveryModeModel.class);
        }

        return Collections.EMPTY_LIST;

    }

    @Override
    public PriceValue getMinimumAmountForFreeShipping(final DeliveryModeModel deliveryMode, final AbstractOrderModel order)
            throws JaloDeliveryModeException {

        AddressModel deliveryAddress = order.getDeliveryAddress();
        if (deliveryAddress == null) {
            throw new JaloDeliveryModeException("getMinimumAmountForFreeShipping(): delivery address was NULL in order " + order, 0);
        }

        CountryModel country = deliveryAddress.getCountry();
        if (country == null) {
            throw new JaloDeliveryModeException("getMinimumAmountForFreeShipping(): country of delivery address " + deliveryAddress
                    + " was NULL in order " + order, 0);
        }

        CurrencyModel currency = order.getCurrency();
        if (currency == null) {
            throw new JaloDeliveryModeException("getMinimumAmountForFreeShipping(): currency was NULL in order " + order, 0);
        }

        final Map<String, Object> params = new HashMap<>();
        params.put(ZoneDeliveryModeValueModel.DELIVERYMODE, deliveryMode);
        params.put(ZoneDeliveryModeValueModel.CURRENCY, currency);
        params.put(CountryModel._TYPECODE, country);

        StringBuilder query = new StringBuilder("SELECT {v.pk}");
        query.append(" FROM {").append(ZoneDeliveryModeValueModel._TYPECODE).append(" AS v");
        query.append(" JOIN ").append(CountryModel._ZONECOUNTRYRELATION).append(" AS z2cRel");
        query.append(" ON {v.").append(ZoneDeliveryModeValueModel.ZONE).append("}={z2cRel.source} }");
        query.append(" WHERE {v.").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("} = ?")
                .append(ZoneDeliveryModeValueModel.DELIVERYMODE);
        query.append(" AND {v.").append(ZoneDeliveryModeValueModel.CURRENCY).append("} = ?").append(ZoneDeliveryModeValueModel.CURRENCY);
        query.append(" AND {v.").append(ZoneDeliveryModeValueModel.VALUE).append("} = 0 ");
        query.append(" AND {z2cRel.target} = ?").append(CountryModel._TYPECODE);
        query.append(" ORDER BY {v.").append(ZoneDeliveryModeValue.MINIMUM).append("} ASC");

        List<ZoneDeliveryModeValueModel> values = doSearch(query.toString(), params, ZoneDeliveryModeValueModel.class);

        if ((values.isEmpty()) && (!(currency.getBase().booleanValue())) && (C2LManager.getInstance().getBaseCurrency() != null)) {
            params.put(ZoneDeliveryModeValueModel.CURRENCY, C2LManager.getInstance().getBaseCurrency());
            values = doSearch(query.toString(), params, ZoneDeliveryModeValueModel.class);
        }
        if (values.isEmpty()) {
            throw new JaloDeliveryModeException("no free shipping threshold defined for mode " + deliveryMode + ", country " + country
                    + ", currency " + currency, 0);
        }

        ZoneDeliveryModeValueModel bestMatch = values.get(0);

        return new PriceValue(currency.getIsocode(), getDoubleAsPrimitive(bestMatch.getMinimum()), order.getNet().booleanValue());
    }

    protected double getDoubleAsPrimitive(Double d) {
        return (d == null) ? 0.0D : d.doubleValue();
    }
}
