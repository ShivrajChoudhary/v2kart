package in.com.v2kart.core.cart.strategies;

import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.CartModel;

public class V2DefaultCartValidationStrategy extends DefaultCartValidationStrategy {
	
	@Override
	protected void validateDelivery(CartModel cartModel) {
		if ((cartModel.getDeliveryAddress() == null)
				|| (isGuestUserCart(cartModel))
				|| (getUserService().getCurrentUser().equals(cartModel
						.getDeliveryAddress().getOwner())))
			return;
		//cartModel.setDeliveryAddress(null);
		getModelService().save(cartModel);
		
	}
	
	

}
