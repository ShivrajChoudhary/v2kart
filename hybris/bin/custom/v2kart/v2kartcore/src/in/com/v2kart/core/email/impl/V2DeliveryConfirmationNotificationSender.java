/**
 * 
 */
package in.com.v2kart.core.email.impl;

import in.com.v2kart.core.sms.V2SmsService;
import in.com.v2kart.core.sms.populator.V2UserSmsDataMapPopulator;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.core.enums.OrderStatus;
/**
 * @author shubhammaheshwari
 *
 */
public class V2DeliveryConfirmationNotificationSender {
    
    private ModelService modelService;
    
    private BusinessProcessService businessProcessService;
    
    private V2SmsService v2SmsService;
    
    private V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator;

    /**
     * @param consignmentModel
     * trigger SMS and Email notification for Delivery Confirmation
     */
    public void triggerDeliveryConfirmationNotification(ConsignmentModel consignmentModel){
        final AbstractOrderModel orderModel = consignmentModel.getOrder();
        // Set Order status as completed.
        orderModel.setStatus(OrderStatus.COMPLETED);
        modelService.save(orderModel);
        // triggering sms for Order Confirmation 
        v2SmsService.sendSms(v2UserSmsDataMapPopulator.createV2OrderConfirmationSmsDataMap((OrderModel)consignmentModel.getOrder()),
                "Delivery_Confirmation_message_template",
                ((CustomerModel)orderModel.getUser()).getMobileNumber());
        
        final ConsignmentProcessModel consignmentProcessModel = businessProcessService.createProcess(
                "sendDeliveryEmailProcess-" + consignmentModel.getCode() + "-" + System.currentTimeMillis(), "sendDeliveryEmailProcess");
        consignmentProcessModel.setConsignment(consignmentModel);
        modelService.save(consignmentProcessModel);
        businessProcessService.startProcess(consignmentProcessModel);
    }
    
    /**
     * @param businessProcessService
     */
    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    /**
     * @param modelService
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
    
    /**
     * @param v2SmsService
     */
    @Required
    public void setV2SmsService(V2SmsService v2SmsService) {
        this.v2SmsService = v2SmsService;
    }

    /**
     * @param v2UserSmsDataMapPopulator
     */
    @Required
    public void setV2UserSmsDataMapPopulator(V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator) {
        this.v2UserSmsDataMapPopulator = v2UserSmsDataMapPopulator;
    }
}
