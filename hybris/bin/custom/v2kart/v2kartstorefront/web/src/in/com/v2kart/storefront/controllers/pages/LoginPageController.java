/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package in.com.v2kart.storefront.controllers.pages;

import in.com.v2kart.storefront.controllers.ControllerConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

/**
 * Login Controller. Handles login and register for the account flow.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/login")
public class LoginPageController extends V2AbstractLoginPageController {
    private HttpSessionRequestCache httpSessionRequestCache;
    
    private static final String MS = "ms";
    
    @Override
    protected String getView() {
        return ControllerConstants.Views.Pages.Account.AccountLoginPage;
    }

    @Override
    protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response) {
        if (httpSessionRequestCache.getRequest(request, response) != null) {
            return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
        }
        return "/my-account";
    }

    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("login");
    }

    @Resource(name = "httpSessionRequestCache")
    public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache) {
        this.httpSessionRequestCache = accHttpSessionRequestCache;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String doLogin(@RequestHeader(value = "referer", required = false) final String referer,
            @RequestParam(value = "error", defaultValue = "false") final boolean loginError, final Model model,
            final HttpServletRequest request, final HttpServletResponse response, final HttpSession session)
            throws CMSItemNotFoundException {
        if (!loginError) {
            storeReferer(referer, request, response);
        }
        Object loginToTrackYourOrders = getSessionService().getAttribute("loginToTrackYourOrders");
        if(loginToTrackYourOrders!=null && ((Boolean)loginToTrackYourOrders).booleanValue()){
            GlobalMessages.addInfoMessage(model, "text.headline.login.trackYourOrders");
            getSessionService().removeAttribute("loginToTrackYourOrders");
        }
        return getDefaultLoginPage(loginError, session, model);
    }

    protected void storeReferer(final String referer, final HttpServletRequest request, final HttpServletResponse response) {
        if (StringUtils.isNotBlank(referer) && !StringUtils.endsWith(referer, "/login")
                && StringUtils.contains(referer, request.getServerName())) {
            httpSessionRequestCache.saveRequest(request, response);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String doRegister(@RequestHeader(value = "referer", required = false) final String referer, final RegisterForm form,
            final BindingResult bindingResult, final Model model, final HttpServletRequest request, final HttpServletResponse response,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        form.setTitleCode(MS);
        getV2RegistrationValidator().validate(form, bindingResult);
        return processRegisterUserRequest(referer, form, bindingResult, model, request, response, redirectModel);
    }
}
