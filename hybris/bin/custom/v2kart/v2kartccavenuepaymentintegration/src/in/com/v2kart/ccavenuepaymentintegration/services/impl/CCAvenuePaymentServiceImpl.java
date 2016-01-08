/**
 *
 */
package in.com.v2kart.ccavenuepaymentintegration.services.impl;

import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.ccavenuepaymentintegration.data.request.CCAvenuePaymentRequest;
import in.com.v2kart.ccavenuepaymentintegration.data.response.CCAvenuePaymentResponse;
import in.com.v2kart.ccavenuepaymentintegration.enums.CCAvenueDecisionEnum;
import in.com.v2kart.ccavenuepaymentintegration.services.CCAvenuePaymentService;
import in.com.v2kart.ccavenuepaymentintegration.services.CreateCCAvenuePaymentRequestStrategy;
import in.com.v2kart.core.model.CCAvenuePaymentResponseInfoModel;
import in.com.v2kart.core.model.V2PaymentResponseInfoModel;
import in.com.v2kart.core.payment.services.V2PaymentResponseStrategy;
import in.com.v2kart.core.payment.services.impl.V2PaymentServiceImpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;


/**
 * The Class CCAvenuePaymentServiceImpl.
 * 
 * @author yamini2280
 */
public class CCAvenuePaymentServiceImpl extends V2PaymentServiceImpl implements CCAvenuePaymentService
{

	private static final Logger LOG = Logger.getLogger(CCAvenuePaymentServiceImpl.class);

	private CreateCCAvenuePaymentRequestStrategy createCCAvenuePaymentRequestStrategy;

	private V2PaymentResponseStrategy ccavenuePaymentResponseStrategy;

	private Converter<V2PaymentResponseInfoModel, PaymentData> ccavenuePaymentResponseDataConverter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.core.payment.services.V2PaymentService#getPaymentInfo(java.lang.String)
	 */
	@Override
	public Map<String, String> getPaymentInfo(final String guid)
	{
		Map<String, String> parameters = new HashMap<>(0);
		final V2PaymentResponseInfoModel responseInfoModel = getV2PaymentDao().getV2PaymentResponseInfoModel(guid);
		if (null != responseInfoModel)
		{
			final PaymentData data = getCcavenuePaymentResponseDataConverter().convert(responseInfoModel);
			parameters = data.getParameters();
		}
		return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.ccavenuepaymentintegration.services.CCAvenuePaymentService#beginHopCreatePayment(de.hybris.
	 * platform.core.model .user.CustomerModel, java.lang.String,
	 * de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CCAvenuePaymentRequest beginHopCreatePayment(final CustomerModel customer, final String enforcedPaymentMethod,
			final AddressData addressData, final String successUrl, final String cancelUrl, final String phoneNumber)
	{
		final String requestUrl = getSiteConfigService().getProperty(CCAVENUE.HOP_POST_REQUEST_URL);
		Assert.notNull(requestUrl, "The CCAVENUE HopRequestUrl cannot be null");

		final CCAvenuePaymentRequest request = getCreateCCAvenuePaymentRequestStrategy().createPaymentRequest(requestUrl,
				successUrl,
				cancelUrl, customer, getSiteConfigService().getProperty(CCAVENUE.HOP_PAYMENT_MERCHANT_KEY),
				enforcedPaymentMethod,
				addressData, getSiteConfigService().getProperty(CCAVENUE.HOP_PAYMENT_CURRENCY),
				getSiteConfigService().getProperty(CCAVENUE.HOP_PAYMENT_LANGUAGE), phoneNumber);

		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.core.payment.services.V2PaymentService#savePaymentInfo(java.util.Map)
	 */
	@Override
	public void savePaymentInfo(final Map<String, String> parameters)
	{
		getModelService().save(getCCAvenuePaymentResponseInfoModel(parameters));

	}

	/**
	 * Gets the billing address.
	 * 
	 * @param cartModel
	 *           the cart model
	 * @param response
	 *           the response
	 * @param externalPaymentInfoModel
	 *           the external payment info model
	 * @return the billing address
	 */
	private AddressModel getBillingAddress(final CartModel cartModel, final CCAvenuePaymentResponse response,
			final V2PGPaymentInfoModel externalPaymentInfoModel)
	{
		AddressModel addressModel = null;
		if (null != cartModel.getPaymentAddress())
		{
			final AddressModel paymentAddress = cartModel.getPaymentAddress();
			addressModel = getBillingAddress(externalPaymentInfoModel, paymentAddress);
			getModelService().remove(paymentAddress);
		}
		else
		{
			final AddressModel shippingaddress = cartModel.getDeliveryAddress();
			addressModel = getBillingAddress(externalPaymentInfoModel, shippingaddress);
		}
		return addressModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.ccavenuepaymentintegration.services.CCAvenuePaymentService#capture(de.hybris.platform.core.
	 * model.user.CustomerModel , java.util.Map, de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public CCAvenuePaymentResponse capture(final CustomerModel customerModel, final Map<String, String> parameters,
			final CartModel cartModel) throws AdapterException
	{
		final CCAvenuePaymentResponse response = (CCAvenuePaymentResponse) getCcavenuePaymentResponseStrategy().interpretResponse(
				parameters);
		if (CCAvenueDecisionEnum.SUCCESS.getValue().equalsIgnoreCase(response.getOrderStatus()))
		{

			final V2PGPaymentInfoModel v2PGPaymentInfoModel = getPGPaymentInfoModel(response);
			v2PGPaymentInfoModel.setPaymentGateway(CCAVENUE.PAYMENT_GATEWAY);
			v2PGPaymentInfoModel.setMode(response.getPaymentMode());
			final String ccAvenueResponseCode = CCAVENUE.PAYMENT_MODE.concat(response.getPaymentMode().replaceAll("\\s", ""));
			v2PGPaymentInfoModel.setPaymentMode(getV2PaymentDao().getV2PaymentMode(ccAvenueResponseCode));
			LOG.debug("CCAvenue response code: " + ccAvenueResponseCode);
			final AddressModel addressModel = getBillingAddress(cartModel, response, v2PGPaymentInfoModel);
			v2PGPaymentInfoModel.setBillingAddress(addressModel);
			this.modelService.saveAll(v2PGPaymentInfoModel, addressModel);
			final PaymentTransactionModel transaction = (PaymentTransactionModel) this.modelService
					.create(PaymentTransactionModel.class);
			transaction.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
			transaction.setPaymentProvider(CCAVENUE.PAYMENT_MODE);
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
	 * Gets the payment transaction entry.
	 * 
	 * @param response
	 *           the response
	 * @param transaction
	 *           the transaction
	 * @param transactionType
	 *           the transaction type
	 * @return the payment transaction entry
	 */
	private PaymentTransactionEntryModel getPaymentTransactionEntry(final CCAvenuePaymentResponse response,
			final PaymentTransactionModel transaction, final PaymentTransactionType transactionType)
	{
		final String newEntryCode = getNewPaymentTransactionEntryCode(transaction, transactionType);

		final PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.modelService
				.create(PaymentTransactionEntryModel.class);
		entry.setAmount(new BigDecimal(response.getAmount()));
		entry.setType(transactionType);
		entry.setRequestId(response.getTrackingId());
		entry.setTime(new Date());
		entry.setPaymentTransaction(transaction);
		entry.setTransactionStatus(response.getOrderStatus());
		entry.setCode(newEntryCode);
		// final String currency = null != response.getCurrency() ? response.getCurrency() : PaymentProperties.INDIA_CURRENCY_ISO;
		// entry.setCurrency(getCommonI18NService().getCurrency(currency));
		entry.setCurrency(getCommonI18NService().getCurrentCurrency());
		return entry;
	}

	/**
	 * Gets the CCAvenue payment response info model.
	 * 
	 * @param parameters
	 *           the parameters
	 * @return the payment response info model
	 */
	private CCAvenuePaymentResponseInfoModel getCCAvenuePaymentResponseInfoModel(final Map<String, String> parameters)
	{
		final CCAvenuePaymentResponseInfoModel ccavenuePaymentInfoInfo = this.modelService
				.create(CCAvenuePaymentResponseInfoModel.class);
		ccavenuePaymentInfoInfo.setAmount(parameters.get(CCAVENUE.ResponseParameters.AMOUNT.getValue()));
		ccavenuePaymentInfoInfo.setBankRefNo(parameters.get(CCAVENUE.ResponseParameters.BANK_REFERENCE_NUMBER.getValue()));
		ccavenuePaymentInfoInfo.setCardName(parameters.get(CCAVENUE.ResponseParameters.CARD_NAME.getValue()));
		ccavenuePaymentInfoInfo.setCurrency(parameters.get(CCAVENUE.ResponseParameters.CURRENCY.getValue()));
		ccavenuePaymentInfoInfo.setTxnid(parameters.get(CCAVENUE.ResponseParameters.ORDER_ID.getValue()));
		ccavenuePaymentInfoInfo.setOrderStatus(parameters.get(CCAVENUE.ResponseParameters.ORDER_STATUS.getValue()));
		ccavenuePaymentInfoInfo.setPaymentMode(parameters.get(CCAVENUE.ResponseParameters.PAYMENT_MODE.getValue()));
		ccavenuePaymentInfoInfo.setTrackingId(parameters.get(CCAVENUE.ResponseParameters.TRACKING_ID.getValue()));
		ccavenuePaymentInfoInfo.setOfferType(parameters.get(CCAVENUE.ResponseParameters.OFFER_TYPE.getValue()));
		ccavenuePaymentInfoInfo.setOfferCode(parameters.get(CCAVENUE.ResponseParameters.OFFER_CODE.getValue()));
		ccavenuePaymentInfoInfo.setVault(parameters.get(CCAVENUE.ResponseParameters.VAULT.getValue()));
		ccavenuePaymentInfoInfo.setDiscountValue(parameters.get(CCAVENUE.ResponseParameters.DISCOUNT_VALUE.getValue()));

		ccavenuePaymentInfoInfo.setStatusCode(parameters.get(CCAVENUE.ResponseParameters.STATUS_CODE.getValue()));
		ccavenuePaymentInfoInfo.setStatusMessage(parameters.get(CCAVENUE.ResponseParameters.STATUS_MESSAGE.getValue()));
		ccavenuePaymentInfoInfo.setFailureMessage(parameters.get(CCAVENUE.ResponseParameters.FAILURE_MESSAGE.getValue()));

		ccavenuePaymentInfoInfo.setMerchantParam1(parameters.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_1.getValue()));
		ccavenuePaymentInfoInfo.setMerchantParam2(parameters.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_2.getValue()));
		ccavenuePaymentInfoInfo.setMerchantParam3(parameters.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_3.getValue()));
		ccavenuePaymentInfoInfo.setMerchantParam4(parameters.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_4.getValue()));
		ccavenuePaymentInfoInfo.setMerchantParam5(parameters.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_5.getValue()));

		return ccavenuePaymentInfoInfo;
	}

	/**
	 * Gets the creates the cc avenue payment request strategy.
	 * 
	 * @return the createCCAvenuePaymentRequestStrategy
	 */
	public CreateCCAvenuePaymentRequestStrategy getCreateCCAvenuePaymentRequestStrategy()
	{
		return createCCAvenuePaymentRequestStrategy;
	}

	/**
	 * Sets the creates the cc avenue payment request strategy.
	 * 
	 * @param createCCAvenuePaymentRequestStrategy
	 *           the createCCAvenuePaymentRequestStrategy to set
	 */
	public void setCreateCCAvenuePaymentRequestStrategy(
			final CreateCCAvenuePaymentRequestStrategy createCCAvenuePaymentRequestStrategy)
	{
		this.createCCAvenuePaymentRequestStrategy = createCCAvenuePaymentRequestStrategy;
	}

	/**
	 * Gets the ccavenue payment response strategy.
	 * 
	 * @return the ccavenuePaymentResponseStrategy
	 */
	public V2PaymentResponseStrategy getCcavenuePaymentResponseStrategy()
	{
		return ccavenuePaymentResponseStrategy;
	}

	/**
	 * Sets the ccavenue payment response strategy.
	 * 
	 * @param ccavenuePaymentResponseStrategy
	 *           the ccavenuePaymentResponseStrategy to set
	 */
	public void setCcavenuePaymentResponseStrategy(final V2PaymentResponseStrategy ccavenuePaymentResponseStrategy)
	{
		this.ccavenuePaymentResponseStrategy = ccavenuePaymentResponseStrategy;
	}

	/**
	 * Gets the ccavenue payment response data converter.
	 * 
	 * @return the ccavenuePaymentResponseDataConverter
	 */
	public Converter<V2PaymentResponseInfoModel, PaymentData> getCcavenuePaymentResponseDataConverter()
	{
		return ccavenuePaymentResponseDataConverter;
	}

	/**
	 * Sets the ccavenue payment response data converter.
	 * 
	 * @param ccavenuePaymentResponseDataConverter
	 *           the ccavenuePaymentResponseDataConverter to set
	 */
	public void setCcavenuePaymentResponseDataConverter(
			final Converter<V2PaymentResponseInfoModel, PaymentData> ccavenuePaymentResponseDataConverter)
	{
		this.ccavenuePaymentResponseDataConverter = ccavenuePaymentResponseDataConverter;
	}

}
