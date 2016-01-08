/**
 * 
 */
package in.com.v2kart.core.dao.impl;

import in.com.v2kart.core.dao.V2NotifyCustomerDao;
import in.com.v2kart.core.model.NotifyCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * @author shubhammaheshwari
 * save customer notification request
 */
public class V2NotifyCustomerDaoImpl implements V2NotifyCustomerDao {
    
    /**
     * model service bean injection 
     */
    private ModelService modelService;
    
    /* (non-Javadoc)
     * @see in.com.v2kart.core.dao.V2NotifyCustomerDao#saveNotification(in.com.v2kart.core.model.NotifyCustomerModel)
     */
    @Override
    public void saveNotification(NotifyCustomerModel notifyCustomerModel) {
        modelService.save(notifyCustomerModel);
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

}
