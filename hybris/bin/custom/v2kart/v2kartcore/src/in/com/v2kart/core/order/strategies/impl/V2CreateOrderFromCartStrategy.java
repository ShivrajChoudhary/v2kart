
package in.com.v2kart.core.order.strategies.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.CartValidator;
import de.hybris.platform.order.strategies.CreateOrderFromCartStrategy;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author Nagarro_Dev
 * 
 */
public class V2CreateOrderFromCartStrategy implements CreateOrderFromCartStrategy {
	/** Logger bean injection */
    private final static Logger LOG = Logger.getLogger(V2CreateOrderFromCartStrategy.class);
    private CartValidator cartValidator;
    private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
   
    //Added By Elpyaz
    private KeyGenerator keyGenerator;
  

	/**
	 * {@inheritDoc}
	 */
    @Override
    public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException {
        if (cartValidator != null) {
            cartValidator.validateCart(cart);
        }
       
         
        // String orderCode = generateOrderCode();
        // LOG.info("Generate orderCode : "+orderCode);
        final OrderModel res = cloneAbstractOrderStrategy.clone(null, null, cart, generateOrderCode(), OrderModel.class, OrderEntryModel.class);
        //Added By Elpyaz
        //res.setCartCode(cart.getCode());
        return res;
    }

    @Required
    public void setCartValidator(final CartValidator cartValidator) {
        this.cartValidator = cartValidator;
    }

    @Required
    public void setCloneAbstractOrderStrategy(final CloneAbstractOrderStrategy cloneAbstractOrderStrategy) {
        this.cloneAbstractOrderStrategy = cloneAbstractOrderStrategy;
    }
    
    public void setKeyGenerator(KeyGenerator keyGenerator) {
  		this.keyGenerator = keyGenerator;
  	}
    
    /**
     * @author ELPYAZ
	 * Generate a code for created order. Default implementation use {@link KeyGenerator}.
	 * 
	 * @param cart
	 *           You can use a cart to generate new code for order.
	 */
	protected String generateOrderCode()
	{
		final Object generatedValue = keyGenerator.generate();
		if (generatedValue instanceof String)
		{
			return (String)generatedValue;
		}
		else
		{
			return String.valueOf(generatedValue);
		}
	}
}
