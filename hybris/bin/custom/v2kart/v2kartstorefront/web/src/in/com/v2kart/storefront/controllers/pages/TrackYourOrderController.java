package in.com.v2kart.storefront.controllers.pages;

import in.com.v2kart.facades.order.V2OrderFacade;
import in.com.v2kart.storefront.controllers.ControllerConstants;
import in.com.v2kart.storefront.forms.TrackOrderForm;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;

@Controller
@Scope("tenant")
@RequestMapping("/trackYourOrder")
public class TrackYourOrderController extends AbstractPageController {

    public static final String REDIRECT_PREFIX = "redirect:";
    private static final String ORDER_DETAIL_CMS_PAGE = "order";
    private static final String TRACK_GUESTORDER_PAGE = "/trackguestorder";
    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Resource(name = "orderFacade")
    private V2OrderFacade orderFacade;

    @Resource(name = "siteConfigService")
    private SiteConfigService siteConfigService;

    @Resource(name = "commerceCommonI18NService")
    CommerceCommonI18NService commerceCommonI18NService;

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    @RequestMapping(method = RequestMethod.GET)
    public String trackYourOrder(final Model model, final HttpServletRequest request, final RedirectAttributes redirectModel) {
        sessionService.setAttribute("loginToTrackYourOrders", Boolean.TRUE);
        return REDIRECT_PREFIX + "/my-account/orders";
    }

    @RequestMapping(value = "/trackGuestUserOrders", method = RequestMethod.GET)
    public String trackYourOrders(final Model model, final HttpServletRequest request, final RedirectAttributes redirectModel)
            throws CMSItemNotFoundException {
        model.addAttribute("trackOrderForm", new TrackOrderForm());
        storeCmsPageInModel(model, getContentPageForLabelOrId(TRACK_GUESTORDER_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRACK_GUESTORDER_PAGE));
        return getViewForPage(model);
    }

    @RequestMapping(value = "/trackGuestOrder", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, String> trackGuestOrder(final TrackOrderForm trackOrderForm, final Model model, final HttpSession session,
            final HttpServletResponse response, final HttpServletRequest request) throws CMSItemNotFoundException {

        String EMPTY = "";
        Map<String, String> requestResponse = new HashMap<>();
        try {
            storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
            model.addAttribute("metaRobots", "noindex,nofollow");
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
            final OrderData orderDetails = orderFacade.getOrderDetailsForCodeWithoutUser(trackOrderForm.getOrderNumber());
            model.addAttribute("orderData", orderDetails);
            if (orderDetails.getUser().getUid().equals(trackOrderForm.getEmailId())) {
                requestResponse.put("success", "true");
                return requestResponse;
            } else {
                requestResponse.put("emailError", "true");
            }
        } catch (final UnknownIdentifierException | IllegalArgumentException | ModelNotFoundException e) {
            requestResponse.put("orderError", "true");
            LOG.warn("Attempted to load a order that does not exist or is not visible");
        }

        requestResponse.put("orderMsg", getMessage("track.order.error.message"));
        requestResponse.put("emailMsg", getMessage("track.order.email.error.message"));

        if (trackOrderForm.getEmailId().equals(EMPTY)) {
            requestResponse.put("emailError", "true");
            requestResponse.put("emailMsg", getMessage("track.order.email.null.message"));
        }

        if (trackOrderForm.getOrderNumber().equals(EMPTY)) {
            requestResponse.put("orderMsg", getMessage("track.order.null.message"));
        }
        return requestResponse;
    }

    @RequestMapping(value = "/trackGuestOrders", method = RequestMethod.POST)
    public String trackGuestOrders(final TrackOrderForm trackOrderForm, final BindingResult bindingErrors, final Model model,
            final HttpSession session, final HttpServletResponse response) throws CMSItemNotFoundException {

        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
        final OrderData orderDetails = orderFacade.getOrderDetailsForCodeWithoutUser(trackOrderForm.getOrderNumber());
        model.addAttribute("orderData", orderDetails);
        return ControllerConstants.Views.Pages.Guest.GuestOrderPage;
    }

    @RequestMapping(value = "/trackGuestUserOrders", method = RequestMethod.POST)
    public String trackGuestUserOrders(final TrackOrderForm trackOrderForm, final BindingResult bindingErrors, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session, final HttpServletResponse response)
            throws CMSItemNotFoundException {

        Boolean orderError = Boolean.FALSE;
        Boolean invalidEmail = Boolean.FALSE;
        try {

            storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
            model.addAttribute("metaRobots", "noindex,nofollow");
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
            final OrderData orderDetails = orderFacade.getOrderDetailsForCodeWithoutUser(trackOrderForm.getOrderNumber());
            model.addAttribute("orderData", orderDetails);
            if (!orderDetails.getUser().getUid().equals(trackOrderForm.getEmailId())) {
                invalidEmail = Boolean.TRUE;
            }
        } catch (final UnknownIdentifierException | IllegalArgumentException | ModelNotFoundException e) {
            LOG.warn("Attempted to load a order that does not exist or is not visible");
            orderError = Boolean.TRUE;
        }
        if (invalidEmail) {
            model.addAttribute("emailMsg", getMessage("track.order.email.error.message"));
            model.addAttribute("trackOrderForm", trackOrderForm);
            storeCmsPageInModel(model, getContentPageForLabelOrId(TRACK_GUESTORDER_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRACK_GUESTORDER_PAGE));
            return getViewForPage(model);

        } else if (orderError) {
            model.addAttribute("orderMsg", getMessage("track.order.error.message"));
            model.addAttribute("trackOrderForm", trackOrderForm);
            storeCmsPageInModel(model, getContentPageForLabelOrId(TRACK_GUESTORDER_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRACK_GUESTORDER_PAGE));
            return getViewForPage(model);
        } else {
            return ControllerConstants.Views.Pages.Guest.GuestOrderPage;
        }

    }

    @RequestMapping(value = "/trackGuestOrders", method = RequestMethod.GET)
    public String moveToHomePage() {
        return REDIRECT_PREFIX + "/";
    }

    public String getMessage(final String Key) {
        String message = "";
        try {
            message = messageSource.getMessage(Key, null, commerceCommonI18NService.getCurrentLocale());
        } catch (final Exception ex) {
            LOG.error("unable to read message:" + ex.getMessage());
        }
        return message;
    }

}
