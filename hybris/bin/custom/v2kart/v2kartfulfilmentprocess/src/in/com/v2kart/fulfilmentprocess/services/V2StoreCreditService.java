/**
 *
 */
package in.com.v2kart.fulfilmentprocess.services;

import in.com.v2kart.core.model.V2CustomerWalletTransactionModel;

import java.util.List;

import de.hybris.platform.core.model.user.CustomerModel;

/**
 * The {@code V2StoreCreditService} class provides API support for the store credit use cases.
 * <p/>
 * Note that the {@code createManualCredit}, {@code refundStoreCredit} all to essentially the same thing: they create a customer
 * transaction. They're implemented as different methods so that the calling code is clear about why it is causing a transaction to be
 * created.
 * 
 * @author Nagarro_Devraj
 * @Since 1.2
 */
public interface V2StoreCreditService {
    /**
     * Create a manual credit transaction. This is used when a CS Agent manually bestows credit to customer.
     * 
     * @param customerModel
     *        The customer who is being given credit.
     * @param description
     *        A description of why the credit is being gifted.
     * @param value
     *        The value fo the credit being gifted. This must be a positive value less than $1000.00.
     */
    String createManualCredit(final CustomerModel customerModel, final String description, final double value);

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
    String refundStoreCredit(final CustomerModel customerModel, final String description, final double value);

    /**
     * Get a customer's store credit balance.
     * 
     * @param customerModel
     *        The customer whose balance is sought.
     * @return The customer's balance.
     */
    double queryBalance(final CustomerModel customerModel);

    /**
     * Get a customer's CustomerWalletTransaction list.
     * 
     * @param customerModel
     *        The customer whose CustomerWalletTransaction list sought.
     * @param count
     *        Count Of Transactions
     * @return The customer's balance.
     */

    List<V2CustomerWalletTransactionModel> getTransactionsForCustomer(final CustomerModel custome, Integer count);
    
    /**
     * Consume store credit as part of an order.
     *
     * @param customerModel The customer whose store credit is being consumed.
     * @param description   A description of why the credit is being consumed.
     * @param value         The value fo the credit being consumed. This must be a positive value.
     */
    String consumeStoreCredit(final CustomerModel customerModel, final String description, final double value);
}
