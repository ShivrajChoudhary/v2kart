/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.controllers.pages.checkout.steps.validation.impl;

import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.storefront.checkout.steps.validation.impl.DefaultDeliveryMethodCheckoutStepValidator;

import in.com.v2kart.facades.order.V2CheckoutFacade;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Anuj
 * 
 */
public class V2DeliveryMethodCheckoutStepValidator extends
		DefaultDeliveryMethodCheckoutStepValidator {

	private V2CheckoutFacade v2CheckoutFacade;

	/**
	 * @return the v2CheckoutFacade
	 */
	public V2CheckoutFacade getV2CheckoutFacade() {
		return v2CheckoutFacade;
	}

	/**
	 * @param v2CheckoutFacade
	 *            the v2CheckoutFacade to set
	 */
	@Required
	public void setV2CheckoutFacade(final V2CheckoutFacade v2CheckoutFacade) {
		this.v2CheckoutFacade = v2CheckoutFacade;
	}

	@Override
	public ValidationResults validateOnEnter(
			final RedirectAttributes redirectAttributes) {

		final ValidationResults result = super
				.validateOnEnter(redirectAttributes);
		if (ValidationResults.SUCCESS.equals(result)) {
			if (getV2CheckoutFacade().hasNoServiceableMode()) {
				GlobalMessages.addFlashMessage(redirectAttributes,
						GlobalMessages.ERROR_MESSAGES_HOLDER,
						"checkout.multi.serviceablemode.notprovided");
				return ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS;
			}
		}

		return result;
	}
}
