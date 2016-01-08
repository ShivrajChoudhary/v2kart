/**
 * 
 */
package in.com.v2kart.facades.order;

import java.util.List;
import java.util.Map;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.enums.OrderStatus;

/**
 * @author shubhammaheshwari
 *
 */
public interface V2OrderFacade extends OrderFacade {
    
    /**
     * @param exceptOrderStatus
     * @return
     * return all order status except the OrderStatus Passed as parameter
     */
    List<OrderStatus> getFilteredOrderStatuses(OrderStatus... exceptOrderStatus);
    
    /**
     * @param orderData
     * @return
     */
    boolean isOrderCancellable(OrderData orderData);
    /**
     * Return order entries which are canceleable.
     * @param orderCode
     * @return
     */
    Map<OrderEntryData, Long> getAllCancellableEntries(String orderCode);
}
