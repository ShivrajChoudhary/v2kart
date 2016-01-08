/**
 *
 */

package in.com.v2kart.commercewebservices.v1.controller;

import de.hybris.platform.acceleratorcms.model.components.NavigationBarCollectionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarComponentModel;
import de.hybris.platform.acceleratorcms.model.components.SimpleBannerComponentModel;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.cms2lib.model.components.RotatingImagesComponentModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.media.impl.MediaDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import in.com.v2kart.commercewebservices.category.V2KartCategoryData;
import in.com.v2kart.commercewebservices.common.StatusData;
import in.com.v2kart.commercewebservices.component.CMSLinkComponentData;
import in.com.v2kart.commercewebservices.component.CMSNavigationNodeData;
import in.com.v2kart.commercewebservices.component.NavigationBarCollectionComponentData;
import in.com.v2kart.commercewebservices.component.NavigationBarComponentData;
import in.com.v2kart.commercewebservices.component.V2KartBannersData;
import in.com.v2kart.commercewebservices.component.V2KartCarouselComponentData;
import in.com.v2kart.commercewebservices.component.V2KartContentComponent;
import in.com.v2kart.commercewebservices.component.V2KartHomeScreenData;
import in.com.v2kart.commercewebservices.component.V2KartParentCategoryData;
import in.com.v2kart.commercewebservices.component.V2KartRotatingImagesComponentData;
import in.com.v2kart.commercewebservices.feedback.data.FeedbackData;
import in.com.v2kart.commercewebservices.feedback.data.FeedbackStatus;
import in.com.v2kart.commercewebservices.model.V2PushNotifyAppRegIDModel;
import in.com.v2kart.commercewebservices.seller.data.BeaSellerStatus;
import in.com.v2kart.core.email.V2HtmlMailSender;
import in.com.v2kart.core.enums.FeedbackCategoryEnum;
import in.com.v2kart.core.enums.FeedbackRatingEnum;
import in.com.v2kart.core.model.V2OfferZoneComponentsModel;
import in.com.v2kart.core.process.email.context.V2BeSellerEmailContext;
import in.com.v2kart.core.process.email.context.V2FeedbackEmailContext;
import in.com.v2kart.core.services.V2FeedbackService;
import in.com.v2kart.facades.core.data.V2FeedbackData;
import in.com.v2kart.facades.core.data.V2SellerData;
import in.com.v2kart.facades.populators.V2ProductPricePopulator;
import in.com.v2kart.facades.seller.SellerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 *
 */

@Controller("carousalContentControllerV1")
@RequestMapping(value = "/{baseSiteId}/homeController")
public class HomePageController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(HomePageController.class);
	private static final String PRODUCT_LISTING_FORMAT = "productListing";
	private static final String THUMBNAIL_FORMAT = "thumbnail";

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@Resource(name = "cmsContentSlotService")
	public CMSContentSlotService cmsContentSlotService;

	@Resource(name = "commercePriceService")
	private CommercePriceService commercerPriceService;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Resource(name = "categoryService")
	private CategoryService categoryService;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "productPricePopulator")
	private V2ProductPricePopulator productPricePopulator;

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "feedbackService")
	private V2FeedbackService feedbackService;

	@Resource(name = "htmlMailSender")
	private V2HtmlMailSender v2HtmlMailSender;

	@Resource(name = "enumerationService")
	private EnumerationService enumeration;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource(name = "sellerFacade")
	private SellerFacade sellerFacade;

	@Resource(name = "mediaDao")
	private MediaDao mediaDao;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "imagePopulator")
	private ImagePopulator imagePopulator;

	@Resource(name = "mediaService")
	private MediaService mediaService;

	public static final String SESSION_CATALOG_VERSIONS = "catalogversions";

	private static final String FEEDBACK_BODY = "FEEDBACK_Body";

	private static final String FEEDBACK_SUBJECT = "smtp.mail.feedback.subject.v2kart";
	private static final String BE_A_SELLER_BODY = "BE_A_SELLER_Body";

	private static final String BE_A_SELLER_SUBJECT = "smtp.mail.beaseller.subject";
	private static final String BE_A_SELLER_TO_EMAIL = "be.a.seller.to.email";

	private static final String FEEDBACK_TO_EMAIL = "customercare.v2kart";

	private static final String MEN_CATEGORY_BANNERS_KEY = "categorylandingpage.men";
	private static final String WOMEN_CATEGORY_BANNERS_KEY = "categorylandingpage.women";
	private static final String KIDS_CATEGORY_BANNERS_KEY = "categorylandingpage.kids";
	private static final String HOME_FURNISH_CATEGORY_BANNERS_KEY = "categorylandingpage.homefurnishing";


	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
			ProductOption.PROMOTIONS);



	/**
	 * This webService method is used to give all the homeScreen data in one call the data includes the
	 * rotatingImageData,BannerData,CarouselComponent Data
	 *
	 * @return V2KartHomeScreenData
	 */

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public V2KartHomeScreenData getHomescreenData()
	{
		final V2KartHomeScreenData homeScreenData = new V2KartHomeScreenData();
		try
		{
			long t1 = 0;
			long t2 = 0;
			long t3 = 0;
			long t4 = 0;
			long t5 = 0;
			long t6 = 0;
			long t7 = 0;

			if (LOG.isDebugEnabled())
			{
				LOG.debug("START time :" + System.currentTimeMillis());
				t1 = System.currentTimeMillis();
			}
			homeScreenData.setNowTrendngcarousals(setCarousalData((ProductCarouselComponentModel) cmsComponentService
					.getSimpleCMSComponent("V2kartNowTrendingProductCarouselComponent")));
			if (LOG.isDebugEnabled())
			{
				t2 = System.currentTimeMillis();
			}
			homeScreenData.setNewlyLaunchcarousals(setCarousalData((ProductCarouselComponentModel) cmsComponentService
					.getSimpleCMSComponent("V2kartNewlyLaunchProductCarouselComponent")));
			if (LOG.isDebugEnabled())
			{
				t3 = System.currentTimeMillis();
			}
			homeScreenData.setTopSellingcarousals(setCarousalData((ProductCarouselComponentModel) cmsComponentService
					.getSimpleCMSComponent("V2kartTopSellingProductCarouselComponent")));
			if (LOG.isDebugEnabled())
			{
				t4 = System.currentTimeMillis();
			}

			homeScreenData.setBanners(getAllCaraousels());
			if (LOG.isDebugEnabled())
			{
				t5 = System.currentTimeMillis();
			}
			homeScreenData.setRotatingImages(getRotatingImageComponent("V2HomepageCarouselComponent-Mobile"));
			if (LOG.isDebugEnabled())
			{
				t6 = System.currentTimeMillis();
			}

			/* homeScreenData.setHomeOptionalBanners(getOfferZoneBanners("OfferZoneComponents")); */
			if (LOG.isDebugEnabled())
			{
				t7 = System.currentTimeMillis();
				LOG.debug("End time :" + System.currentTimeMillis());
			}


			if (LOG.isDebugEnabled())
			{

				LOG.debug("*****************START*****************");
				LOG.debug("now trending time:" + (t2 - t1));
				LOG.debug("newly launched time:" + (t3 - t2));
				LOG.debug("top selling time:" + (t4 - t3));
				LOG.debug("category banners time:" + (t5 - t4));
				LOG.debug("rotating banner time:" + (t6 - t5));
				LOG.debug("offer zone  time:" + (t7 - t6));
				LOG.debug("total time:" + (t7 - t1));
				LOG.debug("*****************END*****************");
			}

		}

		catch (final CMSItemNotFoundException e)
		{
			LOG.error("Exception occurs while reading the homescreen content," + e.getMessage());
		}

		return homeScreenData;
	}

	/**
	 * @param string
	 * @return
	 */
	private V2KartRotatingImagesComponentData getOfferZoneBanners(@RequestParam(required = false) final String component)
	{
		V2OfferZoneComponentsModel carousalComponent = new V2OfferZoneComponentsModel();
		V2KartContentComponent rotatingImageComponent = null;
		final V2KartRotatingImagesComponentData rotatingImageComponentData = new V2KartRotatingImagesComponentData();
		final List<V2KartContentComponent> rotatingImagesComponents = new ArrayList<>();
		long t1 = 0, t2 = 0;
		int i = 0;
		try
		{
			carousalComponent = cmsComponentService.getSimpleCMSComponent(component);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("*****************START*****************");
			}
			for (final SimpleBannerComponentModel model : carousalComponent.getOfferZoneComponents())
			{
				if (LOG.isDebugEnabled())
				{
					t1 = System.currentTimeMillis();
				}
				String code = null;
				int firstIndex;
				int lastIndex;
				rotatingImageComponent = new V2KartContentComponent();

				if (null != model.getMedia())
				{
					rotatingImageComponent.setMediaUrl(model.getMedia().getURL());

					final String modelUrlLink = model.getUrlLink();

					if (null != modelUrlLink)
					{
						if (modelUrlLink.contains("/c/"))
						{
							firstIndex = modelUrlLink.indexOf("/c/");
							lastIndex = modelUrlLink.length();
							code = modelUrlLink.substring(firstIndex + 3, lastIndex);
							rotatingImageComponent.setIsCategoryPage(true);
						}
						else if (modelUrlLink.contains("/p/"))
						{
							firstIndex = modelUrlLink.indexOf("/p/");
							lastIndex = modelUrlLink.length();
							code = modelUrlLink.substring(firstIndex + 3, lastIndex);
							rotatingImageComponent.setIsPdpPage(true);
						}
						else
						{
							code = modelUrlLink.replace("/", "");
						}

						if ((null != code) && (code.contains("?")))
						{
							lastIndex = code.indexOf("?");
							code = code.substring(0, lastIndex);
						}
						rotatingImageComponent.setCode(code);
						rotatingImageComponent.setLink(modelUrlLink);
						rotatingImageComponent.setName(code.toUpperCase());

						rotatingImagesComponents.add(rotatingImageComponent);
					}

				}
				if (LOG.isDebugEnabled())
				{
					t2 = System.currentTimeMillis();
				}

				if (LOG.isDebugEnabled())
				{
					LOG.debug("Offer " + ++i + " takes :" + (t2 - t1));
				}


			}
			if (LOG.isDebugEnabled())
			{
				LOG.debug("*****************END*****************");
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.error("Exception occurs while reading the carousel content," + e.getMessage());
		}
		rotatingImageComponentData.setMedias(rotatingImagesComponents);
		return rotatingImageComponentData;
	}

	/**
	 * This webService method is used to give all the Parent Category data in one call the data includes the
	 * rotatingImageData,BannerData,CarouselComponent Data
	 *
	 * @return V2KartParentCategoryData
	 */

	@RequestMapping(value = "/parentCategoryPage", method = RequestMethod.GET)
	@ResponseBody
	public V2KartParentCategoryData getMenCategoryData(@RequestParam final String code)
	{
		final V2KartParentCategoryData parentCategoryData = new V2KartParentCategoryData();
		try
		{

			if (code.equalsIgnoreCase("men"))
			{
				parentCategoryData.setRotatingImages(getRotatingImageComponent("MenCategoryRotatingCarouselBannerComponent-Mobile"));

				String menBanners = StringUtils.EMPTY;
				try
				{
					menBanners = Config.getParameter(cmsSiteService.getCurrentSite().getUid() + "." + MEN_CATEGORY_BANNERS_KEY);
				}
				catch (final Exception exception)
				{
					LOG.error(getMessage("categorylandingpage.men.key.notfound"), exception);
				}
				parentCategoryData.setBanners(getBannerComponent(menBanners));
				parentCategoryData.setBestSellercarousels(setCarousalData((ProductCarouselComponentModel) cmsComponentService
						.getSimpleCMSComponent("MenCategoryPageBestSellerProductCarouselComponent")));
			}
			else if (code.equalsIgnoreCase("women"))
			{
				parentCategoryData
						.setRotatingImages(getRotatingImageComponent("WomenCategoryRotatingCarouselBannerComponent-Mobile"));

				String womenBanners = StringUtils.EMPTY;
				try
				{
					womenBanners = Config.getParameter(cmsSiteService.getCurrentSite().getUid() + "." + WOMEN_CATEGORY_BANNERS_KEY);
				}
				catch (final Exception exception)
				{
					LOG.error(getMessage("categorylandingpage.women.key.notfound"), exception);
				}
				parentCategoryData.setBanners(getBannerComponent(womenBanners));
				parentCategoryData.setBestSellercarousels(setCarousalData((ProductCarouselComponentModel) cmsComponentService
						.getSimpleCMSComponent("WomenCategoryPageBestSellerProductCarouselComponent")));
			}
			else if (code.equalsIgnoreCase("kids"))
			{
				parentCategoryData.setRotatingImages(getRotatingImageComponent("KidsCategoryRotatingCarouselBannerComponent-Mobile"));

				String kidsBanners = StringUtils.EMPTY;
				try
				{
					kidsBanners = Config.getParameter(cmsSiteService.getCurrentSite().getUid() + "." + KIDS_CATEGORY_BANNERS_KEY);
				}
				catch (final Exception exception)
				{
					LOG.error(getMessage("categorylandingpage.kids.key.notfound"), exception);
				}
				parentCategoryData.setBanners(getBannerComponent(kidsBanners));
				parentCategoryData.setBestSellercarousels(setCarousalData((ProductCarouselComponentModel) cmsComponentService
						.getSimpleCMSComponent("KidsCategoryPageBestSellerProductCarouselComponent")));
			}
			else if (code.equalsIgnoreCase("winter"))
			{
				parentCategoryData
						.setRotatingImages(getRotatingImageComponent("WinterCategoryRotatingCarouselBannerComponent-Mobile"));
				parentCategoryData
						.setBanners(getBannerComponent("WinterCategoryBannerComponent1,WinterCategoryBannerComponent2,WinterCategoryBannerComponent3,WinterCategoryBannerComponent4,WinterCategoryBannerComponent5"));
				parentCategoryData.setBestSellercarousels(setCarousalData((ProductCarouselComponentModel) cmsComponentService
						.getSimpleCMSComponent("WinterCategoryPageBestSellerProductCarouselComponent")));
			}
			else if (code.equalsIgnoreCase("home_furnishing"))
			{
				parentCategoryData
						.setRotatingImages(getRotatingImageComponent("HomeFurnishingCategoryRotatingCarouselBannerComponent-Mobile"));

				String homeFurnishingBanners = StringUtils.EMPTY;
				try
				{
					homeFurnishingBanners = Config.getParameter(cmsSiteService.getCurrentSite().getUid() + "."
							+ HOME_FURNISH_CATEGORY_BANNERS_KEY);
				}
				catch (final Exception exception)
				{
					LOG.error(getMessage("categorylandingpage.homefurnishing.key.notfound"), exception);
				}
				parentCategoryData.setBanners(getBannerComponent(homeFurnishingBanners));
				parentCategoryData.setBestSellercarousels(setCarousalData((ProductCarouselComponentModel) cmsComponentService
						.getSimpleCMSComponent("HomeFurnishingCategoryPageBestSellerProductCarouselComponent")));
			}
			else if (code.equalsIgnoreCase("more"))
			{
				parentCategoryData.setRotatingImages(getRotatingImageComponent("MoreCategoryRotatingCarouselBannerComponent-Mobile"));
				parentCategoryData
						.setBanners(getBannerComponent("MoreCategoryBannerComponent1,MoreCategoryBannerComponent2,MoreCategoryBannerComponent3,MoreCategoryBannerComponent4,MoreCategoryBannerComponent5"));
				parentCategoryData.setBestSellercarousels(setCarousalData((ProductCarouselComponentModel) cmsComponentService
						.getSimpleCMSComponent("MoreCategoryPageBestSellerProductCarouselComponent")));
			}
		}

		catch (final CMSItemNotFoundException e)
		{
			LOG.error("Exception occurs while reading the homescreen content," + e.getMessage());
		}

		return parentCategoryData;
	}

	private V2KartBannersData getBannerComponent(final String componentNames) throws CMSItemNotFoundException
	{
		final V2KartBannersData componentData = new V2KartBannersData();
		final List<V2KartContentComponent> components = new ArrayList<V2KartContentComponent>();

		if ((componentNames != null) && (!componentNames.isEmpty()))
		{
			for (final String componentName : componentNames.split(","))
			{
				getBannersComponent(componentName, components);
			}
		}

		componentData.setMedias(components);
		return componentData;
	}

	/**
	 * This method is used to set the product Data into the V2kartCarouselComponent after converting the product model
	 * into productData.
	 **/

	private V2KartCarouselComponentData setCarousalData(final ProductCarouselComponentModel carousalComponent)
	{

		try
		{
			final List<ProductData> products = new ArrayList<ProductData>();

			final V2KartCarouselComponentData componentData = new V2KartCarouselComponentData();


			for (final ProductModel productModel : carousalComponent.getProducts())
			{
				final ImageData productListingImageData = new ImageData();
				final ImageData productThumbnailImageData = new ImageData();
				final List<MediaContainerModel> mediaContainers = productModel.getGalleryImages();
				if (CollectionUtils.isNotEmpty(mediaContainers))
				{
					try
					{
						final MediaModel media = mediaService.getMediaByFormat(mediaContainers.get(0),
								mediaService.getFormat("Product-176Wx220H"));
						imagePopulator.populate(media, productListingImageData);
						productListingImageData.setImageType(ImageDataType.PRIMARY);
						productListingImageData.setFormat(PRODUCT_LISTING_FORMAT);
					}
					catch (final ModelNotFoundException e)
					{
						LOG.debug("Product(" + productModel.getCode() + ") does not have media with format-'Product-176Wx220H'");
					}
				}

				if (null != productModel.getThumbnail())
				{
					imagePopulator.populate(productModel.getThumbnail(), productThumbnailImageData);
					productThumbnailImageData.setImageType(ImageDataType.PRIMARY);
					productListingImageData.setFormat(THUMBNAIL_FORMAT);
				}

				final Collection<ImageData> productImages = new ArrayList<ImageData>(0);
				productImages.add(productListingImageData);
				productImages.add(productThumbnailImageData);

				final ProductData productData = productFacade.getProductForOptions(productModel, PRODUCT_OPTIONS);
				productData.setImages(productImages);
				products.add(productData);
			}

			componentData.setProducts(products);
			return componentData;
		}

		catch (final Exception e)
		{
			LOG.error("No component found" + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * This WebService API is used to return the Carousal component Data to the on the basis of the Carousal type been
	 * requested for
	 *
	 * @param carousalType
	 * @return V2KartCarouselComponentData
	 */

	@RequestMapping(value = "/carousal", method = RequestMethod.GET)
	@ResponseBody
	public V2KartCarouselComponentData getCarousalContent(@RequestParam final String carousalType)
	{
		ProductCarouselComponentModel carousalComponent = new ProductCarouselComponentModel();
		try
		{
			switch (carousalType)
			{
				case "NowTrendingProductCarousal":
					carousalComponent = cmsComponentService.getSimpleCMSComponent("V2kartNowTrendingProductCarouselComponent");
					break;
				case "NewlyLaunchProductCarousal":
					carousalComponent = cmsComponentService.getSimpleCMSComponent("V2kartNewlyLaunchProductCarouselComponent");
					break;
				case "TopSellingProductCarousal":
					carousalComponent = cmsComponentService.getSimpleCMSComponent("V2kartTopSellingProductCarouselComponent");
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.error("Exception occurs while reading the carousel content," + e.getMessage());
		}

		return setCarousalData(carousalComponent);
	}

	/**
	 * This API is used to get all the categories and its 1st level Sub-Categories.
	 *
	 * @return V2KartCategoryData
	 */

	@RequestMapping(value = "/categories", method = RequestMethod.GET)
	@ResponseBody
	public V2KartCategoryData getCategories()
	{
		// final CatalogVersionModel catalogVersion =
		// sessionService.getAttribute("currentCatalogVersion");
		//
		// final Collection<CategoryModel> categories =
		// categoryService.getRootCategoriesForCatalogVersion(catalogVersion);
		// final Iterator<CategoryModel> categoryIterator =
		// categories.iterator();
		long t1 = 0;
		long t2 = 0;
		if (LOG.isDebugEnabled())
		{
			t1 = System.currentTimeMillis();
			LOG.debug("START time getCategories:" + System.currentTimeMillis());
		}
		final List<V2KartCategoryData> parentDataList = new ArrayList<>();
		List<V2KartCategoryData> subDataList = null;

		V2KartCategoryData rootCategoryData = null;
		V2KartCategoryData parentCategoryData = null;
		V2KartCategoryData subCategoryData = null;
		V2KartCategoryData thirdSubCategoryData = null;
		int firstIndex = 0;
		int lastIndex = 0;


		try
		{
			final NavigationBarCollectionComponentData componentData = getSupportedCategories();


			if (null != componentData)
			{
				rootCategoryData = new V2KartCategoryData();
				rootCategoryData.setCode("categories");
				rootCategoryData.setTitle("Categories");
				for (final NavigationBarComponentData barComponentData : componentData.getComponents())
				{
					MediaModel model = new MediaModel();
					parentCategoryData = new V2KartCategoryData();
					// parentCategoryData.setCode(barComponentData.getLink().getUrl().substring(1));
					parentCategoryData.setTitle(barComponentData.getNavigationNode().getTitle());
					final String parentCategoryCode = barComponentData.getLink().getUrl().replace("/", "");
					parentCategoryData.setCode(parentCategoryCode);
					if (!parentCategoryCode.equalsIgnoreCase("more"))
					{
						try
						{
							model = categoryService.getCategoryForCode(parentCategoryCode).getThumbnail();
						}
						catch (final UnknownIdentifierException exc)
						{
							LOG.debug("Category with code: " + parentCategoryCode + " not found.");
							model = null;
						}
					}
					else
					{
						final List<MediaModel> moreicons = mediaDao.findMediaByCode("more_icon");
						if (null != moreicons && !moreicons.isEmpty())
						{
							model = moreicons.get(0);
						}

					}
					if (null != model)
					{
						final String mediaURL = model.getURL();
						if (null != mediaURL && !mediaURL.equals(""))
						{
							parentCategoryData.setMediaURL(mediaURL);
						}
					}

					subDataList = new ArrayList<V2KartCategoryData>();
					for (final CMSNavigationNodeData navigationNodeData : barComponentData.getNavigationNode().getChildren())
					{

						for (final CMSNavigationNodeData nodeData : navigationNodeData.getChildren())
						{

							subCategoryData = new V2KartCategoryData();
							subCategoryData.setTitle(nodeData.getTitle());
							subDataList.add(subCategoryData);
							for (final CMSLinkComponentData linkComponentData : nodeData.getLinks())
							{
								thirdSubCategoryData = new V2KartCategoryData();
								thirdSubCategoryData.setTitle(linkComponentData.getLinkName());

								if ((linkComponentData.getUrl()).contains("/c/"))
								{
									firstIndex = (linkComponentData.getUrl()).indexOf("/c/");
									lastIndex = (linkComponentData.getUrl()).length();
									thirdSubCategoryData.setCode((linkComponentData.getUrl()).substring(firstIndex + 3, lastIndex));
								}
								subDataList.add(thirdSubCategoryData);
							}
						}
					}

					parentCategoryData.setSubCategory(subDataList);
					parentDataList.add(parentCategoryData);
				}
				rootCategoryData.setSubCategory(parentDataList);
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled())
		{
			t2 = System.currentTimeMillis();
			LOG.debug("Total time taken by getCategories):" + (t2 - t1));
		}
		return rootCategoryData;

	}

	private NavigationBarCollectionComponentData getSupportedCategories() throws CMSItemNotFoundException
	{
		// MobileNavigationSlot
		final ContentSlotModel contentSlotModel = cmsContentSlotService.getContentSlotForId("NavigationBarSlot");
		final List<AbstractCMSComponentModel> abstractCMSComponentModels = contentSlotModel.getCmsComponents();

		NavigationBarCollectionComponentData barCollectionComponentData = new NavigationBarCollectionComponentData();

		for (final AbstractCMSComponentModel component : abstractCMSComponentModels)
		{
			if (component instanceof NavigationBarCollectionComponentModel)
			{

				barCollectionComponentData = navBarColComponentConverter((NavigationBarCollectionComponentModel) component,
						barCollectionComponentData);
			}
		}

		return barCollectionComponentData;
	}

	private NavigationBarCollectionComponentData navBarColComponentConverter(final NavigationBarCollectionComponentModel source,
			final NavigationBarCollectionComponentData targate)
	{
		final List<NavigationBarComponentModel> navBarComponents = source.getComponents();
		final List<NavigationBarComponentData> barComponentDatas = new ArrayList<NavigationBarComponentData>();

		for (final NavigationBarComponentModel navBarComponent : navBarComponents)
		{
			if (navBarComponent.getVisible().booleanValue())
			{
				final NavigationBarComponentData barComponentData = new NavigationBarComponentData();
				barComponentDatas.add(navBarCommponentConverter(navBarComponent, barComponentData));
			}
		}
		targate.setComponents(barComponentDatas);
		return targate;
	}

	private NavigationBarComponentData navBarCommponentConverter(final NavigationBarComponentModel source,
			final NavigationBarComponentData targate)
	{
		final CMSLinkComponentData cmsLinkComponentData = new CMSLinkComponentData();
		targate.setName(source.getName());
		targate.setLink(cmsLinkConverter(cmsLinkComponentData, source.getLink()));
		targate.setNavigationNode(navNodeConverter(source.getNavigationNode(), new CMSNavigationNodeData()));
		return targate;
	}

	private CMSLinkComponentData cmsLinkConverter(final CMSLinkComponentData targate, final CMSLinkComponentModel source)
	{
		targate.setLinkName(source.getLinkName());
		targate.setUrl(source.getUrl());
		return targate;
	}

	private CMSNavigationNodeData navNodeConverter(final CMSNavigationNodeModel source, final CMSNavigationNodeData targate)
	{
		targate.setTitle(source.getTitle());
		targate.setVisible(source.isVisible());
		targate.setChildren(getAllNavNodes(source.getChildren()));
		final List<CMSLinkComponentData> links = new ArrayList<CMSLinkComponentData>();
		for (final CMSLinkComponentModel model : source.getLinks())
		{
			final CMSLinkComponentData data = new CMSLinkComponentData();
			links.add(cmsLinkConverter(data, model));
		}
		targate.setLinks(links);
		return targate;
	}

	private List<CMSNavigationNodeData> getAllNavNodes(final List<CMSNavigationNodeModel> source)
	{
		final List<CMSNavigationNodeData> cmsNavigationNodeDatas = new ArrayList<CMSNavigationNodeData>();

		for (final CMSNavigationNodeModel cmsNavigationNodeModel : source)
		{
			if (!cmsNavigationNodeModel.isVisible())
			{
				continue;
			}
			final CMSNavigationNodeData cmsNavigationNodeData = new CMSNavigationNodeData();
			cmsNavigationNodeData.setTitle(cmsNavigationNodeModel.getTitle());
			cmsNavigationNodeData.setVisible(cmsNavigationNodeModel.isVisible());

			final List<CMSLinkComponentData> links = new ArrayList<CMSLinkComponentData>();
			for (final CMSLinkComponentModel model : cmsNavigationNodeModel.getLinks())
			{
				if (model.getVisible().booleanValue())
				{
					final CMSLinkComponentData data = new CMSLinkComponentData();
					links.add(cmsLinkConverter(data, model));
				}
			}
			cmsNavigationNodeData.setLinks(links);

			final List<CMSNavigationNodeModel> list = cmsNavigationNodeModel.getChildren();
			if ((null != list) && (list.size() > 0))
			{
				cmsNavigationNodeData.setChildren(getAllNavNodes(list));
			}
			cmsNavigationNodeDatas.add(cmsNavigationNodeData);

		}

		return cmsNavigationNodeDatas;
	}

	@RequestMapping(value = "/miniCartStatus", method = RequestMethod.GET)
	@ResponseBody
	public Integer getMiniCart()
	{ // final Map<String, Object> attributes =
		sessionService.getAllAttributes();

		// cartFacade.getMiniCart().getTotalUnitCount();

		return cartFacade.getMiniCart().getTotalUnitCount();
	}

	/**
	 * This API is used to get the component from the rotating image component
	 *
	 * @return V2KartRotatingImagesComponentData
	 */

	@RequestMapping(value = "/rotatingImageComponent", method = RequestMethod.GET)
	@ResponseBody
	public V2KartRotatingImagesComponentData getRotatingImageComponent(@RequestParam(required = false) final String component)
	{
		RotatingImagesComponentModel carousalComponent = new RotatingImagesComponentModel();
		V2KartContentComponent rotatingImageComponent = null;
		final V2KartRotatingImagesComponentData rotatingImageComponentData = new V2KartRotatingImagesComponentData();
		final List<V2KartContentComponent> rotatingImagesComponents = new ArrayList<>();

		try
		{
			carousalComponent = cmsComponentService.getSimpleCMSComponent(component);
			for (final BannerComponentModel model : carousalComponent.getBanners())
			{
				if (!model.getVisible().booleanValue())
				{
					continue;
				}
				String code = null;
				int firstIndex;
				int lastIndex;
				rotatingImageComponent = new V2KartContentComponent();

				if (null != model.getMedia())
				{
					rotatingImageComponent.setMediaUrl(model.getMedia().getURL());

					final String modelUrlLink = model.getUrlLink();

					if (null != modelUrlLink)
					{
						if (modelUrlLink.contains("/c/"))
						{
							firstIndex = modelUrlLink.indexOf("/c/");
							lastIndex = modelUrlLink.length();
							code = modelUrlLink.substring(firstIndex + 3, lastIndex);
							rotatingImageComponent.setIsCategoryPage(true);
						}
						else if (modelUrlLink.contains("/p/"))
						{
							firstIndex = modelUrlLink.indexOf("/p/");
							lastIndex = modelUrlLink.length();
							code = modelUrlLink.substring(firstIndex + 3, lastIndex);
							rotatingImageComponent.setIsPdpPage(true);
						}
						else
						{
							code = modelUrlLink.replace("/", "");
						}

						if ((null != code) && (code.contains("?")))
						{
							lastIndex = code.indexOf("?");
							code = code.substring(0, lastIndex);
						}
						rotatingImageComponent.setCode(code);
						rotatingImageComponent.setLink(modelUrlLink);
						rotatingImageComponent.setName(code.toUpperCase());

						rotatingImagesComponents.add(rotatingImageComponent);
					}

				}


			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.error("Exception occurs while reading the carousel content," + e.getMessage());
		}
		rotatingImageComponentData.setMedias(rotatingImagesComponents);
		return rotatingImageComponentData;
	}

	/**
	 * This API is used to get the banner compnent data.
	 *
	 * @return V2KartBannersData
	 * @throws CMSItemNotFoundException
	 */

	@RequestMapping(value = "/banners", method = RequestMethod.GET)
	@ResponseBody
	public V2KartBannersData getAllCaraousels() throws CMSItemNotFoundException
	{

		final V2KartBannersData componentData = new V2KartBannersData();
		final List<V2KartContentComponent> components = new ArrayList<V2KartContentComponent>();

		getBannersComponent("AppMenCategoryBanner", components);
		getBannersComponent("AppWomenCategoryBanner", components);
		getBannersComponent("AppKidsCategoryBanner", components);

		componentData.setMedias(components);

		return componentData;
	}

	/**
	 * This API call saves the Android Reg id's into the model.
	 *
	 * @param code
	 * @return Staus Data
	 */
	@RequestMapping(value = "/updateRegID", method = RequestMethod.POST)
	@ResponseBody
	public StatusData updateAnroidRegID(@RequestParam(required = true) final String code)
	{
		final StatusData data = new StatusData();

		final V2PushNotifyAppRegIDModel appRegIDModel = new V2PushNotifyAppRegIDModel();
		appRegIDModel.setAndroidRegID(code);

		try
		{
			modelService.save(appRegIDModel);
			data.setCode("1");
			data.setMessage("RegId Updated.");
		}
		catch (final Exception e)
		{
			data.setCode("0");
			data.setMessage("RegID Update failed." + e.getMessage());
		}
		return data;
	}

	private void getBannersComponent(final String key, final List<V2KartContentComponent> bannersList)
			throws CMSItemNotFoundException
	{

		try
		{
			final SimpleBannerComponentModel bannerComponentModel = cmsComponentService.getSimpleCMSComponent(key);
			bannersList.add(getBannersList(bannerComponentModel));
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.error("Exception occured while reading the banner components," + e.getMessage());
		}

	}

	private V2KartContentComponent getBannersList(final SimpleBannerComponentModel bannerComponentModel)
	{

		String code = null;
		int firstIndex;
		int lastIndex;

		final V2KartContentComponent banner = new V2KartContentComponent();
		String modelUrlLink = null;

		if (null != bannerComponentModel)
		{
			modelUrlLink = bannerComponentModel.getUrlLink();
		}

		if (null != modelUrlLink)
		{
			if (modelUrlLink.contains("/c/"))
			{
				firstIndex = modelUrlLink.indexOf("/c/");
				lastIndex = modelUrlLink.length();
				code = modelUrlLink.substring(firstIndex + 3, lastIndex);
				banner.setIsCategoryPage(true);
			}
			else if (modelUrlLink.contains("/p/"))
			{
				firstIndex = modelUrlLink.indexOf("/p/");
				lastIndex = modelUrlLink.length();
				code = modelUrlLink.substring(firstIndex + 3, lastIndex);
				banner.setIsPdpPage(true);
			}
			else if (modelUrlLink.contains("/cl/"))
			{
				firstIndex = modelUrlLink.indexOf("/cl/");
				lastIndex = modelUrlLink.length();
				code = modelUrlLink.substring(firstIndex + 4, lastIndex);
				banner.setIsLandingPage(true);
			}
			else
			{
				code = modelUrlLink.replace("/", "");
			}
			if ((null != code) && (code.contains("?")))
			{
				lastIndex = code.indexOf("?");
				code = code.substring(0, lastIndex);
			}
			banner.setCode(code);
			banner.setLink(modelUrlLink);
			banner.setName(bannerComponentModel.getName());
			if (null != bannerComponentModel.getMedia())
			{
				banner.setMediaUrl(bannerComponentModel.getMedia().getURL());
			}

		}

		return banner;
	}

	@ResponseBody
	@RequestMapping(value = "/feedback", method = RequestMethod.GET)
	public FeedbackData feeddBack() throws CMSItemNotFoundException
	{
		final FeedbackData feedbackData = new FeedbackData();
		final List<String> category = new ArrayList<String>();
		for (final FeedbackCategoryEnum eachCategory : FeedbackCategoryEnum.values())
		{
			category.add(enumeration.getEnumerationName(eachCategory));
		}

		final List<String> rating = new ArrayList<String>();
		for (final FeedbackRatingEnum eachRating : FeedbackRatingEnum.values())
		{
			rating.add(enumeration.getEnumerationName(eachRating));

		}
		feedbackData.setCategories(category);
		feedbackData.setRatings(rating);
		return feedbackData;
	}

	@ResponseBody
	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	public FeedbackStatus feeddBack(@RequestParam(required = true) final String emailId,
			@RequestParam(required = true) final String mobileNo, @RequestParam(required = true) final String category,
			@RequestParam(required = true) final String rating, @RequestParam(required = true) final String message)
			throws CMSItemNotFoundException
	{
		final FeedbackStatus status = new FeedbackStatus();
		final V2FeedbackData feedbackData = new V2FeedbackData();
		feedbackData.setMessage(message);
		feedbackData.setMobileNumber(mobileNo);
		feedbackData.setEmail(emailId);
		feedbackData.setRating(rating);
		feedbackData.setCategory(category);
		try
		{
			feedbackService.saveFeedbackData(feedbackData);
			status.setMessage("Thank you for providing your valuable feedback.");
			status.setStatus(Boolean.TRUE);
		}
		catch (final DuplicateUidException e)
		{
			status.setMessage("Failed to save feedback. Please try again later.");
			status.setStatus(Boolean.FALSE);
		}

		final String feedbackSubject = siteConfigService.getProperty(FEEDBACK_SUBJECT);

		sendFeedbackMail(FEEDBACK_BODY, feedbackData, feedbackSubject);
		return status;
	}

	@ResponseBody
	@RequestMapping(value = "/beADealer", method = RequestMethod.POST)
	public BeaSellerStatus beADealer(@RequestParam(required = true) final String name,
			@RequestParam(required = true) final String emailId, @RequestParam(required = true) final String phone,
			@RequestParam(required = true) final String category, @RequestParam(required = false) final String message)
			throws CMSItemNotFoundException
	{
		final BeaSellerStatus status = new BeaSellerStatus();
		final V2SellerData sellerData = new V2SellerData();
		sellerData.setName(name);
		sellerData.setMessage(message);
		sellerData.setPhone(phone);
		sellerData.setEmail(emailId);
		sellerData.setCategory(category);
		try
		{
			sellerFacade.saveSellerData(sellerData);
			status.setMessage("Details have been saved successfully");
			status.setStatus(Boolean.TRUE);
		}
		catch (final DuplicateUidException e)
		{
			status.setMessage("You are already registered, Please contact @ 8010202222 or dealers@v2kart.com for further queries.");
			status.setStatus(Boolean.FALSE);
		}

		final String beASellerSubject = siteConfigService.getProperty(BE_A_SELLER_SUBJECT);
		sendMail(BE_A_SELLER_BODY, sellerData, beASellerSubject);

		return status;
	}

	private void sendFeedbackMail(final String templateCode, final V2FeedbackData feedbackData, final String mailSubject)
	{

		final VelocityContext feedbackEmailContext = new V2FeedbackEmailContext(feedbackData);
		final String to = siteConfigService.getProperty(FEEDBACK_TO_EMAIL);
		try
		{
			v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(feedbackEmailContext, templateCode, mailSubject, to));
		}
		catch (final EmailException e)
		{
			LOG.info("Failed to send email Be a seller.", e);
		}

	}

	private void sendMail(final String templateCode, final V2SellerData sellerData, final String mailSubject)
	{

		final VelocityContext beSellerEmailContext = new V2BeSellerEmailContext(sellerData);
		final String to = siteConfigService.getProperty(BE_A_SELLER_TO_EMAIL);

		try
		{
			v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(beSellerEmailContext, templateCode, mailSubject, to));
		}
		catch (final EmailException e)
		{
			LOG.info("Failed to send email Be a seller.", e);
		}

	}
}
