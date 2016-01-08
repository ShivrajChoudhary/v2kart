/**
 *
 */
package in.com.v2kart.sapinboundintegration.services;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import in.com.v2kart.sapinboundintegration.ws.order.SOCreateRes;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelRes;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnRes;

/**
 * @author satvir_nagarro
 *
 */
public interface V2SapInboundSaleOrderIntegrationService {

    /**
     * Update erp sales transaction via webservice call as per the process sales request provided.
     *
     * @param order
     *            the order
     * @return the DTSOCreateRes sales response
     */
    SOCreateRes updateErpSales(OrderModel order);

    /**
     * Update Erp sales via webservice call about cancellation
     *
     * @param orderCancelRecordEntryModel
     * @return SOModifyCancelRes
     */
    SOModifyCancelRes cancelModifyErpSales(final OrderCancelRecordEntryModel orderCancelRecordEntryModel);

    /**
     * Update erp sales via webservice call about return of order.
     *
     * @param returnRequestModel
     * @return SOReturnRes
     */
    SOReturnRes returnErpSales(final ReturnRequestModel returnRequestModel);

}
