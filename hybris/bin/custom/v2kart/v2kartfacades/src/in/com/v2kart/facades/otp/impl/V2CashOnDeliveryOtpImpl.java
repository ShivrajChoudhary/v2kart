package in.com.v2kart.facades.otp.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import in.com.v2kart.facades.otp.V2CashOnDeliveryOtp;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * 
 * @author samikshachandra
 *
 */

public class V2CashOnDeliveryOtpImpl extends DefaultCustomerFacade implements V2CashOnDeliveryOtp {

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Map<String, String> autenticateOtp(String otp) {
        CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();

        final Map<String, String> map = new HashMap<>();
        boolean results = true;
        if (customerModel.getModifiedOtpTime().before(new Date())) {
            results = false;
            map.put("errorMessage", "Time out, Please resent OTP");
        }
        else if (!otp.equals(customerModel.getOtp()))
        {
            results = false;
            map.put("errorMessage", "Enter valid OTP");
        }
        map.put("isMatch", String.valueOf(results));
        map.put("currentTime", new Date().toString());
        map.put("ValidTime", customerModel.getModifiedOtpTime().toString());
        return map;
    }

}
