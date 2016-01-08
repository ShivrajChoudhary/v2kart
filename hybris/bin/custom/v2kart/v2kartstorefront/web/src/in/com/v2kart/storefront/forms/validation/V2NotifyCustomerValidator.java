/**
 * 
 */
package in.com.v2kart.storefront.forms.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.com.v2kart.core.enums.V2NotifyCustomerTypeEnum;
import in.com.v2kart.storefront.forms.V2NotifyCustomerForm;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author shubhammaheshwari
 * validates the customer notification form
 */
@Component("notifyCustomerValidator")
public class V2NotifyCustomerValidator implements Validator {
    
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    @Override
    public boolean supports(Class<?> aClass) {
        return V2NotifyCustomerForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        final V2NotifyCustomerForm v2NotifyCustomerForm = (V2NotifyCustomerForm) obj;

        final String notificationType = v2NotifyCustomerForm.getType();
        final String currentUsermail = v2NotifyCustomerForm.getCurrentUserEmailId();
        final String email = v2NotifyCustomerForm.getEmailId();

        if (StringUtils.isEmpty(notificationType)) {
            // email a friend validation
            validateEmailId(email, errors, notificationType);
            if (StringUtils.isEmpty(v2NotifyCustomerForm.getMessage())) {
                errors.rejectValue("message", "Required*", "Required*");
            }
            if (StringUtils.isEmpty(v2NotifyCustomerForm.getUrl())) {
                errors.rejectValue("url", "Required*", "Required*");
            }
        } else if (V2NotifyCustomerTypeEnum.NOTIFY_MY_PRICE.toString().equals(notificationType)) {
            // notify my price validation
            final String notificationPrice = v2NotifyCustomerForm.getNotificationPrice();
            validateEmailId(currentUsermail, errors, notificationType);
            if (StringUtils.isNotEmpty(notificationPrice)) {
                final Pattern p = Pattern.compile("([0-9]*)");

                final Matcher m = p.matcher(notificationPrice);
                if (!m.matches()) {
                    errors.rejectValue("notificationPrice", "Invalid Entry", "Invalid Entry");
                }
            } else {
                errors.rejectValue("notificationPrice", "Required*", "Required*");
            }
        } else {
            validateEmailId(currentUsermail, errors, notificationType);
        }
    }

    private void validateEmailId(final String email, final Errors errors, final String notificationType) {
        if (StringUtils.isNotEmpty(email)) {
            if (StringUtils.length(email) > 255 || !Pattern.matches(EMAIL_REGEX, email)) {
                if (StringUtils.isEmpty(notificationType)) {
                    errors.rejectValue("emailId", "Invalid Entry", "Invalid Entry");
                } else {
                    errors.rejectValue("currentUserEmailId", "Invalid Entry", "Invalid Entry");
                }
            }
        } else {
            if (StringUtils.isEmpty(notificationType)) {
                errors.rejectValue("emailId", "Required*", "Required*");
            } else {
                errors.rejectValue("currentUserEmailId", "Required*", "Required*");
            }
        }
    }
}
