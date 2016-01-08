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
package in.com.v2kart.checkoutaddon.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SopPaymentDetailsForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.storefront.controllers.ControllerConstants;
import de.hybris.platform.storefront.controllers.pages.checkout.steps.PaymentMethodCheckoutStepController;

import in.com.v2kart.ccavenuepaymentintegration.facades.CCAvenuePaymentFacade;
import in.com.v2kart.checkoutaddon.storefront.controllers.V2kartcheckoutaddonControllerConstants;
import in.com.v2kart.checkoutaddon.storefront.forms.V2AddressForm;
import in.com.v2kart.checkoutaddon.storefront.forms.V2PaymentInfoForm;
import in.com.v2kart.checkoutaddon.storefront.forms.validation.V2AddressValidator;
import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.payment.constants.PaymentConstants.PaymentProperties;
import in.com.v2kart.core.serviceability.V2ServiceabilityService;
import in.com.v2kart.core.sms.V2SmsService;
import in.com.v2kart.core.sms.populator.V2UserSmsDataMapPopulator;
import in.com.v2kart.facades.cart.V2CartFacade;
import in.com.v2kart.facades.customer.V2CustomerFacade;
import in.com.v2kart.facades.order.V2CheckoutFacade;
import in.com.v2kart.facades.order.data.V2StoreCreditPaymentInfoData;
import in.com.v2kart.facades.otp.V2CashOnDeliveryOtp;
import in.com.v2kart.facades.payment.V2PaymentFacade;
import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;
import in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.facades.PAYUPaymentFacade;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ccavenue.security.AesCryptUtil;

@RequestMapping(value = "/checkout/multi/payment-method")
public class V2PaymentMethodCheckoutStepController extends
        PaymentMethodCheckoutStepController {
    /**
     *
     */

    private final static String PAYMENT_METHOD = "payment-method";
    private final static String INDIA_COUNTRY_ISO = "IN";
    private static final String IS_PAYMENT_GATEWAY_ERROR_PARAM = "isPaymentGatewayError";

    @Resource(name = "v2SmsService")
    private V2SmsService v2SmsService;

    @Resource(name = "userService")
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public V2SmsService getV2SmsService() {
        return v2SmsService;
    }

    public void setV2SmsService(final V2SmsService v2SmsService) {
        this.v2SmsService = v2SmsService;
    }

    @Resource(name = "v2UserSmsDataMapPopulator")
    private V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator;

    public V2UserSmsDataMapPopulator getV2UserSmsDataMapPopulator() {
        return v2UserSmsDataMapPopulator;
    }

    public void setV2UserSmsDataMapPopulator(final V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator) {
        this.v2UserSmsDataMapPopulator = v2UserSmsDataMapPopulator;
    }

    @Resource(name = "v2CashOnDeliveryOtp")
    private V2CashOnDeliveryOtp v2CashOnDeliveryOtp;

    @Resource(name = "customerFacade")
    protected V2CustomerFacade customerFacade;

    @Resource(name = "v2AddressValidator")
    private V2AddressValidator v2AddressValidator;

    @Resource(name = "v2PaymentFacade")
    private V2PaymentFacade v2PaymentFacade;

    @Resource(name = "acceleratorCheckoutFacade")
    private V2CheckoutFacade acceleratorCheckoutFacade;

    @Resource(name = "payUPaymentFacade")
    private PAYUPaymentFacade payUPaymentFacade;

    @Resource(name = "ccavenuePaymentFacade")
    private CCAvenuePaymentFacade ccavenuePaymentFacade;

    @Resource(name = "cmsSiteService")
    private CMSSiteService cmsSiteService;

    @Resource(name = "priceDataFactory")
    private PriceDataFactory priceDataFactory;

    @Resource(name = "commonI18NService")
    private CommonI18NService commonI18NService;

    @Resource(name = "serviceabilityService")
    private V2ServiceabilityService v2ServiceabilityService;

    @Resource(name = "ebsPaymentFacade")
    private EBSPaymentFacade ebsPaymentFacade;
    
    @Autowired
    private V2CartFacade cartFacade;

    private enum PaymentGateway {
        CCAVENUE("ccavenue"), PAYU("payu"), EBS("ebs");

        private final String value;

        private PaymentGateway(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private enum EnforcedPaymentMethod {
        NET_BANKING("netbanking"), CREDIT_CARD("creditcard"), DEBIT_CARD(
                "debitcard"), CASH_CARD("cashCard");
        private final String value;

        private EnforcedPaymentMethod(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * @return the v2AddressValidator
     */

    public V2AddressValidator getV2AddressValidator() {
        return v2AddressValidator;
    }

    /**
     * @param v2AddressValidator
     *        the v2AddressValidator to set
     */

    public void setV2AddressValidator(
            final V2AddressValidator v2AddressValidator) {
        this.v2AddressValidator = v2AddressValidator;
    }

    /**
     * @return the v2PaymentFacade
     */
    public V2PaymentFacade getV2PaymentFacade() {
        return v2PaymentFacade;
    }

    /**
     * @param v2PaymentFacade
     *        the v2PaymentFacade to set
     */
    public void setV2PaymentFacade(final V2PaymentFacade v2PaymentFacade) {
        this.v2PaymentFacade = v2PaymentFacade;
    }

    /**
     * @return the payUPaymentFacade
     */
    public PAYUPaymentFacade getPayUPaymentFacade() {
        return payUPaymentFacade;
    }

    /**
     * @param payUPaymentFacade
     *        the payUPaymentFacade to set
     */
    public void setPayUPaymentFacade(final PAYUPaymentFacade payUPaymentFacade) {
        this.payUPaymentFacade = payUPaymentFacade;
    }

    @Override
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @RequireHardLogIn
    @PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
    public String enterStep(final Model model,
            final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException {
        final String isPickup = getCartFacade().getSessionCart().isIsPickup() ? Boolean.TRUE
                .toString() : Boolean.FALSE.toString();
        model.addAttribute("isPickup", isPickup);
        return processAddPaymentMethodRequest(model, false);
    }

    protected String processAddPaymentMethodRequest(final Model model,
            final boolean isUsingWallet) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);

        // removing breadcrumbs from checkout pages
        model.asMap().remove(WebConstants.BREADCRUMBS_KEY);

        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        int pickFromStoreCount = 0;

        for (final OrderEntryData cartEntry : cartData.getEntries()) {
            if (cartEntry.getDeliveryPointOfService() != null) {
                pickFromStoreCount++;
            }
        }

        if (pickFromStoreCount == cartData.getEntries().size()) {
            model.addAttribute("isPickUpAllFromStore", Boolean.TRUE);
        }
        if (isUsingWallet) {
            // cartData = getCheckoutFacade().getCheckoutCart();
            acceleratorCheckoutFacade.useMyWalletMoney(cartData, new AddressData());
            model.addAttribute("isUsingWallet", Boolean.TRUE);
        }

        // Use the checkout PCI strategy for getting the URL for creating new
        // subscriptions.
        final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade()
                .getSubscriptionPciOption();
        setCheckoutStepLinksForModel(model, getCheckoutStep());
        model.addAttribute("cartData", cartData);
        if (CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption)) {
            // Redirect the customer to the HOP page or show error message if it
            // fails (e.g. no HOP configurations).
            try {
                /*
                 * final PaymentData hostedOrderPageData = getPaymentFacade().beginHopCreateSubscription ("/checkout/multi/hop/response",
                 * "/integration/merchant_callback"); model.addAttribute("hostedOrderPageData", hostedOrderPageData);
                 */

                final boolean hopDebugMode = getSiteConfigService().getBoolean(
                        PaymentConstants.PaymentProperties.HOP_DEBUG_MODE,
                        false);
                model.addAttribute("hopDebugMode",
                        Boolean.valueOf(hopDebugMode));

                V2PaymentInfoForm v2PaymentInfoForm = (V2PaymentInfoForm) model
                        .asMap().get("v2PaymentInfoForm");
                if (v2PaymentInfoForm == null) {
                    v2PaymentInfoForm = populateV2PaymentForm(cartData
                            .getDeliveryAddress());
                }
                if (isUsingWallet) {
                    v2PaymentInfoForm.setIsUsingWallet(Boolean.TRUE);
                }
                model.addAttribute("v2PaymentInfoForm", v2PaymentInfoForm);
                model.addAttribute("v2addressForm", populateV2AddressForm());
                model.addAttribute("regions", getI18NFacade()
                        .getRegionsForCountryIso(INDIA_COUNTRY_ISO));

                // model.addAttribute("cartData", cartData);
                model.addAttribute("netbanking",
                        EnforcedPaymentMethod.NET_BANKING.getValue());
                model.addAttribute("creditcard",
                        EnforcedPaymentMethod.CREDIT_CARD.getValue());
                model.addAttribute("debitcard",
                        EnforcedPaymentMethod.DEBIT_CARD.getValue());
                model.addAttribute("cashcard",
                        EnforcedPaymentMethod.CASH_CARD.getValue());

                // set cod is available or not
                model.addAttribute("isCODFacilityAvailable",
                        getIsCODFacilityApplicable(cartData));

                storeAttributeInPage(model, v2PaymentInfoForm, cartData);

                final String pgErrorKey = (String) model.asMap().get(
                        IS_PAYMENT_GATEWAY_ERROR_PARAM);
                if (StringUtils.isNotEmpty(pgErrorKey)) {
                    GlobalMessages.addErrorMessage(model, pgErrorKey);
                }
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
            } catch (final Exception e) {
                LOG.error("Failed to build beginCreateSubscription request", e);
                GlobalMessages
                        .addErrorMessage(model,
                                "checkout.multi.paymentMethod.addPaymentDetails.generalError");
            }
        } else if (CheckoutPciOptionEnum.SOP.equals(subscriptionPciOption)) {
            // Build up the SOP form data and render page containing form
            final SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
            try {
                setupSilentOrderPostPage(sopPaymentDetailsForm, model);
                return ControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
            } catch (final Exception e) {
                LOG.error("Failed to build beginCreateSubscription request", e);
                GlobalMessages
                        .addErrorMessage(model,
                                "checkout.multi.paymentMethod.addPaymentDetails.generalError");
                model.addAttribute("sopPaymentDetailsForm",
                        sopPaymentDetailsForm);
            }
        }

        // If not using HOP or SOP we need to build up the payment details form
        /*
         * final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm(); final AddressForm addressForm = new AddressForm();
         * paymentDetailsForm.setBillingAddress(addressForm); model.addAttribute(paymentDetailsForm);
         */

        return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
    }

    @RequestMapping(value = "/cod", method = RequestMethod.POST)
    @RequireHardLogIn
    public String doHandleCODRequest(final Model model,
            final V2PaymentInfoForm v2PaymentInfoForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes)
            throws InvalidCartException, CMSItemNotFoundException {
        try {
            return processCODSubmitRequest(model, v2PaymentInfoForm,
                    bindingResult, redirectAttributes, null);
        } catch (final ModelSavingException e) {
            // if exception occured during Mobile number save.We have to show
            // different message for this exception at storefront so
            // different catch is used
            LOG.error("Mobile number incorrect", e);
            GlobalMessages.addErrorMessage(model,
                    "checkout.placeOrder.phonenumber.error");
            bindingResult.rejectValue("smsmobileno",
                    "checkout.phonenumber.incorrect");
            redirectAttributes.addFlashAttribute("v2PaymentInfoForm",
                    v2PaymentInfoForm);
            storeAttributeInPage(model, v2PaymentInfoForm, null);
            return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            redirectAttributes.addFlashAttribute("v2PaymentForm",
                    v2PaymentInfoForm);
            return getCheckoutStep().currentStep();
        }
    }

    protected String processCODSubmitRequest(final Model model,
            final V2PaymentInfoForm v2PaymentInfoForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes, final CartData cartData)
            throws InvalidCartException, CMSItemNotFoundException {
        AddressData adressData = null;
        OrderData orderData = null;
        final CustomerData currentCustomerData = customerFacade
                .getCurrentCustomer();

        if ((null != v2PaymentInfoForm.getSmsmobileno())
                && (!v2PaymentInfoForm.getSmsmobileno().equals(""))) {
            final List<String> phoneNumbers = currentCustomerData
                    .getPhoneNumbers();
            if (!phoneNumbers.contains(v2PaymentInfoForm.getSmsmobileno())) {
                phoneNumbers.add(v2PaymentInfoForm.getSmsmobileno());
                currentCustomerData.setPhoneNumbers(phoneNumbers);
                customerFacade.updatePhoneNumbers(currentCustomerData, true);
            }
        }

        if (!v2PaymentInfoForm.getIsUsingShippingAddress().booleanValue()) {
            if (null != v2PaymentInfoForm.getPostcode()) {
                v2PaymentInfoForm.setPostcode(v2PaymentInfoForm.getPostcode()
                        .trim());
            }

            v2PaymentInfoForm.setCountryIso(INDIA_COUNTRY_ISO);
            v2PaymentInfoForm.setTitleCode("ms");
            getV2AddressValidator().validate(v2PaymentInfoForm, bindingResult);

            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model,
                        "address.error.formentry.invalid");
                storeAttributeInPage(model, v2PaymentInfoForm, cartData);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
            }
            adressData = getPaymentAddress(v2PaymentInfoForm);
        }
        acceleratorCheckoutFacade.assignCodPaymentMode(adressData);
        orderData = acceleratorCheckoutFacade.placeOrder();
        return redirectToOrderConfirmationPage(orderData);
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @RequireHardLogIn
    public String sendPayment(final Model model,
            @ModelAttribute("v2PaymentInfoForm")
            final V2PaymentInfoForm v2PaymentInfoForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            @RequestParam(value = "isPickUpAllFromStore", required = false)
            final Boolean isPickUpAllFromStore) throws CMSItemNotFoundException {
        try {
            if (isPickUpAllFromStore != null) {
                model.addAttribute("isPickUpAllFromStore", isPickUpAllFromStore);
            }
            cartFacade.removeCOD();
            final String isPickup = getCartFacade().getSessionCart()
                    .isIsPickup() ? Boolean.TRUE.toString() : Boolean.FALSE
                    .toString();
            model.addAttribute("isPickup", isPickup);
            return processSendRequestSubmit(model, v2PaymentInfoForm,
                    bindingResult, redirectAttributes, null);
        } catch (final ModelSavingException e) {
            // if exception occured during Mobile number save.We have to show
            // different message for this exception at storefront so
            // different catch is used
            LOG.error("Mobile number incorrect", e);
            GlobalMessages.addErrorMessage(model,
                    "checkout.placeOrder.phonenumber.error");
            bindingResult.rejectValue("smsmobileno",
                    "checkout.phonenumber.incorrect");
            redirectAttributes.addFlashAttribute("v2PaymentInfoForm",
                    v2PaymentInfoForm);
            storeAttributeInPage(model, v2PaymentInfoForm, null);
            return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            redirectAttributes.addFlashAttribute("v2PaymentInfoForm",
                    v2PaymentInfoForm);
            storeAttributeInPage(model, v2PaymentInfoForm, null);
            return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        }
    }

    protected String processSendRequestSubmit(final Model model,
            final V2PaymentInfoForm v2PaymentInfoForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes, CartData cartData)
            throws CMSItemNotFoundException, V2Exception {
        final String phoneNumber;
        final AddressData addressData;
        List<String> orderConfirmPhoneNumber = new ArrayList<>();

        final CustomerData currentCustomerData = customerFacade
                .getCurrentCustomer();

        if ((null != v2PaymentInfoForm.getSmsmobileno())
                && (!v2PaymentInfoForm.getSmsmobileno().equals(""))) {
            final List<String> phoneNumbers = currentCustomerData
                    .getPhoneNumbers();
            if (!phoneNumbers.contains(v2PaymentInfoForm.getSmsmobileno())) {
                orderConfirmPhoneNumber.add(v2PaymentInfoForm.getSmsmobileno());
                currentCustomerData.setPhoneNumbers(orderConfirmPhoneNumber);
                customerFacade.updatePhoneNumbers(currentCustomerData, true);
            }
        }
        else {
            orderConfirmPhoneNumber = new ArrayList<>(0);
            // orderConfirmPhoneNumber.add(v2PaymentInfoForm.getSmsmobileno());
            currentCustomerData.setPhoneNumbers(orderConfirmPhoneNumber);
            customerFacade.updatePhoneNumbers(currentCustomerData, false);

        }
        if (!v2PaymentInfoForm.getIsUsingShippingAddress().booleanValue()) {
            if (null != v2PaymentInfoForm.getPostcode()) {
                v2PaymentInfoForm.setPostcode(v2PaymentInfoForm.getPostcode()
                        .trim());
            }

            v2PaymentInfoForm.setTitleCode("ms");
            getV2AddressValidator().validate(v2PaymentInfoForm, bindingResult);
            phoneNumber = v2PaymentInfoForm.getMobileno();
            addressData = getPaymentAddress(v2PaymentInfoForm);

            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model,
                        "address.error.formentry.invalid");
                storeAttributeInPage(model, v2PaymentInfoForm, cartData);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
            }
        } else {
            // cartData is not null when it comes from the wallet post
            if (cartData == null) {
                cartData = getCheckoutFacade().getCheckoutCart();
            }
            phoneNumber = cartData.getDeliveryAddress().getPhone();
            addressData = cartData.getDeliveryAddress();
        }
        if (validatePaymentInfoForm(v2PaymentInfoForm)) {
            GlobalMessages.addErrorMessage(model,
                    "checkout.multi.paymentMethod.select.error");
            storeAttributeInPage(model, v2PaymentInfoForm, cartData);
            return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        }
        getV2PaymentFacade().saveBillingAddress(addressData);

        /*
         * if (null == addressData) { addressData = acceleratorCheckoutFacade.getCheckoutCart().getBillingAddress(); }
         */
        final String enforcedPaymentMethod = v2PaymentInfoForm
                .getEnforcedPaymentMethod();
        if (EnforcedPaymentMethod.NET_BANKING.getValue().equals(
                enforcedPaymentMethod)
                || EnforcedPaymentMethod.CREDIT_CARD.getValue().equals(
                        enforcedPaymentMethod)
                || EnforcedPaymentMethod.DEBIT_CARD.getValue().equals(
                        enforcedPaymentMethod)
                || EnforcedPaymentMethod.CASH_CARD.getValue().equals(
                        enforcedPaymentMethod)) {

            // fgPaymentInfoForm.setEnforcedPaymentMethod(Config.getParameter(ICICI.CC_DC_MERCHANT_KEY));
            // In case of HDFC, set required enforcedPaymentMethod
            if (getSiteConfigService().getProperty(
                    PaymentProperties.PAYMENT_GATEWAY + enforcedPaymentMethod)
                    .equalsIgnoreCase(PaymentGateway.PAYU.getValue())) {

                final String pg = PAYU.PG_CC;
                final PaymentData request = getPayUPaymentFacade()
                        .beginHopCreatePayment(enforcedPaymentMethod,
                                addressData, pg, phoneNumber);
                model.addAttribute("hostedOrderPageData", request);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.PayUHostedOrderWaitPage;

            } else if (getSiteConfigService().getProperty(
                    PaymentProperties.PAYMENT_GATEWAY + enforcedPaymentMethod)
                    .equalsIgnoreCase(PaymentGateway.EBS.getValue())) {

                final String pg = EBS.PG_DC;
                final PaymentData request = ebsPaymentFacade
                        .beginHopCreatePayment(pg, addressData, phoneNumber);
                model.addAttribute("hostedOrderPageData", request);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.EBSHostedOrderPostPage;
            } else if (getSiteConfigService().getProperty(
                    PaymentProperties.PAYMENT_GATEWAY + enforcedPaymentMethod)
                    .equalsIgnoreCase(PaymentGateway.CCAVENUE.getValue())) {

                final PaymentData request = getCcavenuePaymentFacade()
                        .beginHopCreatePayment(enforcedPaymentMethod,
                                addressData, phoneNumber);
                model.addAttribute("hostedOrderPageData", request);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.CCAvenueHostedOrderWaitPage;
            } else {

                final PaymentData request = getCcavenuePaymentFacade()
                        .beginHopCreatePayment(enforcedPaymentMethod,
                                addressData, phoneNumber);
                model.addAttribute("hostedOrderPageData", request);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.CCAvenueHostedOrderWaitPage;
            }
        } else {
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            redirectAttributes.addFlashAttribute("v2PaymentInfoForm",
                    v2PaymentInfoForm);
            storeAttributeInPage(model, v2PaymentInfoForm, null);
            return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        }
    }

    @Override
    @RequestMapping(value = { "/add" }, method = RequestMethod.POST)
    @RequireHardLogIn
    public String add(final Model model, @Valid
    final PaymentDetailsForm paymentDetailsForm,
            final BindingResult bindingResult) throws CMSItemNotFoundException {
        getPaymentDetailsValidator()
                .validate(paymentDetailsForm, bindingResult);
        setupAddPaymentPage(model);

        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        model.addAttribute("cartData", cartData);

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model,
                    "checkout.error.paymentethod.formentry.invalid");
            return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
        }

        final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
        paymentInfoData.setId(paymentDetailsForm.getPaymentId());
        paymentInfoData.setCardType(paymentDetailsForm.getCardTypeCode());
        paymentInfoData
                .setAccountHolderName(paymentDetailsForm.getNameOnCard());
        paymentInfoData.setCardNumber(paymentDetailsForm.getCardNumber());
        paymentInfoData.setStartMonth(paymentDetailsForm.getStartMonth());
        paymentInfoData.setStartYear(paymentDetailsForm.getStartYear());
        paymentInfoData.setExpiryMonth(paymentDetailsForm.getExpiryMonth());
        paymentInfoData.setExpiryYear(paymentDetailsForm.getExpiryYear());
        if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount())
                || getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            paymentInfoData.setSaved(true);
        }
        paymentInfoData.setIssueNumber(paymentDetailsForm.getIssueNumber());

        final AddressData addressData;
        if (Boolean.FALSE.equals(paymentDetailsForm.getNewBillingAddress())) {
            addressData = getCheckoutFacade().getCheckoutCart()
                    .getDeliveryAddress();
            if (addressData == null) {
                GlobalMessages
                        .addErrorMessage(
                                model,
                                "checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg");
                return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
            }

            addressData.setBillingAddress(true); // mark this as billing address
        } else {
            final AddressForm addressForm = paymentDetailsForm
                    .getBillingAddress();
            addressData = new AddressData();
            if (addressForm != null) {
                addressData.setId(addressForm.getAddressId());
                addressData.setTitleCode("ms");
                addressData.setFirstName(addressForm.getFirstName());
                addressData.setLastName(addressForm.getLastName());
                addressData.setLine1(addressForm.getLine1());
                addressData.setLine2(addressForm.getLine2());
                addressData.setTown(addressForm.getTownCity());
                addressData.setPostalCode(addressForm.getPostcode());
                addressData.setCountry(getI18NFacade().getCountryForIsocode(
                        addressForm.getCountryIso()));
                if (addressForm.getRegionIso() != null) {
                    addressData.setRegion(getI18NFacade().getRegion(
                            addressForm.getCountryIso(),
                            addressForm.getRegionIso()));
                }

                addressData.setShippingAddress(Boolean.TRUE.equals(addressForm
                        .getShippingAddress()));
                addressData.setBillingAddress(Boolean.TRUE.equals(addressForm
                        .getBillingAddress()));
            }
        }

        getAddressVerificationFacade().verifyAddressData(addressData);
        paymentInfoData.setBillingAddress(addressData);

        final CCPaymentInfoData newPaymentSubscription = getCheckoutFacade()
                .createPaymentSubscription(paymentInfoData);
        if (newPaymentSubscription != null
                && StringUtils.isNotBlank(newPaymentSubscription
                        .getSubscriptionId())) {
            if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount())
                    && getUserFacade().getCCPaymentInfos(true).size() <= 1) {
                getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
            }
            getCheckoutFacade().setPaymentDetails(
                    newPaymentSubscription.getId());
        } else {
            GlobalMessages
                    .addErrorMessage(model,
                            "checkout.multi.paymentMethod.createSubscription.failedMsg");
            return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
        }

        model.addAttribute("paymentId", newPaymentSubscription.getId());
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        return getCheckoutStep().nextStep();
    }

    @RequestMapping(value = "/add/wallet", method = RequestMethod.GET)
    public String useMyWallet(final Model model)
            throws CMSItemNotFoundException {
        return processAddPaymentMethodRequest(model, true);
    }

    @RequireHardLogIn
    @RequestMapping(value = "/add/wallet", method = RequestMethod.POST)
    public String useMyWallet(final Model model,
            @RequestParam(value = "cod", required = true)
            final boolean cod, @RequestParam(value = "pg", required = true)
            final boolean pg, @ModelAttribute("v2PaymentInfoForm")
            final V2PaymentInfoForm v2PaymentInfoForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException {
        String returnPath = V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        try {
            AddressData addressData = changeBillingAddress(v2PaymentInfoForm, bindingResult);
            if (addressData == null) {
                addressData = new AddressData();
            }
            acceleratorCheckoutFacade.useMyWalletMoney(cartData, addressData);
            if (cod) {
                return processCODSubmitRequest(model, v2PaymentInfoForm,
                        bindingResult, redirectAttributes, cartData);
            } else if (pg) {
                return processSendRequestSubmit(model, v2PaymentInfoForm,
                        bindingResult, redirectAttributes, cartData);
            } else {
                OrderData orderData = null;
                final V2StoreCreditPaymentInfoData v2StoreCreditPaymentInfoData = cartData
                        .getStoreCreditPaymentInfo();
                if (v2StoreCreditPaymentInfoData != null
                        && cartData.getTotalPayableBalance().getValue()
                                .doubleValue() <= 0) {
                    orderData = acceleratorCheckoutFacade.placeOrder();
                }
                return redirectToOrderConfirmationPage(orderData);
            }
        } catch (final InvalidCartException e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            redirectAttributes.addFlashAttribute("v2PaymentForm",
                    v2PaymentInfoForm);
            returnPath = getCheckoutStep().currentStep();
        } catch (final V2Exception e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            redirectAttributes.addFlashAttribute("v2PaymentForm",
                    v2PaymentInfoForm);
            storeAttributeInPage(model, v2PaymentInfoForm, cartData);
        }
        return returnPath;
    }

    AddressData changeBillingAddress(final V2PaymentInfoForm v2PaymentInfoForm, final BindingResult bindingResult) {
        final String phoneNumber;
        final AddressData addressData;
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        final CustomerData currentCustomerData = customerFacade
                .getCurrentCustomer();

        if ((null != v2PaymentInfoForm.getSmsmobileno())
                && (!v2PaymentInfoForm.getSmsmobileno().equals(""))) {
            final List<String> phoneNumbers = currentCustomerData
                    .getPhoneNumbers();
            if (!phoneNumbers.contains(v2PaymentInfoForm.getSmsmobileno())) {
                phoneNumbers.add(v2PaymentInfoForm.getSmsmobileno());
                currentCustomerData.setPhoneNumbers(phoneNumbers);
                customerFacade.updatePhoneNumbers(currentCustomerData, true);
            }
        }

        if (!v2PaymentInfoForm.getIsUsingShippingAddress().booleanValue()) {
            if (null != v2PaymentInfoForm.getPostcode()) {
                v2PaymentInfoForm.setPostcode(v2PaymentInfoForm.getPostcode()
                        .trim());
            }

            v2PaymentInfoForm.setTitleCode("ms");
            getV2AddressValidator().validate(v2PaymentInfoForm, bindingResult);
            phoneNumber = v2PaymentInfoForm.getMobileno();
            addressData = getPaymentAddress(v2PaymentInfoForm);

            if (bindingResult.hasErrors()) {
                return new AddressData();
                // GlobalMessages.addErrorMessage(model,
                // "address.error.formentry.invalid");
                // storeAttributeInPage(model, v2PaymentInfoForm, cartData);
                // return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
            }
        } else {
            // cartData is not null when it comes from the wallet post
            // if (cartData == null) {
            // cartData = getCheckoutFacade().getCheckoutCart();
            // }
            phoneNumber = cartData.getDeliveryAddress().getPhone();
            addressData = cartData.getDeliveryAddress();
        }
        // if (validatePaymentInfoForm(v2PaymentInfoForm)) {
        // GlobalMessages.addErrorMessage(model,
        // "checkout.multi.paymentMethod.select.error");
        // storeAttributeInPage(model, v2PaymentInfoForm, cartData);
        // return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        // }
        getV2PaymentFacade().saveBillingAddress(addressData);

        // cartData = getCheckoutFacade().getCheckoutCart();

        return addressData;

    }

    /**
     * @param model
     * @param v2PaymentInfoForm
     * @throws CMSItemNotFoundException
     */
    private void storeAttributeInPage(final Model model,
            final V2PaymentInfoForm v2PaymentInfoForm, CartData cartData)
            throws CMSItemNotFoundException {
        storeCmsPageInModel(
                model,
                getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(
                model,
                getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setCheckoutStepLinksForModel(model, getCheckoutStep());
        if (cartData == null) {
            cartData = getCheckoutFacade().getCheckoutCart();
        }

        final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();

        cartData.setIsCODChargesApplicable(Boolean.TRUE.booleanValue());
        final double minCodCharges = cmsSite.getCodCharges().doubleValue();
        final double codChargesOnOrder = cartData.getTotalPrice().getValue()
                .doubleValue() * 0.02;
        final double codCharges = (minCodCharges < codChargesOnOrder) ? codChargesOnOrder
                : minCodCharges;

        cartData.setCodCharges(priceDataFactory.create(PriceDataType.BUY,
                BigDecimal.valueOf(codCharges),
                commonI18NService.getCurrentCurrency()));

        /*
         * final double shopMoreForFreeCODCharges = cmsSite.getCodChargesApplicableThreshold().doubleValue() -
         * cartData.getTotalPrice().getValue().doubleValue(); cartData.setShopMoreForFreeCOD (priceDataFactory.create(PriceDataType.BUY,
         * BigDecimal.valueOf(shopMoreForFreeCODCharges), commonI18NService.getCurrentCurrency()));
         */

        /*
         * // if cart total price is less than COD applicable threshold value if (cartData.getTotalPrice().getValue().doubleValue() <
         * cmsSite.getCodChargesApplicableThreshold().doubleValue()) {
         *
         * cartData.setIsCODChargesApplicable(Boolean.TRUE.booleanValue());
         *
         * cartData.setCodCharges(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(cmsSite.getCodCharges().doubleValue()),
         * commonI18NService.getCurrentCurrency())); final double shopMoreForFreeCODCharges =
         * cmsSite.getCodChargesApplicableThreshold().doubleValue() - cartData.getTotalPrice().getValue().doubleValue();
         * cartData.setShopMoreForFreeCOD (priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(shopMoreForFreeCODCharges),
         * commonI18NService.getCurrentCurrency())); } else { cartData.setIsCODChargesApplicable(Boolean.FALSE.booleanValue()); }
         */
        model.addAttribute("v2PaymentInfoForm", v2PaymentInfoForm);
        model.addAttribute("regions",
                getI18NFacade().getRegionsForCountryIso(INDIA_COUNTRY_ISO));
        model.addAttribute("cartData", cartData);
        model.addAttribute("netbanking",
                EnforcedPaymentMethod.NET_BANKING.getValue());
        model.addAttribute("creditcard",
                EnforcedPaymentMethod.CREDIT_CARD.getValue());
        model.addAttribute("debitcard",
                EnforcedPaymentMethod.DEBIT_CARD.getValue());
        model.addAttribute("cashcard",
                EnforcedPaymentMethod.CASH_CARD.getValue());

        // set cod is available or not
        model.addAttribute("isCODFacilityAvailable",
                getIsCODFacilityApplicable(cartData));
    }

    private Boolean getIsCODFacilityApplicable(final CartData cartData) {
        // check if cart total is in between min and max cod threshold
        boolean isCODApplicable = false;
        final CMSSiteModel cmssite = getCmsSiteService().getCurrentSite();

        // check on postal code in delivery address entered by user
        if (null != cartData
                && null != cartData.getDeliveryAddress()
                && v2ServiceabilityService.isCodAvailableForArea(cartData
                        .getDeliveryAddress().getPostalCode())
                && null != cmssite.getCodMinimunThreshold()
                && null != cmssite.getCodMaximumThreshold()) {
            /*
             * final BigDecimal total = cartData.getTotalPrice().getValue(); if (total.compareTo(new
             * BigDecimal(cmssite.getCodMinimunThreshold() .doubleValue())) >= 0 && total.compareTo(new BigDecimal(cmssite
             * .getCodMaximumThreshold().doubleValue())) <= 0) { isCODApplicable = true; }
             */
            isCODApplicable = true;
        }

        return Boolean.valueOf(isCODApplicable);
    }

    private V2PaymentInfoForm populateV2PaymentForm(
            final AddressData addressData) {
        final V2PaymentInfoForm v2PaymentInfoForm = new V2PaymentInfoForm();
        v2PaymentInfoForm.setCountryIso(INDIA_COUNTRY_ISO);
        if (addressData == null) {
            v2PaymentInfoForm.setCountryName("India");
        } else {
            v2PaymentInfoForm
                    .setCountryName(addressData.getCountry().getName());
        }
        // v2PaymentInfoForm.setMobileno(addressData.getPhone());
        return v2PaymentInfoForm;
    }

    private Object populateV2AddressForm() {
        final V2AddressForm addressForm = new V2AddressForm();
        addressForm.setCountryIso(INDIA_COUNTRY_ISO);

        /*
         * if (null != addressData && null != addressData.getTitleCode()) { addressForm.setAddressId(addressData.getId());
         *
         * addressForm.setTitleCode(addressData.getTitleCode()); addressForm.setFirstName(addressData.getFirstName());
         *
         * addressForm.setLastName(addressData.getLastName()); addressForm.setLine1(addressData.getLine1());
         *
         * addressForm.setLine2(addressData.getLine2()); addressForm.setLine3(addressData.getLine3());
         * addressForm.setTownCity(addressData.getTown()); addressForm.setRegionIso(addressData.getRegion() != null ?
         *
         * addressData.getRegion().getIsocode() : ""); addressForm.setPostcode(addressData.getPostalCode());
         *
         * addressForm.setCountryIso(addressData.getCountry().getIsocode()); addressForm.setMobileno(addressData.getPhone());
         *
         *
         * addressForm.setSaveInAddressBook(Boolean.FALSE); addressForm.setShippingAddress
         * (Boolean.valueOf(addressData.isShippingAddress())); addressForm.setBillingAddress
         * (Boolean.valueOf(addressData.isBillingAddress())); }
         */
        return addressForm;
    }

    /**
     * Validate payment info form.
     *
     *
     * @param v2PaymentInfoForm
     *        the payment info form
     * @return false, if successful
     */
    private boolean validatePaymentInfoForm(
            final V2PaymentInfoForm v2PaymentInfoForm) {
        return v2PaymentInfoForm.getEnforcedPaymentMethod() == null;
    }

    private AddressData getPaymentAddress(
            final V2PaymentInfoForm v2PaymentInfoForm) {
        AddressData newAddress = null;
        v2PaymentInfoForm.setTitleCode("ms");
        if (v2PaymentInfoForm.getTitleCode() != null) {
            newAddress = new AddressData();
            newAddress.setTitleCode(v2PaymentInfoForm.getTitleCode());
            newAddress.setFirstName(v2PaymentInfoForm.getFirstName());
            newAddress.setLastName(v2PaymentInfoForm.getLastName());
            newAddress.setPhone(v2PaymentInfoForm.getMobileno());
            newAddress.setLine1(v2PaymentInfoForm.getLine1());
            newAddress.setLine2(v2PaymentInfoForm.getLine2());
            newAddress.setTown(v2PaymentInfoForm.getTownCity());
            newAddress.setPostalCode(v2PaymentInfoForm.getPostcode());
            newAddress.setBillingAddress(false);
            newAddress.setShippingAddress(true);
            if (v2PaymentInfoForm.getCountryIso() != null) {
                final CountryData countryData = getI18NFacade()
                        .getCountryForIsocode(v2PaymentInfoForm.getCountryIso());
                newAddress.setCountry(countryData);
            }
            if (v2PaymentInfoForm.getRegionIso() != null
                    && !StringUtils.isEmpty(v2PaymentInfoForm.getRegionIso())) {
                final RegionData regionData = getI18NFacade().getRegion(
                        v2PaymentInfoForm.getCountryIso(),
                        v2PaymentInfoForm.getRegionIso());
                newAddress.setRegion(regionData);
            }
        }

        return newAddress;
    }

    public void setCcavenuePaymentFacade(
            final CCAvenuePaymentFacade ccavenuePaymentFacade) {
        this.ccavenuePaymentFacade = ccavenuePaymentFacade;
    }

    public CCAvenuePaymentFacade getCcavenuePaymentFacade() {
        return ccavenuePaymentFacade;
    }

    /**
     * Native CCAvenue call
     *
     * @param request
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/send/ccAvenue", method = RequestMethod.POST)
    public String doCCAvenue(final HttpServletRequest request, final Model model)
            throws UnsupportedEncodingException {
        final String workingKey = getSiteConfigService().getProperty(
                "payment.ccavenue.working.key"); // Put in the 32 Bit Working
                                                 // Key
                                                 // provided by CCAVENUES.
        final String url = getSiteConfigService().getProperty(
                "hop.post.ccavenue.url");
        final String accessCode = getSiteConfigService().getProperty(
                "payment.ccavenue.access.code");
        final Enumeration enumeration = request.getParameterNames();
        String ccaRequest = "", pname = "", pvalue = "";
        while (enumeration.hasMoreElements()) {
            pname = "" + enumeration.nextElement();
            pvalue = request.getParameter(pname);
            LOG.debug("Key name: " + pname + " has value: " + pvalue);
            ccaRequest = ccaRequest + pname + "="
                    + URLEncoder.encode(pvalue, "UTF-8") + "&";
        }
        LOG.info("ccaRequest: " + ccaRequest);
        final AesCryptUtil aesUtil = new AesCryptUtil(workingKey);
        final String encRequest = aesUtil.encrypt(ccaRequest);
        model.addAttribute("encRequest", encRequest);
        model.addAttribute("url", url);
        model.addAttribute("accessCode", accessCode);
        return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.NativeCCAvenueHostedOrderWaitPage;
    }

    @ResponseBody
    @RequestMapping(value = "/cashOnDeliveryOtp", method = RequestMethod.GET)
    public Map<String, String> cashOnDeliveryOtp() {
        final Map<String, String> results = new HashMap<>();
        final CustomerModel currentCustomer = (CustomerModel) userService.getCurrentUser();
        // final String otpMessage = getCheckoutFacade().getCheckoutCart().getDeliveryAddress().getPhone();
        // results.put("serviceability", String.valueOf(otpMessage));
        final String time = String.valueOf((new Date(new Date().getTime() + 10 * 60000)));
        results.put("time", time);
        v2SmsService.sendSms(v2UserSmsDataMapPopulator.createV2UserOtp(),
                "User_otp_message_template",
                currentCustomer.getMobileNumber());
        return results;
    }

    @ResponseBody
    @RequestMapping(value = "/checkCashOnDeliveryOtp", method = RequestMethod.GET)
    public Map<String, String> checkCashOnDeliveryOtp(@RequestParam(value = "otpdata")
    final String otp) {
        final Map<String, String> results = v2CashOnDeliveryOtp.autenticateOtp(otp);
        return results;

    }

    @RequestMapping(value = "/add/notUseWallet/billingAddress", method = RequestMethod.POST)
    @RequireHardLogIn
    public String notUseWallet(final Model model, final V2PaymentInfoForm v2PaymentInfoForm, final BindingResult bindingResult)
            throws CMSItemNotFoundException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();

        if ((null != v2PaymentInfoForm.getSmsmobileno()) && (!v2PaymentInfoForm.getSmsmobileno().equals(""))) {
            final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
            if (!phoneNumbers.contains(v2PaymentInfoForm.getSmsmobileno())) {
                phoneNumbers.add(v2PaymentInfoForm.getSmsmobileno());
                currentCustomerData.setPhoneNumbers(phoneNumbers);
                customerFacade.updatePhoneNumbers(currentCustomerData, true);
            }
        }

        if (!v2PaymentInfoForm.getIsUsingShippingAddress().booleanValue()) {
            if (null != v2PaymentInfoForm.getPostcode()) {
                v2PaymentInfoForm.setPostcode(v2PaymentInfoForm.getPostcode().trim());
            }

            v2PaymentInfoForm.setTitleCode("ms");
            getV2AddressValidator().validate(v2PaymentInfoForm, bindingResult);
            getPaymentAddress(v2PaymentInfoForm);

            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model, "address.error.formentry.invalid");
                storeAttributeInPage(model, v2PaymentInfoForm, cartData);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
            }
        }
        return processAddPaymentMethodRequest(model, false);
    }

    @RequestMapping(value = "/add/wallet/billingAddress", method = RequestMethod.POST)
    @RequireHardLogIn
    public String useWalletAndAddress(final Model model, final V2PaymentInfoForm v2PaymentInfoForm, final BindingResult bindingResult)
            throws CMSItemNotFoundException {

        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();

        if ((null != v2PaymentInfoForm.getSmsmobileno()) && (!v2PaymentInfoForm.getSmsmobileno().equals(""))) {
            final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
            if (!phoneNumbers.contains(v2PaymentInfoForm.getSmsmobileno())) {
                phoneNumbers.add(v2PaymentInfoForm.getSmsmobileno());
                currentCustomerData.setPhoneNumbers(phoneNumbers);
                customerFacade.updatePhoneNumbers(currentCustomerData, true);
            }
        }

        if (!v2PaymentInfoForm.getIsUsingShippingAddress().booleanValue()) {
            if (null != v2PaymentInfoForm.getPostcode()) {
                v2PaymentInfoForm.setPostcode(v2PaymentInfoForm.getPostcode().trim());
            }

            v2PaymentInfoForm.setTitleCode("ms");
            getV2AddressValidator().validate(v2PaymentInfoForm, bindingResult);
            getPaymentAddress(v2PaymentInfoForm);

            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model, "address.error.formentry.invalid");
                storeAttributeInPage(model, v2PaymentInfoForm, cartData);
                return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
            }
        }

        return processAddPaymentMethodRequest(model, true);
    }

}