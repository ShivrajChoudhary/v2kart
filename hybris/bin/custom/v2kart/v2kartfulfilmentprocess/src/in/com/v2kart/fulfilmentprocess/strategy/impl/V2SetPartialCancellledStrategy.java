/**
 * 
 */
package in.com.v2kart.fulfilmentprocess.strategy.impl;

import de.hybris.platform.ordercancel.OrderStatusChangeStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author Nagarro_Devraj
 * @since 1.2
 * 
 */
public class V2SetPartialCancellledStrategy implements OrderStatusChangeStrategy {

    /** Injecting modelService bean **/
    private ModelService modelService;
  
    @Override
    public void changeOrderStatusAfterCancelOperation(final OrderCancelRecordEntryModel paramOrderCancelRecordEntryModel,
            final boolean paramBoolean) {
        // YTODO Auto-generated method stub
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
