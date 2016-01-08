/**
 *
 */
package in.com.v2kart.fulfilmentprocess.services.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import in.com.v2kart.core.model.V2CustomerWalletTransactionModel;
import in.com.v2kart.fulfilmentprocess.dao.V2WalletCreditDao;
import in.com.v2kart.fulfilmentprocess.services.V2StoreCreditService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation class of V2StoreCreditService.The {@code V2StoreCreditServiceImpl} class provides API support for the store credit use cases.
 * <p/>
 * 
 * @author Nagarro_Devraj
 * @Since 1.2
 * 
 */
public class V2StoreCreditServiceImpl implements V2StoreCreditService {

    /** ModelService bean injection. */
    private ModelService modelService;

    /** The DAO layer for the store credit. **/
    private V2WalletCreditDao v2WalletCreditDao;

    /** UserService bean injection. */
    private UserService userService;

    /** Constant used for admin user. */
    private static final String SYSTEM_USER_UID = "admin";

    @Override
    public String createManualCredit(final CustomerModel customerModel, final String description, final double value) {
        // YTODO Auto-generated method stub
        return null;
    }

    /**
     * Refund store credit that was previously used in an order.
     * 
     * @param customerModel
     *        The customer whose store credit is being refunded.
     * @param description
     *        A description of why the credit is being refunded.
     * @param value
     *        The value of the credit being restored. This must be a positive value.
     */
    @Override
    public String refundStoreCredit(final CustomerModel customerModel, final String description, final double value) {

        return v2WalletCreditDao
                .addTransaction(checkNotNull(customerModel), checkNotNull(description), value, checkNotNull(userService.getAdminUser()));
    }

    /**
     * Get a customer's store credit balance.
     * 
     * @param customer
     *        The customer whose balance is sought.
     * @return The customer's balance.
     */
    @Override
    public double queryBalance(final CustomerModel customer) {

        return checkNotNull(v2WalletCreditDao).getBalanceForCustomer(checkNotNull(customer));
    }

    /**
     * Get a customer's CustomerWalletTransaction list.
     * 
     * @param customerModel
     *        The customer whose CustomerWalletTransaction list sought.
     * @param count
     *        Count Of Transactions
     * @return The customer's balance.
     */
    @Override
    public List<V2CustomerWalletTransactionModel> getTransactionsForCustomer(final CustomerModel customer, final Integer count) {
        checkNotNull(customer);
        return v2WalletCreditDao.getTransactionsForCustomer(customer, count);
    }
    
    /**
     * Consume store credit as part of an order.
     *
     * @param customerModel The customer whose store credit is being consumed.
     * @param description   A description of why the credit is being consumed.
     * @param value         The value fo the credit being consumed. This must be a positive value.
     */
    @Override
    public String consumeStoreCredit(CustomerModel customerModel, String description, double value) {

        UserModel issuer = userService.getUserForUID(SYSTEM_USER_UID);
        return v2WalletCreditDao.addTransaction(checkNotNull(customerModel), checkNotNull(description), 0 - value, checkNotNull(issuer));
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param v2WalletCreditDao
     *        the v2WalletCreditDao to set
     */
    @Required
    public void setV2WalletCreditDao(final V2WalletCreditDao v2WalletCreditDao) {
        this.v2WalletCreditDao = v2WalletCreditDao;
    }

    /**
     * @param userService
     *        the userService to set
     */
    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }
}
