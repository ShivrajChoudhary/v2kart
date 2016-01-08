/**
 * 
 */
package in.com.v2kart.storefront.forms.validation;

import in.com.v2kart.storefront.forms.AddSellerForm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 
 */
@Component("sellerValidator")
public class V2SellerValidator implements Validator {

    public static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    public static final String PHONE_REGEX = "\\d{10}";
    public static final String NAME_REGEX = "\\b[A-Za-z ]+\\b";

    @Override
    public void validate(final Object object, final Errors errors) {
        final AddSellerForm addSellerForm = (AddSellerForm) object;
        final String name = addSellerForm.getName();
        final String email = addSellerForm.getEmail();
        final String phone = addSellerForm.getPhone();
        final String category = addSellerForm.getCategory();
        // final String message = addSellerForm.getMessage();

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name", "beASeller.name.invalid");
        } else if (StringUtils.length(name) > 255 || !validatefield(name, NAME_REGEX)) {
            errors.rejectValue("name", "beASeller.name.invalid");
        }

        if (StringUtils.isBlank(phone)) {
            errors.rejectValue("phone", "beASeller.phone.invalid");
        } else if (StringUtils.length(phone) > 255 || !validatefield(phone, PHONE_REGEX)) {
            errors.rejectValue("phone", "beASeller.phone.invalid");
        }

        if (StringUtils.isEmpty(email))
        {
            errors.rejectValue("email", "beASeller.email.invalid");
        }
        else if (StringUtils.length(email) > 255 || !validatefield(email, EMAIL_REGEX))
        {
            errors.rejectValue("email", "beASeller.email.invalid");
        }
        if (StringUtils.isEmpty(category)){
            errors.rejectValue("category", "beASeller.category.invalid");
        }
        else if(StringUtils.length(category) > 255 || !validatefield(category, NAME_REGEX)){
            errors.rejectValue("category", "beASeller.category.name.invalid");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(final Class<?> arg0) {
        return AddSellerForm.class.equals(arg0);
    }

    protected boolean validatefield(final String field, final String regEx)
    {
        final Pattern pattern = Pattern.compile(regEx);
        final Matcher matcher = pattern.matcher(field);
        return matcher.matches();
    }

}
