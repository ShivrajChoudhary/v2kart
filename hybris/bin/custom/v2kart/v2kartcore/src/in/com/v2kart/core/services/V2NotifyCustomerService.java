/**
 * 
 */
package in.com.v2kart.core.services;

import in.com.v2kart.facades.core.data.V2CustomerNotificationData;

/**
 * @author shubhammaheshwari
 * handles customer notification request from facade
 */
public interface V2NotifyCustomerService {
    /**
     * @param customerNotificationData
     */
    public void notifyCustomer(V2CustomerNotificationData customerNotificationData);
}
