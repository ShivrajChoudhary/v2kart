/**
 * 
 */
package in.com.v2kart.sapinboundintegration.services;

import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.sapinboundintegration.ws.customer.CustCreateRes;
import in.com.v2kart.sapinboundintegration.ws.customer.CustUpdateInRes;

/**
 * Interface for sending SAP Inbound request via web service calls
 * 
 * @author Nagarro-Dev
 * 
 */
public interface V2CustomerSapIntegrationService {

    /**
     * This method is used to search for customer and if customer is not found in SAP a new customer is created, and if the customer is
     * found in SAP, we will update SAPCustomerId in customer model
     * 
     * @param customer
     * @return CustCreateRes
     */
    CustCreateRes createCustomer(final CustomerModel customer);

    /**
     * Update any changes to customer to SAP system
     * 
     * @param customer
     *        Customer to update
     * @param erpUpdate
     *        YTODO
     * 
     * @return CustUpdateInRes
     */
    CustUpdateInRes updateCustomer(CustomerModel customer);

}
