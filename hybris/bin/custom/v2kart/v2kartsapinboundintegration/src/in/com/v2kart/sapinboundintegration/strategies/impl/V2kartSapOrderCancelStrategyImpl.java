package in.com.v2kart.sapinboundintegration.strategies.impl;

import static org.springframework.util.Assert.notNull;

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.strategies.V2kartSapOrderCancelStrategy;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelRes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * @author shailjagupta
 *
 */
public class V2kartSapOrderCancelStrategyImpl implements V2kartSapOrderCancelStrategy {

    private final ObjectFactory soModifyObjectFactory = new ObjectFactory();

    /** Logger instance for this class */
    private final Logger LOG = LoggerFactory.getLogger(V2kartSapOrderCancelStrategyImpl.class);

    /** WebServiceTemplate bean injection */
    private WebServiceTemplate erpCancelModifySalesWebServiceTemplate;

    /** orderModifyCancelReqConverter bean injection */
    private Converter<OrderCancelRecordEntryModel, SOModifyCancelReq.OrderModifyCancelReq> orderModifyCancelReqConverter;

    public Converter<OrderCancelRecordEntryModel, SOModifyCancelReq.OrderModifyCancelReq> getOrderModifyCancelReqConverter() {
        return orderModifyCancelReqConverter;
    }

    public void setOrderModifyCancelReqConverter(
            final Converter<OrderCancelRecordEntryModel, SOModifyCancelReq.OrderModifyCancelReq> orderModifyCancelReqConverter) {
        this.orderModifyCancelReqConverter = orderModifyCancelReqConverter;
    }

    public WebServiceTemplate getErpCancelModifySalesWebServiceTemplate() {
        return erpCancelModifySalesWebServiceTemplate;
    }

    public void setErpCancelModifySalesWebServiceTemplate(
            final WebServiceTemplate erpCancelModifySalesWebServiceTemplate) {
        this.erpCancelModifySalesWebServiceTemplate = erpCancelModifySalesWebServiceTemplate;
    }

    /*
     * (non-Javadoc)
     * @see
     * in.com.v2kart.sapinboundintegration.strategies.V2kartSapOrderCancelStrategy#cancelModifyErpSales(de.hybris.platform
     * .ordercancel.model.OrderCancelRecordEntryModel)
     */
    @Override
    public SOModifyCancelRes cancelModifyErpSales(final OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
        notNull(orderCancelRecordEntryModel, "orderCancelRecordModel may not be null!");
        final SOModifyCancelReq soModifyCancelReq = soModifyObjectFactory.createSOModifyCancelReq();
        final List<SOModifyCancelReq.OrderModifyCancelReq> orderCancelModifyReqList = soModifyCancelReq
                .getOrderModifyCancelReq();

        SOModifyCancelReq.OrderModifyCancelReq orderModifyCancelReq = null;

        orderModifyCancelReq = orderModifyCancelReqConverter.convert(orderCancelRecordEntryModel);

        orderCancelModifyReqList.add(orderModifyCancelReq);

        SOModifyCancelRes response = null;
        try {

            // final JAXBElement<SOCreateReq> jaxbsoCreateReq =
            // objectFactory.createSOCreateReq(createSalesOrderReq);
            LOG.info("Sending cancel order [{}] to SAP Start.", orderCancelRecordEntryModel.getModificationRecord()
                    .getOrder().getCode());
            response = (SOModifyCancelRes) erpCancelModifySalesWebServiceTemplate
                    .marshalSendAndReceive(soModifyCancelReq);
            LOG.info("Sending cancel order [{}] to SAP End.", orderCancelRecordEntryModel.getModificationRecord()
                    .getOrder().getCode());
            // final JAXBElement<SOCreateRes> jaxbsoCreateRes =
            // objectFactory.createSOCreateRes(response);
        } catch (final WebServiceFaultException faultException) {
            final String message = String.format("Error occurred while accessing ERP Sales webservice for Order[%s].",
                    orderCancelRecordEntryModel.getModificationRecord().getOrder(), soModifyCancelReq);
            LOG.error(message, faultException);
            // this.setOrderStatus(order, OrderStatus.SAP_CREATION_FAILURE);
            throw new V2SapConnectionException(message, faultException);
        } catch (final Exception e) {
            final String message = String.format("Error occurred while accessing ERP Sales webservice for Order[%s].",
                    orderCancelRecordEntryModel.getModificationRecord().getOrder(), soModifyCancelReq);
            LOG.error(message, e);
            // if (!OrderStatus.SENT_TO_SAP_FAILURE.equals(order.getStatus())) {
            // this.setOrderStatus(order, OrderStatus.SENT_TO_SAP_FAILURE);
            // }
            throw new V2SapConnectionException(message, e);
        }
        // this.handleErpSalesResponse(order, response);
        return response;
    }

}
