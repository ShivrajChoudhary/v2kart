package in.com.v2kart.core.order.cancel.releasestock;

import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;

public interface V2CancelOrderEntryStockHandler {
	
	public void releaseStockAfterOrderCancelling(final OrderCancelRecordEntryModel result,final OrderCancelRequest orderCancelRequest); 

}
