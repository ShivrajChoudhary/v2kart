package in.com.v2kart.core.sms;

import java.util.Map;

/**
 * The interface to send the SMS via SMS Aggregator
 * @author vikrant2480
 * 
 */
public interface V2SmsService {
    /**
     * Sends the SMS to the customer or merchant
     * @param parameterMap : containing all the dynamic fields in template to be replaced
     * @param template : template of SMS to be sent
     * @param mobileNumber : number on which SMS is to be sent
     */
    void sendSms(Map<String, String> parameterMap, String template, final String mobileNumber);
}
