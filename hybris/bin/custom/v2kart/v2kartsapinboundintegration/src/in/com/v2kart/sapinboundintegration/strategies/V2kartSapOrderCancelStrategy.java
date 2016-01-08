package in.com.v2kart.sapinboundintegration.strategies;

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;

import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelRes;

/**
 * @author shailjagupta
 *
 */
public interface V2kartSapOrderCancelStrategy {

    public SOModifyCancelRes cancelModifyErpSales(final OrderCancelRecordEntryModel orderCancelRecordEntryModel);

}
