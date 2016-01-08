package in.com.v2kart.facades.cart;

import in.com.v2kart.core.vouchers.VoucherCodeResult;
import in.com.v2kart.facades.voucher.data.V2VoucherData;

import java.util.Collection;
import java.util.List;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;

/**
 * 
 * @author vikrant2480
 * 
 */
public interface V2CartFacade extends CartFacade {

    /**
     * Apply or remove gift wrap from an entry
     * 
     * @param entryNo
     *        the entry number to update
     * @param giftWrapped
     *        apply or remove gift wrap
     */
    public void updateGiftWrapForEntry(final int entryNo, boolean giftWrapped);
    
    /**
     * Remove gift wrap from all entries
     * 
     */
    public void disableAllGiftWrap();
    
    /**
     * Calculates cart total
     * 
     */
    public void calculateCartTotal();
    
    /**
     * Attempt to apply a voucher code and recalculates the cart.
     * 
     * @param voucherCode
     *        the code to apply
     * @return the {@link in.com.v2kart.core.vouchers.VoucherCodeResult} of this attempt
     * @throws CalculationException
     *         the calculation exception
     */
    public VoucherCodeResult applyVoucherCode(String voucherCode) throws CalculationException;

    /**
     * Releases the voucher code from the cart.
     * 
     * @param voucherCode
     *        the code to release
     * @return true, if successful
     */
    public boolean releaseVoucherCode(String voucherCode);

    /**
     * Gets all applied voucher codes to a carts in the session.
     * 
     * @return the applied voucher codes
     */
    public Collection<String> getAppliedVoucherCodes();

    /**
     * Gets the applicable vouchers.
     * 
     * @param productModel
     *        the product model
     * @return the applicable vouchers
     */
    public List<V2VoucherData> getApplicableVouchers(ProductModel productModel);
   
    /**
     * update name for pickup order
     */
	public void updateName();
	
	/**
     * update address for home delivery order
     */
	public void updateAddress();
	
	public void removeCart();
	
	public void removeCOD();
}
