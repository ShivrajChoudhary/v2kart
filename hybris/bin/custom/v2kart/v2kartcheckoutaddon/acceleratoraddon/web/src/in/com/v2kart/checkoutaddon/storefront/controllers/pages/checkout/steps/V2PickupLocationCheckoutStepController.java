/**
 * 
 */
package in.com.v2kart.checkoutaddon.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.storefront.controllers.pages.checkout.steps.PickupLocationCheckoutStepController;

import in.com.v2kart.checkoutaddon.storefront.controllers.V2kartcheckoutaddonControllerConstants;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author mandeepjolly
 * 
 */
@Controller
@RequestMapping(value = "/checkout/multi/pickup-location")
public class V2PickupLocationCheckoutStepController extends PickupLocationCheckoutStepController {
    private final static String PICKUP_LOCATION = "pickup-location";

    @RequestMapping(value = "/choose", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    @PreValidateCheckoutStep(checkoutStep = PICKUP_LOCATION)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
    {
        // Try to set default delivery mode
        getCheckoutFacade().setDeliveryModeIfAvailable();

        model.addAttribute("cartData", getCheckoutFacade().getCheckoutCart());
        model.addAttribute("pickupConsolidationOptions", getCheckoutFacade().getConsolidatedPickupOptions());
        model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());
        storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.ChoosePickupLocationPage;
    }

    @Override
    @RequestMapping(value = "/choose", method = RequestMethod.POST)
    @RequireHardLogIn
    public String doSelectDeliveryLocation(@RequestParam(value = "posName")
    final String posName, final Model model,
            final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, CommerceCartModificationException
    {
        final ValidationResults validationResults = getCheckoutStep().validate(redirectAttributes);
        if (getCheckoutStep().checkIfValidationErrors(validationResults))
        {
            return getCheckoutStep().onValidation(validationResults);
        }

        // Consolidate the cart and add unsuccessful modifications to page
        model.addAttribute("validationData", getCheckoutFacade().consolidateCheckoutCart(posName));
        model.addAttribute("cartData", getCheckoutFacade().getCheckoutCart());
        model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());
        storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.ChoosePickupLocationPage;
    }

}
