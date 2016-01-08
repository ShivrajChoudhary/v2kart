package in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl;

import in.com.v2kart.cockpits.cscockpit.widgets.controllers.V2CustomerCreateController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer.ObjectValueHolder;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cscockpit.exceptions.ResourceMessage;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultCustomerCreateController;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 * Class overridden for creating customer and applying validations on the customer fields.
 * 
 * @author busamkumar
 *
 */
public class V2DefaultCustomerCreateController extends DefaultCustomerCreateController implements V2CustomerCreateController {

    private static final String PHONE_REGEX = "^\\d{10}$";
    private static final String NAME_REGEX = "\\b[A-Za-z]+\\b";
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Method for creating customer and applying validations on the customer fields.
     */
    public TypedObject createNewV2Customer(ObjectValueContainer customerObjectValueContainer, String customerTypeCode)
            throws DuplicateUidException, ValueHandlerException, ValidationException {

        if (customerObjectValueContainer != null && customerTypeCode != null && validateCustomer(customerObjectValueContainer)) {
            beforeCreateNewCustomer(customerObjectValueContainer, customerTypeCode);
            CustomerModel customerModel = createNewCustomer(customerObjectValueContainer);
            if (customerModel != null)
                try {
                    getCustomerAccountService().register(customerModel, null);
                    return getCockpitTypeService().wrapItem(customerModel);
                } catch (ModelSavingException e) {
                    String message = e.getMessage();
                    if (e.getCause() instanceof InterceptorException) {
                        String interceptorMessage = e.getCause().getMessage();
                        message = interceptorMessage == null || !interceptorMessage.contains("]:") ? interceptorMessage : StringUtils
                                .substringAfter(interceptorMessage, "]:");
                    }
                    throw new ValueHandlerException(message, e);
                } catch (IllegalArgumentException e) {
                    throw new ValueHandlerException(e.getMessage(), e);
                }
        }
        return null;
    }

    /**
     * Method for applying validations on the customer fields.
     */
    private boolean validateCustomer(ObjectValueContainer customerObjectValueContainer) throws ValueHandlerException, ValidationException {

        List<ResourceMessage> errorMessages = new ArrayList<ResourceMessage>();
        PropertyDescriptor firstNameDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Customer.firstName");
        PropertyDescriptor lastNameDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Customer.lastName");

        if (!checkEmail(customerObjectValueContainer)) {
            errorMessages.add(new ResourceMessage("createCustomer.validation.email.invalid"));
        }
        if (!checkTitle(customerObjectValueContainer)) {
            errorMessages.add(new ResourceMessage("createCustomer.validation.title.blank"));
        }
        if (null != firstNameDescriptor && !checkName(customerObjectValueContainer, firstNameDescriptor)) {
            errorMessages.add(new ResourceMessage("createCustomer.validation.firstName.invalid"));
        }
        if (null != lastNameDescriptor && !checkName(customerObjectValueContainer, lastNameDescriptor)) {
            errorMessages.add(new ResourceMessage("createCustomer.validation.lastName.invalid"));
        }
        if (!checkMobileNumber(customerObjectValueContainer)) {
            errorMessages.add(new ResourceMessage("createCustomer.validation.mobileNumber.invalid"));
        }
        if(!checkDateOfBirth(customerObjectValueContainer)){
            errorMessages.add(new ResourceMessage("createCustomer.validation.dateOfBirth.invalid"));
        }
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages);
        } else {
            return true;
        }
    }

    /**
     * Method for concatenating first name and last name into name.
     */
    @Override
    protected void beforeCreateNewCustomer(ObjectValueContainer customerObjectValueContainer, String customerTypeCode) {

        PropertyDescriptor uidDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Principal.uid");
        PropertyDescriptor firstNameDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Customer.firstName");
        PropertyDescriptor lastNameDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Customer.lastName");

        if (firstNameDescriptor != null && lastNameDescriptor != null) {
            ObjectValueHolder firstNameHolder = customerObjectValueContainer.getValue(firstNameDescriptor, null);
            ObjectValueHolder lastNameHolder = customerObjectValueContainer.getValue(lastNameDescriptor, null);
            ObjectValueHolder fullNameHolder = customerObjectValueContainer.getValue(
                    findPropertyDescriptor(customerObjectValueContainer, "Principal.name"), null);
            String fullName = "";

            if (firstNameHolder.getCurrentValue() != null) {
                fullName += firstNameHolder.getCurrentValue().toString();
                fullName += " ";
            }
            if (lastNameHolder.getCurrentValue() != null) {
                fullName += lastNameHolder.getCurrentValue().toString();
            }

            fullNameHolder.setLocalValue(fullName);

        }
        if (uidDescriptor != null) {
            ObjectValueHolder uidHolder = customerObjectValueContainer.getValue(uidDescriptor, null);
            if (uidHolder != null && (uidHolder.getCurrentValue() instanceof String)
                    && StringUtils.isBlank((String) uidHolder.getCurrentValue())) {
                String newUid = generateNewCustomerUid(customerObjectValueContainer, customerTypeCode);
                if (newUid != null)
                    uidHolder.setLocalValue(newUid);
            }
        }

    }

    /**
     * Method for validating mobile number.
     */
    private boolean checkMobileNumber(ObjectValueContainer customerObjectValueContainer) throws ValueHandlerException {
        PropertyDescriptor mobileNumberDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Customer.mobileNumber");
        ObjectValueHolder mobileNumberHolder = customerObjectValueContainer.getValue(mobileNumberDescriptor, null);
        if (null == mobileNumberHolder.getCurrentValue() || StringUtils.isBlank(mobileNumberHolder.getCurrentValue().toString())
                || !mobileNumberHolder.getCurrentValue().toString().matches(PHONE_REGEX)) {
            return false;
        }
        return true;
    }

    /**
     * Method for validating name.
     */
    private boolean checkName(ObjectValueContainer customerObjectValueContainer, PropertyDescriptor propertyDescriptor)
            throws ValueHandlerException {
        ObjectValueHolder valueHolder = customerObjectValueContainer.getValue(propertyDescriptor, null);
        if (null == valueHolder.getCurrentValue() || StringUtils.isBlank(valueHolder.getCurrentValue().toString())
                || !valueHolder.getCurrentValue().toString().matches(NAME_REGEX)) {
            return false;
        }
        return true;
    }

    /**
     * Method for validating email.
     */
    private boolean checkEmail(ObjectValueContainer customerObjectValueContainer) throws ValueHandlerException {
        PropertyDescriptor emailDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Principal.uid");
        ObjectValueHolder emailValueHolder = customerObjectValueContainer.getValue(emailDescriptor, null);
        if (null == emailValueHolder.getCurrentValue() || StringUtils.isBlank(emailValueHolder.getCurrentValue().toString())
                || !emailValueHolder.getCurrentValue().toString().matches(EMAIL_REGEX)) {
            return false;
        }
        return true;
    }

    /**
     * Method for validating title.
     */
    private boolean checkTitle(ObjectValueContainer customerObjectValueContainer) throws ValueHandlerException {
        PropertyDescriptor titleDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Customer.title");
        ObjectValueHolder titleValueHolder = customerObjectValueContainer.getValue(titleDescriptor, null);
        if (null == titleValueHolder.getCurrentValue()) {
            return false;
        }
        return true;
    }

    /**
     * validation for DOB.
     * @param customerObjectValueContainer
     * @return
     * @throws ValueHandlerException
     */
    private boolean checkDateOfBirth(ObjectValueContainer customerObjectValueContainer) throws ValueHandlerException {
        PropertyDescriptor dobDescriptor = findPropertyDescriptor(customerObjectValueContainer, "Customer.dateOfBirth");
        ObjectValueHolder dobHolder = customerObjectValueContainer.getValue(dobDescriptor, null);
        if (null != dobHolder.getCurrentValue() && validateDob((Date) dobHolder.getCurrentValue()) ) {
            return false;
        }
        return true;
    }
    
    /**
     * Validating DOB. Customer age should be at least 13yrs. 
     * @param dateOfBirth
     * @return
     */
    private boolean validateDob(Date dateOfBirth){
       final LocalDate dob = new LocalDate(dateOfBirth.getTime());
       final LocalDate now = new LocalDate();
       final Period period = new Period(dob, now, PeriodType.yearMonthDay());
        if(dateOfBirth.before(new Date()))
       {
           return false;
       }
        if (period.getDays() > 0) {
            return false;
        }
     
        return true;
    }
    
}
