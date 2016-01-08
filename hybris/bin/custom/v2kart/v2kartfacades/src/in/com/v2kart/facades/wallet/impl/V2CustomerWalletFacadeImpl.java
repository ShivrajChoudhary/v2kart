package in.com.v2kart.facades.wallet.impl;

import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import in.com.v2kart.core.model.V2CustomerWalletTransactionModel;
import in.com.v2kart.facades.wallet.V2CustomerWalletFacade;
import in.com.v2kart.facades.wallet.data.V2WalletTransactionData;
import in.com.v2kart.fulfilmentprocess.services.V2StoreCreditService;

public class V2CustomerWalletFacadeImpl implements V2CustomerWalletFacade{
	private final String STORE_CREDIT_COUNT_KEY="storecredit.top.transactions.count";
	private final Integer MAX_STORE_CREDIT_COUNT=5;
	
	private Converter<V2CustomerWalletTransactionModel,V2WalletTransactionData> v2WalletTransactionConverter;
	
	 public Converter<V2CustomerWalletTransactionModel, V2WalletTransactionData> getV2WalletTransactionConverter() {
		return v2WalletTransactionConverter;
	}

	public void setV2WalletTransactionConverter(
			Converter<V2CustomerWalletTransactionModel, V2WalletTransactionData> v2WalletTransactionConverter) {
		this.v2WalletTransactionConverter = v2WalletTransactionConverter;
	}

	private V2StoreCreditService storeCreditService;
	 private UserService userService;


	public V2StoreCreditService getStoreCreditService() {
		return storeCreditService;
	}

	public void setStoreCreditService(V2StoreCreditService storeCreditService) {
		this.storeCreditService = storeCreditService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public List<V2WalletTransactionData> getCustomerTransactions() {
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		List<V2CustomerWalletTransactionModel> results=new ArrayList<V2CustomerWalletTransactionModel>();
		results=getStoreCreditService().getTransactionsForCustomer(currentCustomer, Config.getInt(STORE_CREDIT_COUNT_KEY, MAX_STORE_CREDIT_COUNT));
		List<V2WalletTransactionData> transactions=new ArrayList<V2WalletTransactionData>();
		if(results.size()>0){
			for(V2CustomerWalletTransactionModel transaction:results){
				transactions.add(getV2WalletTransactionConverter().convert(transaction));
			}
		}
		return transactions;
	}

	@Override
	public Double getBalanceForCustomer() {
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		return getStoreCreditService().queryBalance(currentCustomer);
	}

}
