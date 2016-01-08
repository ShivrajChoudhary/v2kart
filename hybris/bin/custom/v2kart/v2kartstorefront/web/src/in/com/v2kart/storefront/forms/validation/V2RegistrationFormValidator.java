/**
 * 
 */
package in.com.v2kart.storefront.forms.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;

/**
 * @author shubhammaheshwari
 * Class Override to validate the Customer registration form
 */
@Component("v2RegistrationValidator")
public class V2RegistrationFormValidator implements Validator {

    public static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    public static final String MOBILE_NO_REGEX = "\\b\\d{10}\\b";
    public static final String NO_REGEX = "\\b\\d{6}\\b";
    public static final String NAME_REGEX = "\\b[A-Za-z]+[A-Za-z ]*\\b";
    public static final String NAME_REGEX_WITHOUT_SPACE = "\\b[A-Za-z._]+[A-Za-z-s._]*\\b";
    public static final String EMPTY_STRING = "";
    public static final String DEFAULT_ERROR_MESSAGE_STRING = "form.field.required";
    public static final String LONG_LENGTH_ERROR_STRING = "register.length.invalid";
    public static final String INVALID_CHAR_ERROR_STRING = "register.invalid.char";
    public static final String INVALID_CHAR_ERROR_STRING2 = "form.field.invalid.char";

    @Override
    public boolean supports(final Class<?> aClass) {
        return RegisterForm.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors) {
        final RegisterForm registerForm = (RegisterForm) object;
        final String titleCode = registerForm.getTitleCode();

        final String firstName = registerForm.getFirstName();
        final String lastName = registerForm.getLastName();
        final String email = registerForm.getEmail();
        final String pwd = registerForm.getPwd();
        final String checkPwd = registerForm.getCheckPwd();
        final String mobileNumber = registerForm.getMobileNumber();

        if (StringUtils.isEmpty(titleCode)) {
            errors.rejectValue("titleCode", DEFAULT_ERROR_MESSAGE_STRING, EMPTY_STRING);
        } else if (StringUtils.length(titleCode) > 255) {
            errors.rejectValue("titleCode", null, EMPTY_STRING);
        }

        if (StringUtils.isBlank(firstName)) {
            errors.rejectValue("firstName", DEFAULT_ERROR_MESSAGE_STRING, EMPTY_STRING);
        } else if (!validateRegex(firstName, NAME_REGEX)) {
            errors.rejectValue("firstName", INVALID_CHAR_ERROR_STRING, EMPTY_STRING);
        } else if (StringUtils.length(firstName) > 255) {
            errors.rejectValue("firstName", LONG_LENGTH_ERROR_STRING, EMPTY_STRING);
        }

        if (StringUtils.isBlank(lastName)) {
            errors.rejectValue("lastName", DEFAULT_ERROR_MESSAGE_STRING, EMPTY_STRING);
        } else if (!validateRegex(lastName, NAME_REGEX_WITHOUT_SPACE)) {
            errors.rejectValue("lastName", INVALID_CHAR_ERROR_STRING, EMPTY_STRING);
        } else if (StringUtils.length(lastName) > 255) {
            errors.rejectValue("lastName", LONG_LENGTH_ERROR_STRING, EMPTY_STRING);
        }

        if (StringUtils.length(firstName) + StringUtils.length(lastName) > 255) {
            errors.rejectValue("lastName", null, EMPTY_STRING);
            errors.rejectValue("firstName", null, EMPTY_STRING);
        }

        if (StringUtils.isEmpty(email)) {
            errors.rejectValue("email", DEFAULT_ERROR_MESSAGE_STRING, EMPTY_STRING);
        } else if (StringUtils.length(email) > 255 || !validateRegex(email, EMAIL_REGEX)) {
            errors.rejectValue("email", "register.email.invalid", EMPTY_STRING);
        }

        if (StringUtils.isEmpty(pwd)) {
            errors.rejectValue("pwd", DEFAULT_ERROR_MESSAGE_STRING, EMPTY_STRING);
        } else if (StringUtils.length(pwd) < 6 || StringUtils.length(pwd) > 15) {
            errors.rejectValue("pwd", "register.pwd.invalid", EMPTY_STRING);
        }

        if (StringUtils.isNotEmpty(pwd) && StringUtils.isNotEmpty(checkPwd) && !StringUtils.equals(pwd, checkPwd)) {
            errors.rejectValue("checkPwd", "validation.checkPwd.equals");
        } else {
            if (StringUtils.isEmpty(checkPwd)) {
                errors.rejectValue("checkPwd", DEFAULT_ERROR_MESSAGE_STRING, EMPTY_STRING);
            }
        }

        if (StringUtils.isEmpty(mobileNumber)) {
            errors.rejectValue("mobileNumber", DEFAULT_ERROR_MESSAGE_STRING, EMPTY_STRING);
        } else if (mobileNumber.startsWith("0") || !validateRegex(mobileNumber, MOBILE_NO_REGEX)) {
            if (mobileNumber.length() < 10) {
                errors.rejectValue("mobileNumber", "register.mobilenumber.invalid", EMPTY_STRING);
            } else {
                errors.rejectValue("mobileNumber", "register.large.mobilenumber.invalid", EMPTY_STRING);
            }
        }

    }

    public boolean validateRegex(final String str, final String regex) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

}
