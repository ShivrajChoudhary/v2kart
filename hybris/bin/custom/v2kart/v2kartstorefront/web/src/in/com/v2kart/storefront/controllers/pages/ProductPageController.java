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

import in.com.v2kart.core.email.V2HtmlMailSender;
import in.com.v2kart.core.enums.V2NotifyCustomerTypeEnum;
import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;
import in.com.v2kart.core.process.email.context.V2CustomerNotificationEmailContext;
import in.com.v2kart.core.serviceability.V2ServiceabilityService;
import in.com.v2kart.facades.core.data.V2CustomerNotificationData;
import in.com.v2kart.facades.customer.V2CustomerFacade;
import in.com.v2kart.facades.notifycustomer.V2NotifyCustomerFacade;
import in.com.v2kart.facades.wishlist.WishlistFacade;
import in.com.v2kart.storefront.breadcurmb.V2ProductBreadcrumbBuilder;
import in.com.v2kart.storefront.controllers.ControllerConstants;
import in.com.v2kart.storefront.forms.V2NotifyCustomerForm;
import in.com.v2kart.storefront.forms.validation.V2NotifyCustomerValidator;
import in.com.v2kart.storefront.util.MetaSanitizerUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.storefront.data.MetaElementData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ReviewForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ReviewValidator;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.acceleratorstorefrontcommons.variants.VariantSortStrategy;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * Controller for product details page
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/**/p")
public class ProductPageController extends AbstractPageController {
	private static final Logger LOG = Logger
			.getLogger(ProductPageController.class);

	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a
	 * Uri value is incorrectly extracted if it contains on or more '.'
	 * characters. Please see https://jira.springsource.org/browse/SPR-6164 for
	 * a discussion on the issue and future resolution.
	 */
	private static final String PRODUCT_CODE_PATH_VARIABLE_PATTERN = "/{productCode:.*}";
	private static final String REVIEWS_PATH_VARIABLE_PATTERN = "{numberOfReviews:.*}";

	@Resource(name = "productModelUrlResolver")
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "v2productBreadcrumbBuilder")
	private V2ProductBreadcrumbBuilder productBreadcrumbBuilder;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "variantSortStrategy")
	private VariantSortStrategy variantSortStrategy;

	@Resource(name = "reviewValidator")
	private ReviewValidator reviewValidator;

	@Resource(name = "cartFacade")
	private DefaultCartFacade cartFacade;

	@Resource(name = "wishlistFacade")
	private WishlistFacade wishlistFacade;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "v2NotifyCustomerFacade")
	private V2NotifyCustomerFacade v2NotifyCustomerFacade;

	@Resource(name = "notifyCustomerValidator")
	private V2NotifyCustomerValidator notifyCustomerValidator;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "mediaService")
	private MediaService mediaService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource(name = "htmlMailSender")
	private V2HtmlMailSender v2HtmlMailSender;

	@Resource(name = "serviceabilityService")
	private V2ServiceabilityService v2ServiceabilityService;

	@Resource(name = "customerFacade")
	protected V2CustomerFacade customerFacade;

	@Resource(name = "modelService")
	ModelService modelService;
//	@Autowired
//	V2SizeProfileFacade v2SizeProfileFacade;

	/**
	 * @return the wishlistFacade
	 */
	public WishlistFacade getWishlistFacade() {
		return wishlistFacade;
	}

	/**
	 * @param wishlistFacade
	 *            the wishlistFacade to set
	 */
	public void setWishlistFacade(final WishlistFacade wishlistFacade) {
		this.wishlistFacade = wishlistFacade;
	}

	private String websiteUrl;

	private static final String EMAIL_A_FRIEND_BODY = "Email_A_Friend_Body";

	private static final String APPLY_EMAIL_NOTIFY_ME_BODY = "Apply_For_Email_Notify_Me_Body";

	private static final String APPLY_EMAIL_NOTIFY_MY_PRICE_BODY = "Apply_For_Email_Notify_My_Price_Body";

	private static final String CSD_EMAIL_NOTIFY_ME_BODY = "CSD_Email_Notify_Me_Body";

	private static final String NOTIFY_ME_SUBJECT = "smtp.mail.notifyme.subject";

	private static final String NOTIFY_MY_PRICE_SUBJECT = "smtp.mail.notifymyprice.subject";

	private static final String ANONYMOUS_USER = "Anonymous user";

	private static final String USER = "User";

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String productDetail(
			@PathVariable("productCode") final String productCode,
			final Model model,
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam(value = "isAddedToWishlist", required = false) final String isAddedToWishlist,
			@RequestParam(value = "errorMsg", required = false) final String errorMsg)
			throws CMSItemNotFoundException, UnsupportedEncodingException {
		LOG.debug("************START***************");

		LOG.debug("Before processing product code is :"+productCode);
		ProductModel productModel = productService
				.getProductForCode(productCode);
		if (productModel != null) {
			LOG.debug("Product code is " + productModel.getCode());
		}
		// if size variant product doesn't have sizes, redirect style variant
		// product to size variant product.
		if (productModel instanceof V2kartStyleVariantProductModel) {
			List<VariantProductModel> sizeVariants = new ArrayList<VariantProductModel>(
					productModel.getVariants());
			LOG.debug("sizeVariants is :" + sizeVariants);
			if (CollectionUtils.isNotEmpty(sizeVariants)
					&& sizeVariants.size() == 1) {
				final ProductModel sizeVariantProduct = sizeVariants.get(0);
				if (sizeVariantProduct instanceof V2kartSizeVariantProductModel) {
					final String size = ((V2kartSizeVariantProductModel) sizeVariantProduct)
							.getSize();
					if (size.equalsIgnoreCase("NA")
							|| size.equalsIgnoreCase("N/A")||size.equalsIgnoreCase("A")) {
						productModel = sizeVariantProduct;
					}
				}
			}

		}
		final String redirection = checkRequestUrl(request, response,
				productModelUrlResolver.resolve(productModel));
		if (StringUtils.isNotEmpty(redirection)) {
		LOG.debug("redirection URL is :"+redirection);
			return redirection;
		}
		model.addAttribute("errorMsg", errorMsg);
		updatePageTitle(productModel, model);
		populateProductDetailForDisplay(productModel, model, request);
		model.addAttribute(new ReviewForm());
		final List<ProductReferenceData> productReferences = productFacade
				.getProductReferencesForCode(productCode, Arrays.asList(
						ProductReferenceTypeEnum.SIMILAR,
						ProductReferenceTypeEnum.ACCESSORIES), Arrays.asList(
						ProductOption.BASIC, ProductOption.PRICE), null);
		model.addAttribute("productReferences", productReferences);
		model.addAttribute("pageType", PageType.PRODUCT.name());
		model.addAttribute("isAddedToWishlist", isAddedToWishlist);
		final String metaKeywords = MetaSanitizerUtil
				.sanitizeKeywords(productModel.getKeywords());
		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(productModel.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);
		final List<MetaElementData> metadata = (List<MetaElementData>) model
				.asMap().get("metatags");
		final List<MediaContainerModel> mediaContainers = productModel
				.getGalleryImages();
		LOG.debug("MediaContainer is :" + mediaContainers);

		if (CollectionUtils.isNotEmpty(mediaContainers)) {
			try {
				final MediaModel media = mediaService.getMediaByFormat(
						mediaContainers.get(0),
						mediaService.getFormat("Product-300Wx300H"));
				metadata.add(createMetaElementProperty("og:image",
						media.getURL()));
				model.addAttribute("pinitMediaUrl", media.getURL());
			} catch (final ModelNotFoundException e) {
				LOG.debug("Product("
						+ productCode
						+ ") does not have media with format-'Product-300Wx300H'");
			}
		}

		LOG.debug("************END***************");
		return getViewForPage(model);
	}

	private MetaElementData createMetaElementProperty(final String property,
			final String content) {
		final MetaElementData element = new MetaElementData();
		element.setProperty(property);
		element.setContent(content.concat("?v=1"));
		return element;
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/zoomImages", method = RequestMethod.GET)
	public String showZoomImages(
			@PathVariable("productCode") final String productCode,
			@RequestParam(value = "galleryPosition", required = false) final String galleryPosition,
			final Model model) {
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		final ProductData productData = productFacade.getProductForOptions(
				productModel, Collections.singleton(ProductOption.GALLERY));
		final List<Map<String, ImageData>> images = getGalleryImages(productData);
		populateProductData(productData, model);
		if (galleryPosition != null) {
			try {
				model.addAttribute(
						"zoomImageUrl",
						images.get(Integer.parseInt(galleryPosition))
								.get("superZoom").getUrl());
			} catch (final IndexOutOfBoundsException | NumberFormatException ioebe) {
				model.addAttribute("zoomImageUrl", "");
			}
		}
		return ControllerConstants.Views.Fragments.Product.ZoomImagesPopup;
	}

	// Removed
	// ProductOption.REVIEW,ProductOption.SUMMARY,ProductOption.DELIVERY_MODE_AVAILABILITY,ProductOption.CATEGORIES
	// for Performance
	// Issue
	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/quickView", method = RequestMethod.GET)
	public String showQuickView(
			@PathVariable("productCode") final String productCode,
			final Model model, final HttpServletRequest request) {
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		final ProductData productData = productFacade.getProductForOptions(
				productModel, Arrays.asList(ProductOption.BASIC,
						ProductOption.PRICE, ProductOption.DESCRIPTION,
						ProductOption.PROMOTIONS, ProductOption.STOCK,
						ProductOption.VARIANT_FULL, ProductOption.GALLERY));

		sortVariantOptionData(productData);
		populateProductData(productData, model);
		getRequestContextData(request).setProduct(productModel);
		List<OrderEntryData> cartEntries = cartFacade.getSessionCart()
				.getEntries();
		if (CollectionUtils.isNotEmpty(cartEntries)) {
			for (OrderEntryData cartEntry : cartEntries) {
				if (cartEntry.getProduct().getCode().equals(productCode)) {
					model.addAttribute("cartContainsProduct", "true");
					return ControllerConstants.Views.Fragments.Product.QuickViewPopup;
				}
			}
		}
		return ControllerConstants.Views.Fragments.Product.QuickViewPopup;
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/review", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String postReview(@PathVariable final String productCode,
			final ReviewForm form, final BindingResult result,
			final Model model, final HttpServletRequest request,
			final RedirectAttributes redirectAttrs)
			throws CMSItemNotFoundException {
		getReviewValidator().validate(form, result);

		final ProductModel productModel = productService
				.getProductForCode(productCode);

		if (result.hasErrors()) {
			updatePageTitle(productModel, model);
			GlobalMessages.addErrorMessage(model, "review.general.error");
			model.addAttribute("reviewForm", form);
			model.addAttribute("showReviewForm", Boolean.TRUE);
			populateProductDetailForDisplay(productModel, model, request);
			storeCmsPageInModel(model, getPageForProduct(productModel));
			model.addAttribute("reviewFormHasError", Boolean.TRUE);
			return getViewForPage(model);
		}

		final ReviewData review = new ReviewData();
		review.setHeadline(XSSFilterUtil.filter(form.getHeadline()));
		review.setComment(XSSFilterUtil.filter(form.getComment()));
		review.setRating(form.getRating());
		final UserModel user = userService.getCurrentUser();
		if (form.getAlias() != null && !form.getAlias().isEmpty()) {
			review.setAlias(XSSFilterUtil.filter(form.getAlias()));
		} else {
			review.setAlias(XSSFilterUtil.filter(user.getDisplayName()));
		}
		productFacade.postReview(productCode, review);
		GlobalMessages.addFlashMessage(redirectAttrs,
				GlobalMessages.CONF_MESSAGES_HOLDER,
				"review.confirmation.thank.you.title");

		return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/reviewhtml/"
			+ REVIEWS_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String reviewHtml(
			@PathVariable("productCode") final String productCode,
			@PathVariable("numberOfReviews") final String numberOfReviews,
			final Model model, final HttpServletRequest request) {
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		final List<ReviewData> reviews;
		final ProductData productData = productFacade.getProductForOptions(
				productModel,
				Arrays.asList(ProductOption.BASIC, ProductOption.REVIEW));

		if ("all".equals(numberOfReviews)) {
			reviews = productFacade.getReviews(productCode);
		} else {
			final int reviewCount = Math.min(Integer.parseInt(numberOfReviews),
					(productData.getNumberOfReviews() == null ? 0 : productData
							.getNumberOfReviews().intValue()));
			reviews = productFacade.getReviews(productCode,
					Integer.valueOf(reviewCount));
		}

		getRequestContextData(request).setProduct(productModel);
		model.addAttribute("reviews", reviews);
		model.addAttribute("reviewsTotal", productData.getNumberOfReviews());
		model.addAttribute(new ReviewForm());

		return ControllerConstants.Views.Fragments.Product.ReviewsTab;
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/writeReview", method = RequestMethod.GET)
	public String writeReview(@PathVariable final String productCode,
			final Model model) throws CMSItemNotFoundException {
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		model.addAttribute(new ReviewForm());
		setUpReviewPage(model, productModel);
		return ControllerConstants.Views.Pages.Product.WriteReview;
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/emailFriend", method = RequestMethod.GET)
	public String emailFriend(@PathVariable final String productCode,
			final Model model) throws CMSItemNotFoundException {
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		final ProductData productData = productFacade.getProductForOptions(
				productModel, Arrays.asList(ProductOption.BASIC,
						ProductOption.PRICE, ProductOption.SUMMARY,
						ProductOption.DESCRIPTION, ProductOption.GALLERY,
						ProductOption.CATEGORIES, ProductOption.REVIEW,
						ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
						ProductOption.VARIANT_FULL, ProductOption.STOCK,
						ProductOption.VOLUME_PRICES,
						ProductOption.DELIVERY_MODE_AVAILABILITY));
		websiteUrl = Config.getParameter("website."
				+ cmsSiteService.getCurrentSite().getUid() + ".https");
		final UserModel user = userService.getCurrentUser();
		String currentUserEmailId = user.getUid();
		if (currentUserEmailId.equalsIgnoreCase("anonymous")) {
			currentUserEmailId = null;
		}
		String currentUserName;
		if (currentUserEmailId == null) {
			currentUserName = USER;
		} else {
			currentUserName = user.getDisplayName();
		}
		final V2NotifyCustomerForm v2NotifyCustomerForm = new V2NotifyCustomerForm();
		v2NotifyCustomerForm.setCurrentUserEmailId(currentUserEmailId);
		v2NotifyCustomerForm.setCurrentUserName(currentUserName);
		v2NotifyCustomerForm.setUrl(websiteUrl.concat(productData.getUrl()));
		v2NotifyCustomerForm.setName(productModel.getName());
		v2NotifyCustomerForm.setProductCode(productModel.getCode());
		try {
			final List<MediaContainerModel> mediaConatiners = productModel
					.getGalleryImages();
			if (CollectionUtils.isNotEmpty(mediaConatiners)) {
				final MediaModel media = mediaService.getMediaByFormat(
						productModel.getGalleryImages().get(0),
						mediaService.getFormat("96Wx96H"));
				v2NotifyCustomerForm.setMediaUrl(media.getURL());
			}
		} catch (final ModelNotFoundException e) {
			LOG.debug("Product(" + productModel.getCode()
					+ ") does not have media with format-'96Wx96H'");
		}
		final PriceData priceData = productData.getPrice();
		if (priceData != null) {
			v2NotifyCustomerForm.setProductPrice(productData.getPrice()
					.getFormattedValue());
		}
		model.addAttribute(v2NotifyCustomerForm);
		setUpReviewPage(model, productModel);
		return ControllerConstants.Views.Pages.Product.emailFriend;
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/notifymail", method = RequestMethod.GET)
	public String notifyMail(@PathVariable final String productCode,
			final Model model, @RequestParam String page)
			throws CMSItemNotFoundException {
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		final ProductData productData = productFacade.getProductForOptions(
				productModel, Arrays.asList(ProductOption.BASIC,
						ProductOption.PRICE, ProductOption.SUMMARY,
						ProductOption.DESCRIPTION, ProductOption.GALLERY,
						ProductOption.CATEGORIES, ProductOption.REVIEW,
						ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
						ProductOption.VARIANT_FULL, ProductOption.STOCK,
						ProductOption.VOLUME_PRICES,
						ProductOption.DELIVERY_MODE_AVAILABILITY));
		websiteUrl = Config.getParameter("website."
				+ cmsSiteService.getCurrentSite().getUid() + ".https");
		final UserModel user = userService.getCurrentUser();
		String currentUserEmailId = user.getUid();
		if (currentUserEmailId.equalsIgnoreCase("anonymous")) {
			currentUserEmailId = null;
		}
		String currentUserName;
		if (currentUserEmailId == null) {
			currentUserName = USER;
		} else {
			currentUserName = user.getDisplayName();
		}
		final V2NotifyCustomerForm v2NotifyCustomerForm = new V2NotifyCustomerForm();
		v2NotifyCustomerForm.setCurrentUserEmailId(currentUserEmailId);
		v2NotifyCustomerForm.setCurrentUserName(currentUserName);
		v2NotifyCustomerForm.setUrl(websiteUrl.concat(productData.getUrl()));
		v2NotifyCustomerForm.setName(productModel.getName());
		v2NotifyCustomerForm.setProductCode(productModel.getCode());
		try {
			final List<MediaContainerModel> mediaConatiners = productModel
					.getGalleryImages();
			if (CollectionUtils.isNotEmpty(mediaConatiners)) {
				final MediaModel media = mediaService.getMediaByFormat(
						productModel.getGalleryImages().get(0),
						mediaService.getFormat("96Wx96H"));
				v2NotifyCustomerForm.setMediaUrl(media.getURL());
			}
		} catch (final ModelNotFoundException e) {
			LOG.debug("Product(" + productModel.getCode()
					+ ") does not have media with format-'96Wx96H'");
		}
		final PriceData priceData = productData.getPrice();
		if (priceData != null) {
			v2NotifyCustomerForm.setProductPrice(productData.getPrice()
					.getFormattedValue());
		}
		model.addAttribute(v2NotifyCustomerForm);
		model.addAttribute("page", page);
		setUpReviewPage(model, productModel);
		return ControllerConstants.Views.Pages.Product.notifyme;
	}

	protected void setUpReviewPage(final Model model,
			final ProductModel productModel) throws CMSItemNotFoundException {
		final String metaKeywords = MetaSanitizerUtil
				.sanitizeKeywords(productModel.getKeywords());
		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(productModel.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);
		storeCmsPageInModel(model, getPageForProduct(productModel));
		model.addAttribute(
				"product",
				productFacade.getProductForOptions(productModel,
						Arrays.asList(ProductOption.BASIC)));
		updatePageTitle(productModel, model);
	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/writeReview", method = RequestMethod.POST)
	public String writeReview(@PathVariable final String productCode,
			final ReviewForm form, final BindingResult result,
			final Model model, final HttpServletRequest request,
			final RedirectAttributes redirectAttrs)
			throws CMSItemNotFoundException {
		getReviewValidator().validate(form, result);

		final ProductModel productModel = productService
				.getProductForCode(productCode);

		if (result.hasErrors()) {
			GlobalMessages.addErrorMessage(model, "review.general.error");
			populateProductDetailForDisplay(productModel, model, request);
			setUpReviewPage(model, productModel);
			return ControllerConstants.Views.Pages.Product.WriteReview;
		}

		final ReviewData review = new ReviewData();
		review.setHeadline(XSSFilterUtil.filter(form.getHeadline()));
		review.setComment(XSSFilterUtil.filter(form.getComment()));
		review.setRating(form.getRating());
		review.setAlias(XSSFilterUtil.filter(form.getAlias()));
		productFacade.postReview(productCode, review);
		GlobalMessages.addFlashMessage(redirectAttrs,
				GlobalMessages.CONF_MESSAGES_HOLDER,
				"review.confirmation.thank.you.title");

		return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
	}

	@ExceptionHandler(UnknownIdentifierException.class)
	public String handleUnknownIdentifierException(
			final UnknownIdentifierException exception,
			final HttpServletRequest request) {
		request.setAttribute("message", exception.getMessage());
		LOG.debug("Issue of UnknownIdentifierException : "
				+ exception.getMessage());
		return FORWARD_PREFIX + "/404";
	}

	protected void updatePageTitle(final ProductModel productModel,
			final Model model) {
		storeContentPageTitleInModel(model, getPageTitleResolver()
				.resolveProductPageTitle(productModel));
	}

	protected void populateProductDetailForDisplay(
			final ProductModel productModel, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException {
		getRequestContextData(request).setProduct(productModel);

		final ProductData productData = productFacade.getProductForOptions(
				productModel, Arrays.asList(ProductOption.BASIC,
						ProductOption.PRICE, ProductOption.SUMMARY,
						ProductOption.DESCRIPTION, ProductOption.GALLERY,
						ProductOption.CATEGORIES, ProductOption.REVIEW,
						ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
						ProductOption.VARIANT_FULL, ProductOption.STOCK,
						ProductOption.VOLUME_PRICES,
						ProductOption.DELIVERY_MODE_AVAILABILITY));
		LOG.debug("productData in populateProductDetailForDisplay "
				+ productData);
		websiteUrl = Config.getParameter("website."
				+ cmsSiteService.getCurrentSite().getUid() + ".https");
		sortVariantOptionData(productData);
		storeCmsPageInModel(model, getPageForProduct(productModel));
		populateProductData(productData, model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				productBreadcrumbBuilder.getBreadcrumbs(productModel));

		final UserModel user = userService.getCurrentUser();
		// regards size profile
		//populateSizeProfile(productModel, model, user);
		if (user != null)
			LOG.debug("User is" + user.getName());
		String currentUserEmailId = user.getUid();
		if (currentUserEmailId.equalsIgnoreCase("anonymous")) {
			currentUserEmailId = null;
		}
		String currentUserName;
		if (currentUserEmailId == null) {
			currentUserName = USER;
		} else {
			currentUserName = user.getDisplayName();
		}

		final V2NotifyCustomerForm v2NotifyCustomerForm = new V2NotifyCustomerForm();
		v2NotifyCustomerForm.setCurrentUserEmailId(currentUserEmailId);
		v2NotifyCustomerForm.setCurrentUserName(currentUserName);
		v2NotifyCustomerForm.setUrl(websiteUrl.concat(productData.getUrl()));
		v2NotifyCustomerForm.setName(productModel.getName());
		v2NotifyCustomerForm.setProductCode(productModel.getCode());
		try {
			final List<MediaContainerModel> mediaConatiners = productModel
					.getGalleryImages();
			if (CollectionUtils.isNotEmpty(mediaConatiners)) {
				LOG.debug("mediaConatiners in populateProductDetailForDisplay is :"
						+ mediaConatiners);
				final MediaModel media = mediaService.getMediaByFormat(
						productModel.getGalleryImages().get(0),
						mediaService.getFormat("Product-96Wx96H"));
				v2NotifyCustomerForm.setMediaUrl(media.getURL());
			}
		} catch (final ModelNotFoundException e) {
			LOG.debug("Product(" + productModel.getCode()
					+ ") does not have media with format-'96Wx96H'");
		}
		final PriceData priceData = productData.getPrice();
		if (priceData != null) {
			v2NotifyCustomerForm.setProductPrice(productData.getPrice()
					.getFormattedValue());
		}
		model.addAttribute("v2NotifyCustomerForm", v2NotifyCustomerForm);
		if (null != productData.getClassifications()) {
			for (ClassificationData classification : productData
					.getClassifications()) {
				if (null != classification.getCode()
						&& classification.getCode().equalsIgnoreCase(
								"size_dimensions_classification_class")) {
					model.addAttribute("showSizeguide", "true");
				}
			}
		}

	}

	protected void populateProductData(final ProductData productData,
			final Model model) {
		model.addAttribute("galleryImages", getGalleryImages(productData));
		model.addAttribute("product", productData);
	}

	protected void sortVariantOptionData(final ProductData productData) {
		if (CollectionUtils.isNotEmpty(productData.getBaseOptions())) {
			for (final BaseOptionData baseOptionData : productData
					.getBaseOptions()) {
				if (CollectionUtils.isNotEmpty(baseOptionData.getOptions())) {
					Collections.sort(baseOptionData.getOptions(),
							variantSortStrategy);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(productData.getVariantOptions())) {
			Collections.sort(productData.getVariantOptions(),
					variantSortStrategy);
		}
	}

	protected List<Map<String, ImageData>> getGalleryImages(
			final ProductData productData) {
		final List<Map<String, ImageData>> galleryImages = new ArrayList<>();
		LOG.debug("galleryImages is" + productData.getImages());
		if (CollectionUtils.isNotEmpty(productData.getImages())) {
			final List<ImageData> images = new ArrayList<>();
			for (final ImageData image : productData.getImages()) {
				if (ImageDataType.GALLERY.equals(image.getImageType())) {
					LOG.debug("galleryImage available format :"
							+ image.getFormat());
					images.add(image);
				}
			}
			Collections.sort(images, new Comparator<ImageData>() {
				@Override
				public int compare(final ImageData image1,
						final ImageData image2) {
					return image1.getGalleryIndex().compareTo(
							image2.getGalleryIndex());
				}
			});

			if (CollectionUtils.isNotEmpty(images)) {

				int currentIndex = images.get(0).getGalleryIndex().intValue();
				Map<String, ImageData> formats = new HashMap<String, ImageData>();
				for (final ImageData image : images) {
					if (currentIndex != image.getGalleryIndex().intValue()) {
						galleryImages.add(formats);
						formats = new HashMap<>();
						currentIndex = image.getGalleryIndex().intValue();
					}
					formats.put(image.getFormat(), image);
				}
				if (!formats.isEmpty()) {
					galleryImages.add(formats);
				}
			}
		}
		return galleryImages;
	}

	@RequestMapping(value = "/add-to-wishlist", method = RequestMethod.GET)
	@RequireHardLogIn
	public @ResponseBody String addWishlistItem(
			@RequestParam(value = "productCode", required = true) final String productCode,
			@RequestParam(value = "entryNumber", required = false) final String entryNumber)
			throws CommerceCartModificationException {
		Boolean isSuccess = Boolean.FALSE;

		if (StringUtils.isNotBlank(productCode)) {
			wishlistFacade.addWishlistEntry(productCode);
			isSuccess = Boolean.TRUE;
		}
		if (!(null == entryNumber)) {
			final long l = Long.parseLong(entryNumber);
			cartFacade.updateCartEntry(l, 0);
		}

		return isSuccess.toString();
	}

	protected ReviewValidator getReviewValidator() {
		return reviewValidator;
	}

	protected AbstractPageModel getPageForProduct(final ProductModel product)
			throws CMSItemNotFoundException {
		return cmsPageService.getPageForProduct(product);
	}

	@ResponseBody
	@RequestMapping(value = "/notifyCustomer", method = RequestMethod.POST)
	private ValidationResponse notifyCustomer(
			final HttpServletRequest request,
			final Model model,
			@Valid @ModelAttribute("v2NotifyCustomerForm") final V2NotifyCustomerForm v2NotifyCustomerForm,
			@RequestParam(value = "type", required = true) final String notificationType,
			@RequestParam(value = "productCode", required = true) final String productCode,
			final BindingResult bindingResult) {
		final ValidationResponse response = new ValidationResponse();
		v2NotifyCustomerForm.setProductCode(productCode);
		notifyCustomerValidator.validate(v2NotifyCustomerForm, bindingResult);

		if (bindingResult.hasErrors()) {
			response.setStatus("FAIL");
			final List<FieldError> allErrors = bindingResult.getFieldErrors();
			final List<ErrorMessage> errorMesages = new ArrayList<ErrorMessage>();
			for (final FieldError objectError : allErrors) {
				errorMesages.add(new ErrorMessage(objectError.getField(),
						objectError.getCode()));
			}
			response.setErrors(errorMesages);
			return response;
		}

		final V2CustomerNotificationData notificationData = new V2CustomerNotificationData();
		notificationData.setProductCode(v2NotifyCustomerForm.getProductCode());
		notificationData.setType(notificationType);
		if (V2NotifyCustomerTypeEnum.NOTIFY_ME.toString().equals(
				notificationType)) {
			notificationData.setNotificationPrice(Double.valueOf(0));
		} else {
			notificationData.setNotificationPrice(Double.valueOf(
					v2NotifyCustomerForm.getNotificationPrice()).doubleValue());
		}
		notificationData.setEmailId(v2NotifyCustomerForm
				.getCurrentUserEmailId());
		notificationData.setCurrentUserName(v2NotifyCustomerForm
				.getCurrentUserName());
		notificationData.setName(v2NotifyCustomerForm.getName());
		notificationData.setSite(cmsSiteService.getCurrentSite().getUid());
		v2NotifyCustomerFacade.notifyCustomer(notificationData);

		if (V2NotifyCustomerTypeEnum.NOTIFY_ME.toString().equalsIgnoreCase(
				notificationData.getType())) {
			final String notifyMeSubject = siteConfigService
					.getProperty(NOTIFY_ME_SUBJECT);
			// mail to the customer requesting for notify for out of stock
			// product
			sendMail(APPLY_EMAIL_NOTIFY_ME_BODY, notificationData, model,
					response, notifyMeSubject);
			// set the CSD email ID
			final String CSDNotifyMeEmailId = siteConfigService
					.getProperty("website.csd.email.address");
			if (CSDNotifyMeEmailId != null
					|| StringUtils.isNotEmpty(CSDNotifyMeEmailId)) {
				notificationData.setEmailId(CSDNotifyMeEmailId);
				// set the current user email ID for CSD to identify
				notificationData.setCurrentUserEmailId(v2NotifyCustomerForm
						.getCurrentUserEmailId());
				// mail to the CSD(V2) to keep track of customer request
				sendMail(CSD_EMAIL_NOTIFY_ME_BODY, notificationData, model,
						response, notifyMeSubject);
			}
		} else {
			final String notifyMyPriceSubject = siteConfigService
					.getProperty(NOTIFY_MY_PRICE_SUBJECT);
			sendMail(APPLY_EMAIL_NOTIFY_MY_PRICE_BODY, notificationData, model,
					response, notifyMyPriceSubject);
		}

		return response;
	}

	private void sendMail(final String templateCode,
			final V2CustomerNotificationData notificationData,
			final Model model, final ValidationResponse response,
			final String mailSubject) {

		final VelocityContext notifyCustomerEmailContext = new V2CustomerNotificationEmailContext(
				notificationData);
		final String to = notificationData.getEmailId();
		try {
			v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(
					notifyCustomerEmailContext, templateCode, mailSubject, to));
			GlobalMessages.addConfMessage(model, "success");
		} catch (final EmailException e) {
			LOG.info("Failed to send email a friend mail.", e);
			response.setStatus("UNABLE"); // if mail not sent successfully show
											// error message
			GlobalMessages.addConfMessage(model, "error");
		}

	}

	private void sendMailMobile(final String templateCode,
			final V2CustomerNotificationData notificationData,
			final Model model, final String mailSubject,
			final RedirectAttributes redirectAttrs,
			final String successpopupcontent, final String failurepopupcontent) {

		final VelocityContext notifyCustomerEmailContext = new V2CustomerNotificationEmailContext(
				notificationData);
		final String to = notificationData.getEmailId();
		try {
			v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(
					notifyCustomerEmailContext, templateCode, mailSubject, to));
			GlobalMessages.addFlashMessage(redirectAttrs,
					GlobalMessages.CONF_MESSAGES_HOLDER, successpopupcontent);
		} catch (final EmailException e) {
			LOG.info("Failed to send email a friend mail.", e); // if mail not
																// sent
																// successfully
																// show error
																// message
			GlobalMessages.addFlashMessage(redirectAttrs,
					GlobalMessages.ERROR_MESSAGES_HOLDER, failurepopupcontent);
		}

	}

	@RequestMapping(value = "/emailAFriend", method = RequestMethod.POST)
	private @ResponseBody ValidationResponse emailAFriend(
			final HttpServletRequest request,
			final Model model,
			@Valid @ModelAttribute("v2NotifyCustomerForm") final V2NotifyCustomerForm v2NotifyCustomerForm,
			final BindingResult bindingResult) {
		websiteUrl = Config.getParameter("website."
				+ cmsSiteService.getCurrentSite().getUid() + ".https");
		final ValidationResponse response = new ValidationResponse();
		notifyCustomerValidator.validate(v2NotifyCustomerForm, bindingResult);

		if (bindingResult.hasErrors()) {
			response.setStatus("FAIL");
			final List<FieldError> allErrors = bindingResult.getFieldErrors();
			final List<ErrorMessage> errorMesages = new ArrayList<ErrorMessage>();
			for (final FieldError objectError : allErrors) {
				errorMesages.add(new ErrorMessage(objectError.getField(),
						objectError.getCode()));
			}
			response.setErrors(errorMesages);
			return response;
		}

		final V2CustomerNotificationData customerNotificationData = new V2CustomerNotificationData();
		customerNotificationData.setEmailId(v2NotifyCustomerForm.getEmailId());
		ProductModel productModel = productService
				.getProductForCode(v2NotifyCustomerForm.getProductCode());
		if (productModel != null) {
			try {
				final List<MediaContainerModel> mediaConatiners = productModel
						.getGalleryImages();
				if (CollectionUtils.isNotEmpty(mediaConatiners)) {
					final MediaModel media = mediaService.getMediaByFormat(
							productModel.getGalleryImages().get(0),
							mediaService.getFormat("Product-92Wx115H"));
					v2NotifyCustomerForm.setMediaUrl(media.getURL());
				}
			} catch (final ModelNotFoundException e) {
				LOG.debug("Product(" + productModel.getCode()
						+ ") does not have media with format-'96Wx96H'");
			}
		}
		customerNotificationData.setUrl(v2NotifyCustomerForm.getUrl());
		customerNotificationData.setProductCode(v2NotifyCustomerForm
				.getProductCode());
		customerNotificationData.setMessage(v2NotifyCustomerForm.getMessage());
		customerNotificationData.setName(v2NotifyCustomerForm.getName());
		customerNotificationData.setSite(cmsSiteService.getCurrentSite()
				.getUid());
		if (v2NotifyCustomerForm.getMediaUrl() != null
				&& v2NotifyCustomerForm.getMediaUrl().contains("http")) {
			customerNotificationData.setMediaUrl(v2NotifyCustomerForm
					.getMediaUrl());
		} else {
			customerNotificationData.setMediaUrl(websiteUrl
					.concat(v2NotifyCustomerForm.getMediaUrl()));
		}
		final String productPrice = v2NotifyCustomerForm.getProductPrice();
		customerNotificationData.setProductPrice(productPrice.substring(1,
				productPrice.length()));
		final String currentUserEmailId = v2NotifyCustomerForm
				.getCurrentUserEmailId();
		if (currentUserEmailId == null) {
			customerNotificationData.setCurrentUserEmailId(ANONYMOUS_USER);
		} else {
			customerNotificationData.setCurrentUserEmailId(v2NotifyCustomerForm
					.getCurrentUserEmailId());
		}
		customerNotificationData.setCurrentUserName(v2NotifyCustomerForm
				.getCurrentUserName());

		sendMail(EMAIL_A_FRIEND_BODY, customerNotificationData, model,
				response, customerNotificationData.getName());

		return response;

	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/emailFriend", method = RequestMethod.POST)
	private String emailFriendResponse(
			@PathVariable final String productCode,
			final HttpServletRequest request,
			final Model model,
			@Valid @ModelAttribute("v2NotifyCustomerForm") final V2NotifyCustomerForm v2NotifyCustomerForm,
			final BindingResult bindingResult,
			final RedirectAttributes redirectAttr)
			throws CMSItemNotFoundException {
		websiteUrl = Config.getParameter("website."
				+ cmsSiteService.getCurrentSite().getUid() + ".https");
		notifyCustomerValidator.validate(v2NotifyCustomerForm, bindingResult);
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		if (bindingResult.hasErrors()) {
			GlobalMessages.addErrorMessage(model, "mobile.mail.general.error");
			setUpReviewPage(model, productModel);
			model.addAttribute(v2NotifyCustomerForm);
			return ControllerConstants.Views.Pages.Product.emailFriend;
		}

		final V2CustomerNotificationData customerNotificationData = new V2CustomerNotificationData();
		customerNotificationData.setEmailId(v2NotifyCustomerForm.getEmailId());
		customerNotificationData.setUrl(v2NotifyCustomerForm.getUrl());
		customerNotificationData.setProductCode(v2NotifyCustomerForm
				.getProductCode());
		customerNotificationData.setMessage(v2NotifyCustomerForm.getMessage());
		customerNotificationData.setName(v2NotifyCustomerForm.getName());
		customerNotificationData.setSite(cmsSiteService.getCurrentSite()
				.getUid());
		if (v2NotifyCustomerForm.getMediaUrl() != null
				&& v2NotifyCustomerForm.getMediaUrl().contains("http")) {
			customerNotificationData.setMediaUrl(v2NotifyCustomerForm
					.getMediaUrl());
		} else {
			customerNotificationData.setMediaUrl(websiteUrl
					.concat(v2NotifyCustomerForm.getMediaUrl()));
		}
		final String productPrice = v2NotifyCustomerForm.getProductPrice();
		customerNotificationData.setProductPrice(productPrice.substring(1,
				productPrice.length()));
		final String currentUserEmailId = v2NotifyCustomerForm
				.getCurrentUserEmailId();
		if (currentUserEmailId == null) {
			customerNotificationData.setCurrentUserEmailId(ANONYMOUS_USER);
		} else {
			customerNotificationData.setCurrentUserEmailId(v2NotifyCustomerForm
					.getCurrentUserEmailId());
		}
		customerNotificationData.setCurrentUserName(v2NotifyCustomerForm
				.getCurrentUserName());
		String successmessage = "mobile.email.confirmation.thank.you.title";
		String failuremessage = "mobile.email.confirmation.failure.title";
		sendMailMobile(EMAIL_A_FRIEND_BODY, customerNotificationData, model,
				customerNotificationData.getName(), redirectAttr,
				successmessage, failuremessage);

		return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);

	}

	@RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/notifymail", method = RequestMethod.POST)
	private String notifymailresponse(
			@PathVariable final String productCode,
			final HttpServletRequest request,
			final Model model,
			@Valid @ModelAttribute("v2NotifyCustomerForm") final V2NotifyCustomerForm v2NotifyCustomerForm,
			final BindingResult bindingResult,
			final RedirectAttributes redirectAttr,
			@RequestParam(value = "type", required = true) final String notificationType)
			throws CMSItemNotFoundException {
		v2NotifyCustomerForm.setProductCode(productCode);
		notifyCustomerValidator.validate(v2NotifyCustomerForm, bindingResult);
		final ProductModel productModel = productService
				.getProductForCode(productCode);
		String prevpage = request.getParameter("page");
		if (bindingResult.hasErrors()) {
			GlobalMessages.addErrorMessage(model, "mobile.mail.notify.error");
			setUpReviewPage(model, productModel);
			model.addAttribute("page", prevpage);
			model.addAttribute(v2NotifyCustomerForm);
			return ControllerConstants.Views.Pages.Product.notifyme;
		}

		final V2CustomerNotificationData notificationData = new V2CustomerNotificationData();
		notificationData.setProductCode(v2NotifyCustomerForm.getProductCode());
		notificationData.setType(notificationType);
		if (V2NotifyCustomerTypeEnum.NOTIFY_ME.toString().equals(
				notificationType)) {
			notificationData.setNotificationPrice(Double.valueOf(0));
		} else {
			notificationData.setNotificationPrice(Double.valueOf(
					v2NotifyCustomerForm.getNotificationPrice()).doubleValue());
		}
		notificationData.setEmailId(v2NotifyCustomerForm
				.getCurrentUserEmailId());
		notificationData.setCurrentUserName(v2NotifyCustomerForm
				.getCurrentUserName());
		notificationData.setName(v2NotifyCustomerForm.getName());
		notificationData.setSite(cmsSiteService.getCurrentSite().getUid());
		v2NotifyCustomerFacade.notifyCustomer(notificationData);
		String successmessage = "mobile.email.notify.thank.you.title";
		String failuremessage = "mobile.email.notify.failure.title";
		if (V2NotifyCustomerTypeEnum.NOTIFY_ME.toString().equalsIgnoreCase(
				notificationData.getType())) {
			final String notifyMeSubject = siteConfigService
					.getProperty(NOTIFY_ME_SUBJECT);
			// mail to the customer requesting for notify for out of stock
			// product
			sendMailMobile(APPLY_EMAIL_NOTIFY_ME_BODY, notificationData, model,
					notifyMeSubject, redirectAttr, successmessage,
					failuremessage);
			// set the CSD email ID
			final String CSDNotifyMeEmailId = siteConfigService
					.getProperty("website.csd.email.address");
			if (CSDNotifyMeEmailId != null
					|| StringUtils.isNotEmpty(CSDNotifyMeEmailId)) {
				notificationData.setEmailId(CSDNotifyMeEmailId);
				// set the current user email ID for CSD to identify
				notificationData.setCurrentUserEmailId(v2NotifyCustomerForm
						.getCurrentUserEmailId());
				// mail to the CSD(V2) to keep track of customer request
				sendMailMobile(CSD_EMAIL_NOTIFY_ME_BODY, notificationData,
						model, notifyMeSubject, redirectAttr, successmessage,
						failuremessage);
			}
		} else {
			final String notifyMyPriceSubject = siteConfigService
					.getProperty(NOTIFY_MY_PRICE_SUBJECT);
			sendMailMobile(APPLY_EMAIL_NOTIFY_MY_PRICE_BODY, notificationData,
					model, notifyMyPriceSubject, redirectAttr, successmessage,
					failuremessage);
		}
		if (prevpage.equalsIgnoreCase("1")) {
			return REDIRECT_PREFIX + "/my-account/wishlist";
		} else {
			return REDIRECT_PREFIX
					+ productModelUrlResolver.resolve(productModel);
		}

	}

	public static class ValidationResponse {
		private String status = "SUCCESS";
		private List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

		public String getStatus() {
			return status;
		}

		public void setStatus(final String status) {
			this.status = status;
		}

		public List<ErrorMessage> getErrors() {
			return errors;
		}

		public void setErrors(final List<ErrorMessage> errors) {
			this.errors = errors;
		}
	}

	public static class ErrorMessage {

		private final String fieldName;
		private final String message;

		public ErrorMessage(final String fieldName, final String message) {
			this.fieldName = fieldName;
			this.message = message;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getMessage() {
			return message;
		}

	}

	@ResponseBody
	@RequestMapping(value = "/getServiceableCheck", method = RequestMethod.GET)
	public Map<String, String> getServiceableCheck(final Model model,
			@RequestParam(value = "pinCode") final String pinCode) {

		final Map<String, String> results = new HashMap<>();
		final boolean isServiceability = v2ServiceabilityService
				.isProductServicableForPinCode(pinCode);
		final boolean isCodAvailable = v2ServiceabilityService
				.isCodAvailableForArea(pinCode);
		results.put("serviceability", String.valueOf(isServiceability));
		results.put("codServiceability", String.valueOf(isCodAvailable));

		return results;
	}

	protected void populateProductDetailForPickupStore(
			final ProductModel productModel, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException {
		getRequestContextData(request).setProduct(productModel);

		final ProductData productData = productFacade.getProductForOptions(
				productModel, Arrays.asList(ProductOption.BASIC,
						ProductOption.PRICE, ProductOption.SUMMARY,
						ProductOption.DESCRIPTION, ProductOption.GALLERY,
						ProductOption.CATEGORIES, ProductOption.REVIEW,
						ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
						ProductOption.VARIANT_FULL, ProductOption.STOCK,
						ProductOption.VOLUME_PRICES,
						ProductOption.DELIVERY_MODE_AVAILABILITY));
		websiteUrl = Config.getParameter("website."
				+ cmsSiteService.getCurrentSite().getUid() + ".https");
		sortVariantOptionData(productData);
		populateProductData(productData, model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				productBreadcrumbBuilder.getBreadcrumbs(productModel));

		final UserModel user = userService.getCurrentUser();
		// regards size profile
	//	populateSizeProfile(productModel, model, user);
		
		String currentUserEmailId = user.getUid();
		if (currentUserEmailId.equalsIgnoreCase("anonymous")) {
			currentUserEmailId = null;
		}
		String currentUserName;
		if (currentUserEmailId == null) {
			currentUserName = USER;
		} else {
			currentUserName = user.getDisplayName();
		}

		final V2NotifyCustomerForm v2NotifyCustomerForm = new V2NotifyCustomerForm();
		v2NotifyCustomerForm.setCurrentUserEmailId(currentUserEmailId);
		v2NotifyCustomerForm.setCurrentUserName(currentUserName);
		v2NotifyCustomerForm.setUrl(websiteUrl.concat(productData.getUrl()));
		v2NotifyCustomerForm.setName(productModel.getName());
		v2NotifyCustomerForm.setProductCode(productModel.getCode());
		try {
			final List<MediaContainerModel> mediaConatiners = productModel
					.getGalleryImages();
			if (CollectionUtils.isNotEmpty(mediaConatiners)) {
				final MediaModel media = mediaService.getMediaByFormat(
						productModel.getGalleryImages().get(0),
						mediaService.getFormat("Product-96Wx96H"));
				v2NotifyCustomerForm.setMediaUrl(media.getURL());
			}
		} catch (final ModelNotFoundException e) {
			LOG.debug("Product(" + productModel.getCode()
					+ ") does not have media with format-'96Wx96H'");
		}
		final PriceData priceData = productData.getPrice();
		if (priceData != null) {
			v2NotifyCustomerForm.setProductPrice(productData.getPrice()
					.getFormattedValue());
		}
		model.addAttribute("v2NotifyCustomerForm", v2NotifyCustomerForm);
//		SizeProfileForm size = new SizeProfileForm();
	//	size.setProfileName(websiteUrl.concat(productData.getUrl()));
	//	model.addAttribute("sizeProfileForm", size);
		if (null != productData.getClassifications()) {
			for (ClassificationData classification : productData
					.getClassifications()) {
				if (null != classification.getCode()
						&& classification.getCode().equalsIgnoreCase(
								"size_dimensions_classification_class")) {
					model.addAttribute("showSizeguide", "true");
				}
			}
		}

	}

	/* Size Profile of Customer */
	/*public void populateSizeProfile(ProductModel productModel, Model model,
			UserModel user) {
		final ProductData productData = productFacade.getProductForOptions(
				productModel, Arrays.asList(ProductOption.BASIC,
						ProductOption.PRICE, ProductOption.SUMMARY,
						ProductOption.DESCRIPTION, ProductOption.GALLERY,
						ProductOption.CATEGORIES, ProductOption.REVIEW,
						ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
						ProductOption.VARIANT_FULL, ProductOption.STOCK,
						ProductOption.VOLUME_PRICES,
						ProductOption.DELIVERY_MODE_AVAILABILITY));
		LOG.debug("productData in populateProductDetailForDisplay "
				+ productData);
		 for size profile in PDP page 
		CustomerModel customer = (CustomerModel) user;

		List<SizeProfileModel> sizes = customer.getSizeprofile();
		List<String> createdprofiles = new ArrayList<String>();
		for (SizeProfileModel size : sizes) {
			if (size != null && !size.getProfileName().isEmpty())
				createdprofiles.add(size.getProfileName());
		}
		model.addAttribute("createdprofiles", createdprofiles);
		LOG.debug("Size Profile of [" + customer.getDisplayName() + "] is :"
				+ createdprofiles);
		List<String> productTypes = new ArrayList<String>();
		productTypes.add("Upper Wear");
		productTypes.add("Bottom Wear");
		productTypes.add("Others");
		model.addAttribute("productTypes", productTypes);
		List<String> ageGroupList = new ArrayList<String>();
		ageGroupList.add("Infant");
		ageGroupList.add("Child");
		ageGroupList.add("Adult");
		model.addAttribute("ageGroupList", ageGroupList);
		List<String> sexList = new ArrayList<String>();
		sexList.add("Male");
		sexList.add("Female");
		model.addAttribute("sexList", sexList);
		model.addAttribute("customer", customer);

		SizeProfileForm size = new SizeProfileForm();
		model.addAttribute("sizeProfileForm", size);
		-----------------------------------
	}

	@RequestMapping(value = "/size/{profileName}&{productCode}", method = RequestMethod.GET)
	public @ResponseBody String selectedSizeProfile(
			@PathVariable final String profileName,
			@PathVariable final String productCode, final Model model) {
		CustomerModel customer = (CustomerModel) userService.getCurrentUser();
		// for matching the classification attribute
		String result = compare(productCode, customer, profileName);

		LOG.debug("===============================================");
		LOG.debug(result);
		LOG.debug("===============================================");
		return result;
	}

	// size Profile page
	@RequestMapping(value = "/sizeProfile", method = RequestMethod.POST)
	private @ResponseBody ValidationResponse sizeProfile(
			final HttpServletRequest request,
			final Model model,
			@Valid @ModelAttribute("sizeProfileForm") final SizeProfileForm form,
			final BindingResult bindingResult) {

		final ValidationResponse response = new ValidationResponse();
		if (bindingResult.hasErrors()) {
			response.setStatus("FAIL");
			final List<FieldError> allErrors = bindingResult.getFieldErrors();
			final List<ErrorMessage> errorMesages = new ArrayList<ErrorMessage>();
			for (final FieldError objectError : allErrors) {
				errorMesages.add(new ErrorMessage(objectError.getField(),
						objectError.getCode()));
			}
			response.setErrors(errorMesages);
			return response;
		}
		LOG.debug("===============================================");

		LOG.debug(form.getSelectedProfileName());
		LOG.debug(form.getProfileName());
		LOG.debug(form.getProductType());
		LOG.debug(form.getAgeGroup());
		LOG.debug(form.getGender());
		LOG.debug(form.getBust());
		LOG.debug(form.getChest());
		LOG.debug(form.getLength());
		LOG.debug(form.getShoulder());
		LOG.debug(form.getWaist());
		LOG.debug(form.getCollar());
		LOG.debug(form.getSleeve());
		LOG.debug("===============================================");
		try {
			SizeProfileModel sizeProfileData = modelService
					.create(SizeProfileModel.class);
			final CustomerModel user = (CustomerModel) userService
					.getCurrentUser();
			sizeProfileData.setProfileName(form.getProfileName());
			sizeProfileData.setAgeGroup(form.getAgeGroup());
			sizeProfileData.setProductType(form.getProductType());
			sizeProfileData.setGender(form.getGender());
			sizeProfileData.setLength(form.getLength());
			sizeProfileData.setBust(form.getBust());
			sizeProfileData.setChest(form.getChest());
			sizeProfileData.setLength(form.getLength());
			sizeProfileData.setShoulder(form.getShoulder());
			sizeProfileData.setWaist(form.getWaist());
			sizeProfileData.setSleeve(form.getSleeve());
			sizeProfileData.setCollar(form.getCollar());
			sizeProfileData.setCustomer(user);
			LOG.debug(user.getUid());
			this.v2SizeProfileFacade.createSizeProfile(sizeProfileData);
		} catch (final Exception e) {
			LOG.warn("Size profile creation failed: " + e);
			model.addAttribute(form);
			GlobalMessages.addErrorMessage(model, "Creation Falied");
			response.setStatus("FAIL");
		}

		LOG.debug("Response is :" + response.getStatus() + "Error :"
				+ response.getErrors());
		return response;
	}

	//COMPARING THE SELECTED SIZE PROFILE WITH CURRENT PRODUCT
	public String compare(String productCode, CustomerModel customer,
			String profileName) {

		SizeProfileModel selectedSizeProfile = new SizeProfileModel();
		if (profileName != null && !profileName.isEmpty()) {
			List<SizeProfileModel> sizes = customer.getSizeprofile();
			for (SizeProfileModel size : sizes) {
				if (size.getProfileName().equalsIgnoreCase(profileName)) {
					selectedSizeProfile = size;
				}
			}
		}
		LOG.debug("---------------User Selected Profile classification---------------------------");
		LOG.debug("Selected Profile name :"+selectedSizeProfile.getProfileName());
		LOG.debug("Product Type :"+selectedSizeProfile.getProductType());
		LOG.debug("AgeGroup :"+selectedSizeProfile.getAgeGroup());
		LOG.debug("Gender :"+selectedSizeProfile.getGender());
		LOG.debug("Length :"+selectedSizeProfile.getLength());
		LOG.debug("Waist :"+selectedSizeProfile.getWaist());
		LOG.debug("Chest :"+selectedSizeProfile.getChest());
		LOG.debug("Bust :"+selectedSizeProfile.getBust());
		LOG.debug("Shoulder :"+selectedSizeProfile.getShoulder());
		LOG.debug("Collar :"+selectedSizeProfile.getCollar());
		LOG.debug("Sleeve :"+selectedSizeProfile.getSleeve());
		LOG.debug("---------------User Selected Profile classification END---------------------------");
	LinkedHashMap<String, String> sizeDimensions=new LinkedHashMap<String, String>();
	
		ProductModel product = getCurrentProductModel(productCode);
		if (product instanceof V2kartSizeVariantProductModel) {
			final ProductData productData = productFacade.getProductForOptions(
					product, Arrays.asList(ProductOption.CLASSIFICATION));

			if (null != productData.getClassifications()) {
LOG.debug("-------------------------Product size classification Attribute-----------------------------");
				for (ClassificationData classification : productData
						.getClassifications()) {
					if (null != classification.getCode()
							&& classification.getCode().equalsIgnoreCase(
									"size_dimensions_classification_class")) {
						for (FeatureData feature : classification.getFeatures()) {
							for (FeatureValueData value : feature
									.getFeatureValues()) {
								if (value != null) {
	sizeDimensions.put(feature.getName(),value.getValue());
									LOG.info(feature.getName() + ":-"
											+ value.getValue());
								}
							}
						}
					}
				}
				LOG.debug("-------------------------------------MAP OBJECT OUTPUT-------------------------------------------------");				
				LOG.debug(sizeDimensions);				
				LOG.debug("-------------------------------------END-------------------------------------------------");				
			}
		}
		return "TESTING";
	}

	public ProductModel getCurrentProductModel(String productCode) {

		ProductModel productModel = productService
				.getProductForCode(productCode);
		if (productModel != null) {
			LOG.info("Product code is " + productModel.getCode());
		}
		// if size variant product doesn't have sizes, redirect style variant
		// product to size variant product.
		if (productModel instanceof V2kartStyleVariantProductModel) {
			List<VariantProductModel> sizeVariants = new ArrayList<VariantProductModel>(
					productModel.getVariants());
			LOG.debug("sizeVariants is :" + sizeVariants);
			if (CollectionUtils.isNotEmpty(sizeVariants)
					&& sizeVariants.size() == 1) {
				final ProductModel sizeVariantProduct = sizeVariants.get(0);
				if (sizeVariantProduct instanceof V2kartSizeVariantProductModel) {
					final String size = ((V2kartSizeVariantProductModel) sizeVariantProduct)
							.getSize();
					if (size.equalsIgnoreCase("NA")
							|| size.equalsIgnoreCase("N/A") || size.equalsIgnoreCase("A")) {
						productModel = sizeVariantProduct;
					}
				}
			}
		}
		return productModel;

	}
*/}
