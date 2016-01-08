package in.com.v2kart.core.cart.strategies;

import java.util.List;

import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartMergingStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;

public class V2CommerceCartMergingStrategy extends DefaultCommerceCartMergingStrategy {


    @Override
    public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
            throws CommerceCartMergingException {
        
        final Boolean isPickup = fromCart.getIsPickup();
        AddressModel address = fromCart.getDeliveryAddress();
        super.mergeCarts(fromCart, toCart, modifications);
        toCart.setIsPickup(isPickup);
        toCart.setDeliveryAddress(address);
        getModelService().save(toCart);
        
    }
}