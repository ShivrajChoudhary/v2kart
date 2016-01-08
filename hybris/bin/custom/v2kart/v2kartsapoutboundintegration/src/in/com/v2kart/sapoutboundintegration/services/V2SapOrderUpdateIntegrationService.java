package in.com.v2kart.sapoutboundintegration.services;

import in.com.v2kart.sapoutboundintegration.ws.order.returnupdate.RSoOrderStatusUpdateReq;
import in.com.v2kart.sapoutboundintegration.ws.order.returnupdate.RSoOrderStatusUpdateRes;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateReq;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateRes;

/**
 * 
 * @author satvir_nagarro
 * 
 */
public interface V2SapOrderUpdateIntegrationService {

    /**
     * Updates Order details and status and consignment for this SAP feed
     * 
     * @param orderStatusUpdate
     *        orderStatusUpdate
     * @return SoOrderStatusUpdateRes as response
     */
    SoOrderStatusUpdateRes updateOrderStatusFromErp(final SoOrderStatusUpdateReq orderStatusUpdate);

    RSoOrderStatusUpdateRes updateReturnOrderStatusFromErp(final RSoOrderStatusUpdateReq orderStatusUpdate);

}
