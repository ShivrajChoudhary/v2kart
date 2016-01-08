package in.com.v2kart.core.sms.populator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * To populate all the fields required in SMS template
 * 
 * @author vikrant2480
 * 
 */
public class V2UserSmsDataMapPopulator {

    private String siteUrl;
    
    private String customerCare;
    
    @Resource(name="modelService")
    private ModelService modelService;
    
    @Resource(name="userService")
    private UserService userService;
    
    @Resource(name="addressModel")
    private AddressModel addressModel;
    

   

    public AddressModel getAddressModel() {
        return addressModel;
    }


    public void setAddressModel(AddressModel addressModel) {
        this.addressModel = addressModel;
    }


    /**
     * To create user registration data map
     * 
     * @param source
     * @return
     */
    public Map<String, String> createV2UserSmsDataMap(final CustomerModel source) {
        final Map<String, String> v2SmsUserDataMap = new HashMap<String, String>();
        v2SmsUserDataMap.put("username", source.getFirstName());
        v2SmsUserDataMap.put("siteurl", siteUrl);
        v2SmsUserDataMap.put("mobileno", source.getMobileNumber());
        v2SmsUserDataMap.put("first name", source.getFirstName());
        v2SmsUserDataMap.put("Lastname", source.getLastName());
        return v2SmsUserDataMap;

    }
    
    
    /**
     * To create order confirmation data map
     * 
     * @param source
     * @return
     */
    public Map<String, String> createV2OrderConfirmationSmsDataMap(final OrderModel source) {
        final Map<String, String> v2SmsOrderDataMap = new HashMap<String, String>();
        v2SmsOrderDataMap.put("username", ((CustomerModel)source.getUser()).getFirstName());
        v2SmsOrderDataMap.put("orderNo", source.getCode());
        v2SmsOrderDataMap.put("deliveryDate", source.getDeliveryMode().getDescription());
        return v2SmsOrderDataMap;

        
    }
    
    /**
     * To create order dispatched data map
     * 
     * @param source
     * @return
     */
    public Map<String, String> createV2OrderDispatchedSmsDataMap(final ConsignmentModel source) {
        final Map<String, String> v2SmsOrderDataMap = new HashMap<String, String>();
        final AbstractOrderModel orderModel = source.getOrder();
        
        v2SmsOrderDataMap.put("username", ((CustomerModel)orderModel.getUser()).getFirstName());
        v2SmsOrderDataMap.put("orderNo", orderModel.getCode());
        v2SmsOrderDataMap.put("lspName", null != source.getLsp() ? source.getLsp().getName() :"" );
        v2SmsOrderDataMap.put("trackingNo", source.getTrackingID());
        v2SmsOrderDataMap.put("deliveryDate", null != source.getDeliveryMode() ? source.getDeliveryMode().getDescription() : "");
        v2SmsOrderDataMap.put("customerCare", customerCare);
        return v2SmsOrderDataMap;

    }
    
    /**
     * To create OTP for COD
     * 
     * @return
     */
    
    public Map<String, String> createV2UserOtp() {
        CustomerModel customer=(CustomerModel) getUserService().getCurrentUser();
        final Map<String, String> v2SmsUserDataMap = new HashMap<String, String>();
        final int num = new Random().nextInt(900000) + 100000;
        Date otpTime = new Date(new Date().getTime() + 10 * 60000);
        customer.setModifiedOtpTime(otpTime);
        customer.setOtp(Integer.toString(num));
        this.modelService.save(customer);    
        v2SmsUserDataMap.put("otp",Integer.toString(num));
        v2SmsUserDataMap.put("username", customer.getFirstName());
        v2SmsUserDataMap.put("siteurl", siteUrl);
        v2SmsUserDataMap.put("mobileno", customer.getMobileNumber());
        v2SmsUserDataMap.put("first name", customer.getFirstName());
        v2SmsUserDataMap.put("Lastname", customer.getLastName());
        return v2SmsUserDataMap;

    }

    public UserService getUserService() {
        return userService;
    }



    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ModelService getModelService() {
        return modelService;
    }



    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    

    /**
     * @param siteUrl
     *        the siteUrl to set
     */
    @Value("${website.v2kart.http}")
    @Required
    public void setSiteUrl(final String siteUrl) {
        this.siteUrl = siteUrl;
    }

    /**
     * @param customerCare
     */
    @Value("${customercare.v2kart}")
    @Required
    public void setCustomerCare(String customerCare) {
        this.customerCare = customerCare;
    }

}
