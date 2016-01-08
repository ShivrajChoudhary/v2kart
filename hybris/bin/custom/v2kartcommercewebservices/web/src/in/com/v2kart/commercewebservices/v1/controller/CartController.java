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
 */
package in.com.v2kart.commercewebservices.v1.controller;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.DeliveryModesData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionRestrictionFacade;
import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.LowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.ccavenuepaymentintegration.enums.CCAvenueDecisionEnum;
import in.com.v2kart.ccavenuepaymentintegration.facades.CCAvenuePaymentFacade;
import in.com.v2kart.commercewebservices.common.StatusData;
import in.com.v2kart.commercewebservices.component.product.data.CartDeliveryOptionData;
import in.com.v2kart.commercewebservices.component.product.data.CartStatusData;
import in.com.v2kart.commercewebservices.component.product.data.ProductInCartData;
import in.com.v2kart.commercewebservices.component.product.data.ServiceableProductResponse;
import in.com.v2kart.commercewebservices.conv.HttpRequestPaymentInfoPopulator;
import in.com.v2kart.commercewebservices.exceptions.InvalidPaymentInfoException;
import in.com.v2kart.commercewebservices.exceptions.NoCheckoutCartException;
import in.com.v2kart.commercewebservices.exceptions.PaymentAuthorizationException;
import in.com.v2kart.commercewebservices.exceptions.UnsupportedDeliveryAddressException;
import in.com.v2kart.commercewebservices.exceptions.UnsupportedDeliveryModeException;
import in.com.v2kart.commercewebservices.otp.data.OtpData;
import in.com.v2kart.commercewebservices.populator.options.PaymentInfoOption;
import in.com.v2kart.commercewebservices.stock.CommerceStockFacade;
import in.com.v2kart.commercewebservices.validator.CCPaymentInfoValidator;
import in.com.v2kart.commercewebservices.validator.PlaceOrderCartValidator;
import in.com.v2kart.commercewebservices.validator.PointOfServiceValidator;
import in.com.v2kart.core.payment.services.V2PaymentService;
import in.com.v2kart.core.serviceability.V2ServiceabilityService;
import in.com.v2kart.core.sms.V2SmsService;
import in.com.v2kart.core.sms.populator.V2UserSmsDataMapPopulator;
import in.com.v2kart.core.vouchers.VoucherCodeResult;
import in.com.v2kart.facades.cart.impl.DefaultV2CartFacade;
import in.com.v2kart.facades.customer.V2CustomerFacade;
import in.com.v2kart.facades.order.V2CheckoutFacade;
import in.com.v2kart.facades.order.data.V2StoreCreditPaymentInfoData;
import in.com.v2kart.facades.otp.V2CashOnDeliveryOtp;
import in.com.v2kart.facades.wishlist.WishlistFacade;
import in.com.v2kart.v2kartebspaymentintegration.enums.EBSDEecisionEnum;
import in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.enums.PAYUDecisionEnum;
import in.com.v2kart.v2kartpayupaymentintegration.facades.PAYUPaymentFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller("cartControllerV1")
@RequestMapping(value = "/{baseSiteId}/cart")
public class CartController extends BaseController
{
	private static final String COULD_NOT_PROCESS_PAYMENT = "Could not process payment. Please try again later..";
	private static final String COULD_NOT_PLACE_ORDER = "Could not place order. Please try again later..";

	private final static Logger LOG = Logger.getLogger(CartController.class);
	@Resource(name = "commerceWebServicesCartFacade")
	private DefaultV2CartFacade cartFacade;
	/*
	 * @Resource(name = "checkoutFacade") private CheckoutFacade checkoutFacade;
	 */
	@Resource(name = "customerFacade")
	protected V2CustomerFacade customerFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;
	@Resource(name = "cartRestorationConverter")
	private Converter<CommerceCartRestoration, CartRestorationData> cartRestorationConverter;
	// @Resource(name = "deliveryModeConverter")
	// private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	// private Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverte
	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;
	private static final String MS = "ms";
	private static final String SHIPPING_DELIVERY_MODE = "ship";
	private static final String PICKUP_DELIVERY_MODE = "pick";
	@Resource(name = "commercePromotionRestrictionFacade")
	private CommercePromotionRestrictionFacade commercePromotionRestrictionFacade;
	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "ccPaymentInfoValidator")
	private Validator ccPaymentInfoValidator;
	@Resource(name = "deliveryAddressValidator")
	private Validator deliveryAddressValidator;
	@Resource(name = "httpRequestPaymentInfoPopulator")
	private ConfigurablePopulator<HttpServletRequest, CCPaymentInfoData, PaymentInfoOption> httpRequestPaymentInfoPopulator;
	@Resource(name = "placeOrderCartValidator")
	private PlaceOrderCartValidator placeOrderCartValidator;
	@Resource(name = "pointOfServiceValidator")
	private PointOfServiceValidator pointOfServiceValidator;
	@Resource(name = "httpRequestAddressDataPopulator")
	private Populator<HttpServletRequest, AddressData> httpRequestAddressDataPopulator;
	@Resource(name = "addressValidator")
	private Validator addressValidator;

	@Resource(name = "wishlistFacade")
	private WishlistFacade wishlistFacade;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "v2ServiceabilityService")
	V2ServiceabilityService v2ServiceabilityService;

	@Resource(name = "acceleratorCheckoutFacade")
	private V2CheckoutFacade checkoutFacade;

	@Resource(name = "payUPaymentFacade")
	private PAYUPaymentFacade payUPaymentFacade;

	@Resource(name = "ccavenuePaymentFacade")
	private CCAvenuePaymentFacade ccAvenuePaymentFacade;

	@Resource(name = "ebsPaymentFacade")
	private EBSPaymentFacade ebsPaymentFacade;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	public CartData getSessionCart()
	{
		return getSessionCart(false);
	}

	/**
	 * @return the ccAvenuePaymentFacade
	 */
	public CCAvenuePaymentFacade getCcAvenuePaymentFacade()
	{
		return ccAvenuePaymentFacade;
	}

	/**
	 * @return the ebsPaymentFacade
	 */
	public EBSPaymentFacade getEbsPaymentFacade()
	{
		return ebsPaymentFacade;
	}

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	/**
	 * @return the i18NFacade
	 */
	public I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	/**
	 * @param i18nFacade
	 *           the i18NFacade to set
	 */
	public void setI18NFacade(final I18NFacade i18nFacade)
	{
		i18NFacade = i18nFacade;
	}

	@Resource
	StoreFinderFacade storeFinderFacade;

	@Resource(name = "v2SmsService")
	private V2SmsService v2SmsService;

	public V2SmsService getV2SmsService()
	{
		return v2SmsService;
	}

	public void setV2SmsService(final V2SmsService v2SmsService)
	{
		this.v2SmsService = v2SmsService;
	}

	@Resource(name = "v2UserSmsDataMapPopulator")
	private V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator;

	public V2UserSmsDataMapPopulator getV2UserSmsDataMapPopulator()
	{
		return v2UserSmsDataMapPopulator;
	}

	public void setV2UserSmsDataMapPopulator(final V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator)
	{
		this.v2UserSmsDataMapPopulator = v2UserSmsDataMapPopulator;
	}

	@Resource(name = "v2CashOnDeliveryOtp")
	private V2CashOnDeliveryOtp v2CashOnDeliveryOtp;

	/**
	 * @return the v2CashOnDeliveryOtp
	 */
	public V2CashOnDeliveryOtp getV2CashOnDeliveryOtp()
	{
		return v2CashOnDeliveryOtp;
	}

	/**
	 * @param v2CashOnDeliveryOtp
	 *           the v2CashOnDeliveryOtp to set
	 */
	public void setV2CashOnDeliveryOtp(final V2CashOnDeliveryOtp v2CashOnDeliveryOtp)
	{
		this.v2CashOnDeliveryOtp = v2CashOnDeliveryOtp;
	}

	/** V2PaymentService bean injection */
	private V2PaymentService v2PaymentService;

	/**
	 * @return the v2PaymentService
	 */
	public V2PaymentService getV2PaymentService()
	{
		return v2PaymentService;
	}

	/**
	 * @param v2PaymentService
	 *           the v2PaymentService to set
	 */
	public void setV2PaymentService(final V2PaymentService v2PaymentService)
	{
		this.v2PaymentService = v2PaymentService;
	}

	/**
	 * Web service for getting session cart status. If there is no cart in the current session it will be return 0. <br>
	 * Sample call: http://localhost:9001/rest/v1/mysite/cart/status <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 *
	 * @param restore
	 *           enables cart restoration (true by default)
	 *
	 * @return {@link CartData} as response body.
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	@ResponseBody
	public CartStatusData getCartStatus()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartStatus");
		}
		final CartData cartData = cartFacade.getMiniCart();
		final CartStatusData cartStatusData = new CartStatusData();
		cartStatusData.setCartUnitCount(cartData.getTotalItems() != null ? cartData.getTotalItems().intValue() : 0);

		return cartStatusData;
	}

	/**
	 * Web service for getting session cart. If there is no cart in the current session it will be restored if possible,
	 * otherwise new one will be created. <br>
	 * Sample call: http://localhost:9001/rest/v1/mysite/cart/ <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 *
	 * @param restore
	 *           enables cart restoration (true by default)
	 *
	 * @return {@link CartData} as response body.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public CartData getSessionCart(@RequestParam(required = false, defaultValue = "true") final boolean restore)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCart");
		}

		if (!userFacade.isAnonymousUser() && !cartFacade.hasSessionCart() && restore)
		{
			try
			{
				commerceCartService.restoreCart(commerceCartService.getCartForGuidAndSiteAndUser(null,
						baseSiteService.getCurrentBaseSite(), userService.getCurrentUser()));
			}
			catch (final CommerceCartRestorationException e)
			{
				LOG.error("Couldn't restore cart: " + e.getMessage());
			}
		}

		CartData cartData = new CartData();

		cartData = cartFacade.getSessionCart();

		return cartData;
	}

	/**
	 * Web service for getting session cart. If there is no cart in the current session it will be restored if possible,
	 * otherwise new one will be created. <br>
	 * Sample call: http://localhost:9001/rest/v1/mysite/cart/ <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 *
	 * @param restore
	 *           enables cart restoration (true by default)
	 *
	 * @return {@link CartData} as response body.
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public CartData getCart(@RequestParam(required = false, defaultValue = "true") final boolean restore,
			@RequestParam(required = false) final String deliveryMode,
			@RequestParam(value = "shipMode", required = false) final String shipMode,
			@RequestParam(required = false) final String giftEntries,
			@RequestParam(required = false, defaultValue = "false") final boolean updateGiftEntries)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCart");
		}

		final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
		// Change Ship Mode if changed to Home Delivery
		if (shipMode != null)
		{
			if (shipMode.equals(SHIPPING_DELIVERY_MODE) && cartFacade.getSessionCart().isIsPickup())
			{
				cartFacade.updateAddress();
				checkoutFacade.changeCartDeliveryModeToShip();
			}
			else
			{
				if (shipMode.equals(PICKUP_DELIVERY_MODE))
				{
					cartFacade.updateName();
				}
			}
		}
		if (!userFacade.isAnonymousUser() && !cartFacade.hasSessionCart() && restore)
		{

			try
			{
				commerceCartService.restoreCart(commerceCartService.getCartForGuidAndSiteAndUser(null,
						baseSiteService.getCurrentBaseSite(), userService.getCurrentUser()));
			}
			catch (final CommerceCartRestorationException e)
			{
				LOG.error("Couldn't restore cart: " + e.getMessage());
			}
		}
		if (updateGiftEntries)
		{
			cartFacade.disableAllGiftWrap();
			if (giftEntries != null && !giftEntries.isEmpty())
			{
				for (final String giftEntry : StringUtils.split(giftEntries, "_"))
				{
					cartFacade.updateGiftWrapForEntry(Integer.valueOf(giftEntry), true);
				}
				cartFacade.calculateCartTotal();
			}
		}
		if (deliveryMode != null && !deliveryMode.isEmpty())
		{
			checkoutFacade.setDeliveryMode(deliveryMode);
		}
		else
		{
			checkoutFacade.setDeliveryModeIfAvailable();
		}

		CartData cartData = new CartData();

		cartData = cartFacade.getSessionCart();

		getDeliveryMode(cartData);

		// to set the selected deliveryMode
		for (final DeliveryModeData data : cartData.getDeliveryModes())
		{
			if (cartData.getDeliveryMode().getCode().equals(data.getCode()))
			{
				data.setSelected(Boolean.TRUE);

			}
			else
			{
				data.setSelected(Boolean.FALSE);
			}
		}

		getPaymentModes(cartData);
		// to set the COD Charges
		cartData.setIsCODChargesApplicable(Boolean.TRUE.booleanValue());
		final double minCodCharges = cmsSite.getCodCharges().doubleValue();
		final double codChargesOnOrder = cartData.getTotalPrice().getValue().doubleValue() * 0.02;
		final double codCharges = (minCodCharges < codChargesOnOrder) ? codChargesOnOrder : minCodCharges;

		cartData.setCodCharges(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(codCharges),
				commonI18NService.getCurrentCurrency()));

		/*
		 * //to set the shipping details try { final CMSTabParagraphComponentModel deliveryInfoComponent =
		 * (CMSTabParagraphComponentModel) cmsComponentService .getSimpleCMSComponent("shippingTab");
		 *
		 * deliveryInfoComponent.getContent();
		 *
		 * } catch (final CMSItemNotFoundException e) { // YTODO Auto-generated catch block e.printStackTrace(); }
		 */
		return cartData;
	}

	/**
	 * Web service handler for adding new products to the session cart.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry.<br>
	 * Client should provide product code and quantity (optional) as POST body.<br>
	 * It's also possible to add product that will be pickedup in store by specifying optional storeName parameter
	 * (product must be in stock in that particular store).<br>
	 * For Content-Type=application/x-www-form-urlencoded;charset=UTF-8 a sample body is: (urlencoded) is:
	 * entryNumber=1&qty=2..<br>
	 *
	 * Request Method = <code>POST<code>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.
	 *
	 * @param code
	 * @param qty
	 * @param storeName
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 * @throws WebserviceValidationException
	 * @throws StockSystemException
	 * @throws ProductLowStockException
	 */

	@RequestMapping(value = "/entry", method = RequestMethod.POST)
	@ResponseBody
	public CartModificationData addToCart(@PathVariable final String baseSiteId, @RequestParam(required = true) final String code,
			@RequestParam(required = false, defaultValue = "1") final long qty,
			@RequestParam(required = false) final String storeName)
			throws CommerceCartModificationException, WebserviceValidationException, ProductLowStockException, StockSystemException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("addToCart : code = " + code + ", qty = " + qty);
		}
		CartModificationData cartModificationData;
		if (StringUtils.isNotEmpty(storeName))
		{
			final Errors errors = new BeanPropertyBindingResult(storeName, "storeName");
			pointOfServiceValidator.validate(storeName, errors);
			if (errors.hasErrors())
			{
				throw new WebserviceValidationException(errors);
			}

			if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
			{
				throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED,
						baseSiteId);
			}

			final StockData stock = commerceStockFacade.getStockDataForProductAndPointOfService(code, storeName);
			if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
			{
				throw new ProductLowStockException("Product is currently out of stock", LowStockException.NO_STOCK, code);
			}
			else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
			{
				throw new ProductLowStockException("Not enough product in stock", LowStockException.LOW_STOCK, code);
			}
			cartModificationData = cartFacade.addToCart(code, qty, storeName);
		}
		else
		{
			final long qtyWillAdd = getQtyWillAdd(code, qty);
			cartModificationData = new CartModificationData();

			if (qtyWillAdd != 0L)
			{
				cartModificationData = cartFacade.addToCart(code, qtyWillAdd);
			}

			if (qtyWillAdd == 0L)
			{
				cartModificationData.setStatusCode("alreadyMaximumStock");
				cartModificationData.setStatusMessage("Maximum no. of quantity already added.");
			}
			else if (qtyWillAdd < qty)
			{
				cartModificationData.setStatusCode("maximumQuantityAchieved");
				cartModificationData.setStatusMessage("Reduced quantity added.");
			}
			else if (cartModificationData.getQuantityAdded() == 0L)
			{
				cartModificationData.setStatusCode("noStockAvailable");
				cartModificationData.setStatusMessage("Sorry, there is insufficient stock for your shopping bag.");
			}
			else if (cartModificationData.getQuantityAdded() < qty)
			{
				cartModificationData.setStatusCode("lowStockAvailable");
				cartModificationData
						.setStatusMessage("Unfortunately the quantity you chose exceeded the maximum order quantity for this product. The quantity in your shopping bag has been reduced to the maximum order quantity.");
			}

		}
		return cartModificationData;
	}

	/**
	 * Web service for modifying cart entry quantity.<br>
	 * Client should provide cart entry number as path variable and new quantity as url request parameter.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/0?qty=2 <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * Request Method = <code>PUT<code>
	 *
	 * @param entryNumber
	 * @param qty
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "/entry/{entryNumber}", method = RequestMethod.PUT)
	@ResponseBody
	public CartModificationData updateCartEntry(@PathVariable final long entryNumber, @RequestParam(required = true) final long qty)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry : entryNumber = " + entryNumber + ", qty = " + qty);
		}
		return cartFacade.updateCartEntry(entryNumber, qty);
	}

	/**
	 * Web service for deleting cart entry.<br>
	 * Client should provide cart entry number as path variable.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/0<br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * Request Method = <code>DELETE<code>
	 *
	 * @param entryNumber
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "/entry/{entryNumber}", method = RequestMethod.DELETE)
	@ResponseBody
	public CartModificationData deleteCartEntry(@PathVariable final long entryNumber) throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deleteCartEntry : entryNumber = " + entryNumber);
		}
		return cartFacade.updateCartEntry(entryNumber, 0);
	}

	/**
	 * Web service for setting store where cart entry will be picked up.<br>
	 * Client should provide cart entry number as path variable and storeName parameter in body.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/{entryNumber}/store<br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * Request Method = <code>PUT<code>
	 *
	 * @param entryNumber
	 *           identifier of entry which should be updated
	 * @param storeName
	 *           name of store where items will be picked
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 * @throws WebserviceValidationException
	 * @throws StockSystemException
	 * @throws LowStockException
	 */
	@RequestMapping(value = "/entry/{entryNumber}/store", method = RequestMethod.PUT)
	@ResponseBody
	public CartModificationData pickupEntryInStore(@PathVariable final String baseSiteId, @PathVariable final long entryNumber,
			@RequestParam(required = true) final String storeName) throws CommerceCartModificationException, LowStockException,
			StockSystemException, WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry : entryNumber = " + entryNumber + ", storeName = " + storeName);
		}

		final Errors errors = new BeanPropertyBindingResult(storeName, "storeName");
		pointOfServiceValidator.validate(storeName, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled for " + baseSiteId + " site");
		}
		final OrderEntryData orderEntry = getCartEntryForNumber(entryNumber);
		if (orderEntry != null)
		{
			final StockData stock = commerceStockFacade.getStockDataForProductAndPointOfService(orderEntry.getProduct().getCode(),
					storeName);
			if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
			{
				throw new LowStockException("Product [" + orderEntry.getProduct().getCode() + "] is currently out of stock",
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
			{
				throw new LowStockException("Not enough product [" + orderEntry.getProduct().getCode() + "] in stock",
						LowStockException.LOW_STOCK, String.valueOf(entryNumber));
			}
		}

		return cartFacade.updateCartEntry(entryNumber, storeName);
	}

	/**
	 * Web service reseting store where entry should be picked up. Entry will be delivered by selected delivery method<br>
	 * Client should provide cart entry number as path variable.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/{entryNumber}/store<br>
	 * Request Method = <code>DELETE<code>
	 *
	 * @param entryNumber
	 *           identifier of entry which should be updated
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 * @throws StockSystemException
	 * @throws LowStockException
	 */
	@RequestMapping(value = "/entry/{entryNumber}/store", method = RequestMethod.DELETE)
	@ResponseBody
	public CartModificationData updateEntryToDelivery(@PathVariable final String baseSiteId, @PathVariable final long entryNumber)
			throws CommerceCartModificationException, LowStockException, StockSystemException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateEntryToDelivery : entryNumber = " + entryNumber);
		}

		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled for " + baseSiteId + " site");
		}
		final OrderEntryData orderEntry = getCartEntryForNumber(entryNumber);
		if (orderEntry != null)
		{
			final StockData stock = commerceStockFacade.getStockDataForProductAndBaseSite(orderEntry.getProduct().getCode(),
					baseSiteId);
			if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
			{
				throw new LowStockException("Product [" + orderEntry.getProduct().getCode()
						+ "] cannot be shipped - out of stock online",
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
			{
				throw new LowStockException("Product [" + orderEntry.getProduct().getCode()
						+ "] cannot be shipped - not enough product in stock online", LowStockException.LOW_STOCK,
						String.valueOf(entryNumber));
			}
		}
		return cartFacade.updateCartEntry(entryNumber, null);
	}

	/**
	 * Web service for setting cart's delivery address by address id.<br>
	 * Address id must be given as path variable.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/address/delivery/1234 <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>PUT</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if carts delivery address was changed.
	 * @throws UnsupportedDeliveryAddressException
	 * @throws NoCheckoutCartException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/address/delivery/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public CartData setCartDeliveryAddress(@PathVariable final String id) throws UnsupportedDeliveryAddressException,
			NoCheckoutCartException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCartDeliveryAddress : id = " + id);
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot set delivery address. There was no checkout cart created yet!");
		}
		final AddressData address = new AddressData();
		address.setId(id);

		final Errors errors = new BeanPropertyBindingResult(address, "addressData");
		deliveryAddressValidator.validate(address, errors);
		if (errors.hasErrors())
		{
			throw new UnsupportedDeliveryAddressException(id);
		}

		if (checkoutFacade.setDeliveryAddress(address))
		{
			final CartData cartData = getCart(false, null, null, null, false);
			return cartData;
		}

		throw new UnsupportedDeliveryAddressException(id);
	}

	/**
	 * Web service for removing delivery address from current cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/address/delivery <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>DELETE</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if carts delivery address was removed.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/address/delivery", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData removeDeliveryAddress()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeDeliveryAddress");
		}
		checkoutFacade.removeDeliveryAddress();
		return getSessionCart();
	}

	/**
	 * Web service for setting cart's delivery mode by delivery mode code.<br>
	 * Delivery mode code must be given as path variable.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/deliverymode/expressDelivery <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>PUT</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if carts delivery mode was changed.
	 * @throws UnsupportedDeliveryModeException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/deliverymodes/{code}", method = RequestMethod.PUT)
	@ResponseBody
	public CartData setCartDeliveryMode(@PathVariable final String code) throws UnsupportedDeliveryModeException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCartDeliveryMode : code = " + code);
		}

		if (checkoutFacade.setDeliveryMode(code))
		{
			return getSessionCart();
		}
		throw new UnsupportedDeliveryModeException(code);
	}

	/**
	 * Web service for removing delivery mode from current cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/deliverymode <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>DELETE</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if cart's delivery mode was removed.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/deliverymodes", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData removeDeliveryMode()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeDeliveryMode");
		}
		checkoutFacade.removeDeliveryMode();
		return getSessionCart();
	}

	/**
	 * Web service for placing order from current session cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/placeorder. <br>
	 * This method requires authentication.<br>
	 * Method type : <code>POST</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return {@link OrderData} as response body
	 * @throws InvalidCartException
	 * @throws NoCheckoutCartException
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/placeorder", method = RequestMethod.POST)
	@ResponseBody
	public OrderData placeOrder(final HttpSession session) throws InvalidCartException, NoCheckoutCartException,
			WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("placeOrder");
		}

		validateCartForPlaceOrder();

		final OrderData orderData = checkoutFacade.placeOrder();
		final String orderGuid = orderData.getGuid();
		session.setAttribute("orderGuid", orderGuid);
		return orderData;
	}

	protected void validateCartForPlaceOrder() throws NoCheckoutCartException, InvalidCartException, WebserviceValidationException
	{
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot place order. There was no checkout cart created yet!");
		}

		final CartData cartData = cartFacade.getSessionCart();
		final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
		placeOrderCartValidator.validate(cartData, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		try
		{
			List<CartModificationData> modificationList;
			modificationList = cartFacade.validateCartData();
			if (modificationList != null && !modificationList.isEmpty())
			{
				final CartModificationDataList cartModificationDataList = new CartModificationDataList();
				cartModificationDataList.setCartModificationList(modificationList);
				throw new WebserviceValidationException(cartModificationDataList);
			}
		}
		catch (final CommerceCartModificationException e)
		{
			throw new InvalidCartException(e);
		}
	}

	/**
	 * Web service for creating a credit card payment subscription.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/paymentinfo <br>
	 * CCPaymentInfoData parameters need to be send as post body.<br>
	 * Method uses dedicated populator - {@link HttpRequestPaymentInfoPopulator} - to populate the
	 * {@link CCPaymentInfoData} from request parameters.<br>
	 * Method uses dedicated validator - {@link CCPaymentInfoValidator} - to validate request parameters.<br>
	 * This method requires authentication and is restricted for <code>HTTPS</code> channel.<br>
	 * Method type : <code>POST</code>.<br>
	 *
	 * @param request
	 *           inco`g HttpServletRequest. As there are many potential query parameters to handle they are not mapped
	 *           using annotations.
	 *
	 * @return {@link CartData} as response body
	 * @throws WebserviceValidationException
	 * @throws InvalidPaymentInfoException
	 * @throws NoCheckoutCartException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/paymentinfo", method = RequestMethod.POST)
	@ResponseBody
	public CartData addPaymentInfo(final HttpServletRequest request) throws WebserviceValidationException,
			InvalidPaymentInfoException,
			NoCheckoutCartException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("addPaymentInfo");
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
		}

		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		final Errors errors = new BeanPropertyBindingResult(paymentInfoData, "paymentInfoData");

		final Collection<PaymentInfoOption> options = new ArrayList<PaymentInfoOption>();
		options.add(PaymentInfoOption.BASIC);
		options.add(PaymentInfoOption.BILLING_ADDRESS);

		httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, options);
		ccPaymentInfoValidator.validate(paymentInfoData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		final boolean emptySavedPaymentInfos = userFacade.getCCPaymentInfos(true).isEmpty();
		final CCPaymentInfoData createdPaymentInfoData = checkoutFacade.createPaymentSubscription(paymentInfoData);

		if (createdPaymentInfoData.isSaved() && (paymentInfoData.isDefaultPaymentInfo() || emptySavedPaymentInfos))
		{
			userFacade.setDefaultPaymentInfo(createdPaymentInfoData);
		}

		if (checkoutFacade.setPaymentDetails(createdPaymentInfoData.getId()))
		{
			return getSessionCart();
		}
		throw new InvalidPaymentInfoException(createdPaymentInfoData.getId());

	}

	/**
	 * Web service for assigning given payment (by payment id) to the checkout cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/paymentinfo/1234 <br>
	 * This method requires authentication and is restricted for <code>HTTPS</code> channel.<br>
	 * Method type : <code>PUT</code>.
	 *
	 * @return <code>true</code> if paymentInfo was assigned to the session cart.
	 * @throws InvalidPaymentInfoException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/paymentinfo/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public CartData setPaymentDetails(@PathVariable final String id) throws InvalidPaymentInfoException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setPaymentDetails : id = " + id);
		}
		if (checkoutFacade.setPaymentDetails(id))
		{
			return getSessionCart();
		}
		throw new InvalidPaymentInfoException(id);
	}

	/**
	 * Web service for getting all supported delivery modes for the session cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/deliverymodes <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>GET</code>.
	 *
	 * @return List of {@link DeliveryModeData} as response body.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/deliverymodes", method = RequestMethod.GET)
	@ResponseBody
	public DeliveryModesData getSupportedDeliveryModes()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSupportedDeliveryModes");
		}
		final DeliveryModesData deliveryModesData = new DeliveryModesData();
		deliveryModesData.setDeliveryModes(checkoutFacade.getSupportedDeliveryModes());
		return deliveryModesData;
	}

	/**
	 * Web service for authorizing cart's credit cart payment.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/authorizePayment <br>
	 * authorization security code - ccv - must be sent as a post body.<br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>POST</code>.
	 *
	 * @return true if the payment was authorized
	 * @throws PaymentAuthorizationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/authorize", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.ACCEPTED)
	public CartData authorizePayment(@RequestParam(required = true) final String securityCode)
			throws PaymentAuthorizationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("authorizePayment");
		}
		if (checkoutFacade.authorizePayment(securityCode))
		{
			return getSessionCart();
		}
		throw new PaymentAuthorizationException();
	}

	/**
	 * Web service for restoring anonymous cart by guid.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/restore <br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>GET</code>.
	 *
	 * @param guid
	 *
	 * @return {@link CartRestorationData}
	 * @throws CommerceCartRestorationException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/restore", method = RequestMethod.GET)
	@ResponseBody
	public CartRestorationData restoreCart(@RequestParam final String guid) throws CommerceCartRestorationException
	{
		final CartModel cartModel = commerceCartService.getCartForGuidAndSiteAndUser(guid, baseSiteService.getCurrentBaseSite(),
				userService.getAnonymousUser());

		if (cartModel == null)
		{
			throw new CommerceCartRestorationException("Cannot find cart for a given guid: " + guid);
		}

		return cartRestorationConverter.convert(commerceCartService.restoreCart(cartModel));
	}

	/**
	 * Web service for enabling order promotions.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/promotion/{promotionCode}<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>POST</code>.
	 *
	 * @param promotionCode
	 *           promotion code
	 * @return {@link CartData}
	 * @throws {@link CommercePromotionRestrictionException}
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT" })
	/*
	 * To allow trusted client logged in as a customer. ROLE_TRUSTED_CLIENT is forced in spring security configuration.
	 */
	@RequestMapping(value = "/promotion/{promotionCode}", method = RequestMethod.POST)
	@ResponseBody
	public CartData applyPromotion(@PathVariable final String promotionCode) throws CommercePromotionRestrictionException
	{
		commercePromotionRestrictionFacade.enablePromotionForCurrentCart(promotionCode);
		return getSessionCart();
	}

	/**
	 * Web service for disabling order promotions.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/promotion/{promotionCode}<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>DELETE</code>.
	 *
	 * @param promotionCode
	 *           promotion code
	 * @return {@link CartData}
	 * @throws {@link CommercePromotionRestrictionException}
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT" })
	/*
	 * To allow trusted client logged in as a customer. ROLE_TRUSTED_CLIENT is forced in spring security configuration.
	 */
	@RequestMapping(value = "/promotion/{promotionCode}", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData removePromotion(@PathVariable final String promotionCode) throws CommercePromotionRestrictionException,
			NoCheckoutCartException
	{
		commercePromotionRestrictionFacade.disablePromotionForCurrentCart(promotionCode);
		return getSessionCart();
	}

	/**
	 * Web service for applying voucher to cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/voucher/abc-9PSW-EDH2-RXKA <br>
	 * This method requires authentication.<br>
	 * Method type : <code>POST</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return cart data with applied voucher.
	 * @throws NoCheckoutCartException
	 * @throws VoucherOperationException
	 * @throws CalculationException
	 */
	/*
	 * @Secured( { "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	 *
	 * @RequestMapping(value = "/voucher/{voucherCode}", method = RequestMethod.POST)
	 *
	 * @ResponseBody public StatusData applyVoucherForCart(@PathVariable final String voucherCode) throws
	 * NoCheckoutCartException, VoucherOperationException, CalculationException { final StatusData statusData = new
	 * StatusData(); if (LOG.isDebugEnabled()) { LOG.debug("apply voucher : voucherCode = " + voucherCode); } if
	 * (!checkoutFacade.hasCheckoutCart()) { throw new
	 * NoCheckoutCartException("Cannot apply voucher. There was no checkout cart created yet!"); }
	 *
	 * final VoucherCodeResult voucherCodeResult = cartFacade.applyVoucherCode(voucherCode);
	 * statusData.setStatus(Boolean.valueOf(voucherCodeResult.isApplied())); if (statusData.getStatus().booleanValue()) {
	 * statusData.setMessage(getMessage("basket.vouchercode.added")); } else {
	 * statusData.setMessage(getMessage(voucherCodeResult.getRejectMessage())); } return statusData; }
	 */
	/**
	 * Web service for removing voucher from cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/voucher/abc-9PSW-EDH2-RXKA <br>
	 * This method requires authentication.<br>
	 * Method type : <code>DELETE</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return updated cart data.
	 * @throws NoCheckoutCartException
	 * @throws VoucherOperationException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/voucher/{voucherCode}", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData releaseVoucherFromCart(@PathVariable final String voucherCode) throws NoCheckoutCartException,
			VoucherOperationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("release voucher : voucherCode = " + voucherCode);
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot realese voucher. There was no checkout cart created yet!");
		}
		voucherFacade.releaseVoucher(voucherCode.toUpperCase());
		return getSessionCart();
	}

	/**
	 * Web service for one-step checkout from current session cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/checkout <br>
	 * This method requires authentication.<br>
	 * Method type : <code>POST</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @param addressId
	 *           id of created address
	 * @param addressIsocode
	 *           country isocode, parameter is also used as a flag to decide if new address should be created
	 * @param deliveryMode
	 *           delivery mode
	 * @param voucherCode
	 *           voucher code
	 * @param paymentInfoId
	 *           id of created payment info
	 * @param securityCode
	 *           security code for payment validation
	 * @param request
	 *           incoming HttpServletRequest. As there are many potential query parameters to handle they are not mapped
	 *           using annotations.
	 * @return {@link OrderData}
	 * @throws NoCheckoutCartException
	 * @throws UnsupportedDeliveryAddressException
	 * @throws UnsupportedDeliveryModeException
	 * @throws InvalidPaymentInfoException
	 * @throws PaymentAuthorizationException
	 * @throws InvalidCartException
	 * @throws WebserviceValidationException
	 * @throws VoucherOperationException
	 * @throws CalculationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	@ResponseBody
	public OrderData oneStepCheckout(@RequestParam(required = false) final String addressId,
			@RequestParam(value = "country.isocode", required = false) final String addressIsocode,
			@RequestParam final String deliveryMode, @RequestParam(required = false) final String voucherCode,
			@RequestParam(required = false) final String paymentInfoId, @RequestParam final String securityCode,
			@RequestParam(value = "shipMode") final String shipMode, final HttpServletRequest request)
			throws NoCheckoutCartException,
			UnsupportedDeliveryAddressException, UnsupportedDeliveryModeException, InvalidPaymentInfoException,
			PaymentAuthorizationException, InvalidCartException, WebserviceValidationException, VoucherOperationException,
			CalculationException
	{
		// redeeming voucher if needed
		if (voucherCode != null)
		{
			applyVoucherForCart(voucherCode.toUpperCase());
		}
		// Change Ship Mode if changed to Home Delivery
		if (shipMode.equals(SHIPPING_DELIVERY_MODE))
		{
			checkoutFacade.changeCartDeliveryModeToShip();
		}
		// delivery address
		if (addressId != null)
		{
			LOG.info("oneStepCheckout : addressId=" + addressId);
			setCartDeliveryAddress(addressId);
		}
		else if (addressIsocode != null)
		{
			final AddressData address = createAddress(request);
			setCartDeliveryAddress(address.getId());
		}

		// deliveryMode
		setCartDeliveryMode(deliveryMode);

		// paymentInfo
		if (paymentInfoId != null)
		{
			setPaymentDetails(paymentInfoId);
		}
		else
		{
			addPaymentInfo(request);
		}

		// authorize
		authorizePayment(securityCode);

		// placeorder
		return placeOrder(request.getSession());
	}

	/**
	 * Web service for moving items to wishlist.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return cart data with applied voucher.
	 * @throws CommerceCartModificationException
	 */

	@RequestMapping(value = "/add-to-wishlist", method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERGROUP")
	@ResponseBody
	public CartData addWishlistItem(@RequestParam(value = "productCode", required = true) final String productCode,
			@RequestParam(value = "entryNumber", required = true) final String entryNumber) throws CommerceCartModificationException
	{

		if (StringUtils.isNotBlank(productCode) && !(null == entryNumber))
		{

			wishlistFacade.addWishlistEntry(productCode);
			final long l = Long.parseLong(entryNumber);
			cartFacade.updateCartEntry(l, 0);
			return getCart(false, null, null, null, false);
		}
		return null;

	}

	protected OrderEntryData getCartEntryForNumber(final long number)
	{
		final List<OrderEntryData> entries = cartFacade.getSessionCart().getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer requestedEntryNumber = Integer.valueOf((int) number);
			for (final OrderEntryData entry : entries)
			{
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		return null;
	}

	/**
	 * Web service for applying voucher to cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/voucher/abc-9PSW-EDH2-RXKA <br>
	 * This method requires authentication.<br>
	 * Method type : <code>POST</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return cart data with applied voucher.
	 * @throws NoCheckoutCartException
	 * @throws VoucherOperationException
	 * @throws CalculationException
	 */

	@RequestMapping(value = "/applyVoucher/{voucherCode}", method = RequestMethod.POST)
	@ResponseBody
	public CartData applyVoucherForCart(@PathVariable final String voucherCode) throws NoCheckoutCartException,
			VoucherOperationException,
			CalculationException
	{

		final StatusData statusData = new StatusData();
		if (LOG.isDebugEnabled())

		{
			LOG.debug("apply voucher : voucherCode = " + voucherCode);
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot apply voucher. There was no checkout cart created yet!");
		}

		final VoucherCodeResult voucherCodeResult = cartFacade.applyVoucherCode(voucherCode.toUpperCase());

		statusData.setStatus(Boolean.valueOf(voucherCodeResult.isApplied()));
		if (statusData.getStatus().booleanValue())
		{
			statusData.setMessage(getMessage("basket.vouchercode.added"));
		}
		else
		{
			statusData.setMessage(getMessage(voucherCodeResult.getRejectMessage()));
		}

		CartData cartData = new CartData();

		cartData = cartFacade.getSessionCart();

		cartData.setStatus(statusData);

		return cartData;
	}

	@RequestMapping(value = "/releaseVoucherCode", method = RequestMethod.POST)
	@ResponseBody
	public CartData releaseVoucherCode(@RequestParam("voucherCode") final String voucherCode)
			throws CommerceCartModificationException
	{
		final StatusData statusData = new StatusData();

		final boolean releasedVoucherCode = cartFacade.releaseVoucherCode(voucherCode.toUpperCase());
		if (!releasedVoucherCode)
		{
			statusData.setStatus(Boolean.valueOf(releasedVoucherCode));
			statusData.setMessage(getMessage("basket.vouchercode.notreleased"));
		}
		else
		{
			statusData.setStatus(Boolean.valueOf(releasedVoucherCode));
			statusData.setMessage(getMessage("basket.vouchercode.released"));
		}

		CartData cartData = new CartData();

		cartData = cartFacade.getSessionCart();

		cartData.setStatus(statusData);

		return cartData;

	}

	/**
	 * Web service handler for the product serviceable call.
	 *
	 * @param pinCode
	 * @return Serviceable Product List
	 */

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@ResponseBody
	@RequestMapping(value = "/isServiceable/{pinCode}/{countryIsocode}", method = RequestMethod.GET)
	public ServiceableProductResponse isCartServiceable(@PathVariable final String pinCode,
			@PathVariable final String countryIsocode)
	{
		final ServiceableProductResponse serviceableResponse = new ServiceableProductResponse();
		serviceableResponse.setIsServiceable(v2ServiceabilityService.isProductServicableForPinCode(pinCode, countryIsocode));

		return serviceableResponse;
	}

	public void getDeliveryMode(final CartData cartData)
	{

		final List<? extends DeliveryModeData> deliveryMode = checkoutFacade.getSupportedDeliveryModes();
		final List<DeliveryModeData> selectedDeliveryMode = new ArrayList<DeliveryModeData>();
		for (final DeliveryModeData selectDeliveryMode : deliveryMode)
		{
			if (cartData.isIsPickup())
			{
				if (selectDeliveryMode.isIsPickUp())
				{
					selectedDeliveryMode.add(selectDeliveryMode);
				}
			}
			else
			{
				if (!selectDeliveryMode.isIsPickUp())
				{
					selectedDeliveryMode.add(selectDeliveryMode);
				}
			}
		}
		cartData.setDeliveryModes(selectedDeliveryMode);

	}

	private AddressData createAddress(final HttpServletRequest request)
	{
		final AddressData address = new AddressData();
		final Errors errors = new BeanPropertyBindingResult(address, "addressData");

		httpRequestAddressDataPopulator.populate(request, address);
		addressValidator.validate(address, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		address.setShippingAddress(true);
		address.setVisibleInAddressBook(true);
		userFacade.addAddress(address);
		return address;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(value = "/payu/placeorders", method = RequestMethod.POST)
	@ResponseBody
	public OrderData placePAYUOrders(
			final HttpServletRequest request,
			@RequestParam(value = "isSameAsDeliveryAddress", required = true, defaultValue = "false") final boolean isSameAsDeliveryAddress,
			@RequestParam(value = "isUsingWallet", required = true, defaultValue = "false") final boolean isUsingWallet,
			@RequestParam(value = "phoneNumber", required = true, defaultValue = "") final String phoneNumber)
	{

		OrderData orderData = new OrderData();
		final AddressData addressData = getAddressData(request, isSameAsDeliveryAddress);

		// final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();

		getPayUPaymentFacade().saveBillingAddress(addressData);
		final String guid = request.getParameter("guid");
		if (null == guid)
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}
		final Map<String, String> resultMap = getPayUPaymentFacade().getPaymentInfo(guid);
		if (resultMap.isEmpty())
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}
		final String result = resultMap.get(PAYU.ResponseParameters.STATUS.getValue());
		if (result != null && result.equalsIgnoreCase(PAYUDecisionEnum.SUCCESS.getValue()))
		{
			try
			{
				final CartModel cartModel = cartService.getSessionCart();
				final PaymentSubscriptionResultData paymentSubscriptionResultData = getPayUPaymentFacade().completeHopCreatePayment(
						resultMap, true, cartModel);

				if (paymentSubscriptionResultData.isSuccess())
				{
					if (isUsingWallet)
					{
						final CartData cartData = getCart(false, null, null, null, false);
						checkoutFacade.useMyWalletMoney(cartData, new AddressData());
					}
					final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
					if ((null != phoneNumber) && (!phoneNumber.equals("")))
					{
						final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
						if (!phoneNumbers.contains(phoneNumber))
						{
							phoneNumbers.add(phoneNumber);
							currentCustomerData.setPhoneNumbers(phoneNumbers);
							customerFacade.updatePhoneNumbers(currentCustomerData, true);
						}
					}
					orderData = checkoutFacade.placeOrder();
				}
			}
			catch (final Exception e)
			{
				orderData.setStatusDisplay(COULD_NOT_PLACE_ORDER);
			}
		}
		else
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}

		/*
		 * orderData.setCodCharges(priceDataFactory.create(PriceDataType.BUY,
		 * BigDecimal.valueOf(cmsSite.getCodCharges().doubleValue()), commonI18NService.getCurrentCurrency()));
		 */

		return orderData;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(value = "/ccavenue/placeorders", method = RequestMethod.POST)
	@ResponseBody
	public OrderData placeCCAvenueOrders(
			final HttpServletRequest request,
			@RequestParam(value = "isSameAsDeliveryAddress", required = true, defaultValue = "false") final boolean isSameAsDeliveryAddress,
			@RequestParam(value = "isUsingWallet", required = true, defaultValue = "false") final boolean isUsingWallet,
			@RequestParam(value = "phoneNumber", required = true, defaultValue = "") final String phoneNumber)
	{

		OrderData orderData = new OrderData();
		final AddressData addressData = getAddressData(request, isSameAsDeliveryAddress);
		LOG.info("Start placing order");
		// final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();

		getCcAvenuePaymentFacade().saveBillingAddress(addressData);
		final String guid = request.getParameter("guid");
		LOG.info("GUID =" + guid);
		if (null == guid)
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}
		final Map<String, String> resultMap = getCcAvenuePaymentFacade().getPaymentInfo(guid);
		if (resultMap.isEmpty())
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}
		LOG.info("Start placing order111");
		final String result = resultMap.get(CCAVENUE.ResponseParameters.ORDER_STATUS.getValue());
		if (result != null && result.equalsIgnoreCase(CCAvenueDecisionEnum.SUCCESS.getValue()))
		{
			try
			{
				final CartModel cartModel = cartService.getSessionCart();
				final PaymentSubscriptionResultData paymentSubscriptionResultData = getCcAvenuePaymentFacade()
						.completeHopCreatePayment(
								resultMap, true, cartModel);

				if (paymentSubscriptionResultData.isSuccess())
				{
					if (isUsingWallet)
					{
						final CartData cartData = getCart(false, null, null, null, false);
						checkoutFacade.useMyWalletMoney(cartData, new AddressData());
					}
					final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
					if ((null != phoneNumber) && (!phoneNumber.equals("")))
					{
						final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
						if (!phoneNumbers.contains(phoneNumber))
						{
							phoneNumbers.add(phoneNumber);
							currentCustomerData.setPhoneNumbers(phoneNumbers);
							customerFacade.updatePhoneNumbers(currentCustomerData, true);
						}
					}
					orderData = checkoutFacade.placeOrder();
				}
			}
			catch (final Exception e)
			{
				LOG.error("Failed to place Order !!", e);
				orderData.setStatusDisplay(COULD_NOT_PLACE_ORDER);
			}
		}
		else
		{
			LOG.info("Error Occured !");
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}

		/*
		 * orderData.setCodCharges(priceDataFactory.create(PriceDataType.BUY,
		 * BigDecimal.valueOf(cmsSite.getCodCharges().doubleValue()), commonI18NService.getCurrentCurrency()));
		 */

		return orderData;
	}

	/**
	 * Gets the address data.
	 *
	 * @param request
	 *           the request
	 * @param isSameAsDeliveryAddress
	 *           the is same as delivery address
	 * @return the address data
	 */
	private AddressData getAddressData(final HttpServletRequest request, final boolean isSameAsDeliveryAddress)
	{
		AddressData addressData = null;
		if (!isSameAsDeliveryAddress)
		{
			addressData = new AddressData();
			final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");

			httpRequestAddressDataPopulator.populate(request, addressData);
			addressValidator.validate(addressData, errors);

			if (errors.hasErrors())
			{
				throw new WebserviceValidationException(errors);
			}
		}

		return addressData;
	}

	/**
	 * Gets the pay u payment facade.
	 *
	 * @return the payUPaymentFacade
	 */
	public final PAYUPaymentFacade getPayUPaymentFacade()
	{
		return payUPaymentFacade;
	}

	/**
	 * Web service handler for the product serviceable call.
	 *
	 * @param pinCode
	 * @return Serviceable Product List
	 */

	/*
	 * @Secured( { "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	 *
	 * @ResponseBody
	 *
	 * @RequestMapping(value = "/isserviceable/{pinCode}/{isoCode}", method = RequestMethod.GET) public
	 * ServiceableProductResponse isCartServiceable(@PathVariable final String pinCode, @PathVariable final String
	 * isoCode) { final ServiceableProductResponse serviceableProductResponse = new ServiceableProductResponse(); final
	 * CartData cartData = checkoutFacade.getCheckoutCart(); boolean isServiciable = true; for (final OrderEntryData
	 * orderEntry : cartData.getEntries()) { final ProductData productData = orderEntry.getProduct(); isServiciable =
	 * serviceabilityCheck(pinCode, productData.getCode()); if (!isServiciable) { break; } }
	 * serviceableProductResponse.setIsServiceable(isServiciable); return serviceableProductResponse; }
	 *
	 * private boolean serviceabilityCheck(final String pinCode, final String isoCode) {
	 *
	 * boolean serviceable = true;
	 *
	 * serviceable = v2ServiceabilityService.isProductServicableForPinCode(pinCode, isoCode);
	 *
	 * return serviceable;
	 *
	 * }
	 */

	/*
	 * protected DeliveryModeData convert(final DeliveryModeModel deliveryModeModel, final CartModel cartModel) { if
	 * (deliveryModeModel instanceof ZoneDeliveryModeModel) { final ZoneDeliveryModeModel zoneDeliveryModeModel =
	 * (ZoneDeliveryModeModel) deliveryModeModel; //final CartModel cartModel = getCart(); if (cartModel != null) { final
	 * ZoneDeliveryModeData zoneDeliveryModeData = getZoneDeliveryModeConverter().convert(zoneDeliveryModeModel); final
	 * PriceValue deliveryCost = getDeliveryService().getDeliveryCostForDeliveryModeAndAbstractOrder( deliveryModeModel,
	 * cartModel); if (deliveryCost != null) {
	 * zoneDeliveryModeData.setDeliveryCost(getPriceDataFactory().create(PriceDataType.BUY,
	 * BigDecimal.valueOf(deliveryCost.getValue()), deliveryCost.getCurrencyIso())); } return zoneDeliveryModeData; } }
	 * else { return getDeliveryModeConverter().convert(deliveryModeModel); }
	 *
	 * return null; }
	 */

	public CartData getPaymentModes(final CartData cartData)
	{

		final CMSSiteModel cmssite = (CMSSiteModel) baseSiteService.getCurrentBaseSite();
		final List<CartDeliveryOptionData> cartDeliveryOptionDataList = new ArrayList<CartDeliveryOptionData>();

		// final CartDeliveryOptionList cartDeliveryOptionList = new CartDeliveryOptionList();

		/*
		 * if (cartData.getTotalPrice().getValue().doubleValue() <
		 * cmsSite.getCodChargesApplicableThreshold().doubleValue()) {
		 * cartData.setIsCODChargesApplicable(Boolean.TRUE.booleanValue());
		 * cartData.setCodCharges(priceDataFactory.create(PriceDataType.BUY,
		 * BigDecimal.valueOf(cmssite.getCodCharges().doubleValue()), commonI18NService.getCurrentCurrency())); final
		 * double shopMoreForFreeCODCharges = cmssite.getCodChargesApplicableThreshold().doubleValue() -
		 * cartData.getTotalPrice().getValue().doubleValue();
		 * cartData.setShopMoreForFreeCOD(priceDataFactory.create(PriceDataType.BUY,
		 * BigDecimal.valueOf(shopMoreForFreeCODCharges), commonI18NService.getCurrentCurrency())); } else {
		 * cartData.setIsCODChargesApplicable(Boolean.FALSE.booleanValue()); }
		 */

		/*
		 * final CartDeliveryOptionData checkDeliveryOptionData = new CartDeliveryOptionData();
		 * checkDeliveryOptionData.setIsAvailable(getIsChequeFacilityApplicable(cartData, cmssite));
		 * checkDeliveryOptionData.setMode(getMessage("text.payment.mode.cheque")); final String chequeInfo =
		 * getMessage("text.payment.mode.chequeTerms"); checkDeliveryOptionData.setInfo(chequeInfo);
		 */

		final CartDeliveryOptionData codDeliveryOptionData = new CartDeliveryOptionData();
		codDeliveryOptionData.setIsAvailable(getIsCODFacilityApplicable(cartData, cmssite));
		codDeliveryOptionData.setMode(getMessage("text.payment.mode.cod"));
		final String codInfo = getMessage("text.payment.mode.codTerms");
		codDeliveryOptionData.setInfo(codInfo);

		final CartDeliveryOptionData onlineDeliveryOptionData = new CartDeliveryOptionData();
		onlineDeliveryOptionData.setIsAvailable(Boolean.TRUE);
		onlineDeliveryOptionData.setMode(getMessage("text.payment.mode.onlineBanking"));
		// onlineDeliveryOptionData.setInfo("TODO");

		/*
		 * final CartDeliveryOptionData emiDeliveryOptionData = new CartDeliveryOptionData();
		 * emiDeliveryOptionData.setIsAvailable(getIsEMIFacilityApplicable(cartData));
		 * emiDeliveryOptionData.setMode(getMessage("text.payment.mode.emi"));
		 */

		cartDeliveryOptionDataList.add(codDeliveryOptionData);
		cartDeliveryOptionDataList.add(onlineDeliveryOptionData);
		// cartDeliveryOptionList.setPaymentModes(cartDeliveryOptionDataList);
		//
		// cartData.setPaymmentMode(cartDeliveryOptionList);

		cartData.setPaymentModes(cartDeliveryOptionDataList);
		return cartData;

	}

	private Boolean getIsCODFacilityApplicable(final CartData cartData, final CMSSiteModel cmssite)
	{
		Boolean isCODApplicable = Boolean.FALSE;
		if (cartData != null && cmssite.getCodMinimunThreshold() != null && cmssite.getCodMaximumThreshold() != null
				&& null != cartData.getDeliveryAddress()
				&& v2ServiceabilityService.isCodAvailableForArea(cartData.getDeliveryAddress().getPostalCode()))
		{
			isCODApplicable = Boolean.TRUE;
		}
		return isCODApplicable;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ResponseBody
	@RequestMapping(value = "/payment-method/cod", method = RequestMethod.POST)
	public OrderData doHandleCODRequest(
			final HttpServletRequest request,
			@RequestParam(value = "isSameAsDeliveryAddress", required = true, defaultValue = "false") final boolean isSameAsDeliveryAddress,
			@RequestParam(value = "isUsingWallet", required = true, defaultValue = "false") final boolean isUsingWallet,
			@RequestParam(value = "phoneNumber", required = true, defaultValue = "") final String phoneNumber)
			throws InvalidCartException
	{
		if (isUsingWallet)
		{
			final CartData cartData = getCart(false, null, null, null, false);
			checkoutFacade.useMyWalletMoney(cartData, new AddressData());
		}
		OrderData orderData = null;
		/*
		 * try {
		 */

		final AddressData addressData = getAddressData(request, isSameAsDeliveryAddress);
		checkoutFacade.assignCodPaymentMode(addressData);
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		if ((null != phoneNumber) && (!phoneNumber.equals("")))
		{
			final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
			if (!phoneNumbers.contains(phoneNumber))
			{
				phoneNumbers.add(phoneNumber);
				currentCustomerData.setPhoneNumbers(phoneNumbers);
				customerFacade.updatePhoneNumbers(currentCustomerData, true);
			}
		}

		orderData = checkoutFacade.placeOrder();// placeOrder(request.getSession());
		/*
		 * } catch (final Exception e) { LOG.error("Failed to place Order", e); orderData = new OrderData();
		 * orderData.setStatus(OrderStatus.PROCESSING_ERROR); orderData.setStatusDisplay("Failed to place Order"); }
		 */

		final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();

		orderData.setCodCharges(priceDataFactory.create(PriceDataType.BUY,
				BigDecimal.valueOf(cmsSite.getCodCharges().doubleValue()),
				commonI18NService.getCurrentCurrency()));

		return orderData;
	}

	/**
	 * Check whether Product is in cart or not
	 *
	 * @param productCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public ProductInCartData cartCheck(@RequestParam(required = true) final String productCode)
	{
		final ProductInCartData productInCartData = new ProductInCartData();
		final List<OrderEntryData> cartEntries = cartFacade.getSessionCart().getEntries();
		if (CollectionUtils.isNotEmpty(cartEntries))
		{
			for (final OrderEntryData cartEntry : cartEntries)
			{
				if (cartEntry.getProduct().getCode().equals(productCode))
				{
					productInCartData.setProductInCart(Boolean.TRUE);
					return productInCartData;
				}
			}
		}
		productInCartData.setProductInCart(Boolean.FALSE);
		return productInCartData;
	}

	/**
	 * Updating cart if paying through wallet
	 *
	 * @param isUsingWallet
	 * @return
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ResponseBody
	@RequestMapping(value = "/add/wallet", method = RequestMethod.GET)
	public CartData isUsingWallet(@RequestParam(required = true) final boolean isUsingWallet)
	{
		final CartData cartData = getCart(false, null, null, null, false);
		if (isUsingWallet)
		{
			checkoutFacade.useMyWalletMoney(cartData, new AddressData());
			if (cartData.getTotalPayableBalance().getValue().compareTo(BigDecimal.ZERO) == 0)
			{
				for (int i = 0; i < cartData.getPaymentModes().size(); i++)
				{
					cartData.getPaymentModes().get(i).setIsAvailable(Boolean.FALSE);
				}
			}
			else
			{
				for (int i = 0; i < cartData.getPaymentModes().size(); i++)
				{
					if (cartData.getPaymentModes().get(i).getMode().equalsIgnoreCase("COD"))
					{
						cartData.getPaymentModes().get(i).setIsAvailable(Boolean.FALSE);
						break;
					}
				}
			}
		}
		return cartData;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(value = "/wallet/placeorders", method = RequestMethod.POST)
	@ResponseBody
	public OrderData placeWalletOrders(
			final HttpServletRequest request,
			@RequestParam(value = "isSameAsDeliveryAddress", required = true, defaultValue = "false") final boolean isSameAsDeliveryAddress,
			@RequestParam(value = "phoneNumber", required = true, defaultValue = "") final String phoneNumber)
	{
		final CartData cartData = getCart(false, null, null, null, false);

		checkoutFacade.useMyWalletMoney(cartData, new AddressData());
		OrderData orderData = new OrderData();
		try
		{

			final V2StoreCreditPaymentInfoData v2StoreCreditPaymentInfoData = cartData.getStoreCreditPaymentInfo();
			if (v2StoreCreditPaymentInfoData != null && cartData.getTotalPayableBalance().getValue().doubleValue() <= 0)
			{
				final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
				if ((null != phoneNumber) && (!phoneNumber.equals("")))
				{
					final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
					if (!phoneNumbers.contains(phoneNumber))
					{
						phoneNumbers.add(phoneNumber);
						currentCustomerData.setPhoneNumbers(phoneNumbers);
						customerFacade.updatePhoneNumbers(currentCustomerData, true);
					}
				}
				orderData = checkoutFacade.placeOrder();

			}
		}
		catch (final InvalidCartException e)
		{
			orderData.setStatusDisplay(COULD_NOT_PLACE_ORDER);
		}
		return orderData;
	}

	@RequestMapping(value = "/store-pickup", method = RequestMethod.POST)
	@ResponseBody
	public CartData storePickup(@RequestParam(value = "storeId") final String name)
	{
		final CustomerData user = customerFacade.getCurrentCustomer();
		final AddressData selectedAddress = storeFinderFacade.getPointOfServiceForName(name).getAddress();
		selectedAddress.setTitleCode(MS);
		selectedAddress.setFirstName(user.getFirstName());
		selectedAddress.setLastName(user.getLastName());
		userFacade.addAddress(selectedAddress);
		checkoutFacade.setDeliveryAddress(selectedAddress);
		checkoutFacade.changeCartDeliveryMode();
		final CartData cartData = getCart(false, null, null, null, false);
		return cartData;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ResponseBody
	@RequestMapping(value = "/cashOnDeliveryOtp", method = RequestMethod.GET)
	public void cashOnDeliveryOtp()
	{
		final CustomerModel currentCustomer = (CustomerModel) userService.getCurrentUser();
		getV2SmsService().sendSms(v2UserSmsDataMapPopulator.createV2UserOtp(), "User_otp_message_template",
				currentCustomer.getMobileNumber());
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ResponseBody
	@RequestMapping(value = "/checkCashOnDeliveryOtp", method = RequestMethod.POST)
	public OtpData checkCashOnDeliveryOtp(@RequestParam(value = "otpCode") final String otp)
	{
		final Map<String, String> results = getV2CashOnDeliveryOtp().autenticateOtp(otp);
		final OtpData otpData = new OtpData();
		otpData.setCurrentTime(results.get("currentTime"));
		otpData.setIsMatch(results.get("isMatch"));
		otpData.setValidTime(results.get("validTime"));
		otpData.setErrorMessage(results.get("errorMessage"));
		return otpData;

	}

	/**
	 * @param request
	 * @param isSameAsDeliveryAddress
	 * @param isUsingWallet
	 * @return
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(value = "/ebs/placeorders", method = RequestMethod.POST)
	@ResponseBody
	public OrderData placeEBSOrders(
			final HttpServletRequest request,
			@RequestParam(value = "isSameAsDeliveryAddress", required = true, defaultValue = "false") final boolean isSameAsDeliveryAddress,
			@RequestParam(value = "isUsingWallet", required = true, defaultValue = "false") final boolean isUsingWallet,
			@RequestParam(value = "phoneNumber", required = true, defaultValue = "") final String phoneNumber)
	{

		OrderData orderData = new OrderData();
		final AddressData addressData = getAddressData(request, isSameAsDeliveryAddress);

		// final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();

		getV2PaymentService().saveBillingAddress(addressData);
		final String guid = request.getParameter("guid");
		if (null == guid)
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}
		final Map<String, String> resultMap = getPayUPaymentFacade().getPaymentInfo(guid);
		if (resultMap.isEmpty())
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}
		final String result = resultMap.get(PAYU.ResponseParameters.STATUS.getValue());
		if (result != null && result.equalsIgnoreCase(EBSDEecisionEnum.SUCCESS.getValue()))
		{
			try
			{
				final CartModel cartModel = cartService.getSessionCart();
				final PaymentSubscriptionResultData paymentSubscriptionResultData = getEbsPaymentFacade().completeHopCreatePayment(
						resultMap, true, cartModel);

				if (paymentSubscriptionResultData.isSuccess())
				{
					if (isUsingWallet)
					{
						final CartData cartData = getCart(false, null, null, null, false);
						checkoutFacade.useMyWalletMoney(cartData, new AddressData());
					}
					final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
					if ((null != phoneNumber) && (!phoneNumber.equals("")))
					{
						final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
						if (!phoneNumbers.contains(phoneNumber))
						{
							phoneNumbers.add(phoneNumber);
							currentCustomerData.setPhoneNumbers(phoneNumbers);
							customerFacade.updatePhoneNumbers(currentCustomerData, true);
						}
					}
					orderData = checkoutFacade.placeOrder();
				}
			}
			catch (final Exception e)
			{
				orderData.setStatusDisplay(COULD_NOT_PLACE_ORDER);
			}
		}
		else
		{
			orderData.setStatusDisplay(COULD_NOT_PROCESS_PAYMENT);
		}

		/*
		 * orderData.setCodCharges(priceDataFactory.create(PriceDataType.BUY,
		 * BigDecimal.valueOf(cmsSite.getCodCharges().doubleValue()), commonI18NService.getCurrentCurrency()));
		 */

		return orderData;
	}

	private long getQtyWillAdd(final String code, final long qty)
	{
		final List<OrderEntryData> entries = cartFacade.getSessionCart().getEntries();
		long qtyWillAdd = qty;
		if (entries != null)
		{
			for (final OrderEntryData entry : entries)
			{
				if (entry.getProduct().getCode().equals(code))
				{
					final Long entryQuantity = entry.getQuantity();
					if (entryQuantity >= 10)
					{
						qtyWillAdd = 0;
					}
					else if (qtyWillAdd + entryQuantity > 10)
					{
						qtyWillAdd = 10 - entryQuantity;
					}
				}
			}
		}
		return qtyWillAdd;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ResponseBody
	@RequestMapping(value = "/verifyPhoneNumber", method = RequestMethod.POST)
	public void doHandleCODRequest(
			final HttpServletRequest request,
			@RequestParam(value = "phoneNumber", required = true, defaultValue = "") final String phoneNumber)

	{
		/* final PhoneNumberVerifiactionStatus verificationStatus = new PhoneNumberVerifiactionStatus(); */
		try
		{
			final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
			if ((null != phoneNumber) && (!phoneNumber.equals("")))
			{
				final List<String> phoneNumbers = currentCustomerData.getPhoneNumbers();
				if (!phoneNumbers.contains(phoneNumber))
				{
					phoneNumbers.add(phoneNumber);
					currentCustomerData.setPhoneNumbers(phoneNumbers);
					customerFacade.updatePhoneNumbers(currentCustomerData, true);
				}
			}
			/*
			 * verificationStatus.setMessage(""); verificationStatus.setStatus(Boolean.TRUE);
			 */
		}
		catch (final ModelSavingException e)
		{
			throw new ModelSavingException("Please fill the correct phone number");
			/*
			 * verificationStatus.setMessage("Please fill the correct phone number.");
			 * verificationStatus.setStatus(Boolean.TRUE);
			 */
		}
		/* return verificationStatus; */


	}
}
