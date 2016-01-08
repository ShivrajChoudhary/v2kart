package in.com.v2kart.cockpits.cscockpit.services.search.generic.query;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.cscockpit.services.search.generic.query.DefaultCustomerSearchQueryBuilder;
import de.hybris.platform.cscockpit.services.search.impl.DefaultCsTextSearchCommand;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * Class overridden for changing the flexible search query for customer search(including email id and mobile number).
 * 
 * @author busamkumar
 *
 */
public class V2CustomerSearchQueryBuilder extends DefaultCustomerSearchQueryBuilder {

    public static enum TextField {
        Name, Postalcode, Emailid, Mobileno;
    }

    @Override
    protected FlexibleSearchQuery buildFlexibleSearchQuery(DefaultCsTextSearchCommand command) {

        String name = command.getText(TextField.Name);
        String postalcode = command.getText(TextField.Postalcode);
        String emailId = command.getText(TextField.Emailid);
        String mobileNo = command.getText(TextField.Mobileno);

        boolean searchName = StringUtils.isNotEmpty(name);
        boolean searchPostalcode = StringUtils.isNotEmpty(postalcode);
        boolean searchEmailId = StringUtils.isNotEmpty(emailId);
        boolean searchMobileNo = StringUtils.isNotEmpty(mobileNo);

        StringBuilder query = new StringBuilder(300);
        query.append("SELECT DISTINCT {c:pk}, {c:name}, {c:uid} ");

        query.append("FROM {Customer AS c ");

        if (searchPostalcode)
            query.append("LEFT JOIN Address AS a ON {c:pk}={a:owner} ");

        query.append("} ");
        query.append("WHERE ({c:name}!='anonymous' OR {c:name} IS NULL) ");

        if (searchName) {
            if (isCaseInsensitive()) {
                query.append("AND LOWER({c:name}) LIKE LOWER(?customerName) ");
            } else {
                query.append("AND {c:name} LIKE ?customerName ");
            }
        }

        if (searchPostalcode) {
            if (isCaseInsensitive()) {
                query.append("AND LOWER({a:postalcode}) LIKE LOWER(?postalcode) ");
            } else {
                query.append("AND {a:postalcode} LIKE ?postalcode ");
            }
        }

        if (searchEmailId) {
            if (isCaseInsensitive()) {
                query.append("AND LOWER({c:originaluid}) LIKE LOWER(?email) ");
            } else {
                query.append("AND {c:originaluid} LIKE ?email ");
            }
        }

        if (searchMobileNo) {
            if (isCaseInsensitive()) {
                query.append("AND LOWER({c:mobilenumber}) LIKE LOWER(?mobile) ");
            } else {
                query.append("AND {c:mobilenumber} LIKE ?mobile ");
            }
        }

        query.append("ORDER BY {c:name}, {c:uid} ");

        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString());

        if (searchName) {
            searchQuery.addQueryParameter("customerName", "%" + name.trim() + "%");
        }
        if (searchPostalcode) {
            searchQuery.addQueryParameter("postalcode", "%" + postalcode.trim() + "%");
        }
        if (searchEmailId) {
            searchQuery.addQueryParameter("email", "%" + emailId.trim() + "%");
        }
        if (searchMobileNo) {
            searchQuery.addQueryParameter("mobile", "%" + mobileNo.trim() + "%");
        }

        return searchQuery;
    }

}
