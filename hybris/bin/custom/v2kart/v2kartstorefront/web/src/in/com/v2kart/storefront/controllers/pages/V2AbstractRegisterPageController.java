/**
 * 
 */
package in.com.v2kart.storefront.controllers.pages;

import java.util.Collections;

import in.com.v2kart.storefront.forms.validation.V2RegistrationFormValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractRegisterPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.LoginForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;

/**
 * @author shubhammaheshwari
 * Class Override to save the Customer Mobile Number
 */
public abstract class V2AbstractRegisterPageController extends AbstractRegisterPageController {

    @Resource(name = "v2RegistrationValidator")
    private V2RegistrationFormValidator v2RegistrationValidator;

    @Override
    protected String processRegisterUserRequest(final String referer, final RegisterForm form, final BindingResult bindingResult,
            final Model model, final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
            throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            model.addAttribute(form);
            model.addAttribute(new LoginForm());
            model.addAttribute(new GuestForm());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#", getMessageSource().getMessage("header.link.login", null,
                    getI18nService().getCurrentLocale()), null);
            model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));

            return handleRegistrationError(model);
        }

        final RegisterData data = new RegisterData();
        data.setFirstName(form.getFirstName());
        data.setLastName(form.getLastName());
        data.setLogin(form.getEmail());
        data.setPassword(form.getPwd());
        data.setTitleCode(form.getTitleCode());
        data.setMobileNumber(form.getMobileNumber());
        try {
            getCustomerFacade().register(data);
            getAutoLoginStrategy().login(form.getEmail().toLowerCase(), form.getPwd(), request, response);

            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "registration.confirmation.message.title");
        } catch (final DuplicateUidException e) {
            LOG.warn("registration failed: " + e);
            model.addAttribute(form);
            model.addAttribute(new LoginForm());
            model.addAttribute(new GuestForm());
            GlobalMessages.addErrorMessage(model, "form.global.error");
             bindingResult.rejectValue("email", "registration.error.account.exists.title");
    
            return handleRegistrationError(model);
        }

        return REDIRECT_PREFIX + getSuccessRedirect(request, response);
    }

    public V2RegistrationFormValidator getV2RegistrationValidator() {
        return v2RegistrationValidator;
    }

    public void setV2RegistrationValidator(V2RegistrationFormValidator v2RegistrationValidator) {
        this.v2RegistrationValidator = v2RegistrationValidator;
    }

}
