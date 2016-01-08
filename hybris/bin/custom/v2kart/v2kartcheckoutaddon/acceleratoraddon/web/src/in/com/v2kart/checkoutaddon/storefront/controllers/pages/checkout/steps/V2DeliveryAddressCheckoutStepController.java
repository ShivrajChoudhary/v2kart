/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.storefront.controllers.pages.checkout.steps.DeliveryAddressCheckoutStepController;

import in.com.v2kart.checkoutaddon.storefront.controllers.V2kartcheckoutaddonControllerConstants;
import in.com.v2kart.checkoutaddon.storefront.forms.V2AddressForm;
import in.com.v2kart.checkoutaddon.storefront.forms.validation.V2AddressValidator;
import in.com.v2kart.core.serviceability.V2ServiceabilityService;
import in.com.v2kart.facades.customer.V2CustomerFacade;
import in.com.v2kart.facades.order.V2CheckoutFacade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author ankushgarg01
 * 
 */
@Controller
@RequestMapping(value = "/checkout/multi/delivery-address")
public class V2DeliveryAddressCheckoutStepController extends
		DeliveryAddressCheckoutStepController {
	@Resource(name = "customerFacade")
	protected V2CustomerFacade customerFacade;
	/**
     *
     */
	private final static String DELIVERY_ADDRESS = "delivery-address";

	private static final String MS = "ms";

	@Resource(name = "v2AddressValidator")
	private V2AddressValidator v2AddressValidator;

	@Resource(name = "v2CheckoutFacade")
	private V2CheckoutFacade v2CheckoutFacade;

	@Resource(name = "v2ServiceabilityService")
	private V2ServiceabilityService v2ServiceabilityService;

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = DELIVERY_ADDRESS)
	public String enterStep(final Model model,
			final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException {
		// getCheckoutFacade().setDeliveryAddressIfAvailable();
		v2CheckoutFacade.setDeliveryAddressIfAvailableForNewRegister();
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryAddresses",
				getDeliveryAddresses(cartData.getDeliveryAddress()));
		model.addAttribute("noAddress",
				Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()));
		final V2AddressForm v2AddressForm = new V2AddressForm();
		v2AddressForm.setCountryName(getCartFacade().getDeliveryCountries()
				.get(0).getName());
		model.addAttribute("regions",
				getI18NFacade().getRegionsForCountryIso("IN"));
		model.addAttribute("v2addressForm", v2AddressForm);
		model.addAttribute("showSaveToAddressBook", Boolean.TRUE);
		prepareDataForPage(model);
		storeCmsPageInModel(
				model,
				getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(
				model,
				getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		// removing Breadcrumb from checkout page
		// model.addAttribute(WebConstants.BREADCRUMBS_KEY,
		// getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryAddress.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}

	@RequestMapping(value = " /store-pickup", method = RequestMethod.POST)
	@RequireHardLogIn
	public String storePickup(@RequestParam(value = "name")
	final String name, @RequestParam(value = "line1")
	final String line1, @RequestParam(value = "line2")
	final String line2, @RequestParam(value = "town")
	final String town, @RequestParam(value = "postalcode")
	final String postalcode, @RequestParam(value = "pickup")
	final Boolean pickup, @RequestParam(value = "country")
	final String country, final RedirectAttributes redirectModel,
			final Model model) {
		final CustomerData user = customerFacade.getCurrentCustomer();
		final AddressData selectedAddress = new AddressData();

		selectedAddress.setTitleCode(MS);
		selectedAddress.setFirstName(user.getFirstName());
		selectedAddress.setLastName(user.getLastName());
		selectedAddress.setLine1(line1);
		selectedAddress.setLine2(line2);
		selectedAddress.setTown(town);
		selectedAddress.setPostalCode(postalcode);
		final CountryData countryData = getI18NFacade().getCountryForIsocode(
				country);
		selectedAddress.setCountry(countryData);
		// redirectModel.addAttribute("selectedAddress", selectedAddress);
		// return "redirect:/checkout/multi/delivery-address/select";

		getUserFacade().addAddress(selectedAddress);
		getCheckoutFacade().setDeliveryAddress(selectedAddress);
		GlobalMessages.addFlashMessage(redirectModel,
				GlobalMessages.CONF_MESSAGES_HOLDER,
				"checkout.multi.address.added");
		v2CheckoutFacade.changeCartDeliveryMode();
		return getCheckoutStep().nextStep();
	}

	@RequestMapping(value = "/V2-save-delivery-address", method = RequestMethod.POST)
	@RequireHardLogIn
	public String add(@ModelAttribute("v2addressForm")
	final V2AddressForm v2addressForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException {
		v2addressForm.setCountryIso(getCartFacade().getDeliveryCountries()
				.get(0).getIsocode());
		if (null != v2addressForm.getPostcode()) {
			v2addressForm.setPostcode(v2addressForm.getPostcode().trim());
		}
		v2addressForm.setTitleCode(MS);
		getV2AddressValidator().validate(v2addressForm, bindingResult);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryAddresses",
				getDeliveryAddresses(cartData.getDeliveryAddress()));
		this.prepareDataForPage(model);
		if (StringUtils.isNotBlank(v2addressForm.getCountryIso())) {
			model.addAttribute("regions", getI18NFacade()
					.getRegionsForCountryIso(v2addressForm.getCountryIso()));
			model.addAttribute("country", v2addressForm.getCountryIso());
		}

		model.addAttribute("noAddress",
				Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()));
		model.addAttribute("showSaveToAddressBook", Boolean.TRUE);

		if (bindingResult.hasErrors()) {
			GlobalMessages.addErrorMessage(model,
					"address.error.formentry.invalid");
			storePageDataInModel(model);
			return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		if (!v2ServiceabilityService.isProductServicableForPinCode(
				v2addressForm.getPostcode(), v2addressForm.getCountryIso())) {
			GlobalMessages.addErrorMessage(model,
					"checkout.multi.serviceablemode.notprovided");
			storePageDataInModel(model);
			return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final AddressData newAddress = new AddressData();
		newAddress.setTitleCode(v2addressForm.getTitleCode());
		newAddress.setFirstName(v2addressForm.getFirstName());
		newAddress.setLastName(v2addressForm.getLastName());
		newAddress.setLine1(v2addressForm.getLine1());
		newAddress.setLine2(v2addressForm.getLine2());
		newAddress.setTown(v2addressForm.getTownCity());
		newAddress.setPostalCode(v2addressForm.getPostcode());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);
		newAddress.setPhone(v2addressForm.getMobileno());
		if (v2addressForm.getCountryIso() != null) {
			final CountryData countryData = getI18NFacade()
					.getCountryForIsocode(v2addressForm.getCountryIso());
			newAddress.setCountry(countryData);
		}
		if (v2addressForm.getRegionIso() != null
				&& !StringUtils.isEmpty(v2addressForm.getRegionIso())) {
			final RegionData regionData = getI18NFacade()
					.getRegion(v2addressForm.getCountryIso(),
							v2addressForm.getRegionIso());
			newAddress.setRegion(regionData);
		}

		if (v2addressForm.getSaveInAddressBook() != null) {
			newAddress.setVisibleInAddressBook(v2addressForm
					.getSaveInAddressBook().booleanValue());
			if (v2addressForm.getSaveInAddressBook().booleanValue()
					&& getUserFacade().isAddressBookEmpty()) {
				newAddress.setDefaultAddress(true);
			}
		} else if (getCheckoutCustomerStrategy().isAnonymousCheckout()) {
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}

		/*
		 * // Verify the address data. final
		 * AddressVerificationResult<AddressVerificationDecision>
		 * verificationResult = getAddressVerificationFacade()
		 * .verifyAddressData(newAddress); final boolean addressRequiresReview =
		 * getAddressVerificationResultHandler() .handleResult(
		 * verificationResult, newAddress, model, redirectModel, bindingResult,
		 * getAddressVerificationFacade()
		 * .isCustomerAllowedToIgnoreAddressSuggestions(),
		 * "checkout.multi.address.updated");
		 * 
		 * if (addressRequiresReview) { storeCmsPageInModel( model,
		 * getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		 * setUpMetaDataForContentPage( model,
		 * getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		 * return
		 * V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout
		 * .AddEditDeliveryAddressPage; }
		 */
		getUserFacade().addAddress(newAddress);

		final AddressData previousSelectedAddress = getCheckoutFacade()
				.getCheckoutCart().getDeliveryAddress();
		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);
		if (previousSelectedAddress != null
				&& !previousSelectedAddress.isVisibleInAddressBook()) { // temporary
																		// address
																		// should
																		// be
																		// removed
			getUserFacade().removeAddress(previousSelectedAddress);
		}

		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);

		return getCheckoutStep().nextStep();
	}

	private void storePageDataInModel(final Model model)
			throws CMSItemNotFoundException {
		storeCmsPageInModel(
				model,
				getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(
				model,
				getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}

	/**
	 * @return the v2AddressValidator
	 */
	public V2AddressValidator getV2AddressValidator() {
		return v2AddressValidator;
	}

	/**
	 * @param v2AddressValidator
	 *            the v2AddressValidator to set
	 */
	public void setV2AddressValidator(
			final V2AddressValidator v2AddressValidator) {
		this.v2AddressValidator = v2AddressValidator;
	}
}
