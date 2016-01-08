/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.services.impl;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import in.com.v2kart.core.model.EBSPaymentResponseInfoModel;
import in.com.v2kart.core.model.V2PaymentResponseInfoModel;
import in.com.v2kart.core.payment.services.V2PaymentResponseStrategy;
import in.com.v2kart.core.payment.services.impl.V2PaymentServiceImpl;
import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSActionRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSPaymentRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.response.EBSPaymentResponse;
import in.com.v2kart.v2kartebspaymentintegration.enums.EBSDEecisionEnum;
import in.com.v2kart.v2kartebspaymentintegration.services.CreateEBSPaymentRequestStrategy;
import in.com.v2kart.v2kartebspaymentintegration.services.EBSPaymentService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * @author vikrant2480
 * 
 */
public class EBSPaymentServiceImpl extends V2PaymentServiceImpl implements EBSPaymentService {

    private static final Logger LOG = Logger.getLogger(EBSPaymentServiceImpl.class);

    private CreateEBSPaymentRequestStrategy createEBSPaymentRequestStrategy;

    private V2PaymentResponseStrategy ebsPaymentResponseStrategy;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.services.EBSPaymentService#beginHopCreatePayment(java.lang.String, java.lang.String,
     * de.hybris.platform.commercefacades.user.data.AddressData, de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String,
     * de.hybris.platform.core.model.user.CustomerModel)
     */
    @Override
    public EBSPaymentRequest beginHopCreatePayment(final String description, final String returnUrl, final AddressData billingAddressData,
            final String phoneNumber, final CustomerModel customer) {

        final String requestUrl = getSiteConfigService().getProperty(EBS.HOP_POST_REQUEST_URL);
        Assert.notNull(requestUrl, "The EBS HopRequestUrl cannot be null");

        final EBSPaymentRequest request = createEBSPaymentRequestStrategy.createPaymentRequest(
                getSiteConfigService().getProperty(EBS.HOP_POST_SECURE_KEY), requestUrl,
                getSiteConfigService().getProperty(EBS.HOP_PAYMENT_ACCOUNT_ID),
                getSiteConfigService().getProperty(EBS.HOP_PAYMENT_MODE),
                description, returnUrl, billingAddressData, phoneNumber, customer);

        return request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.services.EBSPaymentService#beginHopRefundPayment(java.lang.String, java.lang.String,
     * de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String, de.hybris.platform.core.model.user.CustomerModel)
     */
    @Override
    public EBSActionRequest createActionRequest(final String paymentId, final String refundAmount,
            final String orderCode, final String action) {

        final String requestUrl = getSiteConfigService().getProperty(EBS.REFUND_URL);
        Assert.notNull(requestUrl, "The EBS HopRequestUrl cannot be null");

        final EBSActionRequest request = createEBSPaymentRequestStrategy.createActionRequest(action,
                getSiteConfigService().getProperty(EBS.HOP_POST_SECURE_KEY),
                requestUrl, getSiteConfigService().getProperty(EBS.HOP_PAYMENT_ACCOUNT_ID), paymentId, refundAmount, orderCode);
        return request;
    }

    /**
     * @param createEBSPaymentRequestStrategy
     *        the createEBSPaymentRequestStrategy to set
     */
    @Required
    public void setCreateEBSPaymentRequestStrategy(final CreateEBSPaymentRequestStrategy createEBSPaymentRequestStrategy) {
        this.createEBSPaymentRequestStrategy = createEBSPaymentRequestStrategy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.services.EBSPaymentService#savePaymentInfo(java.util.Map)
     */
    @Override
    public void savePaymentInfo(final Map<String, String> parameters) {
        getModelService().save(getEBSPaymentResponseInfoModel(parameters));
    }

    /**
     * @param parameters
     * @return
     */
    private EBSPaymentResponseInfoModel getEBSPaymentResponseInfoModel(final Map<String, String> parameters) {

        final EBSPaymentResponseInfoModel ebsPaymentResponseInfoModel = this.modelService.create(EBSPaymentResponseInfoModel.class);
        ebsPaymentResponseInfoModel.setResponseCode(parameters.get(EBS.ResponseParameters.RESPONSE_CODE.getValue()));
        ebsPaymentResponseInfoModel.setResponseMessage(parameters.get(EBS.ResponseParameters.RESPONSE_MESSAGE.getValue()));
        ebsPaymentResponseInfoModel.setDateCreated(parameters.get(EBS.ResponseParameters.DATE_CREATED.getValue()));
        ebsPaymentResponseInfoModel.setPaymentID(parameters.get(EBS.ResponseParameters.PAYMENT_ID.getValue()));
        ebsPaymentResponseInfoModel.setMerchantReferenceNumber(parameters.get(EBS.ResponseParameters.MERCHANT_REFERENCE_NUMBER.getValue()));
        ebsPaymentResponseInfoModel.setAmount(parameters.get(EBS.ResponseParameters.AMOUNT.getValue()));
        ebsPaymentResponseInfoModel.setMode(parameters.get(EBS.ResponseParameters.MODE.getValue()));
        ebsPaymentResponseInfoModel.setName(parameters.get(EBS.ResponseParameters.BILLING_NAME.getValue()));
        ebsPaymentResponseInfoModel.setAddress(parameters.get(EBS.ResponseParameters.BILLING_ADDRESS.getValue()));
        ebsPaymentResponseInfoModel.setCity(parameters.get(EBS.ResponseParameters.BILLING_CITY.getValue()));
        ebsPaymentResponseInfoModel.setPostalCode(parameters.get(EBS.ResponseParameters.BILLING_POSTAL_CODE.getValue()));
        ebsPaymentResponseInfoModel.setState(parameters.get(EBS.ResponseParameters.BILLING_STATE.getValue()));
        ebsPaymentResponseInfoModel.setCountry(parameters.get(EBS.ResponseParameters.BILLING_COUNTRY.getValue()));
        ebsPaymentResponseInfoModel.setPhone(parameters.get(EBS.ResponseParameters.BILLING_PHONE.getValue()));
        ebsPaymentResponseInfoModel.setDescription(parameters.get(EBS.ResponseParameters.DESCRIPTION.getValue()));
        ebsPaymentResponseInfoModel.setEmail(parameters.get(EBS.ResponseParameters.BILLING_EMAIL.getValue()));
        ebsPaymentResponseInfoModel.setIsFlagged(parameters.get(EBS.ResponseParameters.IS_FLAGGED.getValue()));
        ebsPaymentResponseInfoModel.setPaymentMethod(parameters.get(EBS.ResponseParameters.PAYMENT_METHOD.getValue()));
        ebsPaymentResponseInfoModel.setTxnid(parameters.get(EBS.ResponseParameters.TRANSACTION_ID.getValue()));
        return ebsPaymentResponseInfoModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.services.EBSPaymentService#capture(de.hybris.platform.core.model.user.CustomerModel,
     * java.util.Map, de.hybris.platform.core.model.order.CartModel)
     */
    @Override
    public EBSPaymentResponse capture(final CustomerModel customerModel, final Map<String, String> parameters, final CartModel cartModel) {
        final EBSPaymentResponse response = (EBSPaymentResponse) ebsPaymentResponseStrategy.interpretResponse(parameters);
        if (EBSDEecisionEnum.SUCCESS.getValue().equalsIgnoreCase(response.getResponseCode())) {
            final V2PGPaymentInfoModel v2PGPaymentInfoModel = getPGPaymentInfoModel(response);
            v2PGPaymentInfoModel.setPaymentGateway(EBS.PAYMENT_GATEWAY);
            v2PGPaymentInfoModel.setMode(response.getDescripton());
            v2PGPaymentInfoModel.setPaymentMode(getV2PaymentDao().getV2PaymentMode(EBS.PAYMENT_MODE + response.getDescripton()));
            LOG.debug("EBS Payment Response : " + EBS.PAYMENT_MODE + response.getDescripton());
            final AddressModel addressModel = getBillingAddress(cartModel, response, v2PGPaymentInfoModel);
            v2PGPaymentInfoModel.setBillingAddress(addressModel);
            this.modelService.saveAll(v2PGPaymentInfoModel, addressModel);
            final PaymentTransactionModel transaction = (PaymentTransactionModel) this.modelService.create(PaymentTransactionModel.class);
            transaction.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
            transaction.setPaymentProvider(EBS.PAYMENT_MODE);
            this.modelService.save(transaction);
            final PaymentTransactionType transactionType = PaymentTransactionType.CAPTURE;
            final PaymentTransactionEntryModel entry = getPaymentTransactionEntry(response, transaction, transactionType);

            this.modelService.save(entry);
            this.modelService.refresh(transaction);

            transaction.setOrder(cartModel);
            cartModel.setPaymentInfo(v2PGPaymentInfoModel);
            this.modelService.saveAll(cartModel, transaction);
            this.modelService.refresh(cartModel);
        }
        return response;
    }

    private PaymentTransactionEntryModel getPaymentTransactionEntry(final EBSPaymentResponse response,
            final PaymentTransactionModel transaction, final PaymentTransactionType transactionType) {
        final String newEntryCode = getNewPaymentTransactionEntryCode(transaction, transactionType);

        final PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.modelService
                .create(PaymentTransactionEntryModel.class);
        entry.setAmount(new BigDecimal(response.getAmount()));
        entry.setType(transactionType);
        entry.setRequestId(response.getPaymentId());
        entry.setTime(new Date());
        entry.setPaymentTransaction(transaction);
        entry.setTransactionStatus(response.getResponseMessage());
        entry.setCode(newEntryCode);
        entry.setCurrency(getCommonI18NService().getCurrentCurrency());
        return entry;
    }

    private AddressModel getBillingAddress(final CartModel cartModel, final EBSPaymentResponse response,
            final V2PGPaymentInfoModel externalPaymentInfoModel) {
        AddressModel addressModel = null;
        if (null != cartModel.getPaymentAddress()) {
            final AddressModel paymentAddress = cartModel.getPaymentAddress();
            addressModel = getBillingAddress(externalPaymentInfoModel, paymentAddress);
            getModelService().remove(paymentAddress);
        } else {
            final AddressModel shippingaddress = cartModel.getDeliveryAddress();
            addressModel = getBillingAddress(externalPaymentInfoModel, shippingaddress);
        }
        return addressModel;
    }

    /**
     * @param ebsPaymentResponseStrategy
     *        the ebsPaymentResponseStrategy to set
     */
    @Required
    public void setEbsPaymentResponseStrategy(final V2PaymentResponseStrategy ebsPaymentResponseStrategy) {
        this.ebsPaymentResponseStrategy = ebsPaymentResponseStrategy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.services.EBSPaymentService#getPaymentIdForTransaction(java.lang.String)
     */
    @Override
    public String getPaymentIdForTransaction(final String txnid) {

        String paymentId = null;
        final V2PaymentResponseInfoModel v2PaymentResponseInfoModel = getV2PaymentDao().getV2PaymentResponseInfoModel(txnid);

        if (v2PaymentResponseInfoModel instanceof EBSPaymentResponseInfoModel) {
            paymentId = ((EBSPaymentResponseInfoModel) v2PaymentResponseInfoModel).getPaymentID();
        }
        return paymentId;
    }

}
