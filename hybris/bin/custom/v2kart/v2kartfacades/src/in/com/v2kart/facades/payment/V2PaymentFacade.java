/**
 * 
 */
package in.com.v2kart.facades.payment;

import java.math.BigDecimal;
import java.util.Currency;

import de.hybris.platform.acceleratorfacades.payment.PaymentFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 * The Interface V2PaymentFacade.
 * 
 * @author Anuj Kumar
 */
public interface V2PaymentFacade extends PaymentFacade {

   
    /**
     * @param addressData
     */
    void saveBillingAddress(AddressData addressData);
    
  
    /**
     * API is used to redeem store credit points
     * 
     * @param cartModel
     *     To get cart data detail
     * @return 
     *     PaymentTransactionEntryModel paymentTransactionEntryModel
     */
    public PaymentTransactionEntryModel redeemStoreCredit( final CartModel cartModel);
    
}
