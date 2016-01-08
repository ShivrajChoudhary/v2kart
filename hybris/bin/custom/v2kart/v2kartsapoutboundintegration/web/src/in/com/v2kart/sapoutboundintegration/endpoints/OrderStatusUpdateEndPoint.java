package in.com.v2kart.sapoutboundintegration.endpoints;

import in.com.v2kart.sapoutboundintegration.services.V2SapOrderUpdateIntegrationService;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateReq;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateRes;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * 
 * @author devraj802
 * 
 */
@Endpoint
public class OrderStatusUpdateEndPoint {

    @Autowired
    private V2SapOrderUpdateIntegrationService v2SapOrderUpdateIntegrationService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @PayloadRoot(localPart = "SoOrderStatusUpdateReq", namespace = "http://v2kretail.in/ecomm/ordr")
    @ResponsePayload
    public SoOrderStatusUpdateRes handleCreate(@RequestPayload final SoOrderStatusUpdateReq request) {
        SoOrderStatusUpdateRes response;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Order status update starts for order [{}]", request.getSONo());
            LOG.debug(ReflectionToStringBuilder.toString(request, ToStringStyle.MULTI_LINE_STYLE));
        }
        response = v2SapOrderUpdateIntegrationService.updateOrderStatusFromErp(request);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Order status update finished for order [{}]", request.getSONo());
        }
        return response;
    }
}
