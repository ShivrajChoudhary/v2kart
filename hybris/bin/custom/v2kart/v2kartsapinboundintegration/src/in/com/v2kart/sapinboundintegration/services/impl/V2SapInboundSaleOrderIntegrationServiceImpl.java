/**
 *
 */
package in.com.v2kart.sapinboundintegration.services.impl;

import static org.springframework.util.Assert.notNull;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;

import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService;
import in.com.v2kart.sapinboundintegration.services.V2StockService;
import in.com.v2kart.sapinboundintegration.strategies.V2kartSapOrderCancelStrategy;
import in.com.v2kart.sapinboundintegration.strategies.V2kartSapReturnOrderStrategy;
import in.com.v2kart.sapinboundintegration.ws.order.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq;
import in.com.v2kart.sapinboundintegration.ws.order.SOCreateRes;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelRes;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnRes;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * @author satvir_nagarro
 *
 */
public class V2SapInboundSaleOrderIntegrationServiceImpl implements V2SapInboundSaleOrderIntegrationService {

    /** Logger instance for this class */
    private final Logger LOG = LoggerFactory.getLogger(V2SapInboundSaleOrderIntegrationServiceImpl.class);

    private final ObjectFactory objectFactory = new ObjectFactory();

    /** WebServiceTemplate bean injection */
    private WebServiceTemplate erpSalesWebServiceTemplate;

    /** orderCreationReqConverter bean injection */
    private Converter<OrderModel, SOCreateReq.OrderCreationReq> orderCreationReqConverter;

    /** ModelService bean injection */
    private ModelService modelService;

    /** StockService bean injection */
    private V2StockService stockService;

    private V2kartSapOrderCancelStrategy v2kartSapOrderCancelStrategy;

    private V2kartSapReturnOrderStrategy v2kartSapReturnOrderStrategy;

    public V2kartSapReturnOrderStrategy getV2kartSapReturnOrderStrategy() {
        return v2kartSapReturnOrderStrategy;
    }

    public void setV2kartSapReturnOrderStrategy(final V2kartSapReturnOrderStrategy v2kartSapReturnOrderStrategy) {
        this.v2kartSapReturnOrderStrategy = v2kartSapReturnOrderStrategy;
    }

    public V2kartSapOrderCancelStrategy getV2kartSapOrderCancelStrategy() {
        return v2kartSapOrderCancelStrategy;
    }

    public void setV2kartSapOrderCancelStrategy(final V2kartSapOrderCancelStrategy v2kartSapOrderCancelStrategy) {
        this.v2kartSapOrderCancelStrategy = v2kartSapOrderCancelStrategy;
    }

    @Override
    public SOCreateRes updateErpSales(final OrderModel order) {

        notNull(order, "order may not be null!");
        final SOCreateReq createSalesOrderReq = objectFactory.createSOCreateReq();
        final List<SOCreateReq.OrderCreationReq> orderCreationReqList = createSalesOrderReq.getOrderCreationReq();

        final SOCreateReq.OrderCreationReq orderCreationReq = orderCreationReqConverter.convert(order);
        orderCreationReqList.add(orderCreationReq);

        SOCreateRes response = null;
        try {

            LOG.info("Sending order [{}] to SAP Start.", order.getCode());
            // reserve stock here
            this.reserveStockForOrder(order);
            this.setOrderStatus(order, OrderStatus.CONFIRMED);
            response = (SOCreateRes) erpSalesWebServiceTemplate.marshalSendAndReceive(createSalesOrderReq);
            if (LOG.isDebugEnabled()) {
                LOG.debug("*********SAP RESOPNSE ***************");
                final StringBuilder respMsgCombine = new StringBuilder();
                for (final SOCreateRes.OrderCreationRes orderCreationRes : response.getOrderCreationRes()) {
                    // check response code and all the error status and message to be stored in order if SAP_CREATION_FAILURE
                    if (StringUtils.isNotEmpty(orderCreationRes.getRespCode())) {
                        LOG.debug("RES CODE IS :" + orderCreationRes.getRespCode());
                    }
                    respMsgCombine.append(orderCreationRes.getSAPCode()).append(" - ");
                    respMsgCombine.append(orderCreationRes.getRespMsg());
                    respMsgCombine.append(", ");
                }
                respMsgCombine.replace(respMsgCombine.length() - 2, respMsgCombine.length() + 1, "");
                LOG.debug("Response msg :" + respMsgCombine);
            }

            LOG.info("Sending order [{}] to SAP End.", order.getCode());
        } catch (final WebServiceFaultException faultException) {
            final String message = String.format("Error occurred while creating ERP Sales for Order[%s].\nSent SOCreateReq:-\n%s", order,
                    createSalesOrderReq);
            LOG.error(message, faultException);
            this.setOrderStatus(order, OrderStatus.SAP_CREATION_FAILURE);
            throw new V2SapConnectionException(message, faultException);
        } catch (final WebServiceException wsException) {
            final String message = String.format(
                    "Error occurred while accessing ERP Sales webservice for Order[%s].\nSent SOCreateReq:-\n%s", order,
                    createSalesOrderReq);
            LOG.error(message, wsException);
            this.setOrderStatus(order, OrderStatus.SENT_TO_SAP_FAILURE);
            throw new V2SapConnectionException(message, wsException);
        } catch (final Exception e) {
            final String message = String.format(
                    "Error occurred while accessing ERP Sales webservice for Order[%s].\nSent SOCreateReq:-\n%s", order,
                    createSalesOrderReq);
            LOG.error(message, e);
            if (!OrderStatus.SENT_TO_SAP_FAILURE.equals(order.getStatus())) {
                this.setOrderStatus(order, OrderStatus.SENT_TO_SAP_FAILURE);
            }
            throw new V2SapConnectionException(message, e);
        }
        this.handleErpSalesResponse(order, response);
        return response;
    }

    private void setOrderStatus(final OrderModel order, final OrderStatus orderStatus) {
        order.setStatus(orderStatus);
        modelService.save(order);
        modelService.refresh(order);
    }

    private void handleErpSalesResponse(final OrderModel order, final SOCreateRes soCreateRes) {
        notNull(soCreateRes, "soCreateRes may not be null!");
        notNull(order, "order may not be null!");
        OrderStatus orderStatus = null;
        final StringBuilder respMsgCombine = new StringBuilder();
        for (final SOCreateRes.OrderCreationRes orderCreationRes : soCreateRes.getOrderCreationRes()) {
            // check response code and all the error status and message to be stored in order if SAP_CREATION_FAILURE
            if (LOG.isDebugEnabled()) {
                LOG.debug("Order [" + order.getCode() + "] SAP RespCode[" + orderCreationRes.getRespCode() + "], RspMsg ["
                        + orderCreationRes.getRespMsg() + "], SAPCode [" + orderCreationRes.getSAPCode() + "]");
            }
            if (StringUtils.isNotEmpty(orderCreationRes.getRespCode())) {
                if ("S".equalsIgnoreCase(orderCreationRes.getRespCode())) {
                    orderStatus = OrderStatus.CONFIRMED;
                } else if (StringUtils.isNotEmpty(orderCreationRes.getSAPCode())
                        && orderCreationRes.getSAPCode().contains("already exists")) {
                    orderStatus = OrderStatus.CONFIRMED;
                } else {
                    orderStatus = OrderStatus.SAP_CREATION_FAILURE;
                }
            }
            respMsgCombine.append(orderCreationRes.getSAPCode()).append(" - ");
            respMsgCombine.append(orderCreationRes.getRespMsg());
            respMsgCombine.append(", ");
        }
        respMsgCombine.replace(respMsgCombine.length() - 2, respMsgCombine.length() + 1, "");

        order.setSapResponseDescription(respMsgCombine.toString());
        this.setOrderStatus(order, orderStatus);
    }

    /**
     * Reserves stock for full order from default warehouse "6583" for base store
     *
     * @param order
     */
    private void reserveStockForOrder(final OrderModel order) {
        notNull(order, "order may not be null!");
        for (final AbstractOrderEntryModel abstractOrderEntryModel : order.getEntries()) {
            try {
                stockService.reserveWithNegativeStockLevel(abstractOrderEntryModel.getProduct(), order.getStore()
                        .getDefaultDeliveryOrigin().getWarehouses().get(0), abstractOrderEntryModel.getQuantity().intValue(),
                        "Stock Reserved for Order" + order.getCode());
            } catch (final InsufficientStockLevelException e) {
                // TODO What if no sufficient stock available at HYBRIS for this
                // default warehouse
                LOG.error(String.format(
                        "Insufficient Stock Level for resrving stock for product code [%s] and warehouse code [%s] for order [%s]",
                        abstractOrderEntryModel.getProduct().getCode(), order.getStore().getDefaultDeliveryOrigin().getWarehouses().get(0)
                                .getCode(), order.getCode()), e);
            }
        }
    }

    /**
     * @param orderCreationReqConverter
     *        the orderCreationReqConverter to set
     */
    @Required
    public void setOrderCreationReqConverter(final Converter<OrderModel, SOCreateReq.OrderCreationReq> orderCreationReqConverter) {
        this.orderCreationReqConverter = orderCreationReqConverter;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param erpSalesWebServiceTemplate
     *        the erpSalesWebServiceTemplate to set
     */
    @Required
    public void setErpSalesWebServiceTemplate(final WebServiceTemplate erpSalesWebServiceTemplate) {
        this.erpSalesWebServiceTemplate = erpSalesWebServiceTemplate;
    }

    /**
     * @param stockService
     *        the stockService to set
     */
    @Required
    public void setStockService(final V2StockService stockService) {
        this.stockService = stockService;
    }

    /*
     * (non-Javadoc)
     *
     * @see in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService#cancelModifyErpSales(de.
     * hybris.platform.ordercancel.model.OrderCancelRecordEntryModel)
     */
    @Override
    public SOModifyCancelRes cancelModifyErpSales(final OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
        return getV2kartSapOrderCancelStrategy().cancelModifyErpSales(orderCancelRecordEntryModel);
    }

    /*
     * (non-Javadoc)
     *
     * @see in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService#returnErpSales(de.hybris
     * .platform.returns.model.ReturnRequestModel)
     */
    @Override
    public SOReturnRes returnErpSales(final ReturnRequestModel returnRequestModel) {
        return getV2kartSapReturnOrderStrategy().returnErpSales(returnRequestModel);
    }
}
