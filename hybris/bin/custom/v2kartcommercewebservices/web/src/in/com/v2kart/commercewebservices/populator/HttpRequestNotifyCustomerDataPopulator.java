package in.com.v2kart.commercewebservices.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.core.constants.GeneratedV2kartCoreConstants.Enumerations.V2NotifyCustomerTypeEnum;
import in.com.v2kart.facades.core.data.V2CustomerNotificationData;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component("httpRequestNotifyCustomerDataPopulator")
@Scope("prototype")
public class HttpRequestNotifyCustomerDataPopulator extends AbstractHttpRequestDataPopulator implements
		Populator<HttpServletRequest, V2CustomerNotificationData>
{

	private static final String MESSAGE = "message";
	private static final String EMAIL = "emailId";
	private static final String MEDIA_URL = "mediaUrl";
	private static final String PRODUCT_CODE = "productCode";
	private static final String NAME = "name";
	private static final String CURRENT_USER_EMAILID = "currentUserEmailId";
	private static final String CURRENT_USER_NAME = "currentUserName";
	private static final String TYPE = "type";
	private static final String PRODUCT_PRICE = "productPrice";
	private static final String URL = "url";
	private static final String NOTIFICATION_PRICE = "notificationPrice";

	@Override
	public void populate(final HttpServletRequest request, final V2CustomerNotificationData customerNotificationData)
			throws ConversionException
	{

		Assert.notNull(request, "Parameter source cannot be null.");
		Assert.notNull(customerNotificationData, "Parameter target cannot be null.");

		customerNotificationData.setMessage(updateStringValueFromRequest(request, MESSAGE, customerNotificationData.getMessage()));
		customerNotificationData.setEmailId(updateStringValueFromRequest(request, EMAIL, customerNotificationData.getEmailId()));
		customerNotificationData.setMediaUrl(updateStringValueFromRequest(request, MEDIA_URL,
				customerNotificationData.getMediaUrl()));
		customerNotificationData.setProductCode(updateStringValueFromRequest(request, PRODUCT_CODE,
				customerNotificationData.getProductCode()));
		customerNotificationData.setName(updateStringValueFromRequest(request, NAME, customerNotificationData.getName()));
		customerNotificationData.setCurrentUserEmailId(updateStringValueFromRequest(request, CURRENT_USER_EMAILID,
				customerNotificationData.getCurrentUserEmailId()));
		customerNotificationData.setCurrentUserName(updateStringValueFromRequest(request, CURRENT_USER_NAME,
				customerNotificationData.getCurrentUserName()));
		customerNotificationData.setType(updateStringValueFromRequest(request, TYPE, customerNotificationData.getType()));
		customerNotificationData.setProductPrice(updateStringValueFromRequest(request, PRODUCT_PRICE,
				customerNotificationData.getProductPrice()));
		customerNotificationData.setUrl(updateStringValueFromRequest(request, URL, customerNotificationData.getUrl()));
		customerNotificationData.setNotificationPrice(updateDoubleValueFromRequest(request, NOTIFICATION_PRICE,
				Double.valueOf(customerNotificationData.getNotificationPrice())) != null ? updateDoubleValueFromRequest(request,
				NOTIFICATION_PRICE, Double.valueOf(customerNotificationData.getNotificationPrice())).doubleValue() : 0);

		if (V2NotifyCustomerTypeEnum.NOTIFY_MY_PRICE.toString().equalsIgnoreCase(customerNotificationData.getType()))
		{
			customerNotificationData.setEmailId(customerNotificationData.getCurrentUserEmailId());
		}

	}

}
