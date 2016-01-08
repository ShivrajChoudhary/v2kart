package in.com.v2kart.facades.populators;

import in.com.v2kart.core.V2CartService;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.PriceValue;

public class V2CartPopulator extends CartPopulator<CartData> {

    private V2CartService cartService;
    
    @Autowired
    private Converter<AddressModel, AddressData> addressConverter;
    
    public Converter<AddressModel, AddressData> getAddressConverter() {
		return addressConverter;
	}

	public void setAddressConverter(
			Converter<AddressModel, AddressData> addressConverter) {
		this.addressConverter = addressConverter;
	}


    @Override
    public void populate(CartModel source, CartData target) {
        super.populate(source, target);
        addDeliveryCost(source, target);
        addFreeShippingInformation(source, target);
        if(source.getIsPickup() == null){
        	 target.setIsPickup(false);
        }
        else{
        target.setIsPickup(source.getIsPickup());
        }
        
        if(source.getDeliveryAddress() != null){
        target.setDeliveryAddress(getAddressConverter().convert(source.getDeliveryAddress()));
        }
    }

    
	protected void addDeliveryCost(final CartModel source, final CartData target) {

        // set delivery cost irrespective of delivery mode
        target.setDeliveryCost(createPrice(source, source.getDeliveryCost()));
    }

    protected void addFreeShippingInformation(final CartModel source, final CartData target) {
        PriceValue shopMinForFreeDelivery = getCartService().getShopMinimumForFreeDelivery(source);

        BigDecimal minAmountBigDecimal = new BigDecimal(shopMinForFreeDelivery.getValue());
        BigDecimal cartSubtotal = target.getSubTotal().getValue();
        BigDecimal shopMoreBigDecimal = minAmountBigDecimal.subtract(cartSubtotal);

        PriceData shopMoreForFreeDelivery = getPriceDataFactory().create(PriceDataType.BUY, shopMoreBigDecimal,
                shopMinForFreeDelivery.getCurrencyIso());

        target.setShopMoreForFreeDelivery(shopMoreForFreeDelivery);
    }

    public V2CartService getCartService() {
        return this.cartService;
    }

    @Required
    public void setCartService(V2CartService cartService) {
        this.cartService = cartService;
    }
}
