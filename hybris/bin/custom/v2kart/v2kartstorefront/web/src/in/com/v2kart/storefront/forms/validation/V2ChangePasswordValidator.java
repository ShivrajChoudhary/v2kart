package in.com.v2kart.storefront.forms.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PasswordValidator;

/**
 * To validate current password, new password and confirm new password
 * 
 * @author vikrant2480
 * 
 */
@Component("v2ChangePasswordValidator")
public class V2ChangePasswordValidator extends PasswordValidator {
    @Override
    public void validate(Object object, Errors errors) {

        final UpdatePasswordForm passwordForm = (UpdatePasswordForm) object;
        final String currPasswd = passwordForm.getCurrentPassword();
        final String newPasswd = passwordForm.getNewPassword();
        final String checkPasswd = passwordForm.getCheckNewPassword();

        if (StringUtils.isEmpty(currPasswd))
        {
            errors.rejectValue("currentPassword", "updatePwd.pwd.required");
        }

        if (StringUtils.isEmpty(newPasswd))
        {
            errors.rejectValue("newPassword", "updatePwd.pwd.required");
        }
        else if (StringUtils.length(newPasswd) < 6 || StringUtils.length(newPasswd) > 15)
        {

            errors.rejectValue("newPassword", "updatePwd.pwd.invalidlength");
        }

        if (StringUtils.isEmpty(checkPasswd))
        {
            errors.rejectValue("checkNewPassword", "updatePwd.pwd.required");
        }
        else if (StringUtils.length(checkPasswd) < 6 || StringUtils.length(checkPasswd) > 15)
        {

            errors.rejectValue("checkNewPassword", "updatePwd.pwd.invalidlength");
        }

        if (!StringUtils.isEmpty(newPasswd) && !StringUtils.isEmpty(currPasswd) && currPasswd.equalsIgnoreCase(newPasswd))
        {
            errors.rejectValue("newPassword", "updatePwd.pwd.sameoldnewpassword");
        }

    }
}
