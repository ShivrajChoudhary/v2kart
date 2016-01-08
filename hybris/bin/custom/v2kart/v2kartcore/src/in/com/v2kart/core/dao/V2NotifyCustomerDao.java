/**
 * 
 */
package in.com.v2kart.core.dao;

import in.com.v2kart.core.model.NotifyCustomerModel;

/**
 * @author shubhammaheshwari
 * Save the Customer Notification Request
 */
public interface V2NotifyCustomerDao {
    /**
     * @param notifyCustomerModel
     * Saves Customer Notification Request in the DB
     */
    public void saveNotification(NotifyCustomerModel notifyCustomerModel);
}
