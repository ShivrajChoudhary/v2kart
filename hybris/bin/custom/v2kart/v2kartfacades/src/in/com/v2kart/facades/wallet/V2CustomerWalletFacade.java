package in.com.v2kart.facades.wallet;

import in.com.v2kart.facades.wallet.data.V2WalletTransactionData;

import java.util.List;
/**
 * API to get wallet data for customer
 * @author shailjagupta
 *
 */
public interface V2CustomerWalletFacade {
	/**
	 * API to get latest 5 Transaction for customer
	 * @return
	 */
	List<V2WalletTransactionData> getCustomerTransactions();
	/**
	 * API to get To wallet balance for customer.
	 * @return
	 */
	Double getBalanceForCustomer();

}
