package in.com.v2kart.sapoutboundintegration.endpoints;

import in.com.v2kart.sapinboundintegration.ws.orderreturn.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnReq;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnRes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * @author shailjagupta
 * 
 */
@Endpoint
public class OrderReturnEndPointTest {

    private final Logger LOG = LoggerFactory.getLogger(OrderReturnEndPointTest.class);

    private final ObjectFactory objectFactory = new ObjectFactory();

    @PayloadRoot(localPart = "SOReturnReq", namespace = "http://v2kretail.in/ecomm/ordr")
    @ResponsePayload
    public SOReturnRes handleCreate(@RequestPayload final SOReturnReq request) {
        SOReturnRes response;
        response = createResponse();
        return response;
    }

    private SOReturnRes createResponse() {
        final SOReturnRes response = objectFactory.createSOReturnRes();
        final SOReturnRes.OrderReturnRes orderReturnRes = new SOReturnRes.OrderReturnRes();
        orderReturnRes.setRespCode("S");
        orderReturnRes.setRespMsg("Success");
        response.getOrderReturnRes().add(orderReturnRes);
        return response;
    }

}
