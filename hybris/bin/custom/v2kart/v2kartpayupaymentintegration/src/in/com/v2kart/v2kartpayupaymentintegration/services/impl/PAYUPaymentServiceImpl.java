/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.services.impl;

import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.model.PAYUPaymentResponseInfoModel;
import in.com.v2kart.core.model.V2PaymentResponseInfoModel;
import in.com.v2kart.core.payment.constants.PaymentConstants.PaymentProperties;
import in.com.v2kart.core.payment.services.V2PaymentResponseStrategy;
import in.com.v2kart.core.payment.services.impl.V2PaymentServiceImpl;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU.RequestParameters;
import in.com.v2kart.v2kartpayupaymentintegration.data.request.PAYUPaymentRequest;
import in.com.v2kart.v2kartpayupaymentintegration.data.response.PAYUPaymentResponse;
import in.com.v2kart.v2kartpayupaymentintegration.data.response.PayUAbstractCommandResponse;
import in.com.v2kart.v2kartpayupaymentintegration.data.response.PayURefundCommandResponse;
import in.com.v2kart.v2kartpayupaymentintegration.enums.PAYUDecisionEnum;
import in.com.v2kart.v2kartpayupaymentintegration.services.CreatePAYUPaymentRequestStrategy;
import in.com.v2kart.v2kartpayupaymentintegration.services.PAYUEncryptionService;
import in.com.v2kart.v2kartpayupaymentintegration.services.PAYUPaymentService;
import in.com.v2kart.v2kartpayupaymentintegration.services.PayUCommand;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * The Class PAYUPaymentServiceImpl.
 * 
 * @author Anuj
 */
public class PAYUPaymentServiceImpl extends V2PaymentServiceImpl implements PAYUPaymentService {

    private static final Logger LOG = Logger.getLogger(PAYUPaymentServiceImpl.class);

    private CreatePAYUPaymentRequestStrategy createPAYUPaymentRequestStrategy;

    private V2PaymentResponseStrategy payUPaymentResponseStrategy;

    private Converter<V2PaymentResponseInfoModel, PaymentData> payUPaymentResponseDataConverter;

    private PAYUEncryptionService payUEncryptionService;

    private PayUCommand payURefundPaymentCommand;

    public PayUCommand getPayURefundPaymentCommand() {
        return payURefundPaymentCommand;
    }

    public void setPayURefundPaymentCommand(final PayUCommand payURefundPaymentCommand) {
        this.payURefundPaymentCommand = payURefundPaymentCommand;
    }

    public PAYUEncryptionService getPayUEncryptionService() {
        return payUEncryptionService;
    }

    public void setPayUEncryptionService(final PAYUEncryptionService payUEncryptionService) {
        this.payUEncryptionService = payUEncryptionService;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public Map<String, String> getPaymentInfo(final String guid) {
        Map<String, String> parameters = new HashMap<String, String>(0);
        final V2PaymentResponseInfoModel responseInfoModel = getV2PaymentDao().getV2PaymentResponseInfoModel(guid);
        if (null != responseInfoModel) {
            final PaymentData data = getPayUPaymentResponseDataConverter().convert(responseInfoModel);
            parameters = data.getParameters();
        }
        return parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartpayupaymentintegration.services.PAYUPaymentService#beginHopCreatePayment(de.hybris.platform
     * .core.model.user. CustomerModel, java.lang.String, de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PAYUPaymentRequest beginHopCreatePayment(final CustomerModel customer, final String enforcedPaymentMethod,
            final AddressData addressData, final String successUrl, final String cancelUrl, final String failureUrl, final String pg,
            final String phoneNumber) {
        final String requestUrl = getSiteConfigService().getProperty(PAYU.HOP_POST_REQUEST_URL);
        Assert.notNull(requestUrl, "The PAYU HopRequestUrl cannot be null");

        final PAYUPaymentRequest request = getCreatePAYUPaymentRequestStrategy().createPaymentRequest(requestUrl, successUrl, cancelUrl,
                failureUrl, customer, getSiteConfigService().getProperty(PAYU.HOP_PAYMENT_MERCHANT_KEY), enforcedPaymentMethod,
                addressData, getSiteConfigService().getProperty(PAYU.HOP_PAYMENT_MERCHANT_SALT), pg, phoneNumber);

        return request;
    }

    @Override
    public void savePaymentInfo(final Map<String, String> parameters) {
        getModelService().save(getPayUPaymentResponseInfoModel(parameters));

    }

    @Override
    public PAYUPaymentResponse capture(final CustomerModel customerModel, final Map<String, String> parameters, final CartModel cartModel)
            throws AdapterException {
        final PAYUPaymentResponse response = (PAYUPaymentResponse) getPayUPaymentResponseStrategy().interpretResponse(parameters);
        if (PAYUDecisionEnum.SUCCESS.getValue().equalsIgnoreCase(response.getStatus())) {
            final V2PGPaymentInfoModel v2PGPaymentInfoModel = getPGPaymentInfoModel(response);
            v2PGPaymentInfoModel.setPaymentGateway(PAYU.PAYMENT_GATEWAY);
            v2PGPaymentInfoModel.setMode(response.getMode());
            v2PGPaymentInfoModel.setPaymentMode(getV2PaymentDao().getV2PaymentMode(PAYU.PAYMENT_MODE + response.getMode()));
            LOG.debug("PayU Response Code : " + PAYU.PAYMENT_MODE + response.getMode());
            v2PGPaymentInfoModel.setCardNumber(response.getCardnum());
            final AddressModel addressModel = getBillingAddress(cartModel, response, v2PGPaymentInfoModel);
            v2PGPaymentInfoModel.setBillingAddress(addressModel);
            this.modelService.saveAll(v2PGPaymentInfoModel, addressModel);
            final PaymentTransactionModel transaction = (PaymentTransactionModel) this.modelService.create(PaymentTransactionModel.class);
            transaction.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
            transaction.setPaymentProvider(PAYU.PAYMENT_MODE);
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

    /**
     * Gets the billing address.
     * 
     * @param cartModel
     *        the cart model
     * @param response
     *        the response
     * @param externalPaymentInfoModel
     *        the V2PG payment info model
     * @return the billing address
     */
    private AddressModel getBillingAddress(final CartModel cartModel, final PAYUPaymentResponse response,
            final V2PGPaymentInfoModel externalPaymentInfoModel) {
        AddressModel addressModel = null;
        if (null != cartModel.getPaymentAddress()) {
            final AddressModel paymentAddress = cartModel.getPaymentAddress();
            addressModel = getBillingAddress(externalPaymentInfoModel, paymentAddress);
            getModelService().remove(paymentAddress);
        } else {
            if (response.getUdf1() != null && !response.getUdf1().isEmpty()) {
                addressModel = getBillingAddress(response);
                addressModel.setOwner(externalPaymentInfoModel);
            } else {
                final AddressModel shippingaddress = cartModel.getDeliveryAddress();
                addressModel = getBillingAddress(externalPaymentInfoModel, shippingaddress);
            }
        }
        return addressModel;
    }

    /**
     * Gets the billing address.
     * 
     * @param response
     *        the response
     * @return the billing address
     */
    private AddressModel getBillingAddress(final PAYUPaymentResponse response) {
        final AddressModel addressModel = (AddressModel) this.modelService.create(AddressModel.class);

        String[] values = response.getUdf1().split(PAYU.FIELD_SEPERATOR);
        addressModel.setTitle(getTitleModelDao().findUniqueModelByCode(values[0]));
        addressModel.setFirstname(values[1]);
        addressModel.setLastname(values[2]);
        addressModel.setCellphone(values[3]);
        values = response.getUdf2().split(PAYU.FIELD_SEPERATOR);
        addressModel.setLine1(values[0]);
        addressModel.setLine2(values[1]);
        values = response.getUdf3().split(PAYU.FIELD_SEPERATOR);
        addressModel.setTown(values[0]);
        addressModel.setPostalcode(values[1]);
        addressModel.setBillingAddress(Boolean.TRUE);

        final CountryModel countryModel = getCommonI18NService().getCountry(PaymentProperties.INDIA_COUNTRY_ISO);
        addressModel.setCountry(countryModel);
        addressModel.setRegion(getCommonI18NService().getRegion(countryModel, values[2]));

        return addressModel;
    }

    /**
     * Gets the payment transaction entry.
     * 
     * @param response
     *        the response
     * @param transaction
     *        the transaction
     * @param transactionType
     *        the transaction type
     * @return the payment transaction entry
     */
    private PaymentTransactionEntryModel getPaymentTransactionEntry(final PAYUPaymentResponse response,
            final PaymentTransactionModel transaction, final PaymentTransactionType transactionType) {
        final String newEntryCode = getNewPaymentTransactionEntryCode(transaction, transactionType);

        final PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.modelService
                .create(PaymentTransactionEntryModel.class);
        entry.setAmount(new BigDecimal(response.getAmount()));
        entry.setType(transactionType);
        entry.setRequestId(response.getMihpayid());
        entry.setTime(new Date());
        entry.setPaymentTransaction(transaction);
        entry.setTransactionStatus(response.getStatus());
        entry.setCode(newEntryCode);
        entry.setCurrency(getCommonI18NService().getCurrentCurrency());
        return entry;
    }

    /* *//**
     * Gets the billing address.
     * 
     * @param cartModel
     *        the cart model
     * @param response
     *        the response
     * @param externalPaymentInfoModel
     *        the external payment info model
     * @return the billing address
     */
    /*
     * private AddressModel getBillingAddress(final CartModel cartModel, final PAYUPaymentResponse response, final
     * V2ExternalPaymentInfoModel externalPaymentInfoModel) { AddressModel addressModel = null; if (null != cartModel.getPaymentAddress()) {
     * final AddressModel paymentAddress = cartModel.getPaymentAddress(); addressModel = getBillingAddress(externalPaymentInfoModel,
     * paymentAddress); getModelService().remove(paymentAddress); } else { if (response.getUdf1() != null && !response.getUdf1().isEmpty())
     * { addressModel = getBillingAddress(response); addressModel.setOwner(externalPaymentInfoModel); } else { final AddressModel
     * shippingaddress = cartModel.getDeliveryAddress(); addressModel = getBillingAddress(externalPaymentInfoModel, shippingaddress); } }
     * return addressModel; }
     */

    /**
     * Gets the billing address.
     * 
     * @param response
     *        the response
     * @return the billing address
     */
    /*
     * private AddressModel getBillingAddress(final PAYUPaymentResponse response) { final AddressModel addressModel = (AddressModel)
     * this.modelService.create(AddressModel.class);
     * 
     * String[] values = response.getUdf1().split(PAYU.FIELD_SEPERATOR);
     * addressModel.setTitle(getTitleModelDao().findUniqueModelByCode(values[0])); addressModel.setFirstname(values[1]);
     * addressModel.setLastname(values[2]); addressModel.setCellphone(values[3]); values = response.getUdf2().split(PAYU.FIELD_SEPERATOR);
     * addressModel.setLine1(values[0]); addressModel.setLine2(values[1]); values = response.getUdf3().split(PAYU.FIELD_SEPERATOR);
     * addressModel.setTown(values[0]); addressModel.setPostalcode(values[1]); addressModel.setBillingAddress(Boolean.TRUE);
     * 
     * final CountryModel countryModel = getCommonI18NService().getCountry(PaymentProperties.INDIA_COUNTRY_ISO);
     * addressModel.setCountry(countryModel); addressModel.setRegion(getCommonI18NService().getRegion(countryModel, values[2]));
     * 
     * return addressModel; }
     */

    /**
     * Gets the PAYU payment response info model.
     * 
     * @param parameters
     *        the parameters
     * @return the payment response info model
     */
    private PAYUPaymentResponseInfoModel getPayUPaymentResponseInfoModel(final Map<String, String> parameters) {
        final PAYUPaymentResponseInfoModel payUPaymentInfoInfo = this.modelService.create(PAYUPaymentResponseInfoModel.class);
        payUPaymentInfoInfo.setAmount(parameters.get(PAYU.ResponseParameters.AMOUNT.getValue()));
        payUPaymentInfoInfo.setTxnid(parameters.get(PAYU.ResponseParameters.TRANSACTION_ID.getValue()));
        payUPaymentInfoInfo.setHash(parameters.get(PAYU.ResponseParameters.HASH.getValue()));
        payUPaymentInfoInfo.setAddedon(parameters.get(PAYU.ResponseParameters.ADD_ON.getValue()));
        payUPaymentInfoInfo.setBankcode(parameters.get(PAYU.ResponseParameters.BANK_CODE.getValue()));
        payUPaymentInfoInfo.setBankRefNumber(parameters.get(PAYU.ResponseParameters.BANK_REFERENCE_NUMBER.getValue()));
        payUPaymentInfoInfo.setCardnum(parameters.get(PAYU.ResponseParameters.CARD_NUMBER.getValue()));
        payUPaymentInfoInfo.setDiscount(parameters.get(PAYU.ResponseParameters.DISCOUNT.getValue()));
        payUPaymentInfoInfo.setEmail(parameters.get(PAYU.ResponseParameters.EMAIL.getValue()));
        payUPaymentInfoInfo.setError(parameters.get(PAYU.ResponseParameters.ERROR.getValue()));
        payUPaymentInfoInfo.setErrorMessage(parameters.get(PAYU.ResponseParameters.ERROR_MESSAGE.getValue()));
        payUPaymentInfoInfo.setFirstname(parameters.get(PAYU.ResponseParameters.FIRST_NAME.getValue()));
        payUPaymentInfoInfo.setKey(parameters.get(PAYU.ResponseParameters.KEY.getValue()));
        payUPaymentInfoInfo.setMihpayid(parameters.get(PAYU.ResponseParameters.MIH_PAY_ID.getValue()));
        payUPaymentInfoInfo.setMode(parameters.get(PAYU.ResponseParameters.MODE.getValue()));
        payUPaymentInfoInfo.setNameOnCard(parameters.get(PAYU.ResponseParameters.NAME_ON_CARD.getValue()));
        if (parameters.get(PAYU.ResponseParameters.NET_AMOUNT_DEBIT.getValue()) != null) {
            payUPaymentInfoInfo.setNetAmountDebit(parameters.get(PAYU.ResponseParameters.NET_AMOUNT_DEBIT.getValue()));
        }
        payUPaymentInfoInfo.setPaymentSource(parameters.get(PAYU.ResponseParameters.PAYMENT_SOURCE.getValue()));
        payUPaymentInfoInfo.setPgType(parameters.get(PAYU.ResponseParameters.PG_TYPE.getValue()));
        payUPaymentInfoInfo.setPhone(parameters.get(PAYU.ResponseParameters.PHONE.getValue()));
        payUPaymentInfoInfo.setProductInfo(parameters.get(PAYU.ResponseParameters.PRODUCT_INFO.getValue()));
        payUPaymentInfoInfo.setStatus(parameters.get(PAYU.ResponseParameters.STATUS.getValue()));
        payUPaymentInfoInfo.setUnmappedStatus(parameters.get(PAYU.ResponseParameters.UNMAPPED_STATUS.getValue()));
        payUPaymentInfoInfo.setUdf1(parameters.get(PAYU.ResponseParameters.UDF_1.getValue()));
        payUPaymentInfoInfo.setUdf2(parameters.get(PAYU.ResponseParameters.UDF_2.getValue()));
        payUPaymentInfoInfo.setUdf3(parameters.get(PAYU.ResponseParameters.UDF_3.getValue()));
        payUPaymentInfoInfo.setUdf4(parameters.get(PAYU.ResponseParameters.UDF_4.getValue()));
        payUPaymentInfoInfo.setUdf5(parameters.get(PAYU.ResponseParameters.UDF_5.getValue()));
        return payUPaymentInfoInfo;
    }

    /**
     * @return the createPAYUPaymentRequestStrategy
     */
    public CreatePAYUPaymentRequestStrategy getCreatePAYUPaymentRequestStrategy() {
        return createPAYUPaymentRequestStrategy;
    }

    /**
     * @param createPAYUPaymentRequestStrategy
     *        the createPAYUPaymentRequestStrategy to set
     */
    public void setCreatePAYUPaymentRequestStrategy(final CreatePAYUPaymentRequestStrategy createPAYUPaymentRequestStrategy) {
        this.createPAYUPaymentRequestStrategy = createPAYUPaymentRequestStrategy;
    }

    /**
     * @return the payUPaymentResponseStrategy
     */
    public V2PaymentResponseStrategy getPayUPaymentResponseStrategy() {
        return payUPaymentResponseStrategy;
    }

    /**
     * @param payUPaymentResponseStrategy
     *        the payUPaymentResponseStrategy to set
     */
    public void setPayUPaymentResponseStrategy(final V2PaymentResponseStrategy payUPaymentResponseStrategy) {
        this.payUPaymentResponseStrategy = payUPaymentResponseStrategy;
    }

    /**
     * @return the payUPaymentResponseDataConverter
     */
    public Converter<V2PaymentResponseInfoModel, PaymentData> getPayUPaymentResponseDataConverter() {
        return payUPaymentResponseDataConverter;
    }

    /**
     * @param payUPaymentResponseDataConverter
     *        the payUPaymentResponseDataConverter to set
     */
    public void setPayUPaymentResponseDataConverter(
            final Converter<V2PaymentResponseInfoModel, PaymentData> payUPaymentResponseDataConverter) {
        this.payUPaymentResponseDataConverter = payUPaymentResponseDataConverter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartpayupaymentintegration.services.PAYUPaymentService#refundPayment(java.lang.String, double)
     */
    @Override
    public boolean refundOrCancelPayment(final String mihPayId, final double amount) {
        boolean result = true;
        final Map<String, String> parameters = new HashMap<String, String>();
        try {
            populateMandatoryParamsForCommandExecution(parameters, PAYU.Commands.CANCEL_REFUND_TRANSACTIONS.getValue(), mihPayId);
            parameters.put(PAYU.RequestParameters.VAR2.getValue(), String.valueOf(System.currentTimeMillis()));
            parameters.put(PAYU.RequestParameters.VAR3.getValue(), String.valueOf(amount));
            parameters.remove(RequestParameters.SALT.getValue());
            final PayUAbstractCommandResponse commandResponse = payURefundPaymentCommand
                    .executeCommand(parameters);

            final PayURefundCommandResponse refundCommandResponse = (PayURefundCommandResponse) commandResponse;
            refundCommandResponse.toString();
            LOG.info("Response on refund: " + commandResponse);

            if (commandResponse == null || commandResponse.getStatus() == null || Integer.parseInt(commandResponse.getStatus()) != 1) {
                final StringBuilder errMsgBuilder = new StringBuilder();
                errMsgBuilder.append("Refund Error");
                errMsgBuilder.append(commandResponse != null ? ": " + commandResponse.getMsg() : "");
                throw new AdapterException(errMsgBuilder.toString());
            }

        } catch (final V2Exception e) {
            LOG.error(e.getMessage());
            result = false;
            throw new AdapterException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * @param parameters
     * @param value
     * @param mihPayId
     * @throws V2Exception
     */
    private void populateMandatoryParamsForCommandExecution(final Map<String, String> parameters, final String command, final String var1)
            throws V2Exception {
        parameters.put(PAYU.RequestParameters.KEY.getValue(), getSiteConfigService().getProperty(PAYU.HOP_PAYMENT_MERCHANT_KEY));
        parameters.put(PAYU.RequestParameters.SALT.getValue(), getSiteConfigService().getProperty(PAYU.HOP_PAYMENT_MERCHANT_SALT));
        parameters.put(PAYU.RequestParameters.COMMAND.getValue(), command);
        parameters.put(PAYU.RequestParameters.VAR1.getValue(), var1);
        parameters.put(PAYU.RequestParameters.HASH.getValue(), payUEncryptionService.getRequestHashForCommandExecution(parameters));

    }

}
