package in.com.v2kart.facades.order;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;

public interface V2CheckoutFacade extends CheckoutFacade {

    boolean hasNoServiceableMode();
    
    void assignCodPaymentMode(final AddressData adressData);
    
    void useMyWalletMoney(CartData cartData, AddressData address);
    
    void changeCartDeliveryMode();
    void changeCartDeliveryModeToShip();

	void storeDeliveryOwner(String store);

	boolean setDeliveryAddressIfAvailableForNewRegister();
  

    
}
