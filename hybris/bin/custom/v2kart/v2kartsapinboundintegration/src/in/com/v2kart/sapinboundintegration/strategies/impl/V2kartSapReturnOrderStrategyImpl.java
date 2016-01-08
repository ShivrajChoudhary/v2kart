/**
 *
 */
package in.com.v2kart.sapinboundintegration.strategies.impl;

import static org.springframework.util.Assert.notNull;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.strategies.V2kartSapReturnOrderStrategy;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnReq;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnRes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * @author shailjagupta
 *
 */
public class V2kartSapReturnOrderStrategyImpl implements V2kartSapReturnOrderStrategy {

    /** Logger instance for this class */
    private final Logger LOG = LoggerFactory.getLogger(V2kartSapReturnOrderStrategyImpl.class);

    private final ObjectFactory soReturnObjectFactory = new ObjectFactory();

    /** WebServiceTemplate bean injection */
    private WebServiceTemplate erpOrderReturnWebServiceTemplate;

    /** orderReturnReqConverter bean injection */
    private Converter<ReturnRequestModel, SOReturnReq.OrderReturnReq> orderReturnReqConverter;

    private ModelService modelService;

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public WebServiceTemplate getErpOrderReturnWebServiceTemplate() {
        return erpOrderReturnWebServiceTemplate;
    }

    public Converter<ReturnRequestModel, SOReturnReq.OrderReturnReq> getOrderReturnReqConverter() {
        return orderReturnReqConverter;
    }

    @Override
    public SOReturnRes returnErpSales(final ReturnRequestModel returnRequestModel) {

        notNull(returnRequestModel, "returnRequestModel may not be null!");
        final OrderModel order = returnRequestModel.getOrder();
        final SOReturnReq returnSalesOrderReq = soReturnObjectFactory.createSOReturnReq();
        final List<SOReturnReq.OrderReturnReq> orderReturnReqList = returnSalesOrderReq.getOrderReturnReq();
        final SOReturnReq.OrderReturnReq orderReturnReq = orderReturnReqConverter.convert(returnRequestModel);
        orderReturnReqList.add(orderReturnReq);
        SOReturnRes response = null;
        List<SOReturnRes.OrderReturnRes> orderReturnRes = null;
        StringBuilder resDesc = new StringBuilder();
        try {
            LOG.info("Sending order [{}] to SAP Start.", order.getCode());
            response = (SOReturnRes) erpOrderReturnWebServiceTemplate.marshalSendAndReceive(returnSalesOrderReq);
            LOG.info("Sending order [{}] to SAP End.", order.getCode());
            orderReturnRes = response.getOrderReturnRes();
            if (orderReturnRes != null) {
                final SOReturnRes.OrderReturnRes r = orderReturnRes.get(0);
                resDesc = new StringBuilder("SAP RESPONSE :RespCode[");
                resDesc.append(r.getRespCode() + "]");
                resDesc.append("respMsg [");
                resDesc.append(r.getRespMsg() + "]");
                order.setSapResponseDescription(resDesc.toString());
            }
        } catch (final WebServiceFaultException faultException) {
            final String message = String.format(
                    "Error occurred while accessing ERP return webservice for Order[%s].\nSent SOReturnReq:-\n%s", order,
                    returnSalesOrderReq);
            LOG.error(message, faultException);
            this.setOrderReturnRequestStatus(returnRequestModel, OrderStatus.RETURN_SENT_TO_SAP_FAILED);
            throw new V2SapConnectionException(message, faultException);
        } catch (final Exception e) {
            final String message = String.format(
                    "Error occurred while accessing ERP return webservice for Order[%s].\nSent SOReturnReq:-\n%s", order,
                    returnSalesOrderReq);
            LOG.error(message, e);
            this.setOrderReturnRequestStatus(returnRequestModel, OrderStatus.RETURN_SENT_TO_SAP_FAILED);
            throw new V2SapConnectionException(message, e);
        }
        return response;
    }

    private void setOrderReturnRequestStatus(final ReturnRequestModel returnRequestModel, final OrderStatus orderStatus) {
        final OrderModel order = returnRequestModel.getOrder();
        order.setSapResponseDescription("SAP SENDING FAILED");
        order.setStatus(orderStatus);
        getModelService().save(order);
    }

    /**
     * @param erpOrderReturnWebServiceTemplate
     *        the erpOrderReturnWebServiceTemplate to set
     */
    @Required
    public void setErpOrderReturnWebServiceTemplate(final WebServiceTemplate erpOrderReturnWebServiceTemplate) {
        this.erpOrderReturnWebServiceTemplate = erpOrderReturnWebServiceTemplate;
    }

    /**
     * @param orderReturnReqConverter
     *        the orderReturnReqConverter to set
     */
    @Required
    public void setOrderReturnReqConverter(final Converter<ReturnRequestModel, SOReturnReq.OrderReturnReq> orderReturnReqConverter) {
        this.orderReturnReqConverter = orderReturnReqConverter;
    }

}
