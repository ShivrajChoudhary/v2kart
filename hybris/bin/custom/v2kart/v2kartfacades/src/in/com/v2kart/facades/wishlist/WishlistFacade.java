package in.com.v2kart.facades.wishlist;

import in.com.v2kart.facades.core.data.WishlistData;

import java.util.List;

/**
 * @author arunkumar
 * 
 */
public interface WishlistFacade {

    boolean addWishlistEntry(String productCode);

    List<WishlistData> getWishlists();

    void removeWishlistEntryData(String productCode);

    void sendWishlistEmailEvent(final String email, final String wishlistCode);
}
