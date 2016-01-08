package in.com.v2kart.core;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.util.PriceValue;

/**
 * V2CartService is the Cart service, which is responsible to extract the minimum amount of Order, for which the shipping charges are 0.
 * 
 * @author Rampal Lather
 *
 */

public interface V2CartService extends CartService {

    PriceValue getShopMinimumForFreeDelivery(AbstractOrderModel order);
}
