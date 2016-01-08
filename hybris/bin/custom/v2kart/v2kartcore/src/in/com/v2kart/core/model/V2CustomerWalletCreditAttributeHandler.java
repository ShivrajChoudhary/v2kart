/**
 * 
 */
package in.com.v2kart.core.model;

import in.com.v2kart.fulfilmentprocess.services.V2StoreCreditService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import org.springframework.beans.factory.annotation.Required;

/**
 * The class <Code>V2CustomerWalletCreditAttributeHandler</Code> is dynamic attribute handler class for {@link CustomerModel} "walletCreditBalance"
 * attribute. The <Code>get</Code> method is used to return total wallet credit balance of customer.
 * 
 * @author Nagarro_Devraj
 * @since 1.2
 * 
 */
public class V2CustomerWalletCreditAttributeHandler implements DynamicAttributeHandler<Double, CustomerModel> {

    /** StoreCreditService bean injection. */
    private V2StoreCreditService storeCreditService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#get(de.hybris.platform.servicelayer.model.AbstractItemModel)
     */
    @Override
    public Double get(final CustomerModel customerModel) {
        if (customerModel.getPk() != null && !CustomerType.GUEST.equals(customerModel.getType())) {

            return storeCreditService.queryBalance(customerModel);
        }
        return 0d;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#set(de.hybris.platform.servicelayer.model.AbstractItemModel,
     * java.lang.Object)
     */
    @Override
    public void set(final CustomerModel paramMODEL, final Double paramVALUE) {
        // YTODO Auto-generated method stub

    }

    /**
     * @param storeCreditService
     *        the storeCreditService to set
     */
    @Required
    public void setStoreCreditService(final V2StoreCreditService storeCreditService) {
        this.storeCreditService = storeCreditService;
    }
}
