package in.com.v2kart.core.services;

import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.TaxValue;

public interface V2CalculationService {
    
    public OrderModel recalculatePromotionsForOriginalOrder(OrderModel originalOrder);
    
    public OrderModel calculateTotalsForCancellation(OrderModel order,Boolean isPartialCancel);
    
    public void calculateTotalsForReturn(AbstractOrderModel order, boolean recalculate);
    
    public void calculateRefundOrder(final AbstractOrderModel order) throws CalculationException;
       

}
