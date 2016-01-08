/**
 *
 */
package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.sapinboundintegration.ws.customer.CustUpdateInReq;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Populator for converting from {@link CustomerModel} (Source) to Web Service response of type {@link CustomerUpdateInReq} (Target)
 * 
 * @author satvir_nagarro
 * @version 1.0
 */
public class V2CustomerUpdateInIntegrationPopulator implements Populator<CustomerModel, CustUpdateInReq> {

    protected final static String SAP_INBOUND_DATE_FORMAT = "ddMMyyyy";

    @Override
    public void populate(final CustomerModel customer, final CustUpdateInReq target) throws ConversionException {
        target.setCustId(customer.getSapCustomerId());
        if (customer.getTitle() != null) {
            // target.setTitle(customer.getTitle().getCode());
        }
        target.setFName(customer.getFirstName());
        // target.setMName(value);
        target.setLName(customer.getLastName());
        if (customer.getDateOfBirth() != null) {
            target.setDOB(DateFormatUtils.format(customer.getDateOfBirth(), SAP_INBOUND_DATE_FORMAT));
        }
        target.setEmail(customer.getUid());
        target.setMobile(customer.getMobileNumber());
        this.setPresentAddress(customer, target);
    }

    private void setPresentAddress(final CustomerModel customer, final CustUpdateInReq target) {
        AddressModel presentAddress = null;
        for (final AddressModel addess : customer.getAddresses()) {
            if (addess.getBillingAddress().booleanValue()) {
                presentAddress = addess;
            }

        }
        if (presentAddress != null) {
            target.setAdd1(presentAddress.getLine1());
            target.setAdd2(presentAddress.getLine2());
            // target.setAdd3(presentAddress.getlLine3());
            target.setCity(presentAddress.getTown());
            target.setPin(presentAddress.getPostalcode());
            if (presentAddress.getRegion() != null) {
                target.setState(presentAddress.getRegion().getIsocodeShort());
            }
            target.setCountry(presentAddress.getCountry().getIsocode());
        }
    }

}