package in.com.v2kart.core.vouchers.impl;

import in.com.v2kart.core.vouchers.V2VoucherService;
import in.com.v2kart.core.vouchers.VoucherCodeResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.voucher.impl.DefaultVoucherService;
import de.hybris.platform.voucher.jalo.Voucher;
import de.hybris.platform.voucher.model.DateRestrictionModel;
import de.hybris.platform.voucher.model.ProductCategoryRestrictionModel;
import de.hybris.platform.voucher.model.ProductRestrictionModel;
import de.hybris.platform.voucher.model.PromotionVoucherModel;
import de.hybris.platform.voucher.model.RestrictionModel;
import de.hybris.platform.voucher.model.UserRestrictionModel;
import de.hybris.platform.voucher.model.VoucherModel;

public class DefaultV2VoucherService extends DefaultVoucherService implements V2VoucherService {

    private final static Logger LOG = Logger.getLogger(DefaultV2VoucherService.class.getName());

    @Autowired
    public DefaultVoucherService voucherService;

    @Autowired
    public CartService cartService;

    @Autowired
    public CommerceCartService commerceCartService;

    @Autowired
    private PromotionsService promotionsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Override
    public VoucherCodeResult applyVoucher(final String voucherCode) throws CalculationException {
        boolean isVoucherCodeApplied = false;
        final VoucherCodeResult voucherCodeResult = new VoucherCodeResult(false);
        final CartModel cartModel = cartService.getSessionCart();
        final Collection<String> appliedVoucherCodes = voucherService.getAppliedVoucherCodes(cartModel);
        if (CollectionUtils.isNotEmpty(appliedVoucherCodes) && appliedVoucherCodes.size() == 1
                && appliedVoucherCodes.iterator().next().equals(voucherCode)) {
            // dont need to do nothing
            return new VoucherCodeResult(true);
        }
        try {
            for (final String voucher : voucherService.getAppliedVoucherCodes(cartModel)) {
                voucherService.releaseVoucher(voucher, cartModel);
            }
            if (!StringUtils.isEmpty(voucherCode)) {
                isVoucherCodeApplied = voucherService.redeemVoucher(voucherCode, cartModel);
                if (!isVoucherCodeApplied) {
                    voucherCodeResult.setRejectMessage("basket.vouchercode.invalid");
                    return voucherCodeResult;
                }
                final CommerceCartParameter parameter = new CommerceCartParameter();
                parameter.setEnableHooks(true);
                parameter.setCart(cartModel);
                commerceCartService.recalculateCart(parameter);
                final Collection<DiscountValue> discountCollection = cartModel.getGlobalDiscountValues();
                boolean isVoucherAppliedSuccessfully = true;
                final VoucherModel voucher = voucherService.getVoucher(voucherCode);
                if (null != voucher) {
                    for (final DiscountValue discountValue : discountCollection) {
                        if (discountValue.getCode().equals(voucher.getCode()) && discountValue.getAppliedValue() == 0) {
                            isVoucherAppliedSuccessfully = false;
                        }
                    }
                } else {
                    isVoucherAppliedSuccessfully = false;
                }
                if (isVoucherAppliedSuccessfully) {
                    voucherCodeResult.setApplied(true);
                    voucherCodeResult.setRejectMessage(null);
                } else {
                    voucherCodeResult.setRejectMessage("basket.vouchercode.invalid");
                    voucherService.releaseVoucher(voucherCode, cartModel);
                }
                return voucherCodeResult;
            }
            // Get the number of promotions before and after the recalculation. If a promotion has been applied due to
            // the voucher then the promotional results will be bigger after the recalculation.
            // final int before = getPromotionCount(cartModel);
            // commerceCartService.recalculateCart(cartModel);
            // final int after = getPromotionCount(cartModel);

            // if (after > before)
            // {
            // // Promotional voucher successfully applied
            // promoCodeResult.setApplied(true);
            // promoCodeResult.setRejectMessage(null);
            // return promoCodeResult;
            // }
            // else
            // {
            // // potentialMessage = getPromoPotentialMessage(promotionCode);
            // promoCodeResult.setRejectMessage("basket.promotioncode.redeem.unsuccessful");
            // // release all vouchers
            // for (final String voucher : voucherService.getAppliedVoucherCodes(cartModel))
            // {
            // voucherService.releaseVoucher(voucher, cartModel);
            // }
            // }
        } catch (final JaloPriceFactoryException ex) {
            LOG.error("error redeeming voucher --" + voucherCode, ex);
            return new VoucherCodeResult(false);
        }

        return voucherCodeResult;
    }

    @Override
    public boolean releaseVoucherCode(final String voucherCode) {
        final CartModel cartModel = cartService.getSessionCart();
        final Collection<String> appliedVoucherCodes = voucherService.getAppliedVoucherCodes(cartModel);
        try {
            if (CollectionUtils.isNotEmpty(appliedVoucherCodes) && appliedVoucherCodes.contains(voucherCode)) {
                voucherService.releaseVoucher(voucherCode, cartModel);
            }
            final CommerceCartParameter parameter = new CommerceCartParameter();
            parameter.setEnableHooks(true);
            parameter.setCart(cartModel);
            commerceCartService.recalculateCart(parameter);

        } catch (final JaloPriceFactoryException | CalculationException ex) {
            LOG.error("Error releasing voucher --" + voucherCode, ex);
            return false;
        }
        return true;
    }

    @Override
    public Collection<String> getAppliedVoucherCodes() {
        final CartModel cartModel = cartService.getSessionCart();
        return voucherService.getAppliedVoucherCodes(cartModel);
    }

    /**
     * Get Applicable Vouchers for product with specific code
     * 
     * @return List list of vouchers
     */

    @Override
    public List<PromotionVoucherModel> getApplicableVouchers(final ProductModel productModel) {
        final List<PromotionVoucherModel> applicableVouchers = new ArrayList<PromotionVoucherModel>();
        for (final VoucherModel voucher : getAllVouchers()) {
            if (voucher instanceof PromotionVoucherModel) {
                final PromotionVoucherModel promotionVoucher = (PromotionVoucherModel) voucher;
                if (BooleanUtils.toBoolean(promotionVoucher.getEnable())
                        && isApplicable(voucher, null, productModel, userService.getCurrentUser())) {
                    applicableVouchers.add((PromotionVoucherModel) voucher);

                }
            }
        }
        return applicableVouchers;
    }

    /**
     * Checks if is applicable.
     * 
     * @param voucherModel
     *        the voucher model
     * @param orderModel
     *        the order model
     * @param productModel
     *        the product model
     * @param userModel
     *        the user model
     * @return true, if is applicable
     */
    private boolean isApplicable(final VoucherModel voucherModel, final AbstractOrderModel orderModel, final ProductModel productModel,
            final UserModel userModel) {
        final Voucher voucher = getVoucher(voucherModel);
        if (null != orderModel) {
            return voucher.isApplicable(getAbstractOrder(orderModel));
        } else {
            boolean isFulfilled = true;
            for (final Iterator<RestrictionModel> iterator = voucherModel.getRestrictions().iterator(); iterator.hasNext();) {
                final RestrictionModel nextRestriction = iterator.next();
                if (nextRestriction instanceof ProductRestrictionModel || nextRestriction instanceof ProductCategoryRestrictionModel) {
                    isFulfilled = voucher.isApplicable(getProduct(productModel));
                } else if (nextRestriction instanceof DateRestrictionModel) {
                    isFulfilled = isApplicable((DateRestrictionModel) nextRestriction, new Date());
                } else if (nextRestriction instanceof UserRestrictionModel) {
                    isFulfilled = isApplicable((UserRestrictionModel) nextRestriction, userModel);
                }
                if (!isFulfilled) {
                    break;
                }
            }
            return isFulfilled;
        }
    }

    /**
     * Checks if is applicable.
     * 
     * @param restriction
     *        the restriction
     * @param user
     *        the user
     * @return true, if is applicable
     */
    private boolean isApplicable(final UserRestrictionModel restriction, final UserModel user) {
        for (final Iterator<PrincipalModel> iterator = restriction.getUsers().iterator(); iterator.hasNext();) {
            final PrincipalModel nextPrincipal = iterator.next();
            if (nextPrincipal instanceof UserModel) {
                if (((UserModel) nextPrincipal).getPk().equals(user.getPk()) == BooleanUtils.toBoolean(restriction.getPositive())) {
                    return true;
                }
            } else if ((nextPrincipal instanceof UserGroupModel)
                    && (((UserGroupModel) nextPrincipal).getMembers().contains(user) == BooleanUtils.toBoolean(restriction.getPositive()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is applicable.
     * 
     * @param restriction
     *        the restriction
     * @param currentDate
     *        the current Date
     * @return true, if is applicable
     */
    private boolean isApplicable(final DateRestrictionModel restriction, final Date currentDate) {
        final Date startDate = restriction.getStartDate();
        final Date endDate = restriction.getEndDate();
        final boolean start = (startDate == null) || (startDate.before(currentDate) == BooleanUtils.toBoolean(restriction.getPositive()));
        final boolean end = (endDate == null) || (endDate.after(currentDate) == BooleanUtils.toBoolean(restriction.getPositive()));
        return ((start) && (end));
    }

    public int getPromotionCount(final CartModel cartModel) {
        return (promotionsService.getPromotionResults(cartModel).getAppliedOrderPromotions().size() + promotionsService
                .getPromotionResults(cartModel).getAppliedProductPromotions().size());
    }

}
