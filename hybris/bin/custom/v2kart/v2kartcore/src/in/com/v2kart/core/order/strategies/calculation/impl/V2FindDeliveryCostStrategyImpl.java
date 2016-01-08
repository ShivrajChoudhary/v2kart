package in.com.v2kart.core.order.strategies.calculation.impl;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.order.strategies.calculation.impl.DefaultFindDeliveryCostStrategy;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.PriceValue;

/**
 * V2Kart implementation of {@link FindDeliveryCostStrategy}.
 */
public class V2FindDeliveryCostStrategyImpl extends DefaultFindDeliveryCostStrategy {

    private static final Logger LOG = Logger.getLogger(V2FindDeliveryCostStrategyImpl.class);

    // step 1 : delegate to jalo
    @Override
    public PriceValue getDeliveryCost(final AbstractOrderModel order) {
        ServicesUtil.validateParameterNotNullStandardMessage("order", order);
        try {
            getModelService().save(order);
            final AbstractOrder orderItem = getModelService().getSource(order);
            final DeliveryMode dMode = orderItem.getDeliveryMode();

            // if order is pick in store, delivery charges should be retrieved from local.properties
            if (order instanceof CartModel) {
                CartModel cart = (CartModel) order;

                if (cart.getIsPickup()) {
                    double defaultPickUpDeliveryCost = Double.parseDouble(Config.getParameter("deliverycost.default.pickup.store"));
                    
                    LOG.info("Order [" + order.getCode() + "] is pick-up in store ...setting default delivery cost : " + defaultPickUpDeliveryCost);
                    return new PriceValue(order.getCurrency().getIsocode(), defaultPickUpDeliveryCost, order.getNet().booleanValue());
                }
            }
            return dMode.getCost(orderItem);

        } catch (final Exception e) {
            double defaultDeliveryCost = Double.parseDouble(Config.getParameter("deliverycost.default.tentative"));

            LOG.warn("Could not find deliveryCost for order [" + order.getCode() + "] due to : " + e.getMessage()
                    + "... setting default delivery cost : " + defaultDeliveryCost);
            return new PriceValue(order.getCurrency().getIsocode(), defaultDeliveryCost, order.getNet().booleanValue());
        }
    }

}
