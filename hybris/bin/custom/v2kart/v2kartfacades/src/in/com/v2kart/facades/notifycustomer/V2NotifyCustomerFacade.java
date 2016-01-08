/**
 * 
 */
package in.com.v2kart.facades.notifycustomer;

import in.com.v2kart.facades.core.data.V2CustomerNotificationData;

/**
 * @author shubhammaheshwari
 * handle customer notification request from Controller
 */
public interface V2NotifyCustomerFacade {
    /**
     * @param customerNotificationData
     */
    public void notifyCustomer(V2CustomerNotificationData customerNotificationData);
}
