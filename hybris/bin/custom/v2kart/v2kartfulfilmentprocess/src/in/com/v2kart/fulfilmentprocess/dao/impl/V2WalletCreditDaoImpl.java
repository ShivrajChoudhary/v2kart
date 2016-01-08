/**
 * 
 */
package in.com.v2kart.fulfilmentprocess.dao.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import in.com.v2kart.core.model.V2CustomerWalletTransactionModel;
import in.com.v2kart.fulfilmentprocess.dao.V2WalletCreditDao;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Class <code>V2WalletCreditDaoImpl</code> is implementation class of <code>V2WalletCreditDao</code>
 * 
 * @author Nagarro_Devraj
 * @since 1.2
 * 
 */
public class V2WalletCreditDaoImpl extends AbstractItemDao implements V2WalletCreditDao {

    /** Application log file. */
    private static final Logger LOG = Logger.getLogger(V2WalletCreditDaoImpl.class);
    /** KeyGenerator */
    private KeyGenerator keyGenerator;

    /**
     * Query used to fetch a customer's balance. This will sum the transaction values for the relevant customer.
     */
    private final String QUERY_BALANCE = "SELECT sum({ct.value}) " + "FROM {" + V2CustomerWalletTransactionModel._TYPECODE + " AS ct} "
            + "WHERE {ct." + CustomerModel._TYPECODE + "} = (?customer)";

    /**
     * Query used to fetch the list of customer transactions for the relevant customer, ordered by posted date.
     */
    private final String QUERY_CUSTOMER_TRANSACTIONS = "SELECT {ct." + V2CustomerWalletTransactionModel.PK + "} " + "FROM {"
            + V2CustomerWalletTransactionModel._TYPECODE + " AS ct} " + "WHERE {ct." + CustomerModel._TYPECODE + "} = (?customer) "
            + "ORDER BY {ct." + V2CustomerWalletTransactionModel.POSTEDDATE + "} DESC ";

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBalanceForCustomer(final CustomerModel customer) {

        // preconditions
        checkNotNull(customer);

        // plug in the parameters
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("customer", customer);

        // run the query
        final SearchResult<Double> result = this.getSearchResult(QUERY_BALANCE, queryParams, Double.class, null);

        // done
        final Double balance = result.getResult().get(0);
        
        return (balance != null) ? Math.round(balance*100.0)/100.0 : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<V2CustomerWalletTransactionModel> getTransactionsForCustomer(final CustomerModel customer,Integer count) {

        // plug in the parameters
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("customer", customer);

        // run the query
        final SearchResult<V2CustomerWalletTransactionModel> result = this.getSearchResult(QUERY_CUSTOMER_TRANSACTIONS, queryParams, 
        		V2CustomerWalletTransactionModel.class, count);

        // if debugging is turned on, log the results
        if (LOG.isDebugEnabled()) {
            for (final V2CustomerWalletTransactionModel customerTransactionModel : result.getResult()) {
                LOG.debug("\t " + customerTransactionModel.getPostedDate().toString() + " : " + 
                			customerTransactionModel.getDescription() + " = " + customerTransactionModel.getValue());
            }
        }

        // done
        return result.getResult();
    }

    /**
     * This method is used to get Search Result for a given Query
     * 
     * @param query
     *        Query to be executed
     * @param params
     *        Query parameters
     * @param resultClass
     *        Resulted Class Name
     * @param count
     *        Limit of the Query Result
     * @return SearchResult
     */
    protected <T> SearchResult<T> getSearchResult(String query, Map<String, Object> params, Class<T> resultClass, Integer count) {
        FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
        if (params != null) {
            fQuery.addQueryParameters(params);
        }

        fQuery.setResultClassList(Collections.singletonList(resultClass));

        if (count != null) {
            fQuery.setCount(count.intValue());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Executed Query: " + fQuery);
        }

        return search(fQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addTransaction(final CustomerModel customer, final String description, final double value, final UserModel issuer) {

        // preconditions
        checkNotNull(customer);
        checkNotNull(description);

        final ModelService modelService = getModelService();
        final String storeCreditRequestId = (String) keyGenerator.generate();
        // create the model
        final V2CustomerWalletTransactionModel trx = modelService.create(V2CustomerWalletTransactionModel.class);
        trx.setRequestId(storeCreditRequestId);
        trx.setCustomer(customer);
        trx.setDescription(description);
        trx.setValue(value);
        trx.setPostedDate(new Date());
        /* if (issuer != null) { trx.setIssuer(issuer); } */
        // find the model service, and write our data
        modelService.save(trx);
        modelService.refresh(trx);
        return storeCreditRequestId;
    }

    /**
     * @return the keyGenerator
     */
    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * @param keyGenerator
     *        the keyGenerator to set
     */
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
}
