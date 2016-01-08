/**
 * 
 */
package in.com.v2kart.core.payment.services;


import java.math.BigDecimal;
import java.util.Currency;
import in.com.v2kart.core.model.V2PaymentModeModel;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * @author Anuj Kumar
 * 
 */
public interface V2PaymentService extends PaymentService {

    /**
     * @param addressData
     */
    void saveBillingAddress(AddressData addressData);

    /**
     * @param code
     * @return
     */
    V2PaymentModeModel getV2PaymentMode(String code);

    /**
     * 
     * @param cartModel
     * @param storeCreditPaymentInfoModel
     * @return
     */
    boolean setStoreCreditPaymentInfo(final CartModel cartModel, final V2StoreCreditPaymentInfoModel storeCreditPaymentInfoModel);

    /**
     * Redeem Storecredit amount
     * 
     * @param totalAmount
     *        order total
     * @param currency
     *        order currency
     * @return payment transaction
     */
    public PaymentTransactionEntryModel redeemStoreCredit(final String merchantTransactionCode, final BigDecimal totalAmount,
            final Currency currency, CartModel cartModel);
    
    /**
     * 
     * @param paramPaymentTransactionModel
     * @param paramBigDecimal
     * @param currency
     * @return
     * @throws AdapterException
     */
    public PaymentTransactionEntryModel refundFollowOn(OrderModel orderModel, PaymentTransactionModel paramPaymentTransactionModel, BigDecimal amount, Currency currency)
    throws AdapterException;

}
