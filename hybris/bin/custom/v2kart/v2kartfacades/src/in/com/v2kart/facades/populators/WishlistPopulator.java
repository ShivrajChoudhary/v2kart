package in.com.v2kart.facades.populators;

import java.util.ArrayList;
import java.util.List;

import in.com.v2kart.facades.core.data.WishlistData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import de.hybris.platform.wishlist2.Wishlist2Service;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * @author arunkumar
 * 
 */
public class WishlistPopulator implements Populator<Wishlist2Model, WishlistData> {
    private Converter<Wishlist2EntryModel, OrderEntryData> wishlistEntryConverter;

    private Wishlist2Service wishlistService;

    private ProductService productService;

    public Converter<Wishlist2EntryModel, OrderEntryData> getWishlistEntryConverter() {
        return wishlistEntryConverter;
    }

    /**
     * @param wishlistService
     *        the wishlistService to set
     */
    public void setWishlistService(final Wishlist2Service wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * @param productService
     *        the productService to set
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public void setWishlistEntryConverter(final Converter<Wishlist2EntryModel, OrderEntryData> wishlistEntryConverter) {
        this.wishlistEntryConverter = wishlistEntryConverter;
    }

    @Override
    public void populate(final Wishlist2Model source, final WishlistData prototype) throws ConversionException {
        prototype.setName(source.getName());
        prototype.setHasDiscontinuedProducts(Boolean.FALSE);
        prototype.setCode(source.getPk().getLongValueAsString());
        final List<Wishlist2EntryModel> sourceEntries = source.getEntries();
        final List<OrderEntryData> wishlistItems = new ArrayList<OrderEntryData>();
        OrderEntryData orderEntryData = null;
        if (sourceEntries != null) {
            for (final Wishlist2EntryModel oneSourceEntry : sourceEntries) {
                try {
                    if (oneSourceEntry.getProduct() != null) {
                        final ProductModel productModel = productService.getProductForCode(oneSourceEntry.getProduct().getCode());
                        if (productModel != null) {
                            orderEntryData = wishlistEntryConverter.convert(oneSourceEntry);
                            wishlistItems.add(orderEntryData);
                        }
                    }

                } catch (final UnknownIdentifierException e) {
                    // remove discontinued products
                    wishlistService.removeWishlistEntryForProduct(oneSourceEntry.getProduct(), source);
                }
            }
            prototype.setEntries(wishlistItems);
        }

    }
}
