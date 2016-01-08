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

import in.com.v2kart.facades.cancel.data.CancelData;
import in.com.v2kart.facades.cancel.data.CancelEntryData;
import in.com.v2kart.facades.core.data.WishlistData;
import in.com.v2kart.facades.customer.V2CustomerFacade;
import in.com.v2kart.facades.order.V2OrderFacade;
import in.com.v2kart.facades.order.V2OrderReturnFacade;
import in.com.v2kart.facades.order.cancel.V2OrderCancellationFacade;
import in.com.v2kart.facades.order.data.ReturnData;
import in.com.v2kart.facades.order.data.ReturnEntryData;
import in.com.v2kart.facades.wallet.V2CustomerWalletFacade;
import in.com.v2kart.facades.wallet.data.V2WalletTransactionData;
import in.com.v2kart.facades.wishlist.WishlistFacade;
import in.com.v2kart.storefront.controllers.ControllerConstants;
import in.com.v2kart.storefront.forms.OrderCancellationForm;
import in.com.v2kart.storefront.forms.OrderReturnForm;
import in.com.v2kart.storefront.forms.V2AddressForm;
import in.com.v2kart.storefront.forms.V2NotifyCustomerForm;
import in.com.v2kart.storefront.forms.V2UpdateProfileForm;
import in.com.v2kart.storefront.forms.validation.V2AddressFormValidator;
import in.com.v2kart.storefront.forms.validation.V2ChangePasswordValidator;
import in.com.v2kart.storefront.forms.validation.V2ProfileValidator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController.SelectOption;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateEmailForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.EmailValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

/**
 * Controller for home page
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-account")
public class AccountPageController extends AbstractSearchPageController {
    // Internal Redirects
    private static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";
    private static final String REDIRECT_TO_ADDRESS_BOOK_PAGE = REDIRECT_PREFIX + "/my-account/address-book";
    private static final String REDIRECT_TO_PAYMENT_INFO_PAGE = REDIRECT_PREFIX + "/my-account/payment-details";
    private static final String REDIRECT_TO_PROFILE_PAGE = REDIRECT_PREFIX + "/my-account/profile";
    private static final String REDIRECT_TO_WISHLIST_PAGE = REDIRECT_PREFIX + "/my-account/wishlist";

    /**
     * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it contains on or more
     * '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on the issue and future resolution.
     */
    private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
    private static final String ADDRESS_CODE_PATH_VARIABLE_PATTERN = "{addressCode:.*}";

    private static final String ORDERSTATUS_CODE_PATH_VARIABLE_PATTERN = "{orderStatus:.*}";

    // CMS Pages
    private static final String ACCOUNT_CMS_PAGE = "account";
    private static final String PROFILE_CMS_PAGE = "profile";
    private static final String UPDATE_PASSWORD_CMS_PAGE = "updatePassword";
    private static final String UPDATE_PROFILE_CMS_PAGE = "update-profile";
    private static final String UPDATE_EMAIL_CMS_PAGE = "update-email";
    private static final String ADDRESS_BOOK_CMS_PAGE = "address-book";
    private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";
    private static final String PAYMENT_DETAILS_CMS_PAGE = "payment-details";
    private static final String ORDER_HISTORY_CMS_PAGE = "orders";
    private static final String ORDER_DETAIL_CMS_PAGE = "order";
    private static final String WISHLIST_CMS_PAGE = "wishlist";
    private static final String WALLET_CMS_PAGE = "wallet";
    private static final String ORDER_CANCEL_CUSTOMER_CARE_NUMBER = "order.cancel.customer.care.number";
    private static final String MS = "ms";
    private static final String ACTION_STRING = "HOLD";
    private static final String ORDER_STATUS_NOTCOMPLETED = "recentOrders";

    private static final Logger LOG = Logger.getLogger(AccountPageController.class);

    @Resource(name = "orderFacade")
    private V2OrderFacade orderFacade;

    @Resource(name = "acceleratorCheckoutFacade")
    private CheckoutFacade checkoutFacade;

    @Resource(name = "userFacade")
    protected UserFacade userFacade;

    @Resource(name = "customerFacade")
    protected V2CustomerFacade customerFacade;

    @Resource(name = "accountBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

    @Resource(name = "v2ChangePasswordValidator")
    private V2ChangePasswordValidator v2ChangePasswordValidator;

    @Resource(name = "addressValidator")
    private AddressValidator addressValidator;

    @Resource(name = "v2addressValidator")
    private V2AddressFormValidator v2addressValidator;

    @Resource(name = "v2profileValidator")
    private V2ProfileValidator v2profileValidator;

    @Resource(name = "emailValidator")
    private EmailValidator emailValidator;

    @Resource(name = "i18NFacade")
    private I18NFacade i18NFacade;

    @Resource(name = "addressVerificationFacade")
    private AddressVerificationFacade addressVerificationFacade;

    @Resource(name = "addressVerificationResultHandler")
    private AddressVerificationResultHandler addressVerificationResultHandler;

    @Resource(name = "wishlistFacade")
    private WishlistFacade wishlistFacade;

    @Resource(name = "cartFacade")
    private CartFacade cartFacade;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "productService")
    private ProductService productService;

    @Resource(name = "mediaService")
    private MediaService mediaService;

    @Resource(name = "cmsSiteService")
    private CMSSiteService cmsSiteService;

    @Resource(name = "productModelUrlResolver")
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Resource(name = "siteConfigService")
    private SiteConfigService siteConfigService;

    @Resource(name = "v2CustomerWalletFacade")
    private V2CustomerWalletFacade v2CustomerWalletFacade;

    @Resource(name = "enumerationService")
    private EnumerationService enumerationService;

    @Resource(name = "orderCancellationFacade")
    private V2OrderCancellationFacade orderCancellationFacade;

    @Resource(name = "orderReturnFacade")
    private V2OrderReturnFacade orderReturnFacade;

    public V2ChangePasswordValidator getV2ChangePasswordValidator() {
        return v2ChangePasswordValidator;
    }

    public V2AddressFormValidator getV2addressValidator() {
        return v2addressValidator;
    }

    public void setV2addressValidator(V2AddressFormValidator v2addressValidator) {
        this.v2addressValidator = v2addressValidator;
    }

    protected AddressValidator getAddressValidator() {
        return addressValidator;
    }

    protected V2ProfileValidator getV2profileValidator() {
        return v2profileValidator;
    }

    protected EmailValidator getEmailValidator() {
        return emailValidator;
    }

    protected I18NFacade getI18NFacade() {
        return i18NFacade;
    }

    protected AddressVerificationFacade getAddressVerificationFacade() {
        return addressVerificationFacade;
    }

    protected AddressVerificationResultHandler getAddressVerificationResultHandler() {
        return addressVerificationResultHandler;
    }

    @ModelAttribute("countries")
    public Collection<CountryData> getCountries() {
        return checkoutFacade.getDeliveryCountries();
    }

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return userFacade.getTitles();
    }

    @ModelAttribute("countryDataMap")
    public Map<String, CountryData> getCountryDataMap() {
        final Map<String, CountryData> countryDataMap = new HashMap<>();
        for (final CountryData countryData : getCountries()) {
            countryDataMap.put(countryData.getIsocode(), countryData);
        }
        return countryDataMap;
    }

    /**
     * @return the wishlistFacade
     */
    public WishlistFacade getWishlistFacade() {
        return wishlistFacade;
    }

    /**
     * @param wishlistFacade
     *        the wishlistFacade to set
     */
    public void setWishlistFacade(final WishlistFacade wishlistFacade) {
        this.wishlistFacade = wishlistFacade;
    }

    /**
     * @return the cartFacade
     */
    public CartFacade getCartFacade() {
        return cartFacade;
    }

    /**
     * @param cartFacade
     *        the cartFacade to set
     */
    public void setCartFacade(final CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }

    @RequestMapping(value = "/addressform", method = RequestMethod.GET)
    public String getCountryAddressForm(@RequestParam("addressCode") final String addressCode,
            @RequestParam("countryIsoCode") final String countryIsoCode, final Model model) {
        model.addAttribute("supportedCountries", getCountries());
        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
        model.addAttribute("country", countryIsoCode);

        final AddressForm addressForm = new AddressForm();
        model.addAttribute("addressForm", addressForm);
        for (final AddressData addressData : userFacade.getAddressBook()) {
            if (addressData.getId() != null && addressData.getId().equals(addressCode)
                    && countryIsoCode.equals(addressData.getCountry().getIsocode())) {
                model.addAttribute("addressData", addressData);
                addressForm.setAddressId(addressData.getId());
                addressForm.setTitleCode(MS);
                addressForm.setFirstName(addressData.getFirstName());
                addressForm.setLastName(addressData.getLastName());
                addressForm.setLine1(addressData.getLine1());
                addressForm.setLine2(addressData.getLine2());
                addressForm.setTownCity(addressData.getTown());
                addressForm.setPostcode(addressData.getPostalCode());
                addressForm.setCountryIso(addressData.getCountry().getIsocode());

                if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode())) {
                    addressForm.setRegionIso(addressData.getRegion().getIsocode());
                }

                break;
            }
        }
        return ControllerConstants.Views.Fragments.Account.CountryAddressForm;
    }

    @RequestMapping(method = RequestMethod.GET)
    @RequireHardLogIn
    public String account(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs(null));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    @RequireHardLogIn
    public String orders(@RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode,
            @RequestParam(value = "currentTab", required = false, defaultValue = "accessibletabsnavigation0-6") final String currentTab,
            final Model model) throws CMSItemNotFoundException {
        // Handle paged search results
        final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
        List<OrderStatus> filteredOrderStatus = orderFacade.getFilteredOrderStatuses(OrderStatus.COMPLETED);
        final SearchPageData<OrderHistoryData> searchPageData = orderFacade.getPagedOrderHistoryForStatuses(
                pageableData, filteredOrderStatus.toArray(new OrderStatus[filteredOrderStatus.size()]));
        populateModel(model, searchPageData, showMode);

        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("currentTab", currentTab);
        return getViewForPage(model);
    }

    @RequestMapping(value = "/orders/" + ORDERSTATUS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String ordersByStatusCode(@PathVariable("orderStatus") final String orderStatus,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode,
            @RequestParam(value = "currentTab", required = false, defaultValue = "accessibletabsnavigation0-6") final String currentTab,
            final Model model) throws CMSItemNotFoundException {
        // Handle paged search results
        final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
        SearchPageData<OrderHistoryData> searchPageData;
        // change here get all order status except COMPLETED
        if (orderStatus.equalsIgnoreCase(ORDER_STATUS_NOTCOMPLETED)) {
            List<OrderStatus> filteredOrderStatus = orderFacade.getFilteredOrderStatuses(OrderStatus.COMPLETED);
            searchPageData = orderFacade.getPagedOrderHistoryForStatuses(pageableData,
                    filteredOrderStatus.toArray(new OrderStatus[filteredOrderStatus.size()]));
            model.addAttribute("currentTab", "accessibletabsnavigation0-6");
        } else {
            searchPageData = orderFacade.getPagedOrderHistoryForStatuses(pageableData);
            model.addAttribute("currentTab", "accessibletabsnavigation0-7");
        }
        populateModel(model, searchPageData, showMode);
        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String order(@PathVariable("orderCode") final String orderCode, final Model model) throws CMSItemNotFoundException {
        try {
            OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
            Map<OrderEntryData,Long> entries = orderFacade.getAllCancellableEntries(orderCode);
            Boolean isCanceleable = orderFacade.isOrderCancellable(orderDetails)
                    && setOrderCancelable(orderDetails, entries);
            orderDetails.setIsCancelable(isCanceleable);
            Boolean isReturnable = orderReturnFacade.setOrderReturnable(orderCode);
            orderDetails.setIsReturnable(isReturnable);
            model.addAttribute("orderData", orderDetails);
            // sets the customer care number for cancelling the order
            model.addAttribute("orderCancelCustomerCareNumber",
                    siteConfigService.getProperty(ORDER_CANCEL_CUSTOMER_CARE_NUMBER));

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
            breadcrumbs.add(new Breadcrumb("/my-account/orders", getMessageSource().getMessage(
                    "text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.order.orderBreadcrumb",
                    new Object[] { orderDetails.getCode() }, "Order {0}", getI18nService().getCurrentLocale()), null));
            model.addAttribute("breadcrumbs", breadcrumbs);

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load a order that does not exist or is not visible", e);
            return REDIRECT_MY_ACCOUNT;
        }
        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
        return getViewForPage(model);
    }

    @RequestMapping(value = "/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN + "/cancel", method = RequestMethod.GET)
    public String cancelOrder(@PathVariable("orderCode") final String orderCode, final Model model) {
        Map<OrderEntryData,Long> entries = orderFacade.getAllCancellableEntries(orderCode);
        OrderCancellationForm form = new OrderCancellationForm();
        form.setOrderCode(orderCode);
        populateCancellationForm(form, entries);
        model.addAttribute("cancelForm", form);
        model.addAttribute("cancelReasons", populateSelectBoxForString(getAllCancelReasons()));
        return ControllerConstants.Views.Pages.Account.AccountOrderCancellationPage;
    }

    /**
     * @param cancelableEntries
     * @param orderDetails
     */
    private Boolean setOrderCancelable(final OrderData orderDetails, Map<OrderEntryData,Long> cancelleableEntries) {
        Boolean result = Boolean.TRUE;
        // whole order is only cancelable when
        // there is no consignment and
        // no order entry or line item is cancelled before
        if (cancelleableEntries != null && cancelleableEntries.size() != 0) {
            if ((orderDetails.getConsignments().size() != 0)) {
                for (ConsignmentData consignment : orderDetails.getConsignments()) {
                    if (ConsignmentStatus.READY_TO_DISPATCH.equals(consignment.getStatus())) {
                        result = result && Boolean.FALSE;
                    }
                }
            } else {
                result = Boolean.TRUE;
            }
        }
        return result;
    }

    private void populateCancellationForm(OrderCancellationForm form, Map<OrderEntryData,Long> canceleableEntries) {
        if (canceleableEntries != null && canceleableEntries.size() != 0) {
            List<CancelEntryData> entries = new ArrayList<CancelEntryData>(canceleableEntries.size());
            for (Map.Entry<OrderEntryData, Long> entry : canceleableEntries.entrySet()) {
                CancelEntryData cancelData = new CancelEntryData();
                cancelData.setProductName(entry.getKey().getProduct().getName().toUpperCase());
                cancelData.setMaxQuantity(entry.getValue());
                cancelData.setOrderEntry(entry.getKey().getEntryNumber());
                entries.add(cancelData);
            }
            form.setCancelledEntries(entries);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/order/getRefund", method = RequestMethod.POST)
    public String getRefundAmount(final Model model, final OrderCancellationForm form) throws CMSItemNotFoundException {
        final String isFullCancelRequest = form.getIsFull();
        CancelData cancelData = new CancelData();
        cancelData.setOrderCode(form.getOrderCode());
        List<CancelEntryData> entriesToBeCancelled=new ArrayList<CancelEntryData>();
        for(CancelEntryData entry:form.getCancelledEntries()){
            if(entry.getQuantity()!=null && entry.getQuantity()>0){
                entriesToBeCancelled.add(entry);
            }
        }
        cancelData.setCancelledEntries(entriesToBeCancelled);
        cancelData.setNote(form.getNote());
        cancelData.setReason(form.getReason());
        if (!isFullCancelRequest.isEmpty() && isFullCancelRequest != null
                && isFullCancelRequest.equalsIgnoreCase("true")) {
            cancelData.setIsFull(Boolean.TRUE);
        } else {
            cancelData.setIsFull(Boolean.FALSE);
        }
        String amount = orderCancellationFacade.getRefundAmount(cancelData).getFormattedValue();
        return amount;
    }

    @RequestMapping(value = "/order/cancel", method = RequestMethod.POST)
    public String cancelOrder(final Model model, final OrderCancellationForm form) throws CMSItemNotFoundException {
        final String isFullCancelRequest = form.getIsFull();
        final String orderCode = form.getOrderCode();
        CancelData cancelData = new CancelData();
        cancelData.setOrderCode(form.getOrderCode());
        if (!isFullCancelRequest.isEmpty() && isFullCancelRequest != null
                && isFullCancelRequest.equalsIgnoreCase("true")) {
            cancelData.setNote(form.getNote());
            cancelData.setReason(form.getReason());
            cancelData.setIsFull(Boolean.TRUE);
            orderCancellationFacade.cancelFullOrder(cancelData);
        } else {
            cancelData.setIsFull(Boolean.FALSE);
            List<CancelEntryData> entriesToBeCancelled=new ArrayList<CancelEntryData>();
            for(CancelEntryData entry:form.getCancelledEntries()){
                if(entry.getQuantity()!=null && entry.getQuantity()>0){
                    entriesToBeCancelled.add(entry);
                }
            }
            cancelData.setCancelledEntries(entriesToBeCancelled);
            orderCancellationFacade.cancelPartialOrder(cancelData);
        }
        return REDIRECT_PREFIX + "/my-account/order/" + orderCode;
    }

    @RequestMapping(value = "/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN + "/return", method = RequestMethod.GET)
    public String returnOrder(@PathVariable("orderCode") final String orderCode, final Model model) {
        OrderReturnForm form = new OrderReturnForm();
        form.setOrderCode(orderCode);
        Map<OrderEntryData, Long> returnableEntries = orderReturnFacade.getAllReturnableOrderEntries(orderCode);
        populateOrderReturnForm(returnableEntries, form);
        model.addAttribute("returnForm", form);
        model.addAttribute("refundReasons", populateSelectBoxForString(getAllRefundReasons()));
        return ControllerConstants.Views.Pages.Account.AccountOrderReturnPage;
    }

    private void populateOrderReturnForm(Map<OrderEntryData, Long> returnableEntries, OrderReturnForm form) {
        List<ReturnEntryData> returnEntries = new ArrayList<ReturnEntryData>();
        for (Map.Entry<OrderEntryData, Long> entry : returnableEntries.entrySet()) {
            ReturnEntryData returnEntry = new ReturnEntryData();
            returnEntry.setMaxQuantity(entry.getValue());
            returnEntry.setOrderEntry(entry.getKey().getEntryNumber());
            returnEntry.setProductName(entry.getKey().getProduct().getName().toUpperCase());
            returnEntry.setReturnAction(ACTION_STRING);
            returnEntries.add(returnEntry);
        }
        form.setAction(ACTION_STRING);
        form.setIsFull("false");
        form.setReturnableEntries(returnEntries);
    }

    @ResponseBody
    @RequestMapping(value = "/order/return/getRefundAmount", method = RequestMethod.POST)
    public String getReturnRefundAmount(final Model model, final OrderReturnForm form) throws CMSItemNotFoundException {
        final String isFullReturnRequest = form.getIsFull();
        ReturnData returnData = new ReturnData();
        returnData.setOrderCode(form.getOrderCode());
        returnData.setNote(form.getNote());
        returnData.setReason(form.getReason());
        returnData.setReturnAction(form.getAction());
        List<ReturnEntryData> entriesToBeReturned=new ArrayList<ReturnEntryData>();
        for(ReturnEntryData entry:form.getReturnableEntries()){
            if(entry.getQuantity()!=null && entry.getQuantity()>0){
                entriesToBeReturned.add(entry);
            }
        }
        returnData.setReturnedEntries(entriesToBeReturned);
        if (!isFullReturnRequest.isEmpty() && isFullReturnRequest != null
                && isFullReturnRequest.equalsIgnoreCase("true")) {
            returnData.setIsFull(Boolean.TRUE);
        } else {
            returnData.setIsFull(Boolean.FALSE);
        }
        String amount = orderReturnFacade.getRefundAmount(returnData).getFormattedValue();
        return amount;
    }

    @RequestMapping(value = "/order/return", method = RequestMethod.POST)
    public String returnOrder(final Model model, final OrderReturnForm form) throws CMSItemNotFoundException {
        final String isFullReturnRequest = form.getIsFull();
        final String orderCode = form.getOrderCode();
        ReturnData returnData = new ReturnData();
        returnData.setOrderCode(form.getOrderCode());

        if (!isFullReturnRequest.isEmpty() && isFullReturnRequest != null
                && isFullReturnRequest.equalsIgnoreCase("true")) {
            returnData.setNote(form.getNote());
            returnData.setReason(form.getReason());
            returnData.setReturnAction(form.getAction());
            returnData.setIsFull(Boolean.TRUE);
            orderReturnFacade.processFullOrderReturn(returnData);
        } else {
            returnData.setIsFull(Boolean.FALSE);
            List<ReturnEntryData> entriesToBeReturned=new ArrayList<ReturnEntryData>();
            for(ReturnEntryData entry:form.getReturnableEntries()){
                if(entry.getQuantity()!=null && entry.getQuantity()>0){
                    entriesToBeReturned.add(entry);
                }
            }
            returnData.setReturnedEntries(entriesToBeReturned);
            orderReturnFacade.processPartialOrderReturn(returnData);
        }
        return REDIRECT_PREFIX + "/my-account/order/" + orderCode;
    }

    /*
     * method to get all cancel reasons
     */
    protected Map<String, String> getAllCancelReasons() {
        Map<String, String> cancelReasons = new HashMap<String, String>();
        List<CancelReason> enumerationValues = enumerationService.getEnumerationValues(CancelReason.class);
        Iterator<CancelReason> iterator = enumerationValues.iterator();
        while (iterator.hasNext()) {
            CancelReason reason = iterator.next();
            cancelReasons.put(reason.getCode().toLowerCase(), reason.getCode());
        }
        return orderCancellationFacade.filterCancelReasons(cancelReasons);
    }

    /*
     * method to get all refund reasons
     */
    protected Map<String, String> getAllRefundReasons() {
        Map<String, String> refundReasons = new HashMap<String, String>();
        List<RefundReason> enumerationValues = enumerationService.getEnumerationValues(RefundReason.class);
        Iterator<RefundReason> iterator = enumerationValues.iterator();
        while (iterator.hasNext()) {
            RefundReason reason = iterator.next();
            refundReasons.put(reason.getCode().toLowerCase(), reason.getCode());
        }
        return orderReturnFacade.filterRefundReasons(refundReasons);
    }

    /**
     * Populate select box for string.
     * 
     * @param seletedData
     *        the list of seletedData
     * @return the list
     */
    private List<SelectOption> populateSelectBoxForString(final Map<String, String> seletedData) {
        final List<SelectOption> selectBoxList = new ArrayList<SelectOption>();
        for (final Entry<String, String> entry : seletedData.entrySet()) {
            selectBoxList.add(new SelectOption(entry.getKey(), entry.getValue()));
        }
        return selectBoxList;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    @RequireHardLogIn
    public String profile(final Model model) throws CMSItemNotFoundException {
        final List<TitleData> titles = userFacade.getTitles();

        final CustomerData customerData = customerFacade.getCurrentCustomer();
        if (customerData.getTitleCode() != null) {
            model.addAttribute("title", findTitleForCode(titles, customerData.getTitleCode()));
        }

        model.addAttribute("customerData", customerData);

        storeCmsPageInModel(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    protected TitleData findTitleForCode(final List<TitleData> titles, final String code) {
        if (code != null && !code.isEmpty() && titles != null && !titles.isEmpty()) {
            for (final TitleData title : titles) {
                if (code.equals(title.getCode())) {
                    return title;
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/update-email", method = RequestMethod.GET)
    @RequireHardLogIn
    public String editEmail(final Model model) throws CMSItemNotFoundException {
        final CustomerData customerData = customerFacade.getCurrentCustomer();
        final UpdateEmailForm updateEmailForm = new UpdateEmailForm();

        updateEmailForm.setEmail(customerData.getDisplayUid());

        model.addAttribute("updateEmailForm", updateEmailForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/update-email", method = RequestMethod.POST)
    @RequireHardLogIn
    public String updateEmail(final UpdateEmailForm updateEmailForm, final BindingResult bindingResult,
            final Model model, final RedirectAttributes redirectAttributes, final HttpServletRequest request)
            throws CMSItemNotFoundException {
        getEmailValidator().validate(updateEmailForm, bindingResult);

        String returnAction = REDIRECT_TO_PROFILE_PAGE;

        if (!bindingResult.hasErrors() && !updateEmailForm.getEmail().equals(updateEmailForm.getChkEmail())) {
            bindingResult.rejectValue("chkEmail", "validation.checkEmail.equals", new Object[] {},
                    "validation.checkEmail.equals");
        }

        if (bindingResult.hasErrors()) {
            returnAction = errorUpdatingEmail(model);
        } else {
            try {
                customerFacade.changeUid(updateEmailForm.getEmail(), updateEmailForm.getPassword());
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        "text.account.profile.confirmationUpdated", null);

                // Replace the spring security authentication with the new UID
                final String newUid = customerFacade.getCurrentCustomer().getUid().toLowerCase();
                final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
                final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                        newUid, null, oldAuthentication.getAuthorities());
                newAuthentication.setDetails(oldAuthentication.getDetails());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            } catch (final DuplicateUidException e) {
                bindingResult.rejectValue("email", "profile.email.unique");
                returnAction = errorUpdatingEmail(model);
            } catch (final PasswordMismatchException passwordMismatchException) {
                bindingResult.rejectValue("password", "profile.currentPassword.invalid");
                returnAction = errorUpdatingEmail(model);
            }
        }

        return returnAction;
    }

    protected String errorUpdatingEmail(final Model model) throws CMSItemNotFoundException {
        GlobalMessages.addErrorMessage(model, "form.global.error");
        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        return getViewForPage(model);
    }

    @RequestMapping(value = "/update-profile", method = RequestMethod.GET)
    @RequireHardLogIn
    public String editProfile(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("titleData", userFacade.getTitles());

        final CustomerData customerData = customerFacade.getCurrentCustomer();
        final V2UpdateProfileForm v2updateProfileForm = new V2UpdateProfileForm();

        // v2updateProfileForm.setTitleCode(customerData.getTitleCode());
        v2updateProfileForm.setFirstName(customerData.getFirstName());
        v2updateProfileForm.setLastName(customerData.getLastName());
        v2updateProfileForm.setEmail(customerData.getDisplayUid());
        v2updateProfileForm.setMobileNumber(customerData.getMobileNumber());
        v2updateProfileForm.setDateOfBirth(customerData.getDateOfBirth());
        v2updateProfileForm.setMaritalStatus(customerData.getMaritalStatus());
        v2updateProfileForm.setGender(customerData.getGender());
        List<String> maritalStatusList = new ArrayList<String>();
        maritalStatusList.add("Single");
        maritalStatusList.add("Married");
        model.addAttribute("maritalStatusList", maritalStatusList);
        model.addAttribute("v2UpdateProfileForm", v2updateProfileForm);
        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.update.profile"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/update-profile", method = RequestMethod.POST)
    @RequireHardLogIn
    public String updateProfile(final V2UpdateProfileForm v2UpdateProfileForm, final BindingResult bindingResult,
            final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        v2UpdateProfileForm.setTitleCode(MS);
        v2UpdateProfileForm.setInternalDateOfBirth(v2UpdateProfileForm.getMobileDateOfBirth());
        getV2profileValidator().validate(v2UpdateProfileForm, bindingResult);

        String returnAction = ControllerConstants.Views.Pages.Account.AccountProfileEditPage;
        final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
        final CustomerData customerData = new CustomerData();
        customerData.setTitleCode(MS);
        customerData.setFirstName(v2UpdateProfileForm.getFirstName());
        customerData.setLastName(v2UpdateProfileForm.getLastName());
        customerData.setUid(currentCustomerData.getUid());
        customerData.setDisplayUid(v2UpdateProfileForm.getEmail());
        customerData.setMobileNumber(v2UpdateProfileForm.getMobileNumber());
        customerData.setDateOfBirth(v2UpdateProfileForm.getDateOfBirth());
        customerData.setMaritalStatus(v2UpdateProfileForm.getMaritalStatus());
        customerData.setGender(v2UpdateProfileForm.getGender());

        model.addAttribute("titleData", userFacade.getTitles());

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
            v2UpdateProfileForm.setEmail(currentCustomerData.getDisplayUid());
            List<String> maritalStatusList = new ArrayList<String>();
            maritalStatusList.add("Single");
            maritalStatusList.add("Married");
            model.addAttribute("maritalStatusList", maritalStatusList);
            return getViewForPage(model);
        } else {
            try {
                customerFacade.updateProfile(customerData);
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        "text.account.profile.confirmationUpdated", null);
                returnAction = REDIRECT_TO_PROFILE_PAGE;
            } catch (final DuplicateUidException e) {
                bindingResult.rejectValue("email", "registration.error.account.exists.title");
                GlobalMessages.addErrorMessage(model, "form.global.error");
            }
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        return returnAction;
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.GET)
    @RequireHardLogIn
    public String updatePassword(final Model model) throws CMSItemNotFoundException {
        final UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm();

        model.addAttribute("updatePasswordForm", updatePasswordForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

        model.addAttribute("breadcrumbs",
                accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    @RequireHardLogIn
    public String updatePassword(final UpdatePasswordForm updatePasswordForm, final BindingResult bindingResult,
            final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        getV2ChangePasswordValidator().validate(updatePasswordForm, bindingResult);
        if (!bindingResult.hasErrors()) {
            if (updatePasswordForm.getNewPassword().equals(updatePasswordForm.getCheckNewPassword())) {
                try {
                    customerFacade.changePassword(updatePasswordForm.getCurrentPassword(),
                            updatePasswordForm.getNewPassword());
                } catch (final PasswordMismatchException localException) {
                    bindingResult.rejectValue("currentPassword", "profile.currentPassword.invalid", new Object[] {},
                            "profile.currentPassword.invalid");
                }
            } else {
                bindingResult.rejectValue("checkNewPassword", "validation.checkPwd.equals", new Object[] {},
                        "validation.checkPwd.equals");
            }
        }

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

            model.addAttribute("breadcrumbs",
                    accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
            return getViewForPage(model);
        } else {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                    "text.account.confirmation.password.updated", null);
            return REDIRECT_TO_PROFILE_PAGE;
        }
    }

    @RequestMapping(value = "/address-book", method = RequestMethod.GET)
    @RequireHardLogIn
    public String getAddressBook(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("addressData", userFacade.getAddressBook());

        storeCmsPageInModel(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.addressBook"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/add-address", method = RequestMethod.GET)
    @RequireHardLogIn
    public String addAddress(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
        model.addAttribute("titleData", userFacade.getTitles());
        final V2AddressForm v2AddressForm = getPreparedAddressForm();
        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso("IN"));
        model.addAttribute("v2AddressForm", v2AddressForm);
        model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
        model.addAttribute("isDefaultAddress", Boolean.FALSE);
        storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/my-account/address-book", getMessageSource().getMessage(
                "text.account.addressBook", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.addressBook.addEditAddress",
                null, getI18nService().getCurrentLocale()), null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    protected V2AddressForm getPreparedAddressForm() {
        final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
        final V2AddressForm addressForm = new V2AddressForm();
        addressForm.setCountryIso("India");
        addressForm.setFirstName(currentCustomerData.getFirstName());
        addressForm.setLastName(currentCustomerData.getLastName());
        // addressForm.setTitleCode(currentCustomerData.getTitleCode());
        return addressForm;
    }

    @RequestMapping(value = "/add-address", method = RequestMethod.POST)
    @RequireHardLogIn
    public String addAddress(final V2AddressForm v2AddressForm, final BindingResult bindingResult, final Model model,
            final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        v2AddressForm.setCountryIso("IN");
        if (null != v2AddressForm.getPostcode()) {
            v2AddressForm.setPostcode(v2AddressForm.getPostcode().trim());
        }
        getV2addressValidator().validate(v2AddressForm, bindingResult);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
            model.addAttribute("titleData", userFacade.getTitles());
            // model.addAttribute("addressFrom", addressForm);
            model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
            model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(v2AddressForm.getAddressId())));

            if (v2AddressForm.getCountryIso() != null) {
                model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(v2AddressForm.getCountryIso()));
                v2AddressForm.setCountryIso("India");
                model.addAttribute("country", v2AddressForm.getCountryIso());
            }

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
            breadcrumbs.add(new Breadcrumb("/my-account/address-book", getMessageSource().getMessage(
                    "text.account.addressBook", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(
                    "text.account.addressBook.addEditAddress", null, getI18nService().getCurrentLocale()), null));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return getViewForPage(model);
        }

        final AddressData newAddress = new AddressData();
        newAddress.setTitleCode(MS);
        newAddress.setFirstName(v2AddressForm.getFirstName());
        newAddress.setLastName(v2AddressForm.getLastName());
        newAddress.setLine1(v2AddressForm.getLine1());
        newAddress.setLine2(v2AddressForm.getLine2());
        newAddress.setTown(v2AddressForm.getTownCity());
        newAddress.setPostalCode(v2AddressForm.getPostcode());
        newAddress.setBillingAddress(false);
        newAddress.setShippingAddress(true);
        newAddress.setVisibleInAddressBook(true);
        newAddress.setCountry(getI18NFacade().getCountryForIsocode(v2AddressForm.getCountryIso()));
        newAddress.setPhone(v2AddressForm.getPhoneNo());

        if (v2AddressForm.getRegionIso() != null && !StringUtils.isEmpty(v2AddressForm.getRegionIso())) {
            newAddress
                    .setRegion(getI18NFacade().getRegion(v2AddressForm.getCountryIso(), v2AddressForm.getRegionIso()));
        }

        if (userFacade.isAddressBookEmpty()) {
            newAddress.setDefaultAddress(true);
            newAddress.setVisibleInAddressBook(true);
        } else {
            newAddress.setDefaultAddress(v2AddressForm.getDefaultAddress() != null
                    && v2AddressForm.getDefaultAddress().booleanValue());
        }

        final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
                .verifyAddressData(newAddress);
        final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult,
                newAddress, model, redirectModel, bindingResult,
                getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
                "checkout.multi.address.added");

        if (addressRequiresReview) {
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(v2AddressForm.getCountryIso()));
            model.addAttribute("country", v2AddressForm.getCountryIso());
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

            // comment
            model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
            model.addAttribute("titleData", userFacade.getTitles());
            model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
            model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(v2AddressForm.getAddressId())));
            if (v2AddressForm.getCountryIso() != null) {
                model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(v2AddressForm.getCountryIso()));
                model.addAttribute("country", v2AddressForm.getCountryIso());
            }

            return getViewForPage(model);
        }
        userFacade.addAddress(newAddress);

        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    protected void setUpAddressFormAfterError(final V2AddressForm addressForm, final Model model) {
        model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
        model.addAttribute("titleData", userFacade.getTitles());
        model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
        model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));
        if (addressForm.getCountryIso() != null) {
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
            model.addAttribute("country", addressForm.getCountryIso());
        }
    }

    @RequestMapping(value = "/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String editAddress(@PathVariable("addressCode") final String addressCode, final Model model) throws CMSItemNotFoundException {
        final V2AddressForm v2AddressForm = new V2AddressForm();
        model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
        model.addAttribute("titleData", userFacade.getTitles());
        model.addAttribute("v2AddressForm", v2AddressForm);
        model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));

        for (final AddressData addressData : userFacade.getAddressBook()) {
            if (addressData.getId() != null && addressData.getId().equals(addressCode)) {
                model.addAttribute("regions",
                        getI18NFacade().getRegionsForCountryIso(addressData.getCountry().getIsocode()));
                model.addAttribute("country", addressData.getCountry().getIsocode());
                model.addAttribute("addressData", addressData);
                v2AddressForm.setAddressId(addressData.getId());
                v2AddressForm.setTitleCode(addressData.getTitleCode());
                v2AddressForm.setFirstName(addressData.getFirstName());
                v2AddressForm.setLastName(addressData.getLastName());
                v2AddressForm.setLine1(addressData.getLine1());
                v2AddressForm.setLine2(addressData.getLine2());
                v2AddressForm.setTownCity(addressData.getTown());
                v2AddressForm.setPostcode(addressData.getPostalCode());
                v2AddressForm.setCountryIso(addressData.getCountry().getName());
                v2AddressForm.setPhoneNo(addressData.getPhone());

                if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode())) {
                    v2AddressForm.setRegionIso(addressData.getRegion().getIsocode());
                }

                if (isDefaultAddress(addressData.getId())) {
                    v2AddressForm.setDefaultAddress(Boolean.TRUE);
                    model.addAttribute("isDefaultAddress", Boolean.TRUE);
                } else {
                    v2AddressForm.setDefaultAddress(Boolean.FALSE);
                    model.addAttribute("isDefaultAddress", Boolean.FALSE);
                }
                break;
            }
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/my-account/address-book", getMessageSource().getMessage(
                "text.account.addressBook", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.addressBook.addEditAddress",
                null, getI18nService().getCurrentLocale()), null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("edit", Boolean.TRUE);
        return getViewForPage(model);
    }

    /**
     * Method checks if address is set as default
     * 
     * @param addressId
     *        - identifier for address to check
     * @return true if address is default, false if address is not default
     */
    protected boolean isDefaultAddress(final String addressId) {
        final AddressData defaultAddress = userFacade.getDefaultAddress();
        return (defaultAddress != null && defaultAddress.getId() != null && defaultAddress.getId().equals(addressId));
    }

    @RequestMapping(value = "/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.POST)
    @RequireHardLogIn
    public String editAddress(final V2AddressForm v2AddressForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        if (null != v2AddressForm.getPostcode()) {
            v2AddressForm.setPostcode(v2AddressForm.getPostcode().trim());
        }
        getV2addressValidator().validate(v2AddressForm, bindingResult);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso("IN"));
            setUpAddressFormAfterError(v2AddressForm, model);
            model.addAttribute("edit", Boolean.TRUE);

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
            breadcrumbs.add(new Breadcrumb("/my-account/address-book", getMessageSource().getMessage(
                    "text.account.addressBook", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(
                    "text.account.addressBook.addEditAddress", null, getI18nService().getCurrentLocale()), null));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return getViewForPage(model);
        }

        model.addAttribute("metaRobots", "noindex,nofollow");
        v2AddressForm.setCountryIso("IN");
        final AddressData newAddress = new AddressData();
        newAddress.setId(v2AddressForm.getAddressId());
        newAddress.setTitleCode(MS);
        newAddress.setFirstName(v2AddressForm.getFirstName());
        newAddress.setLastName(v2AddressForm.getLastName());
        newAddress.setLine1(v2AddressForm.getLine1());
        newAddress.setLine2(v2AddressForm.getLine2());
        newAddress.setTown(v2AddressForm.getTownCity());
        newAddress.setPostalCode(v2AddressForm.getPostcode());
        newAddress.setBillingAddress(false);
        newAddress.setShippingAddress(true);
        newAddress.setVisibleInAddressBook(true);
        newAddress.setCountry(getI18NFacade().getCountryForIsocode(v2AddressForm.getCountryIso()));
        newAddress.setPhone(v2AddressForm.getPhoneNo());

        if (v2AddressForm.getRegionIso() != null && !StringUtils.isEmpty(v2AddressForm.getRegionIso())) {
            newAddress
                    .setRegion(getI18NFacade().getRegion(v2AddressForm.getCountryIso(), v2AddressForm.getRegionIso()));
        }

        if (Boolean.TRUE.equals(v2AddressForm.getDefaultAddress()) || userFacade.getAddressBook().size() <= 1) {
            newAddress.setDefaultAddress(true);
            newAddress.setVisibleInAddressBook(true);
        }

        final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
                .verifyAddressData(newAddress);
        final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult,
                newAddress, model, redirectModel, bindingResult,
                getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
                "checkout.multi.address.updated");

        if (addressRequiresReview) {
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(v2AddressForm.getCountryIso()));
            model.addAttribute("country", v2AddressForm.getCountryIso());
            model.addAttribute("edit", Boolean.TRUE);
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            return getViewForPage(model);
        }

        userFacade.editAddress(newAddress);

        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    @RequestMapping(value = "/select-suggested-address", method = RequestMethod.POST)
    public String doSelectSuggestedAddress(final AddressForm addressForm, final RedirectAttributes redirectModel) {
        final Set<String> resolveCountryRegions = org.springframework.util.StringUtils.commaDelimitedListToSet(Config
                .getParameter("resolve.country.regions"));

        final AddressData selectedAddress = new AddressData();
        selectedAddress.setId(addressForm.getAddressId());
        selectedAddress.setTitleCode(MS);
        selectedAddress.setFirstName(addressForm.getFirstName());
        selectedAddress.setLastName(addressForm.getLastName());
        selectedAddress.setLine1(addressForm.getLine1());
        selectedAddress.setLine2(addressForm.getLine2());
        selectedAddress.setTown(addressForm.getTownCity());
        selectedAddress.setPostalCode(addressForm.getPostcode());
        selectedAddress.setBillingAddress(false);
        selectedAddress.setShippingAddress(true);
        selectedAddress.setVisibleInAddressBook(true);

        final CountryData countryData = i18NFacade.getCountryForIsocode(addressForm.getCountryIso());
        selectedAddress.setCountry(countryData);

        if (resolveCountryRegions.contains(countryData.getIsocode())) {
            if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso())) {
                final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(),
                        addressForm.getRegionIso());
                selectedAddress.setRegion(regionData);
            }
        }

        if (resolveCountryRegions.contains(countryData.getIsocode())) {
            if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso())) {
                final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(),
                        addressForm.getRegionIso());
                selectedAddress.setRegion(regionData);
            }
        }

        if (Boolean.TRUE.equals(addressForm.getEditAddress())) {
            selectedAddress.setDefaultAddress(Boolean.TRUE.equals(addressForm.getDefaultAddress())
                    || userFacade.getAddressBook().size() <= 1);
            userFacade.editAddress(selectedAddress);
        } else {
            selectedAddress.setDefaultAddress(Boolean.TRUE.equals(addressForm.getDefaultAddress())
                    || userFacade.isAddressBookEmpty());
            userFacade.addAddress(selectedAddress);
        }

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "account.confirmation.address.added");

        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    @RequestMapping(value = "/remove-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = { RequestMethod.GET,
            RequestMethod.POST })
    @RequireHardLogIn
    public String removeAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel) {
        final AddressData addressData = new AddressData();
        addressData.setId(addressCode);
        userFacade.removeAddress(addressData);

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "account.confirmation.address.removed");
        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    @RequestMapping(value = "/set-default-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String setDefaultAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel) {
        final AddressData addressData = new AddressData();
        addressData.setDefaultAddress(true);
        addressData.setVisibleInAddressBook(true);
        addressData.setId(addressCode);
        userFacade.setDefaultAddress(addressData);
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "account.confirmation.default.address.changed");
        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    @RequestMapping(value = "/payment-details", method = RequestMethod.GET)
    @RequireHardLogIn
    public String paymentDetails(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("customerData", customerFacade.getCurrentCustomer());
        model.addAttribute("paymentInfoData", userFacade.getCCPaymentInfos(true));
        storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_DETAILS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.paymentDetails"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/set-default-payment-details", method = RequestMethod.POST)
    @RequireHardLogIn
    public String setDefaultPaymentDetails(@RequestParam final String paymentInfoId) {
        CCPaymentInfoData paymentInfoData = null;
        if (StringUtils.isNotBlank(paymentInfoId)) {
            paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentInfoId);
        }
        userFacade.setDefaultPaymentInfo(paymentInfoData);
        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    @RequestMapping(value = "/remove-payment-method", method = RequestMethod.POST)
    @RequireHardLogIn
    public String removePaymentMethod(final Model model, @RequestParam(value = "paymentInfoId") final String paymentMethodId,
            final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        userFacade.unlinkCCPaymentInfo(paymentMethodId);
        GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                "text.account.profile.paymentCart.removed");
        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    @RequestMapping(value = "/wishlist", method = RequestMethod.GET)
    @RequireHardLogIn
    public String wishlist(final Model model) throws CMSItemNotFoundException {

        final UserModel user = userService.getCurrentUser();
        String currentUserEmailId = user.getUid();
        if (currentUserEmailId.equalsIgnoreCase("anonymous")) {
            currentUserEmailId = null;
        }
        final String currentUserName = user.getDisplayName();

        final List<WishlistData> wishlistData = wishlistFacade.getWishlists();
        WishlistData wishlist;

        if (wishlistData != null && wishlistData.size() > 0) {
            final Iterator<WishlistData> wishlists = wishlistData.iterator();
            while (wishlists.hasNext()) {
                wishlist = wishlists.next();
                final List<OrderEntryData> orderEntries = wishlist.getEntries();
                if (orderEntries != null && orderEntries.size() > 0) {
                    final Iterator<OrderEntryData> orderEntryIterator = orderEntries.iterator();
                    OrderEntryData orderEntryData;
                    while (orderEntryIterator.hasNext()) {
                        orderEntryData = orderEntryIterator.next();
                        final ProductModel productModel = productService.getProductForCode(orderEntryData.getProduct()
                                .getCode());
                        if (orderEntryData.getProduct().getStock().getStockLevelStatus().getCode().equals("outOfStock")) {
                            String websiteUrl = Config.getParameter("website."
                                    + cmsSiteService.getCurrentSite().getUid() + ".https");
                            final V2NotifyCustomerForm v2NotifyCustomerForm = new V2NotifyCustomerForm();
                            v2NotifyCustomerForm.setCurrentUserEmailId(currentUserEmailId);
                            v2NotifyCustomerForm.setCurrentUserName(currentUserName);
                            v2NotifyCustomerForm.setUrl(websiteUrl.concat(orderEntryData.getProduct().getUrl()));
                            v2NotifyCustomerForm.setName(productModel.getName());
                            v2NotifyCustomerForm.setProductCode(productModel.getCode());
                            try {
                                final List<MediaContainerModel> mediaConatiners = productModel.getGalleryImages();
                                if (CollectionUtils.isNotEmpty(mediaConatiners)) {
                                    final MediaModel media = mediaService.getMediaByFormat(productModel
                                            .getGalleryImages().get(0), mediaService.getFormat("Product-96Wx96H"));
                                    v2NotifyCustomerForm.setMediaUrl(media.getURL());
                                }
                            } catch (final ModelNotFoundException e) {
                                LOG.debug("Product(" + productModel.getCode()
                                        + ") does not have media with format-'Product-96Wx96H'");
                            }
                            final PriceData priceData = orderEntryData.getProduct().getPrice();
                            if (priceData != null) {
                                v2NotifyCustomerForm.setProductPrice(orderEntryData.getProduct().getPrice()
                                        .getFormattedValue());
                            }
                            model.addAttribute("v2NotifyCustomerForm", v2NotifyCustomerForm);
                        }
                    }

                }

            }
        }
        model.addAttribute("wishlists", wishlistFacade.getWishlists());
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.wishlist"));
        storeCmsPageInModel(model, getContentPageForLabelOrId(WISHLIST_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(WISHLIST_CMS_PAGE));
        return getViewForPage(model);

    }

    @RequestMapping(value = "/remove-wishlist/{productCode:.*}", method = RequestMethod.GET)
    public String removeWishlistItem(@PathVariable("productCode") final String productCode, final RedirectAttributes redirectAttributes) {
        wishlistFacade.removeWishlistEntryData(productCode);
        GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                "wishlist.selected.item.removed");
        return REDIRECT_TO_WISHLIST_PAGE;
    }

    @RequestMapping(value = "/add-cart-wishlist/{productCode:.*}", method = RequestMethod.GET)
    public String addToCart(@PathVariable("productCode") final String productCode, final Model model,
            final RedirectAttributes redirectAttributes,
            final HttpServletRequest request) {
        long actualQuantity = 0;

        actualQuantity = addItemtoCart(1, productCode, actualQuantity, redirectAttributes, request);

        if (actualQuantity != 0) {
            wishlistFacade.removeWishlistEntryData(productCode);
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                    "wishlist.items.cart.added");
        }

        return REDIRECT_TO_WISHLIST_PAGE;
    }

    private long addItemtoCart(final long quantity, final String code, final long actualQuantity,
            final RedirectAttributes redirectAttributes, final HttpServletRequest request) {

        if (quantity > 0) {
            try {

                final CartModificationData cartModification = cartFacade.addToCart(code, quantity);

                if (cartModification.getQuantityAdded() != quantity) {

                    if (cartModification.getQuantityAdded() > 0) {
                        // Less than successful

                        GlobalMessages.addFlashMessage(
                                redirectAttributes,
                                GlobalMessages.ERROR_MESSAGES_HOLDER,
                                "basket.page.message.update.reducedNumberOfItemsAdded.lowStock",
                                new Object[] {
                                        cartModification.getEntry().getProduct().getName(),
                                        cartModification.getQuantityAdded(),
                                        quantity,
                                        request.getRequestURL().append(
                                                cartModification.getEntry().getProduct().getUrl()) });
                        // GlobalMessages.addMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        // "wishlist.list.added");
                    } else {
                        // No more stock available
                        GlobalMessages.addFlashMessage(
                                redirectAttributes,
                                GlobalMessages.ERROR_MESSAGES_HOLDER,
                                "basket.page.message.update.reducedNumberOfItemsAdded.noStock",
                                new Object[] {
                                        cartModification.getEntry().getProduct().getName(),
                                        request.getRequestURL().append(
                                                cartModification.getEntry().getProduct().getUrl()) });
                    }

                }
                if (cartModification.getQuantityAdded() > 0L) {

                    return actualQuantity + cartModification.getQuantityAdded();
                }

                // Put in the cart again after it has been modified
            } catch (final CommerceCartModificationException ex) {
                // response.setErrorMessage(ex.getMessage());
                LOG.warn("Couldn't add product of code " + code + " to cart.", ex);
                // model.addAttribute("cartData", cartFacade.getSessionCart());

            }
        }
        return actualQuantity;
    }

    @RequestMapping(value = "/login-add-to-wishlist", method = RequestMethod.GET)
    @RequireHardLogIn
    public String addWishlistItemWithoutLogin(@RequestParam(value = "productCode", required = true) final String productCode,
            @RequestParam(value = "wishlist", required = false) final String wishlist,
            @RequestParam(value = "entryNumber", required = false) final String entryNumber, final RedirectAttributes redirectModel)
            throws CommerceCartModificationException {
        Boolean isSuccess = Boolean.FALSE;
        if (StringUtils.isNotBlank(productCode)) {
            wishlistFacade.addWishlistEntry(productCode);
            isSuccess = Boolean.TRUE;
        }
        if (null != wishlist) {
            if (!(null == entryNumber)) {
                final long l = Long.parseLong(entryNumber);
                cartFacade.updateCartEntry(l, 0);
            }

            return "redirect:/cart?wishlist=true";
        }

        redirectModel.addAttribute("isAddedToWishlist", isSuccess);
        final ProductModel productModel = productService.getProductForCode(productCode);
        return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
    }

    @RequestMapping(value = "/wallet", method = RequestMethod.GET)
    @RequireHardLogIn
    public String wallet(final Model model) throws CMSItemNotFoundException {
        List<V2WalletTransactionData> transactions = v2CustomerWalletFacade.getCustomerTransactions();
        Double customerBalance = v2CustomerWalletFacade.getBalanceForCustomer();
        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", customerBalance);
        storeCmsPageInModel(model, getContentPageForLabelOrId(WALLET_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(WALLET_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.wallet"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        return getViewForPage(model);
    }

    @RequestMapping(value = "/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN + "/cancelOrder", method = RequestMethod.GET)
    public String cancelOrderPage(@PathVariable("orderCode") final String orderCode, final Model model) throws CMSItemNotFoundException {
        Map<OrderEntryData,Long> entries = orderFacade.getAllCancellableEntries(orderCode);
        OrderCancellationForm form = new OrderCancellationForm();
        form.setOrderCode(orderCode);
        populateCancellationForm(form, entries);
        model.addAttribute("cancelForm", form);
        model.addAttribute("hideBreadcrumb", true);
        model.addAttribute("cancelReasons", populateSelectBoxForString(getAllCancelReasons()));
        storeCmsPageInModel(model, getContentPageForLabelOrId("/cancelOrder"));
        setUpMetaDataForContentPage(model,
                getContentPageForLabelOrId("/cancelOrder"));
        return getViewForPage(model);
    }

    @RequestMapping(value = "/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN + "/returnOrder", method = RequestMethod.GET)
    public String returnOrderPage(@PathVariable("orderCode") final String orderCode, final Model model) throws CMSItemNotFoundException {
        OrderReturnForm form = new OrderReturnForm();
        form.setOrderCode(orderCode);
        Map<OrderEntryData, Long> returnableEntries = orderReturnFacade.getAllReturnableOrderEntries(orderCode);
        populateOrderReturnForm(returnableEntries, form);
        model.addAttribute("returnForm", form);
        model.addAttribute("refundReasons", populateSelectBoxForString(getAllRefundReasons()));
        storeCmsPageInModel(model, getContentPageForLabelOrId("/returnOrder"));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId("/returnOrder"));
        return getViewForPage(model);
    }
}
