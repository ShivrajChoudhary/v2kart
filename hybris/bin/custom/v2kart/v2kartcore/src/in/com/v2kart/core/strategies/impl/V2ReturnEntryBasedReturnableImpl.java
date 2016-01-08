package in.com.v2kart.core.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.strategy.impl.DefaultReturnEntryBasedReturnableCheck;

/**
 * <V2ReturnEntryBasedReturnableImpl> Custom strategy for ReturnEntryBasedReturnable
 * 
 * @author Nagarro_Devraj802
 * @since 1.2
 * 
 */
public class V2ReturnEntryBasedReturnableImpl extends DefaultReturnEntryBasedReturnableCheck {

    /** {@inheritDoc} */
    @Override
    public boolean perform(final OrderModel order, final AbstractOrderEntryModel orderentry, final long returnQuantity) {
        if ((returnQuantity < 1L) || (orderentry.getQuantity().longValue() < returnQuantity)) {
            return false;
        } else {
            return true;
        }
    }
}
