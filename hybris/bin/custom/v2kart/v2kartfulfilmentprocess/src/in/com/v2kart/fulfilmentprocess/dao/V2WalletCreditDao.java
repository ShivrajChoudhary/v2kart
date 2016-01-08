/**
 * 
 */
package in.com.v2kart.fulfilmentprocess.dao;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import in.com.v2kart.core.model.V2CustomerWalletTransactionModel;

import java.util.List;

/**
 * Class <Code>V2WalletCreditDao</code> contains API related to customer wallet credit details.
 * 
 * @author Nagarro_Devraj
 * @since 1.2
 * 
 */
public interface V2WalletCreditDao {
    /**
     * Get the current store credit balance for a customer.
     * 
     * @param customer
     *        The customer whose store credit balance is sought.
     * @return The customer's store credit balance.
     */
    double getBalanceForCustomer(final CustomerModel customer);

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
     * Add a new transaction for the customer.
     * 
     * @param customer
     *        The customer who we're creating the transaction for.
     * @param description
     *        The description to associate with the transaction.
     * @param value
     *        The value of the transaction. This should be positive for a credit (that is, to increase the customer's store credit balance)
     *        or negative for a debit (that is, to reduce the customer's store credit balance).
     * @param paymentOrRefundTxn
     */
    String addTransaction(final CustomerModel customer, final String description, final double value, UserModel issuer);
}
