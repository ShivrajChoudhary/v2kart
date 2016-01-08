/**
 * 
 */
package in.com.v2kart.core.dao;

import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;

/**
 * 
 * @author Nagarro_devraj802
 * 
 */
public interface V2CustomerDao {

    /**
     * Returns a list of <Code>CustomerModel</code> according to supplied params.
     * 
     * @param isUpdate
     *        Defines whether customer to be updated or created are to be picked
     * @return List of <Code>CustomerModel</code> according to supplied params.
     */
    List<CustomerModel> findCustomerToResendSap(final Boolean isUpdate);
    
    List<CustomerModel> findAllCustomers();

}
