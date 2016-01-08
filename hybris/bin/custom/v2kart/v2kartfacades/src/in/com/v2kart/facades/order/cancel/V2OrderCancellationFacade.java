package in.com.v2kart.facades.order.cancel;

import in.com.v2kart.facades.cancel.data.CancelData;

import java.util.Map;

import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.OrderModel;

public interface V2OrderCancellationFacade {
	
	OrderCancelEntryStatus  cancelPartialOrder(final CancelData cancelData);
	
	OrderCancelEntryStatus  cancelFullOrder(final CancelData cancelData);
	
	PriceData getRefundAmount(final CancelData cancelData);
	
	Map<String, String> filterCancelReasons(Map<String, String> cancelReasonMap);
	
	Boolean isCancelationValid(OrderModel order);
}
