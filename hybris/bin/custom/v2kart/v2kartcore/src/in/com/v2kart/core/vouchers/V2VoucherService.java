package in.com.v2kart.core.vouchers;

import java.util.Collection;
import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.voucher.model.PromotionVoucherModel;

/**
 * 
 * @author vikrant2480
 * 
 */
public interface V2VoucherService {

    /**
     * Apply a voucher code to the cart.
     * 
     * @param voucherCode
     *        The code to apply
     * @throws CalculationException
     * 
     */
    public VoucherCodeResult applyVoucher(final String voucherCode) throws CalculationException;

    /**
     * Remove a voucher code from the cart.
     * 
     * @param voucherCode
     *        The code to remove
     * 
     */
    public boolean releaseVoucherCode(final String voucherCode);

    /**
     * Get list of voucher codes applied to the cart
     * 
     * @return List list of applied
     */
    public Collection<String> getAppliedVoucherCodes();

    /**
     * Get Applicable Vouchers for product with specific code
     * 
     * @return List list of vouchers
     */
    public List<PromotionVoucherModel> getApplicableVouchers(ProductModel productModel);

}
