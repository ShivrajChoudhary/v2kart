/**
 * 
 */
package in.com.v2kart.core.process.email.context;

import in.com.v2kart.facades.core.data.V2CustomerNotificationData;

import org.apache.velocity.VelocityContext;

/**
 * @author shubhammaheshwari
 *
 */
public class V2CustomerNotificationEmailContext extends VelocityContext {
    
    private final V2CustomerNotificationData customerNotificationData;
    
    /**
     * @param customerNotificationData
     */
    public V2CustomerNotificationEmailContext(final V2CustomerNotificationData customerNotificationData) {
        this.customerNotificationData = customerNotificationData;
    }

    public V2CustomerNotificationData getCustomerNotificationData() {
        return customerNotificationData;
    }

   
}
