package in.com.v2kart.facades.populators;

import java.math.BigDecimal;

import de.hybris.platform.commercefacades.order.converters.populator.ExtendedCartPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;

/**
 * 
 @author Rampal Lather
 * 
 */

public class V2ExtendedCartPopulator extends ExtendedCartPopulator {

    private V2CartPopulator cartPopulator;

    @Override
    public void populate(final CartModel source, final CartData target) {
        super.populate(source, target);
        cartPopulator.addDeliveryCost(source, target);
        cartPopulator.addFreeShippingInformation(source, target);
        addTotalGiftWrapPrice(source, target);
        if(source.getIsPickup() == null){
       	 target.setIsPickup(false);
       }
       else{
       target.setIsPickup(source.getIsPickup());
       }
        
        setCartSubTotal(source, target);
    }

    protected void addTotalGiftWrapPrice(final AbstractOrderModel source, final AbstractOrderData target)
    {
        if (source.getTotalGiftWrapPrice() != null && source.getTotalGiftWrapPrice() != 0.0) {

            target.setTotalGiftWrapPrice(createPrice(source, source.getTotalGiftWrapPrice()));
        }
    }
    
    protected void setCartSubTotal(final AbstractOrderModel source,final AbstractOrderData target)
    {
        if (target.getProductDiscounts() != null &&  target.getSubTotal() !=null) {
        	BigDecimal productDiscount = target.getProductDiscounts().getValue();
        	if(productDiscount != null) {
        		target.setSubTotal(createPrice(source, productDiscount.doubleValue() + target.getSubTotal().getValue().doubleValue()));
        	}
        }
    }

    public V2CartPopulator getCartPopulator() {
        return this.cartPopulator;
    }

    public void setCartPopulator(V2CartPopulator cartPopulator) {
        this.cartPopulator = cartPopulator;
    }
}
