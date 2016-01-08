package in.com.v2kart.facades.cart.impl;

import in.com.v2kart.core.vouchers.V2VoucherService;
import in.com.v2kart.core.vouchers.VoucherCodeResult;
import in.com.v2kart.facades.cart.V2CartFacade;
import in.com.v2kart.facades.order.V2CheckoutFacade;
import in.com.v2kart.facades.voucher.data.V2VoucherData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.voucher.model.PromotionVoucherModel;
import de.hybris.platform.voucher.model.VoucherModel;

/**
 * 
 * @author vikrant2480
 * 
 */
public class DefaultV2CartFacade extends DefaultCartFacade implements V2CartFacade {

    V2VoucherService v2VoucherService;
    
    @Autowired
    private CustomerFacade customerFacade;
    
    @Autowired
    private V2CheckoutFacade checkoutFacade;

    @Autowired
    private AbstractConverter<VoucherModel, V2VoucherData> v2VoucherConverter;
    
    @Autowired
    private ModelService modelService;

    @Override
    public void updateGiftWrapForEntry(final int entryNo, boolean giftWrapped) {
        final CartModel cartModel = getCartService().getSessionCart();
        List<AbstractOrderEntryModel> orderEntries = cartModel.getEntries();
        if (CollectionUtils.isNotEmpty(orderEntries)) {
            orderEntries.get(entryNo).setGiftWrap(giftWrapped);
        }
    }

    @Override
    public void disableAllGiftWrap() {
        final CartModel cartModel = getCartService().getSessionCart();
        List<AbstractOrderEntryModel> orderEntries = cartModel.getEntries();
        if (CollectionUtils.isNotEmpty(orderEntries)) {
            for (AbstractOrderEntryModel orderEntry : orderEntries) {
                orderEntry.setGiftWrap(false);
            }
        }
        calculateCartTotal();
    }

    @Override
    public void calculateCartTotal() {
        final CartModel cartModel = getCartService().getSessionCart();
        cartModel.setCalculated(false);
        getCartService().calculateCart(cartModel);
        getCartService().saveOrder(cartModel);
    }

    @Override
    public VoucherCodeResult applyVoucherCode(String voucherCode) throws CalculationException {
        return v2VoucherService.applyVoucher(voucherCode);
    }

    @Override
    public boolean releaseVoucherCode(String voucherCode) {
        return v2VoucherService.releaseVoucherCode(voucherCode);
    }

    @Override
    public Collection<String> getAppliedVoucherCodes() {
        return v2VoucherService.getAppliedVoucherCodes();
    }

    @Override
    public List<V2VoucherData> getApplicableVouchers(ProductModel productModel) {
        final List<PromotionVoucherModel> voucherModels = v2VoucherService.getApplicableVouchers(productModel);
        final List<V2VoucherData> voucherList = new ArrayList<V2VoucherData>();

        for (final PromotionVoucherModel promotionVoucherModel : voucherModels) {
            final V2VoucherData voucherData = v2VoucherConverter.convert(promotionVoucherModel);
            voucherList.add(voucherData);
        }
        Collections.sort(voucherList);
        return voucherList;
    }

    public void setV2VoucherService(V2VoucherService v2VoucherService) {
        this.v2VoucherService = v2VoucherService;
    }

	@Override
	public void updateName() {
		AddressModel address = getCartService().getSessionCart().getDeliveryAddress();
		address.setFirstname(customerFacade.getCurrentCustomer().getFirstName());
		address.setLastname(customerFacade.getCurrentCustomer().getLastName());
		modelService.save(address);
		modelService.refresh(address);
	}

	@Override
	public void updateAddress() {
		CartModel cart = getCartService().getSessionCart();
		cart.setDeliveryAddress(null);
		modelService.save(cart);
		modelService.refresh(cart);
	}

	@Override
	public void removeCart() {
		CartModel cart = getCartService().getSessionCart();
		modelService.remove(cart);
		
	}
	
	@Override
	public void removeCOD(){
		CartModel cart = getCartService().getSessionCart();
		cart.setCodCharges(0.0);
		modelService.save(cart);
		modelService.refresh(cart);
		
	}

}
