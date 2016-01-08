package in.com.v2kart.core.dao.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import in.com.v2kart.core.dao.V2CustomerDao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Nagarro_devraj802
 * @version 1.0
 * 
 */
public class V2CustomerDaoImpl extends AbstractItemDao implements V2CustomerDao {
    private static final Logger LOG = Logger.getLogger(V2CustomerDaoImpl.class);

    @Override
    public List<CustomerModel> findCustomerToResendSap( final Boolean isUpdate) {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {c:").append(CustomerModel.PK).append("}").append(" FROM { ").append(CustomerModel._TYPECODE)
                .append(" AS c ").append("} WHERE {modifiedTime}>?failureCustomerPastTime ").append("AND {c:orderPlaced}=1 " );

        if (isUpdate.booleanValue()) {
            queryString.append("AND {c:sapCustomerId} Is Not Null  AND {c:sapErpUpdated}=FALSE ");
        } else {
            queryString.append("AND {c:sapCustomerId} Is Null");
        }
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter("failureCustomerPastTime", getBeforeDate(Config.getInt("noOfBackDaysToRetrySapFailedNewCustomer", 0)));

        final SearchResult<CustomerModel> result = this.getFlexibleSearchService().search(query);
        return result.getResult();
    }

    /**
     * Returns back date from current date.
     * 
     * @param noOfBackDays
     * 
     * @return Date
     */
    private Date getBeforeDate(final int noOfBackDays) {

        final Calendar dayBeforeDate = Calendar.getInstance();
        dayBeforeDate.set(Calendar.HOUR_OF_DAY, 0);
        dayBeforeDate.set(Calendar.MINUTE, 0);
        dayBeforeDate.set(Calendar.SECOND, 0);

        dayBeforeDate.add(Calendar.DATE, noOfBackDays);

        return dayBeforeDate.getTime();
    }

    @Override
    public List<CustomerModel> findAllCustomers() {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {c:").append(CustomerModel.PK).append("}").append(" FROM { ").append(CustomerModel._TYPECODE)
                .append(" AS c ").append("}");
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        final SearchResult<CustomerModel> result = this.getFlexibleSearchService().search(query);
        return result.getResult();
    }
}
