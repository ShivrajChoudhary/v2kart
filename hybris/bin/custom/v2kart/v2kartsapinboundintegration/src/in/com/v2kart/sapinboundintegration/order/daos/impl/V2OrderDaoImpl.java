package in.com.v2kart.sapinboundintegration.order.daos.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.daos.impl.DefaultOrderDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import in.com.v2kart.sapinboundintegration.order.daos.V2OrderDao;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * <FGOrderDaoImpl>
 *
 * @author satvir_nagarro
 *
 */
public class V2OrderDaoImpl extends DefaultOrderDao implements V2OrderDao {

    private static final Logger LOG = Logger.getLogger(V2OrderDaoImpl.class);

    @Override
    public List<OrderModel> findSalesFailureOrders() {
        final StringBuilder queryBuilder = new StringBuilder("SELECT {o.%s} " + "FROM {%s AS o} "
                + "WHERE {o.%s} IN (?statusList) AND {o.%s}>?failureOrderPastTime");
        final String formattedQuery = String.format(queryBuilder.toString(), OrderModel.PK, OrderModel._TYPECODE, OrderModel.STATUS,
                OrderModel.CREATIONTIME);
        final Map<String, Object> queryParams = new HashMap<String, Object>();

        queryParams.put("statusList", Arrays.asList(OrderStatus.SENT_TO_SAP_FAILURE, OrderStatus.SAP_CREATION_FAILURE));
        // TODO
        final Calendar day15BeforeDate = Calendar.getInstance();
        day15BeforeDate.set(Calendar.HOUR_OF_DAY, 0);
        day15BeforeDate.set(Calendar.MINUTE, 0);
        day15BeforeDate.set(Calendar.SECOND, 0);
        // TODO
        day15BeforeDate.add(Calendar.DATE, -Config.getInt("noOfOldDaysToRetrySapFailedOrders", 0));
        queryParams.put("failureOrderPastTime", day15BeforeDate.getTime());
        final FlexibleSearchQuery query = new FlexibleSearchQuery(formattedQuery);
        query.getQueryParameters().putAll(queryParams);

        final SearchResult<OrderModel> result = this.getFlexibleSearchService().search(query);

        if (LOG.isDebugEnabled()) {
            LOG.debug(query);
            for (final OrderModel salesOrder : result.getResult()) {
                LOG.debug("\t " + salesOrder.getCode() + " : " + salesOrder.getStatus() + " : " + salesOrder.getVersionID());
            }
        }
        return result.getResult();
    }

    @Override
    public OrderModel findOrderByCode(final String code) {
        final StringBuilder queryBuilder = new StringBuilder("SELECT {%s} FROM {%s} WHERE {" + OrderModel.CODE + "} =?code AND {"
                + OrderModel.VERSIONID + "} IS NULL");
        final String formattedQuery = String.format(queryBuilder.toString(), OrderModel.PK, OrderModel._TYPECODE, OrderModel.CODE);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(formattedQuery);
        query.addQueryParameter("code", code);

        final OrderModel orderModel = this.getFlexibleSearchService().searchUnique(query);

        return orderModel;
    }

    /**
     * @author Shivraj
     *
     * @see in.com.v2kart.sapinboundintegration.order.daos.V2OrderDao#findAllTypeOfFailureOrders()
     *
     *      this method is used to getting all failure order based on status
     */
    @Override
    public List<OrderModel> findAllTypeOfFailureOrders() {
        final StringBuilder queryBuilder = new StringBuilder("SELECT {o.%s} " + "FROM {%s AS o} "
                + "WHERE {o.%s} IN (?statusList) AND {o.%s}>?failureOrderPastTime");
        final String formattedQuery = String.format(queryBuilder.toString(), OrderModel.PK, OrderModel._TYPECODE, OrderModel.STATUS,
                OrderModel.CREATIONTIME);
        final Map<String, Object> queryParams = new HashMap<String, Object>();

        queryParams.put("statusList", Arrays.asList(OrderStatus.SENT_TO_SAP_FAILURE, OrderStatus.SAP_CREATION_FAILURE,
                OrderStatus.PAYMENT_AMOUNT_NOT_RESERVED, OrderStatus.PAYMENT_NOT_AUTHORIZED, OrderStatus.UPDATE_CUSTOMER_TO_SAPERP_FAILED));
        // TODO
        final Calendar day15BeforeDate = Calendar.getInstance();
        day15BeforeDate.set(Calendar.HOUR_OF_DAY, 0);
        day15BeforeDate.set(Calendar.MINUTE, 0);
        day15BeforeDate.set(Calendar.SECOND, 0);
        // TODO
        day15BeforeDate.add(Calendar.DATE, -Config.getInt("noOfOldDaysToRetrySapFailedOrders", 0));
        queryParams.put("failureOrderPastTime", day15BeforeDate.getTime());
        final FlexibleSearchQuery query = new FlexibleSearchQuery(formattedQuery);
        query.getQueryParameters().putAll(queryParams);

        final SearchResult<OrderModel> result = this.getFlexibleSearchService().search(query);

        if (LOG.isDebugEnabled()) {
            LOG.debug(query);
            for (final OrderModel salesOrder : result.getResult()) {
                LOG.debug("\t " + salesOrder.getCode() + " : " + salesOrder.getStatus() + " : " + salesOrder.getVersionID());
            }
        }
        return result.getResult();
    }
}
