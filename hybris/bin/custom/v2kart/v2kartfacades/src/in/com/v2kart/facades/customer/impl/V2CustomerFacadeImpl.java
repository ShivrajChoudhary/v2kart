/**
 * 
 */
package in.com.v2kart.facades.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import in.com.v2kart.facades.customer.V2CustomerFacade;
import in.com.v2kart.sapinboundintegration.services.V2CustomerSapIntegrationService;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.mobileservices.enums.PhoneNumberFormat;
import de.hybris.platform.mobileservices.model.text.PhoneNumberModel;
import de.hybris.platform.mobileservices.model.text.UserPhoneNumberModel;

/**
 * @author shubhammaheshwari
 * 
 */
public class V2CustomerFacadeImpl extends DefaultCustomerFacade implements V2CustomerFacade {
    private static final Logger LOG = Logger.getLogger(V2CustomerFacadeImpl.class);

    /** V2CustomerSapIntegrationService bean injection */
    private V2CustomerSapIntegrationService v2CustomerSapIntegrationService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade#register(de.hybris.platform.commercefacades.user.data.RegisterData
     * ) Override to add the customer mobile number during registration.
     */
    @Override
    public void register(final RegisterData registerData) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("registerData", registerData);
        Assert.hasText(registerData.getFirstName(), "The field [FirstName] cannot be empty");
        Assert.hasText(registerData.getLastName(), "The field [LastName] cannot be empty");
        Assert.hasText(registerData.getLogin(), "The field [Login] cannot be empty");

        final CustomerModel newCustomer = getModelService().create(CustomerModel.class);
        newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));
        newCustomer.setFirstName(registerData.getFirstName());
        newCustomer.setLastName(registerData.getLastName());

        if (StringUtils.isNotBlank(registerData.getFirstName()) && StringUtils.isNotBlank(registerData.getLastName())) {
            newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));
        }
        final TitleModel title = getUserService().getTitleForCode(registerData.getTitleCode());
        newCustomer.setTitle(title);
        newCustomer.setMobileNumber(registerData.getMobileNumber());
        setUidForRegister(registerData, newCustomer);
        newCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
        newCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
        getCustomerAccountService().register(newCustomer, registerData.getPassword());
    }

    @Override
    public void updateProfile(final CustomerData customerData) throws DuplicateUidException {
        validateDataBeforeUpdate(customerData);

        final String name = getCustomerNameStrategy().getName(customerData.getFirstName(), customerData.getLastName());
        final CustomerModel customer = getCurrentSessionCustomer();
        customer.setOriginalUid(customerData.getDisplayUid());
        customer.setMobileNumber(customerData.getMobileNumber());
        customer.setFirstName(customerData.getFirstName());
        customer.setLastName(customerData.getLastName());
        customer.setDateOfBirth(customerData.getDateOfBirth());
        customer.setMaritalStatus(customerData.getMaritalStatus());

        if (null != customerData.getGender() && !customerData.getGender().isEmpty()) {
            customer.setGender(Gender.valueOf(customerData.getGender().toUpperCase()));
        }
        getCustomerAccountService().updateProfile(customer, customerData.getTitleCode(), name, customerData.getUid());
        this.synchronizeCustomer();
    }

    private void synchronizeCustomer() {
        // Do not let any exception roll to caller. Integration call failure should not fail normal flow for caller.
        try {
            CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
            v2CustomerSapIntegrationService.updateCustomer(customer);
        } catch (final Exception exception) {
            LOG.error("An unexpected exception has occurred. Detailed stack trace of the exception is provided.");
            LOG.error(exception.getStackTrace());
        }
    }

    @Required
    public void setV2CustomerSapIntegrationService(V2CustomerSapIntegrationService v2CustomerSapIntegrationService) {
        this.v2CustomerSapIntegrationService = v2CustomerSapIntegrationService;
    }

    @Override
    public void updatePhoneNumbers(final CustomerData customerData, boolean retainOldVaue) {
        final CustomerModel customer = getCurrentSessionCustomer();
        List<UserPhoneNumberModel> phoneNumberModels;
        if(retainOldVaue) {
            phoneNumberModels = new ArrayList<UserPhoneNumberModel>(customer.getPhoneNumbers());
        } else {
            phoneNumberModels = new ArrayList<UserPhoneNumberModel>(0);
        }

        for (String phoneNumber : customerData.getPhoneNumbers()) {
            PhoneNumberModel phoneNumberModel = new PhoneNumberModel();
            phoneNumberModel.setNumber(phoneNumber);
            phoneNumberModel.setCountry(getCommonI18NService().getCountry("IN"));
            phoneNumberModel.setFormat(PhoneNumberFormat.LOCAL);
            // getModelService().save(phoneNumberModel);

            UserPhoneNumberModel userPhoneNumberModel = new UserPhoneNumberModel();
            userPhoneNumberModel.setPhoneNumber(phoneNumberModel);
            // getModelService().save(userPhoneNumberModel);
            phoneNumberModels.add(userPhoneNumberModel);
        }
        customer.setPhoneNumbers(phoneNumberModels);
        getModelService().save(customer);
    }
}
