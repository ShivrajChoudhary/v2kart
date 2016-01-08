package in.com.v2kart.storefront.forms.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.com.v2kart.storefront.forms.V2FeedbackForm;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("feedbackValidator")
public class V2FeedbackValidator implements Validator {

    public static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    public static final String PHONE_REGEX = "\\d{10}";
    @Override
    public boolean supports(Class<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void validate(Object object, Errors error) {
        final V2FeedbackForm feedbackForm = (V2FeedbackForm) object;
        final String message = feedbackForm.getMessage();
        final String email = feedbackForm.getEmail();
        final String phone = feedbackForm.getMobileNumber();
        final String category = feedbackForm.getCategory();
        final String rating = feedbackForm.getRating();
        //phone
        if (StringUtils.isBlank(phone)) {
            error.rejectValue("mobileNumber", "feedback.message.required");
        } else if (StringUtils.length(phone) > 10 || !validatefield(phone, PHONE_REGEX)) {
            error.rejectValue("mobileNumber", "feedback.mobileNumber.invalid");
        }
        
        //email
        if (StringUtils.isEmpty(email))
        {
            error.rejectValue("email", "feedback.message.required");
        }
        else if (StringUtils.length(email) > 255 || !validatefield(email, EMAIL_REGEX))
        {
            error.rejectValue("email", "feedback.email.invalid");
        }
        
        //message
        if (StringUtils.isEmpty(message)){
            error.rejectValue("message", "feedback.message.required");
        }
        if(message.length()>200)
        {
            error.rejectValue("message", "feedback.messagelength.invalid"); 
        }
        
        if((category == null) ||(category.equals(""))){
            error.rejectValue("category", "feedback.message.required");
        }
        
        if(rating == null){
            error.rejectValue("rating", "feedback.message.required");
        }
    }
    protected boolean validatefield(final String field, final String regEx)
    {
        final Pattern pattern = Pattern.compile(regEx);
        final Matcher matcher = pattern.matcher(field);
        return matcher.matches();
    }

}
