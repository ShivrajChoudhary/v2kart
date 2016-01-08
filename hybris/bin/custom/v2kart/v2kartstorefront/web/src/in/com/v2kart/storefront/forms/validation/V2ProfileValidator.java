package in.com.v2kart.storefront.forms.validation;

import in.com.v2kart.storefront.forms.V2UpdateProfileForm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ProfileValidator;

@Component("v2profileValidator")
public class V2ProfileValidator extends ProfileValidator {

    public static final String MOBILE_NO_REGEX = "\\b\\d{10}\\b";
    public static final String NAME_REGEX = "\\b[A-Za-z._]+[A-Za-z-s._]*\\b";
    public static final String NAME_REGEX_WITH_SPACE = "^[a-zA-Z]*[ ]*[a-zA-Z]*$";

    @Override
    public void validate(final Object object, final Errors errors) {
        super.validate(object, errors);
        final V2UpdateProfileForm profileForm = (V2UpdateProfileForm) object;
        final String mobileNumber = profileForm.getMobileNumber();
        final String firstName = profileForm.getFirstName().trim();
        final String lastName = profileForm.getLastName().trim();

        // if (StringUtils.isEmpty(mobileNumber)) {
        // errors.rejectValue("mobileNumber", "profile.mobileNumber.invalid");
        // } else
        
        if (!validateRegex(firstName, NAME_REGEX_WITH_SPACE))
        {
            errors.rejectValue("firstName", "profile.validfirstName.invalid");
        }

        if (!validateRegex(lastName, NAME_REGEX))
        {
            errors.rejectValue("lastName", "profile.validlastName.invalid");
        }

        if (mobileNumber.startsWith("0") || !validateRegex(mobileNumber, MOBILE_NO_REGEX)) {
            if (mobileNumber.length() < 10) {
                errors.rejectValue("mobileNumber", "register.mobilenumber.invalid");
            } else {
                errors.rejectValue("mobileNumber", "register.large.mobilenumber.invalid");
            }

        }
    }

    public boolean validateRegex(final String str, final String regex) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
