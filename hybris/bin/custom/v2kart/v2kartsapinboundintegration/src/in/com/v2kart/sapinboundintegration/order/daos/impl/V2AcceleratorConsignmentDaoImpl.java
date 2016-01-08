package in.com.v2kart.sapinboundintegration.order.daos.impl;

import de.hybris.platform.acceleratorservices.order.dao.impl.DefaultAcceleratorConsignmentDao;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import in.com.v2kart.sapinboundintegration.order.daos.V2AcceleratorConsignmentDao;

/**
 * 
 * @author satvir_nagarro
 * 
 */
public class V2AcceleratorConsignmentDaoImpl extends DefaultAcceleratorConsignmentDao implements V2AcceleratorConsignmentDao {

    @Override
    public ConsignmentModel findConsignmentByCodeAndOrderCode(final String consignmentCode, final String orderCode) {

        final StringBuilder queryString = new StringBuilder(100);
        queryString.append("SELECT {c:").append(ConsignmentModel.PK).append("} FROM { ").append(ConsignmentModel._TYPECODE)
                .append(" AS c ");
        queryString.append("JOIN ").append(AbstractOrderModel._TYPECODE).append(" AS ao ON ");
        queryString.append("{c:").append(ConsignmentModel.ORDER).append("}={ao:").append(AbstractOrderModel.PK).append('}');
        queryString.append("} WHERE {c:code}=?consignmentCode AND {ao:code}=?orderCode");
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter("consignmentCode", consignmentCode);
        query.addQueryParameter("orderCode", orderCode);
        final ConsignmentModel result = this.getFlexibleSearchService().searchUnique(query);
        return result;

    }

}
