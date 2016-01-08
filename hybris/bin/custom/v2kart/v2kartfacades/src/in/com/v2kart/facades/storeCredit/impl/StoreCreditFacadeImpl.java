package in.com.v2kart.facades.storeCredit.impl;

import java.math.BigDecimal;

import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.facades.order.data.V2StoreCreditPaymentInfoData;
import in.com.v2kart.facades.storeCredit.StoreCreditFacade;
import in.com.v2kart.fulfilmentprocess.services.V2StoreCreditService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The {@code StoreCreditFacadeImpl} class implements the store credit facade.
 * 
 * @author Nagarro_Devraj
 * @since 1.2
 */
public class StoreCreditFacadeImpl extends DefaultCustomerFacade implements StoreCreditFacade {

    /**
     * Application logfile.
     */
    private static final Logger LOG = Logger.getLogger(StoreCreditFacadeImpl.class);

    /**
     * The store credit service.
     */
    private V2StoreCreditService storeCreditService;

    /**
     * Query the balance of the store credit for the currently logged-in customer.
     * 
     * @return The store credit balance. If the logged in customer has no store credit, this will be zero. If there is no currently logged
     *         in customer, this will be null.
     */
    @Override
    public Double queryBalance() {

        // find the currently logged in customer
        final CustomerModel customerModel = getCurrentSessionCustomer();

        // if there is no current customer, no result
        if (customerModel == null) {
            return null;
        }

        // find the customer's balance
        return storeCreditService.queryBalance(customerModel);
    }

    @Required
    public void setStoreCreditService(V2StoreCreditService storeCreditService) {
        this.storeCreditService = storeCreditService;
    }
} // class
