/**
 * 
 */
package in.com.v2kart.facades.notifycustomer.impl;

import in.com.v2kart.core.services.V2NotifyCustomerService;
import in.com.v2kart.facades.core.data.V2CustomerNotificationData;
import in.com.v2kart.facades.notifycustomer.V2NotifyCustomerFacade;

/**
 * @author shubhammaheshwari
 *
 */
public class V2NotifyCustomerFacadeImpl implements V2NotifyCustomerFacade {
    
    /**
     *  customerNotificationService bean injection
     */
    private V2NotifyCustomerService customerNotificationService;
    
    /* (non-Javadoc)
     * @see in.com.v2kart.facades.notifycustomer.V2NotifyCustomerFacade#notifyCustomer(in.com.v2kart.facades.core.data.V2CustomerNotificationData)
     */
    @Override
    public void notifyCustomer(V2CustomerNotificationData customerNotificationData) {
        customerNotificationService.notifyCustomer(customerNotificationData);
    }

    public V2NotifyCustomerService getCustomerNotificationService() {
        return customerNotificationService;
    }

    public void setCustomerNotificationService(V2NotifyCustomerService customerNotificationService) {
        this.customerNotificationService = customerNotificationService;
    }

}
