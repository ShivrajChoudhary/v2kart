/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.storefront.controllers.pages.checkout.steps.DeliveryMethodCheckoutStepController;

import in.com.v2kart.checkoutaddon.storefront.controllers.V2kartcheckoutaddonControllerConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author ankushgarg01
 * 
 */
@Controller
@RequestMapping(value = "/checkout/multi/delivery-method")
public class V2DeliveryMethodCheckoutStepController extends
		DeliveryMethodCheckoutStepController {
	private final static String DELIVERY_METHOD = "delivery-method";

	@RequestMapping(value = "/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateCheckoutStep(checkoutStep = DELIVERY_METHOD)
	public String enterStep(final Model model,
			final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException {

		super.enterStep(model, redirectAttributes);

		// removing breadcrumb from checkout pages
		model.asMap().remove(WebConstants.BREADCRUMBS_KEY);
		final List<? extends DeliveryModeData> deliveryMode = getCheckoutFacade()
				.getSupportedDeliveryModes();
		final List<DeliveryModeData> selectedDeliveryMode = new ArrayList<DeliveryModeData>();
		for (final DeliveryModeData selectDeliveryMode : deliveryMode) {
			if (getCartFacade().getSessionCart().isIsPickup()) {
				if (selectDeliveryMode.isIsPickUp()) {
					selectedDeliveryMode.add(selectDeliveryMode);
				}
			} else {
				if (!selectDeliveryMode.isIsPickUp()) {
					selectedDeliveryMode.add(selectDeliveryMode);
				}
			}
		}

		model.addAttribute("deliveryMethods", selectedDeliveryMode);

		// final CartData cartData = getCheckoutFacade().getCheckoutCart();
		// cartData.setDeliveryMode(selectedDeliveryMode.get(0));
		// model.addAttribute("cartData", cartData);

		return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
	}

	@RequestMapping(value = "/choose/selected", method = RequestMethod.GET)
	@RequireHardLogIn
	public String selectedDeliveryMethod(@RequestParam("delivery_method")
	final String selectedDeliveryMethod) {
		if (StringUtils.isNotEmpty(selectedDeliveryMethod)) {
			getCheckoutFacade().setDeliveryMode(selectedDeliveryMethod);
		}

		return getCheckoutStep().currentStep();
	}

}
