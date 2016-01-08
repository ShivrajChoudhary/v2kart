/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.data.response;

import de.hybris.platform.acceleratorservices.payment.data.CustomerBillToData;
import de.hybris.platform.acceleratorservices.payment.data.CustomerShipToData;

import in.com.v2kart.core.payment.data.response.V2PaymentResponse;


/**
 * The Class CCAvenuePaymentResponse.
 * 
 * @author yamini2280
 */
public class CCAvenuePaymentResponse extends V2PaymentResponse
{

	private CustomerShipToData customerShipToData;

	private CustomerBillToData customerBillToData;

	private String offerCode;

	private String offerType;

	private String vault;

	private String currency;

	private String orderStatus;

	private String cardName;

	private String paymentMode;

	private String trackingId;

	private String bankRefNo;

	private String statusCode;

	private String statusMessage;

	private String failureMessage;

	private String discountValue;

	private String merchantParam1;

	private String merchantParam2;

	private String merchantParam3;

	private String merchantParam4;

	private String merchantParam5;

	/**
	 * Gets the customer ship to data.
	 * 
	 * @return the customerShipToData
	 */
	public CustomerShipToData getCustomerShipToData()
	{
		return customerShipToData;
	}

	/**
	 * Sets the customer ship to data.
	 * 
	 * @param customerShipToData
	 *           the customerShipToData to set
	 */
	public void setCustomerShipToData(final CustomerShipToData customerShipToData)
	{
		this.customerShipToData = customerShipToData;
	}

	/**
	 * Gets the customer bill to data.
	 * 
	 * @return the customerBillToData
	 */
	public CustomerBillToData getCustomerBillToData()
	{
		return customerBillToData;
	}

	/**
	 * Sets the customer bill to data.
	 * 
	 * @param customerBillToData
	 *           the customerBillToData to set
	 */
	public void setCustomerBillToData(final CustomerBillToData customerBillToData)
	{
		this.customerBillToData = customerBillToData;
	}

	/**
	 * Gets the currency.
	 * 
	 * @return the currency
	 */
	public String getCurrency()
	{
		return currency;
	}

	/**
	 * Sets the currency.
	 * 
	 * @param currency
	 *           the currency to set
	 */
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	/**
	 * Gets the order status.
	 * 
	 * @return the orderStatus
	 */
	public String getOrderStatus()
	{
		return orderStatus;
	}

	/**
	 * Sets the order status.
	 * 
	 * @param orderStatus
	 *           the orderStatus to set
	 */
	public void setOrderStatus(final String orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	/**
	 * Gets the card name.
	 * 
	 * @return the cardName
	 */
	public String getCardName()
	{
		return cardName;
	}

	/**
	 * Sets the card name.
	 * 
	 * @param cardName
	 *           the cardName to set
	 */
	public void setCardName(final String cardName)
	{
		this.cardName = cardName;
	}

	/**
	 * Gets the payment mode.
	 * 
	 * @return the paymentMode
	 */
	public String getPaymentMode()
	{
		return paymentMode;
	}

	/**
	 * Sets the payment mode.
	 * 
	 * @param paymentMode
	 *           the paymentMode to set
	 */
	public void setPaymentMode(final String paymentMode)
	{
		this.paymentMode = paymentMode;
	}

	/**
	 * Gets the tracking id.
	 * 
	 * @return the trackingId
	 */
	public String getTrackingId()
	{
		return trackingId;
	}

	/**
	 * Sets the tracking id.
	 * 
	 * @param trackingId
	 *           the trackingId to set
	 */
	public void setTrackingId(final String trackingId)
	{
		this.trackingId = trackingId;
	}

	/**
	 * Gets the bank ref no.
	 * 
	 * @return the bankRefNo
	 */
	public String getBankRefNo()
	{
		return bankRefNo;
	}

	/**
	 * Sets the bank ref no.
	 * 
	 * @param bankRefNo
	 *           the bankRefNo to set
	 */
	public void setBankRefNo(final String bankRefNo)
	{
		this.bankRefNo = bankRefNo;
	}

	/**
	 * Gets the status code.
	 * 
	 * @return the statusCode
	 */
	public String getStatusCode()
	{
		return statusCode;
	}

	/**
	 * Sets the status code.
	 * 
	 * @param statusCode
	 *           the statusCode to set
	 */
	public void setStatusCode(final String statusCode)
	{
		this.statusCode = statusCode;
	}

	/**
	 * Gets the status message.
	 * 
	 * @return the statusMessage
	 */
	public String getStatusMessage()
	{
		return statusMessage;
	}

	/**
	 * Sets the status message.
	 * 
	 * @param statusMessage
	 *           the statusMessage to set
	 */
	public void setStatusMessage(final String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	/**
	 * Gets the failure message.
	 * 
	 * @return the failureMessage
	 */
	public String getFailureMessage()
	{
		return failureMessage;
	}

	/**
	 * Sets the failure message.
	 * 
	 * @param failureMessage
	 *           the failureMessage to set
	 */
	public void setFailureMessage(final String failureMessage)
	{
		this.failureMessage = failureMessage;
	}

	/**
	 * Gets the merchant param1.
	 * 
	 * @return the merchantParam1
	 */
	public String getMerchantParam1()
	{
		return merchantParam1;
	}

	/**
	 * Sets the merchant param1.
	 * 
	 * @param merchantParam1
	 *           the merchantParam1 to set
	 */
	public void setMerchantParam1(final String merchantParam1)
	{
		this.merchantParam1 = merchantParam1;
	}

	/**
	 * Gets the merchant param2.
	 * 
	 * @return the merchantParam2
	 */
	public String getMerchantParam2()
	{
		return merchantParam2;
	}

	/**
	 * Sets the merchant param2.
	 * 
	 * @param merchantParam2
	 *           the merchantParam2 to set
	 */
	public void setMerchantParam2(final String merchantParam2)
	{
		this.merchantParam2 = merchantParam2;
	}

	/**
	 * Gets the merchant param3.
	 * 
	 * @return the merchantParam3
	 */
	public String getMerchantParam3()
	{
		return merchantParam3;
	}

	/**
	 * Sets the merchant param3.
	 * 
	 * @param merchantParam3
	 *           the merchantParam3 to set
	 */
	public void setMerchantParam3(final String merchantParam3)
	{
		this.merchantParam3 = merchantParam3;
	}

	/**
	 * Gets the merchant param4.
	 * 
	 * @return the merchantParam4
	 */
	public String getMerchantParam4()
	{
		return merchantParam4;
	}

	/**
	 * Sets the merchant param4.
	 * 
	 * @param merchantParam4
	 *           the merchantParam4 to set
	 */
	public void setMerchantParam4(final String merchantParam4)
	{
		this.merchantParam4 = merchantParam4;
	}

	/**
	 * Gets the merchant param5.
	 * 
	 * @return the merchantParam5
	 */
	public String getMerchantParam5()
	{
		return merchantParam5;
	}

	/**
	 * Sets the merchant param5.
	 * 
	 * @param merchantParam5
	 *           the merchantParam5 to set
	 */
	public void setMerchantParam5(final String merchantParam5)
	{
		this.merchantParam5 = merchantParam5;
	}

	/**
	 * Gets the offer code.
	 * 
	 * @return the offerCode
	 */
	public String getOfferCode()
	{
		return offerCode;
	}

	/**
	 * Sets the offer code.
	 * 
	 * @param offerCode
	 *           the offerCode to set
	 */
	public void setOfferCode(final String offerCode)
	{
		this.offerCode = offerCode;
	}

	/**
	 * Gets the offer type.
	 * 
	 * @return the offerType
	 */
	public String getOfferType()
	{
		return offerType;
	}

	/**
	 * Sets the offer type.
	 * 
	 * @param offerType
	 *           the offerType to set
	 */
	public void setOfferType(final String offerType)
	{
		this.offerType = offerType;
	}

	/**
	 * Gets the vault.
	 * 
	 * @return the vault
	 */
	public String getVault()
	{
		return vault;
	}

	/**
	 * Sets the vault.
	 * 
	 * @param vault
	 *           the vault to set
	 */
	public void setVault(final String vault)
	{
		this.vault = vault;
	}

	/**
	 * Gets the discount value.
	 * 
	 * @return the discountValue
	 */
	public String getDiscountValue()
	{
		return discountValue;
	}

	/**
	 * Sets the discount value.
	 * 
	 * @param discountValue
	 *           the discountValue to set
	 */
	public void setDiscountValue(final String discountValue)
	{
		this.discountValue = discountValue;
	}

}
