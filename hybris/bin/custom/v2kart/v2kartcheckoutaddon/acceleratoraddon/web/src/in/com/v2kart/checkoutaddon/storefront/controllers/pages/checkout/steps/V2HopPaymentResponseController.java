/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.storefront.controllers.pages.checkout.steps.HopPaymentResponseController;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.ccavenuepaymentintegration.facades.CCAvenuePaymentFacade;
import in.com.v2kart.checkoutaddon.storefront.controllers.V2kartcheckoutaddonControllerConstants;
import in.com.v2kart.core.payment.services.PaymentResponseService;
import in.com.v2kart.facades.payment.V2PaymentFacade;
import in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.facades.PAYUPaymentFacade;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping(value = "/checkout/multi/hop")
public class V2HopPaymentResponseController extends HopPaymentResponseController {
    private static final String IS_PAYMENT_GATEWAY_ERROR_PARAM = "isPaymentGatewayError";

    @Resource(name = "cartService")
    private CartService cartService;

    @Resource(name = "payUPaymentResponseService")
    private PaymentResponseService payUPaymentResponseService;

    @Resource(name = "payUPaymentFacade")
    private PAYUPaymentFacade payUPaymentFacade;

    @Resource(name = "ccavenuePaymentFacade")
    private CCAvenuePaymentFacade ccAvenuePaymentFacade;

    @Resource(name = "ccavenuePaymentResponseService")
    private PaymentResponseService ccavenuePaymentResponseService;

    @Resource(name = "v2PaymentFacade")
    private V2PaymentFacade v2PaymentFacade;

    @Resource(name = "ebsPaymentResponseService")
    private PaymentResponseService ebsPaymentResponseService;

    @Resource(name = "ebsPaymentFacade")
    private EBSPaymentFacade ebsPaymentFacade;

    /**
     * @return the ebsPaymentResponseService
     */
    public PaymentResponseService getEbsPaymentResponseService() {
        return ebsPaymentResponseService;
    }

    /**
     * @return the ebsPaymentFacade
     */
    public EBSPaymentFacade getEbsPaymentFacade() {
        return ebsPaymentFacade;
    }

    /**
     * @return the cartService
     */
    public CartService getCartService() {
        return cartService;
    }

    /**
     * @return the payUPaymentResponseService
     */
    public PaymentResponseService getPayUPaymentResponseService() {
        return payUPaymentResponseService;
    }

    /**
     * @return the payUPaymentFacade
     */
    public PAYUPaymentFacade getPayUPaymentFacade() {
        return payUPaymentFacade;
    }

    @RequestMapping(value = "/payu/response", method = RequestMethod.POST)
    public String doHandlePAYUHopResponse(final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        OrderData orderData = null;
        try {
            final Map<String, String> resultMap = getPayUPaymentResponseService().getRequestParameterMap(request);
            getPayUPaymentFacade().savePaymentInfo(resultMap);

            final CartModel cartModel = cartService.getSessionCart();

            PaymentSubscriptionResultData paymentSubscriptionResultData = null;
            paymentSubscriptionResultData = getPayUPaymentFacade().completeHopCreatePayment(resultMap, true, cartModel);
            if (paymentSubscriptionResultData.isSuccess()) {
                orderData = getCheckoutFacade().placeOrder();
            } else {
                // HOP ERROR!
                LOG.error("Failed to create subscription.  Please check the log files for more information");
                redirectAttributes.addFlashAttribute(IS_PAYMENT_GATEWAY_ERROR_PARAM, "checkout.error.payment.failed");
                return REDIRECT_PREFIX + "/checkout/multi/payment-method/add";

            }
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorResponse";
        }
        return redirectToOrderConfirmationPage(orderData);
    }

    @RequestMapping(value = "/payu/errorResponse", method = RequestMethod.POST)
    public String doHandlePayUHopErrorResponse(final HttpServletRequest request, final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException {
        final Map<String, String> resultMap;
        try {
            resultMap = getPayUPaymentResponseService().getRequestParameterMap(request);
            getPayUPaymentFacade().savePaymentInfo(resultMap);
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorResponse";
        }
        LOG.error("Failed to place Order: " + resultMap.get(PAYU.ResponseParameters.ERROR_MESSAGE));
        redirectAttributes.addFlashAttribute(IS_PAYMENT_GATEWAY_ERROR_PARAM, "checkout.error.payment.failed");
        return REDIRECT_PREFIX + "/checkout/multi/payment-method/add";
    }

    @RequestMapping(value = "/ccavenue/response", method = RequestMethod.POST)
    public String doHandleCCAvenueHopResponse(final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        OrderData orderData = null;
        try {
            final Map<String, String> resultMap = getCcavenuePaymentResponseService().getRequestParameterMap(request);
            getCcAvenuePaymentFacade().savePaymentInfo(resultMap);

            final CartModel cartModel = cartService.getSessionCart();

            PaymentSubscriptionResultData paymentSubscriptionResultData = null;
            paymentSubscriptionResultData = getCcAvenuePaymentFacade().completeHopCreatePayment(resultMap, true, cartModel);
            if (paymentSubscriptionResultData.isSuccess()) {
                orderData = getCheckoutFacade().placeOrder();
            } else {
                // HOP ERROR!
                LOG.error("Failed to create subscription.  Please check the log files for more information");
                redirectAttributes.addFlashAttribute(IS_PAYMENT_GATEWAY_ERROR_PARAM, "checkout.error.payment.failed");
                return REDIRECT_PREFIX + "/checkout/multi/payment-method/add";

            }
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorResponse";
        }
        return redirectToOrderConfirmationPage(orderData);
    }

    @RequestMapping(value = "/ccavenue/errorResponse", method = RequestMethod.POST)
    public String doHandleCCAvenueHopErrorResponse(final HttpServletRequest request, final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException {
        final Map<String, String> resultMap;
        try {
            resultMap = getCcavenuePaymentResponseService().getRequestParameterMap(request);
            getCcAvenuePaymentFacade().savePaymentInfo(resultMap);
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorResponse";
        }
        LOG.error("Failed to place Order: " + resultMap.get(CCAVENUE.ResponseParameters.FAILURE_MESSAGE));
        redirectAttributes.addFlashAttribute(IS_PAYMENT_GATEWAY_ERROR_PARAM, "checkout.error.payment.failed");
        return REDIRECT_PREFIX + "/checkout/multi/payment-method/add";
    }

    @RequestMapping(value = "/ebs/response", method = RequestMethod.GET)
    public String doHandleEBSHopResponse(@RequestParam(value = "DR", required = true) final String dr, final HttpServletRequest request,
            final RedirectAttributes redirectAttributes) {
        OrderData orderData = null;
        try {
            final Map<String, String> resultMap = ebsPaymentResponseService.getRequestParameterMap(request);
            ebsPaymentFacade.savePaymentInfo(resultMap);

            final CartModel cartModel = cartService.getSessionCart();

            PaymentSubscriptionResultData paymentSubscriptionResultData = null;
            paymentSubscriptionResultData = ebsPaymentFacade.completeHopCreatePayment(resultMap, true, cartModel);
            if (paymentSubscriptionResultData.isSuccess()) {
                orderData = getCheckoutFacade().placeOrder();
            } else {
                // HOP ERROR!
                LOG.error("Failed to create subscription.  Please check the log files for more information");
                redirectAttributes.addFlashAttribute(IS_PAYMENT_GATEWAY_ERROR_PARAM, "checkout.error.payment.failed");
                return REDIRECT_PREFIX + "/checkout/multi/payment-method/add";

            }
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorResponse";
        }
        return redirectToOrderConfirmationPage(orderData);
    }

    public CCAvenuePaymentFacade getCcAvenuePaymentFacade() {
        return ccAvenuePaymentFacade;
    }

    public PaymentResponseService getCcavenuePaymentResponseService() {
        return ccavenuePaymentResponseService;
    }

    @RequestMapping(value = "/payu/saveResponse", method = RequestMethod.POST)
    public String doHandlePAYUHopSaveResponse(final HttpServletRequest request) throws CMSItemNotFoundException {
        Map<String, String> resultMap;
        try {
            resultMap = getPayUPaymentResponseService().getRequestParameterMap(request);
            getPayUPaymentFacade().savePaymentInfo(resultMap);
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorDisplay";
        }
        return "redirect:/checkout/multi/hop/saveInfoDisplay/";
    }

    @RequestMapping(value = "/ccavenue/saveResponse", method = RequestMethod.POST)
    public String doHandleCCAvenueHopSaveResponse(final HttpServletRequest request) throws CMSItemNotFoundException {
        Map<String, String> resultMap;
        try {
        	 LOG.info("start saving ccavenue saveResponse");
            resultMap = getCcavenuePaymentResponseService().getRequestParameterMap(request);
            getCcAvenuePaymentFacade().savePaymentInfo(resultMap);
            LOG.info("End saving ccavenue saveResponse");
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorDisplay";
        }
        return "redirect:/checkout/multi/hop/saveInfoDisplay/";
    }

    @RequestMapping(value = "/ebs/saveResponse", method = RequestMethod.POST)
    public String doHandleEbsHopSaveResponse(final HttpServletRequest request) throws CMSItemNotFoundException {
        Map<String, String> resultMap;
        try {
            resultMap = getEbsPaymentResponseService().getRequestParameterMap(request);
            getEbsPaymentFacade().savePaymentInfo(resultMap);
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            return "redirect:/checkout/multi/hop/errorDisplay";
        }
        return "redirect:/checkout/multi/hop/saveInfoDisplay/";
    }

    @RequestMapping(value = "/saveInfoDisplay", method = RequestMethod.GET)
    public String doHostedOrderPageSaveDisplay(final Model model, final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException {
        // final ValidationResults validationResults = getCheckoutStep().validate(redirectAttributes);
        // storeCmsPageInModel(model, getContentPageForLabelOrId(ERROR_CMS_PAGE));
        // setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ERROR_CMS_PAGE));

        model.addAttribute(WebConstants.MODEL_KEY_ADDITIONAL_BREADCRUMB,
                getResourceBreadcrumbBuilder().getBreadcrumbs("breadcrumb.not.found"));

        return V2kartcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderSavePage;
    }

}