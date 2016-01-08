package in.com.v2kart.core.payment.services.impl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import in.com.v2kart.core.dao.BaseDao;
import in.com.v2kart.core.model.V2PaymentModeModel;
import in.com.v2kart.core.payment.dao.V2PaymentDao;
import in.com.v2kart.core.payment.data.response.V2PaymentResponse;
import in.com.v2kart.core.payment.services.V2PaymentService;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import in.com.v2kart.core.payment.constants.PaymentConstants;
import in.com.v2kart.fulfilmentprocess.services.V2StoreCreditService;

public class V2PaymentServiceImpl extends DefaultPaymentServiceImpl implements V2PaymentService {
    public final static String WALLET = "V2-Wallet";
    private CartService cartService;
    private CommonI18NService commonI18NService;
    protected ModelService modelService;
    private SiteConfigService siteConfigService;
  

    private UserService userService;
    private KeyGenerator keyGenerator;

    @Resource(name = "titleModelDao")
    private BaseDao<TitleModel> titleModelDao;

    @Resource(name = "v2PaymentDao")
    private V2PaymentDao v2PaymentDao;

    /** StoreCreditService bean injection. */
    private V2StoreCreditService storeCreditService;

    /**
     * @return the cartService
     */
    public CartService getCartService() {
        return cartService;
    }

    /**
     * @param cartService
     *        the cartService to set
     */
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * @return the commonI18NService
     */
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    /**
     * @param commonI18NService
     *        the commonI18NService to set
     */
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @return the siteConfigService
     */
    public SiteConfigService getSiteConfigService() {
        return siteConfigService;
    }

    /**
     * @param siteConfigService
     *        the siteConfigService to set
     */
    public void setSiteConfigService(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    /**
     * @return the v2PaymentDao
     */
    public V2PaymentDao getV2PaymentDao() {
        return v2PaymentDao;
    }

    /**
     * @param v2PaymentDao
     *        the v2PaymentDao to set
     */
    @Required
    public void setV2PaymentDao(V2PaymentDao v2PaymentDao) {
        this.v2PaymentDao = v2PaymentDao;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @param userService
     *        the userService to set
     */
    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @return the keyGenerator
     */
    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * @param keyGenerator
     *        the keyGenerator to set
     */
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    /**
     * @return the titleModelDao
     */
    public BaseDao<TitleModel> getTitleModelDao() {
        return titleModelDao;
    }

    /**
     * @param titleModelDao
     *        the titleModelDao to set
     */
    public void setTitleModelDao(BaseDao<TitleModel> titleModelDao) {
        this.titleModelDao = titleModelDao;
    }

    @Override
    public void saveBillingAddress(AddressData addressData) {
        final CartModel cartModel = getCartService().getSessionCart();
        if (null == cartModel) {
            return;
        }
        final AddressModel addressModel;
        final AddressModel shippingaddress = cartModel.getDeliveryAddress();
        // When Cart payment address is not updated and use shipping address is checked
        if (null == cartModel.getPaymentAddress() && null == addressData) {
            addressModel = getModelService().clone(shippingaddress);
            addressModel.setShippingAddress(Boolean.FALSE);
            addressModel.setOwner(cartModel);
            // When Cart payment address is not updated and billing address is added by customer
        } else if (null == cartModel.getPaymentAddress() && null != addressData) {
            addressModel = (AddressModel) getModelService().create(AddressModel.class);
            populateAddressModel(addressData, addressModel);
            addressModel.setOwner(cartModel);
            // When Cart payment address is updated and use shipping address is checked
        } else if (null != cartModel.getPaymentAddress() && null == addressData) {
            getModelService().remove(cartModel.getPaymentAddress());
            addressModel = getModelService().clone(shippingaddress);
            addressModel.setShippingAddress(Boolean.FALSE);
            addressModel.setOwner(cartModel);
        } else {
            // When Cart payment address is updated and use billing address is added by customer
            addressModel = cartModel.getPaymentAddress();
            populateAddressModel(addressData, addressModel);
        }
        addressModel.setBillingAddress(Boolean.TRUE);
        cartModel.setPaymentAddress(addressModel);
        getModelService().saveAll(cartModel, addressModel);

    }

    /**
     * Populate address model.
     * 
     * @param addressData
     *        the address data
     * @param addressModel
     *        the address model
     */
    private void populateAddressModel(final AddressData addressData, final AddressModel addressModel) {
        addressModel.setTitle(getTitleModelDao().findUniqueModelByCode(addressData.getTitleCode()));
        addressModel.setFirstname(addressData.getFirstName());
        addressModel.setLastname(addressData.getLastName());
        addressModel.setPhone1(addressData.getPhone());
        addressModel.setLine1(addressData.getLine1());
        addressModel.setLine2(addressData.getLine2());
        addressModel.setTown(addressData.getTown());
        addressModel.setPostalcode(addressData.getPostalCode());
        final CountryModel countryModel = getCommonI18NService().getCountry(PaymentConstants.PaymentProperties.INDIA_COUNTRY_ISO);
        addressModel.setCountry(countryModel);
        if (addressData.getRegion() != null) {
            addressModel.setRegion(getCommonI18NService().getRegion(countryModel, addressData.getRegion().getIsocode()));
        }

    }

    /**
     * Gets the billing address.
     * 
     * @param v2PGPaymentInfoModel
     *        the V2 PG payment info model
     * @param address
     *        the address
     * @return the billing address
     */
    protected AddressModel getBillingAddress(final V2PGPaymentInfoModel v2PGPaymentInfoModel, final AddressModel address) {
        final AddressModel addressModel;
        addressModel = modelService.clone(address);
        addressModel.setBillingAddress(Boolean.TRUE);
        addressModel.setShippingAddress(Boolean.FALSE);
        addressModel.setOwner(v2PGPaymentInfoModel);
        return addressModel;
    }

    /**
     * Gets the Payment gateway payment info model.
     * 
     * @param response
     *        the response
     * @return the Paymentgateway payment info model
     */
    /**
     * Gets the PG payment info model.
     * 
     * @param response
     *        the response
     * @return the V2PGPaymentInfoModel
     */
    protected V2PGPaymentInfoModel getPGPaymentInfoModel(final V2PaymentResponse response) {
        final V2PGPaymentInfoModel pgPaymentInfoModel = (V2PGPaymentInfoModel) this.modelService.create(V2PGPaymentInfoModel.class);

        pgPaymentInfoModel.setCode(keyGenerator.generate().toString());
        pgPaymentInfoModel.setUser(getUserService().getCurrentUser());
        pgPaymentInfoModel.setAmount(Double.valueOf(response.getAmount()));
        pgPaymentInfoModel.setTxnid(response.getTxnid());

        pgPaymentInfoModel.setBillingAddress(getUserService().getCurrentUser().getDefaultPaymentAddress());
        return pgPaymentInfoModel;
    }

    @Override
    public V2PaymentModeModel getV2PaymentMode(String code) {
        return v2PaymentDao.getV2PaymentMode(code);
    }

    @Override
    public boolean setStoreCreditPaymentInfo(final CartModel cartModel, final V2StoreCreditPaymentInfoModel storeCreditPaymentInfoModel) {
        ServicesUtil.validateParameterNotNull(cartModel, "Cart model cannot be null");
        ServicesUtil.validateParameterNotNull(storeCreditPaymentInfoModel, "store credit payment info model cannot be null");

        cartModel.setStoreCreditPaymentInfo(storeCreditPaymentInfoModel);
        cartModel.setPaymentInfo(storeCreditPaymentInfoModel);
        getModelService().saveAll(cartModel, storeCreditPaymentInfoModel, storeCreditPaymentInfoModel.getBillingAddress());
        getModelService().refresh(cartModel);
        return true;
    }

    @Override
    public PaymentTransactionEntryModel redeemStoreCredit(final String merchantTransactionCode, final BigDecimal totalAmount,
            final Currency currency, final CartModel cartModel) {
        final String debitMessage = "Redeemed for order " + cartModel.getCode();
        final String storeCreditRequestId = storeCreditService.consumeStoreCredit((CustomerModel) cartModel.getUser(), debitMessage,
                totalAmount.doubleValue());
        if (StringUtils.isNotEmpty(storeCreditRequestId)) {
            final PaymentTransactionModel transaction = this.modelService.create(PaymentTransactionModel.class);
            transaction.setCode(merchantTransactionCode);
            this.modelService.save(transaction);
            transaction.setOrder(cartModel);
            transaction.setPaymentProvider(WALLET);
            transaction.setRequestId(storeCreditRequestId);
            final PaymentTransactionEntryModel entry = this.createTransactionEntryForStoreCredit(transaction, totalAmount, currency,
                    storeCreditRequestId,
                    PaymentTransactionType.CAPTURE);
            this.modelService.saveAll(cartModel, transaction);
            return entry;
        }
        return null;
    }

    @Override
    public PaymentTransactionEntryModel refundFollowOn(OrderModel orderPreview, PaymentTransactionModel transaction, BigDecimal amount,
            Currency currency) throws AdapterException {

        // Store credit refund here
        String requestId = storeCreditService.refundStoreCredit((CustomerModel) transaction.getOrder().getUser(),
                "Refund for order " + transaction.getOrder().getCode(), amount.doubleValue());

        if (StringUtils.isNotEmpty(requestId)) {
            return this.createTransactionEntryForStoreCredit(transaction, amount, currency,
                    requestId,
                    PaymentTransactionType.REFUND_FOLLOW_ON);
        }
        return null;

    }

    /**
     * API is used to create Transaction Entry For Store Credit points.
     * 
     * @param transaction
     * @param totalAmount
     * @param currency
     * @param storeCreditRequestId
     * @param tnxType
     * @return
     */
    private PaymentTransactionEntryModel createTransactionEntryForStoreCredit(final PaymentTransactionModel transaction,
            final BigDecimal totalAmount,
            final Currency currency, final String storeCreditRequestId,
            final PaymentTransactionType tnxType) {
        final PaymentTransactionEntryModel entry = this.modelService
                .create(PaymentTransactionEntryModel.class);
        // final String newEntryCode = getNewEntryCode(transaction);
        entry.setRequestId(storeCreditRequestId);
        entry.setAmount(totalAmount);
        if (currency != null) {
            entry.setCurrency(this.commonI18NService.getCurrency(currency.getCurrencyCode()));
        }
        entry.setPaymentTransaction(transaction);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.toString());
        entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.toString());
        entry.setType(tnxType);
        entry.setCode("Code");// TODO
        entry.setTime(new Date());
        this.modelService.save(entry);
        return entry;
    }

    @Required
    public void setStoreCreditService(V2StoreCreditService storeCreditService) {
        this.storeCreditService = storeCreditService;
    }

}
