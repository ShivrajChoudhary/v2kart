package in.com.v2kart.sapoutboundintegration.endpoints;

import in.com.v2kart.sapinboundintegration.ws.ordermodification.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelRes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class OrderCancellationEndPointTest {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final ObjectFactory objectFactory = new ObjectFactory();

    @PayloadRoot(localPart = "SOModifyCancelReq", namespace = "http://v2kretail.in/ecomm/ordr")
    @ResponsePayload
    public SOModifyCancelRes handleCreate(@RequestPayload
    final SOModifyCancelReq request) {
        SOModifyCancelRes response;
        response = createResponse();
        return response;
    }

    private SOModifyCancelRes createResponse() {
        final SOModifyCancelRes response = objectFactory.createSOModifyCancelRes();
        final SOModifyCancelRes.OrderModifyCancelRes orderModifyCancelRes = new SOModifyCancelRes.OrderModifyCancelRes();
        orderModifyCancelRes.setRespCode("S");
        orderModifyCancelRes.setRespMsg("Success");
        response.getOrderModifyCancelRes().add(orderModifyCancelRes);
        return response;
    }
}
