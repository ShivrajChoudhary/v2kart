package in.com.v2kart.storefront.forms.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePwdForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PasswordValidator;

/**
 * To validate current password, new password and confirm new password
 * 
 * @author vikrant2480
 * 
 */
@Component("v2UpdatePasswordValidator")
public class V2UpdatePasswordValidator extends PasswordValidator {
    @Override
    public void validate(Object object, Errors errors) {

        final UpdatePwdForm passwordForm = (UpdatePwdForm) object;
        final String newPasswd = passwordForm.getPwd();
        final String checkPasswd = passwordForm.getCheckPwd();

        if (StringUtils.isEmpty(checkPasswd))
        {
            errors.rejectValue("checkPwd", "updatePwd.checkPwd.invalid");
        }

        if (!StringUtils.isEmpty(newPasswd) && !StringUtils.isEmpty(checkPasswd) && !newPasswd.equals(checkPasswd))
        {
            errors.rejectValue("checkPwd", "updatePwd.checkPwd.notmatch");
        }

    }
}
