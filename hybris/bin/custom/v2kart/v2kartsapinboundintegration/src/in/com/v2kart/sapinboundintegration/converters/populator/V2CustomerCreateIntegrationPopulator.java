/**
 *
 */
package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.sapinboundintegration.ws.customer.CustCreateReq;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Generic Populator for populating attributes from {@link CustomerModel} (Source) to Value Container used for communication via Web
 * Service.
 * 
 */
public class V2CustomerCreateIntegrationPopulator implements Populator<CustomerModel, CustCreateReq> {

    protected final static String SAP_INBOUND_DATE_FORMAT = "ddMMyyyy";

    private final static String BLANK_STR = "";

    @Override
    public void populate(final CustomerModel customer, final CustCreateReq target) throws ConversionException {
        target.setCustId(getBlankIfNull(customer.getSapCustomerId()));
        if (customer.getTitle() != null) {
            // target.setTitle(customer.getTitle().getCode());
        }
        target.setFName(customer.getFirstName());
        // target.setMName(value);
        target.setLName(customer.getLastName());
        if (customer.getDateOfBirth() != null) {
            target.setDOB(DateFormatUtils.format(customer.getDateOfBirth(), SAP_INBOUND_DATE_FORMAT));
        } else {
            target.setDOB(BLANK_STR);
        }
        target.setEmail(customer.getUid());
        target.setMobile(getBlankIfNull(customer.getMobileNumber()));
        this.setPresentAddress(customer, target);
    }

    private void setPresentAddress(final CustomerModel customer, final CustCreateReq target) {
        AddressModel presentAddress = null;
        for (final AddressModel addess : customer.getAddresses()) {
            if (addess.getBillingAddress().booleanValue()) {
                presentAddress = addess;
            }

        }
        if (presentAddress != null) {
            target.setAdd1(presentAddress.getLine1());
            target.setAdd2(presentAddress.getLine2());
            target.setAdd3(BLANK_STR);
            target.setCity(presentAddress.getTown());
            target.setPin(presentAddress.getPostalcode());
            if (presentAddress.getRegion() != null) {
                target.setState(presentAddress.getRegion().getIsocodeShort());
            }
            target.setCountry(presentAddress.getCountry().getIsocode());
        }
    }

    protected String getBlankIfNull(final String str) {
        if (StringUtils.isEmpty(str)) {
            return BLANK_STR;
        } else {
            return str;
        }
    }

}
