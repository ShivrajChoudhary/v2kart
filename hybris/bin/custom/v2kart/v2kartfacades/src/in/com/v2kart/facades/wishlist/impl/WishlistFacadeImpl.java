package in.com.v2kart.facades.wishlist.impl;

import java.util.List;

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import de.hybris.platform.wishlist2.Wishlist2Service;
import in.com.v2kart.facades.core.data.WishlistData;
import in.com.v2kart.facades.wishlist.WishlistFacade;

/**
 * @author arunkumar
 * 
 */
public class WishlistFacadeImpl implements WishlistFacade {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Wishlist2Service wishlistService;

    private ProductService productService;

    public static final Wishlist2EntryPriority DEFAULT_PRIORITY = Wishlist2EntryPriority.MEDIUM;

    public static final Integer DEFAULT_DESIRED = 1;

    public static final String DEFAULT_COMMENT = null;

    public static final String DEFAULT_NAME = "DEFAULT_WISHLIST_NAME";

    private Converter<Wishlist2Model, WishlistData> wishlistConverter;

    @Resource
    private BaseStoreService baseStoreService;
    @Resource
    private BaseSiteService baseSiteService;
    @Resource
    private UserService userService;
    @Resource
    private CommonI18NService commonI18NService;
    @Resource
    private EventService eventService;

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * @param wishlistService
     *        the wishlistService to set
     */
    public void setWishlistService(final Wishlist2Service wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * @param wishlistConverter
     *        the wishlistConverter to set
     */
    public void setWishlistConverter(final Converter<Wishlist2Model, WishlistData> wishlistConverter) {
        this.wishlistConverter = wishlistConverter;
    }

    @Override
    public boolean addWishlistEntry(final String productCode) {
        final ProductModel productModel = productService.getProductForCode(productCode);
        if (productModel == null) {
            LOG.warn("Could not find product for code: {}. Returning null.", productCode);
            return false;
        }
        final Wishlist2Model wishlistModel = getDefaultWishlistModel();
        Wishlist2EntryModel entryModel;
        try {

            entryModel = wishlistService.getWishlistEntryForProduct(productModel, wishlistModel);

        } catch (final UnknownIdentifierException e) {

            LOG.debug("Adding new entry to wishlist. Product code: {}", productCode);
            wishlistService.addWishlistEntry(wishlistModel, productModel, DEFAULT_DESIRED, DEFAULT_PRIORITY, DEFAULT_COMMENT);
            return true;
        }

        if (entryModel != null) {
            LOG.debug("Product {} already existed in wishlist. Skipping.", productCode);
            // Product already in list. Don't add again.
            return false;
        }
        return false;
    }

    @Override
    public List<WishlistData> getWishlists() {
        final List<Wishlist2Model> wishlistsModel = wishlistService.getWishlists();
        if (wishlistsModel.size() > 0) {
            final List<WishlistData> wishlistsData = Converters.convertAll(wishlistsModel, wishlistConverter);
            return wishlistsData;
        }
        return null;
    }

    private Wishlist2Model getDefaultWishlistModel() {
        if (!wishlistService.hasDefaultWishlist()) {
            LOG.warn("Creating new default wishlist");
            wishlistService.createDefaultWishlist(DEFAULT_NAME, null);
        }
        return wishlistService.getDefaultWishlist();
    }

    @Override
    public void removeWishlistEntryData(final String productCode) {
        ProductModel productModel = null;

        final Wishlist2Model wishlistModel = getDefaultWishlistModel();
        try {

            productModel = productService.getProductForCode(productCode);
            if (productModel == null) {
                LOG.warn("Could not find product for code: {}. Returning null.", productCode);
                return;
            }

        } catch (final UnknownIdentifierException e) {

            LOG.debug("Product Model not found with product code ", productCode);
            // remove discontinued products
            for (final Wishlist2EntryModel wishlistEntryModel : wishlistModel.getEntries()) {
                if (wishlistEntryModel.getProduct().getCode().equals(productCode)) {

                    productModel = wishlistEntryModel.getProduct();
                    break;
                }

            }

        }

        wishlistService.removeWishlistEntryForProduct(productModel, wishlistModel);

    }

    @Override
    public void sendWishlistEmailEvent(final String email, final String wishlistCode) {
        /*
         * for (final Wishlist2Model wishlist : wishlistService.getWishlists(userService.getCurrentUser())) { if
         * (wishlist.getPk().equals(PK.parse(wishlistCode))) { eventService.publishEvent(initializeEvent(new WishlistEmailEvent(email,
         * wishlist))); } }
         */
    }

    /*
     * protected AbstractCommerceUserEvent initializeEvent(final AbstractCommerceUserEvent event) {
     * event.setBaseStore(baseStoreService.getCurrentBaseStore()); event.setSite(baseSiteService.getCurrentBaseSite());
     * event.setCustomer((CustomerModel) userService.getCurrentUser()); event.setLanguage(commonI18NService.getCurrentLanguage());
     * event.setCurrency(commonI18NService.getCurrentCurrency()); return event; }
     */

}
