/**
 *
 */
package in.com.v2kart.commercewebservices.validator;

import in.com.v2kart.core.enums.V2NotifyCustomerTypeEnum;
import in.com.v2kart.facades.core.data.V2CustomerNotificationData;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * @author hariom1466
 * 
 */
@Component(value = "customerNotificationValidator")
public class V2CustomerNotificationDataValidator implements Validator
{



	public static final String EMAIL_REGEX = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return V2CustomerNotificationData.class.isAssignableFrom(aClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final V2CustomerNotificationData customerNotificationForm = (V2CustomerNotificationData) obj;

		final String notificationType = customerNotificationForm.getType();
		final String currentUsermail = customerNotificationForm.getCurrentUserEmailId();
		final String email = customerNotificationForm.getEmailId();

		if (StringUtils.isEmpty(notificationType))
		{
			// email a friend validation
			validateEmailId(email, errors, notificationType);
			if (StringUtils.isEmpty(customerNotificationForm.getMessage()))
			{
				errors.rejectValue("message", "Required*", "Required*");
			}
			if (StringUtils.isEmpty(customerNotificationForm.getUrl()))
			{
				errors.rejectValue("url", "Required*", "Required*");
			}
		}
		else if (V2NotifyCustomerTypeEnum.NOTIFY_MY_PRICE.toString().equals(notificationType))
		{
			// notify my price validation
			//final double notificationPrice = customerNotificationForm.getNotificationPrice();
			validateEmailId(currentUsermail, errors, notificationType);
		}
		else
		{
			validateEmailId(currentUsermail, errors, notificationType);
		}
	}

	private void validateEmailId(final String email, final Errors errors, final String notificationType)
	{
		if (StringUtils.isNotEmpty(email))
		{
			if (StringUtils.length(email) > 255 || !Pattern.matches(EMAIL_REGEX, email))
			{
				if (StringUtils.isEmpty(notificationType))
				{
					errors.rejectValue("emailId", "Invalid Entry", "Invalid Entry");
				}
				else
				{
					errors.rejectValue("currentUserEmailId", "Invalid Entry", "Invalid Entry");
				}
			}
		}
		else
		{
			if (StringUtils.isEmpty(notificationType))
			{
				errors.rejectValue("emailId", "Required*", "Required*");
			}
			else
			{
				errors.rejectValue("currentUserEmailId", "Required*", "Required*");
			}
		}
	}


}
