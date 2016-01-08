/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.forms.validation;

import in.com.v2kart.checkoutaddon.storefront.forms.V2PaymentInfoForm;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Class to validate {@link V2PaymentInfoValidator}.
 *
 * @author Anuj Kumar
 */
@Component("v2PaymentInfoValidator")
public class V2PaymentInfoValidator implements Validator {

    @Override
    public boolean supports(final Class<?> aClass) {
        return V2PaymentInfoForm.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors) {
        final V2PaymentInfoForm v2PaymentInfoForm = (V2PaymentInfoForm) object;
    }

}
