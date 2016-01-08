package in.com.v2kart.storefront.controllers.pages;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;

/**
 * Controller for Error page
 */
@Controller
@Scope("tenant")
@RequestMapping("/serverError")
public class ErrorPageController extends AbstractPageController {

    private static final Logger LOG = Logger.getLogger(ErrorPageController.class);

    private static final String SERVER_ERROR_PAGE = "/serverError";

    @Resource(name = "simpleBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

    @Resource(name = "uiExperienceService")
    private UiExperienceService uiExperienceService;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String serverError(final Model model) throws CMSItemNotFoundException {
        LOG.error("Server Error Page");
        ContentPageModel contentPageModel = getContentPageForLabelOrId(SERVER_ERROR_PAGE);
        storeCmsPageInModel(model, contentPageModel);
        setUpMetaDataForContentPage(model, contentPageModel);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.server.error"));
        final UiExperienceLevel currentUiExperienceLevel = uiExperienceService.getUiExperienceLevel();
        if (UiExperienceLevel.MOBILE.equals(currentUiExperienceLevel)) {
            model.addAttribute("message", "system.error.page.server.error");
        } else {
            GlobalMessages.addErrorMessage(model, "system.error.page.server.error");
        }
        return getViewForPage(model);
    }
}
