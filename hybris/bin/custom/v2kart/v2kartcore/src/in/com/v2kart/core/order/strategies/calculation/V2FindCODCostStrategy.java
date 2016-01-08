package in.com.v2kart.core.order.strategies.calculation;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.PriceValue;

/**
 * Strategy focused on resolving COD cost for a given order. COD cost depends on the amount of order at the checkout.
 * 
 * @author vikrant2480
 * 
 */
public interface V2FindCODCostStrategy {

    /**
     * Returns order's COD cost of the given order.
     * 
     * @param order
     *        {@link AbstractOrderModel}
     * @return {@link PriceValue} representing COD cost introduced in the order.
     */
    PriceValue getCODCharges(AbstractOrderModel order, double discountValues);
}
