package in.com.v2kart.facades.order.impl;

//import in.com.v2kart.checkoutaddon.storefront.forms.V2PaymentInfoForm;
import in.com.v2kart.core.payment.services.V2PaymentService;
import in.com.v2kart.facades.cart.impl.DefaultV2CartFacade;
import in.com.v2kart.facades.order.V2CheckoutFacade;
import in.com.v2kart.facades.order.data.V2StoreCreditPaymentInfoData;
import in.com.v2kart.facades.payment.V2PaymentFacade;
import in.com.v2kart.facades.storeCredit.StoreCreditFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.converters.populator.AddressPopulator;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2CODPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

//import in.com.v2kart.checkoutaddon.storefront.forms.V2PaymentInfoForm;
public class V2CheckoutFacadeImpl extends DefaultAcceleratorCheckoutFacade implements V2CheckoutFacade {

    private KeyGenerator uidKeyGenerator;
    private AddressPopulator addressPopulator;
    private AddressReversePopulator addressReversePopulator;
    private V2PaymentService v2PaymentService;
    /**The store credit service. */
    private StoreCreditFacade storeCreditFacade;
    private V2PaymentFacade paymentFacade;
    @Autowired
    private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
    
    @Autowired
    private DefaultV2CartFacade cartFacade;


    private final String CASH_PAYMENT_MODE = "CASH";
    private final String PAYMENT_GATEWAY = "COD";

    @Override
    public boolean hasNoServiceableMode() {
        return getSupportedDeliveryModes().isEmpty() ? true : false;
    }

    @Override
    public void assignCodPaymentMode(final AddressData adressData) {
        final CartModel cartModel = getCart();

        final PaymentInfoModel codPaymentInfo = getModelService().create(V2CODPaymentInfoModel.class);
        codPaymentInfo.setPaymentMode(v2PaymentService.getV2PaymentMode(CASH_PAYMENT_MODE));
        codPaymentInfo.setPaymentGateway(PAYMENT_GATEWAY);
        populatePaymentInfo(adressData, cartModel, codPaymentInfo);
    }

    @Override
    public void useMyWalletMoney(CartData cartData, AddressData address) {
        final Double storeCredit = storeCreditFacade.queryBalance();
        final boolean isOnlyStoreCredit = isOnlyStoreCredit(storeCredit, cartData);

        if (storeCredit != null && storeCredit.intValue() != 0.0) {

            V2StoreCreditPaymentInfoData storeCreditPaymentInfoData = new V2StoreCreditPaymentInfoData();
            storeCreditPaymentInfoData.setBillingAddress(address);

            if (isOnlyStoreCredit) {
                storeCreditPaymentInfoData.setStoreCreditAmount(cartData.getTotalPrice().getValue());
            } else {
                storeCreditPaymentInfoData.setStoreCreditAmount(BigDecimal.valueOf(storeCredit.doubleValue()));
            }
            setUpStoreCreditPaymentInfoModel(storeCreditPaymentInfoData);
            cartData.setStoreCreditPaymentInfo(storeCreditPaymentInfoData);
            setTotalPayableBalance(cartData);
        }
    }
    
    @Override 
    protected void beforePlaceOrder(final CartModel cartModel)
    {
        getPaymentFacade().redeemStoreCredit(cartModel);
        super.beforePlaceOrder(cartModel);
    }
    

    @Override
    public CartData getCheckoutCart() {
        CartModel cartModel = getCart();
        PaymentInfoModel storeCreditPaymentInfo = cartModel.getStoreCreditPaymentInfo();
        if (storeCreditPaymentInfo != null && storeCreditPaymentInfo instanceof V2StoreCreditPaymentInfoModel) {
            getModelService().remove(storeCreditPaymentInfo);
            getModelService().refresh(cartModel);
        }
        final CartData cartData = cartFacade.getSessionCart();
   		if (cartData != null)
   		{
   			if(!(cartData.isIsPickup() && cartData.getDeliveryAddress() != null))
   			{
   				cartData.setDeliveryAddress(getDeliveryAddress());
   			}
   			
   			cartData.setDeliveryMode(getDeliveryMode());
   			cartData.setPaymentInfo(getPaymentDetails());
   		}

   		return cartData;
   	

    }
    
    @Override
    public void changeCartDeliveryMode(){
       
        CartModel cartModel = getCart();
        cartModel.setIsPickup(true);
          getModelService().save(cartModel);
          getModelService().refresh(cartModel);
    }
    
    @Override
    public void changeCartDeliveryModeToShip(){
        
        	CartModel cartModel = getCart();
        	cartModel.setIsPickup(false);
          getModelService().save(cartModel);
          getModelService().refresh(cartModel);
    }
    private void setTotalPayableBalance(CartData cartData) {
        CartModel cartModel = getCart();
        cartData.setTotalPayableBalance(createPrice(cartModel, cartModel.getTotalPayableBalance().doubleValue()));
    }

    protected PriceData createPrice(final AbstractOrderModel source, final Double val)
    {
        if (source == null)
        {
            throw new IllegalArgumentException("source order must not be null");
        }

        final CurrencyModel currency = source.getCurrency();
        if (currency == null)
        {
            throw new IllegalArgumentException("source order currency must not be null");
        }

        // Get double value, handle null as zero
        final double priceValue = val != null ? val.doubleValue() : 0d;

        return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currency);
    }

    private void setUpStoreCreditPaymentInfoModel(final V2StoreCreditPaymentInfoData storeCreditPaymentInfoData) {
        final CartModel cartModel = getCart();

        if (cartModel != null) {
            if (checkIfCurrentUserIsTheCartUser()) {

                V2StoreCreditPaymentInfoModel storeCreditPaymentInfoModel = null;

                if (cartModel.getStoreCreditPaymentInfo() != null) {
                    storeCreditPaymentInfoModel = (V2StoreCreditPaymentInfoModel) cartModel.getStoreCreditPaymentInfo();
                } else {
                    storeCreditPaymentInfoModel = getModelService().create(V2StoreCreditPaymentInfoModel.class);
                    storeCreditPaymentInfoModel.setCode(generateNewPaymentUid());
                    storeCreditPaymentInfoModel.setUser(cartModel.getUser());
                }
                final AddressModel billingAddress = getModelService().create(AddressModel.class);
                AddressModel address = cartModel.getPaymentAddress();
                if(null == address){
                    address = cartModel.getDeliveryAddress();
                }
                addressPopulator.populate(address, storeCreditPaymentInfoData.getBillingAddress());
//                addressPopulator.populate(cartModel.getDeliveryAddress(), storeCreditPaymentInfoData.getBillingAddress());
                addressReversePopulator.populate(storeCreditPaymentInfoData.getBillingAddress(), billingAddress);
                billingAddress.setOriginal(cartModel.getDeliveryAddress());
                billingAddress.setOwner(getCurrentUserForCheckout());
                billingAddress.setBillingAddress(Boolean.TRUE);
                billingAddress.setShippingAddress(Boolean.FALSE);
                storeCreditPaymentInfoModel.setBillingAddress(billingAddress);

                storeCreditPaymentInfoModel.setStoreCreditAmount(storeCreditPaymentInfoData.getStoreCreditAmount());
                v2PaymentService.setStoreCreditPaymentInfo(cartModel, storeCreditPaymentInfoModel);
            }
        }
    }

    private boolean isOnlyStoreCredit(final Double storeCredit, final CartData cartData) {
        final int returnVal = Double.compare(storeCredit.doubleValue(), cartData.getTotalPrice().getValue().doubleValue());

        if (storeCredit != null && returnVal >= 0) {
            return true;
        }
        return false;
    }

    /**
     * @param addressData
     * @param cartModel
     * @param paymentInfo
     */
    private void populatePaymentInfo(final AddressData addressData, final CartModel cartModel, final PaymentInfoModel paymentInfo) {
        paymentInfo.setSaved(true);
        paymentInfo.setUser(getCurrentUserForCheckout());
        paymentInfo.setCode(generateNewPaymentUid());
        this.populateBillingAddress(addressData, cartModel, paymentInfo);
        cartModel.setPaymentInfo(paymentInfo);
        getModelService().saveAll(cartModel, paymentInfo);
        getModelService().refresh(cartModel);
    }

    protected String generateNewPaymentUid() {
        if (uidKeyGenerator != null) {
            return (String) uidKeyGenerator.generate();
        } else {
            return null;
        }
    }

    private void populateBillingAddress(final AddressData addressData, final CartModel cartModel, final PaymentInfoModel paymentInfo) {
        if (addressData != null) {
            final AddressModel addressModel = getModelService().create(AddressModel.class);
            addressReversePopulator.populate(addressData, addressModel);
            addressModel.setOwner(paymentInfo);
            addressModel.setBillingAddress(Boolean.TRUE);
            getModelService().save(addressModel);
            paymentInfo.setBillingAddress(addressModel);
        } else {
            final AddressModel billingAddressModel = getModelService().create(AddressModel.class);
            final AddressData billingAddressData = new AddressData();
            addressPopulator.populate(cartModel.getDeliveryAddress(), billingAddressData);
            addressReversePopulator.populate(billingAddressData, billingAddressModel);
            billingAddressModel.setBillingAddress(Boolean.TRUE);
            billingAddressModel.setShippingAddress(Boolean.FALSE);
            billingAddressModel.setOwner(paymentInfo);
            paymentInfo.setBillingAddress(billingAddressModel);
        }
    }

    public void setUidKeyGenerator(KeyGenerator uidKeyGenerator) {
        this.uidKeyGenerator = uidKeyGenerator;
    }

    public void setAddressPopulator(AddressPopulator addressPopulator) {
        this.addressPopulator = addressPopulator;
    }

    public void setAddressReversePopulator(AddressReversePopulator addressReversePopulator) {
        this.addressReversePopulator = addressReversePopulator;
    }

    public void setV2PaymentService(V2PaymentService v2PaymentService) {
        this.v2PaymentService = v2PaymentService;
    }

    @Required
    public void setStoreCreditFacade(StoreCreditFacade storeCreditFacade) {
        this.storeCreditFacade = storeCreditFacade;
    }
    
    public V2PaymentFacade getPaymentFacade() {
        return paymentFacade;
    }
    
    @Required
    public void setPaymentFacade(V2PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }
    
    @Override
    public boolean setDeliveryModeIfAvailable()
    {
    final CartModel cartModel = getCart();
    if (cartModel != null)
    {
    // validate delivery mode if already exists
    final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
    parameter.setEnableHooks(true);
    parameter.setCart(cartModel);
    getCommerceCheckoutService().validateDeliveryMode(parameter);


    final List<? extends DeliveryModeData> availableDeliveryModes = getSupportedDeliveryModes();
    if (!availableDeliveryModes.isEmpty())
    {
            final List<DeliveryModeData> selectedDeliveryMode = new ArrayList<DeliveryModeData>();
            for (final DeliveryModeData selectDeliveryMode : availableDeliveryModes) {
                if (cartModel.getIsPickup()) {
                    if (selectDeliveryMode.isIsPickUp()) {
                        selectedDeliveryMode.add(selectDeliveryMode);
                    }
                } else {
                    if (!selectDeliveryMode.isIsPickUp()) {
                        selectedDeliveryMode.add(selectDeliveryMode);
                    }
                }
            }

            if (cartModel.getDeliveryMode() == null)
            {
                return setDeliveryMode(selectedDeliveryMode.get(0).getCode());
            }
            else{
                if(!(cartModel.getDeliveryMode().getIsPickUp().compareTo(cartModel.getIsPickup())==0 )){
                    return setDeliveryMode(selectedDeliveryMode.get(0).getCode());
                }
            }
    }
    return true;
    }
    return false;
    }

	@Override
	public void storeDeliveryOwner(String store) {
		CartModel cart = getCart();
		AddressModel address = cart.getDeliveryAddress();
		address.setCompany(store);
		getModelService().save(address);
		getModelService().refresh(address);
		getModelService().refresh(cart);
		
	}
    
    @Override
	public boolean setDeliveryAddressIfAvailableForNewRegister()
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			if (cartModel.getDeliveryAddress() == null)
			{
				final UserModel currentUser = getCurrentUserForCheckout();
				if (cartModel.getUser().equals(currentUser))
				{
					final AddressModel currentUserDefaultShipmentAddress = currentUser.getDefaultShipmentAddress();
					if (currentUserDefaultShipmentAddress != null)
					{
						final AddressModel supportedDeliveryAddress = getDeliveryAddressModelForCode(currentUserDefaultShipmentAddress
								.getPk().toString());
						if (supportedDeliveryAddress != null)
						{
							final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
							parameter.setEnableHooks(true);
							parameter.setCart(cartModel);
							parameter.setAddress(supportedDeliveryAddress);
							parameter.setIsDeliveryAddress(false);
							return getCommerceCheckoutService().setDeliveryAddress(parameter);
						}
					}
					else{
						commerceCartCalculationStrategy.calculateCart(cartModel);
					}
				}

				// Could not use default address, try any address
				final List<AddressModel> supportedDeliveryAddresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(
						cartModel, true);
				if (supportedDeliveryAddresses != null && !supportedDeliveryAddresses.isEmpty())
				{
					final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
					parameter.setEnableHooks(true);
					parameter.setCart(cartModel);
					parameter.setAddress(supportedDeliveryAddresses.get(0));
					parameter.setIsDeliveryAddress(false);
					return getCommerceCheckoutService().setDeliveryAddress(parameter);
				}
			}
		}

		return false;
	}
    @Override 
    protected void afterPlaceOrder(final CartModel cartModel, final OrderModel orderModel)
	{
		if (orderModel != null)
		{
			String cartCode = cartModel.getCode();
			// Remove cart
			getCartService().removeSessionCart();
			getModelService().refresh(orderModel);
			
			// save cart code in ordermodel
			orderModel.setCartCode(cartCode);
			getModelService().save(orderModel);
		}
	}
    
}
