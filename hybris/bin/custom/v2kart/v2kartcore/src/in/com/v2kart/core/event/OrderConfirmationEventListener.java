/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package in.com.v2kart.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import in.com.v2kart.core.sms.V2SmsService;
import in.com.v2kart.core.sms.populator.V2UserSmsDataMapPopulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.mobileservices.model.text.UserPhoneNumberModel;
import de.hybris.platform.orderprocessing.events.OrderPlacedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

/**
 * Listener for order confirmation events.
 */
public class OrderConfirmationEventListener extends AbstractSiteEventListener<OrderPlacedEvent> {

    private ModelService modelService;
    private BusinessProcessService businessProcessService;

    @Autowired
    private V2SmsService v2SmsService;

    @Autowired
    private V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator;

    protected BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    protected void onSiteEvent(final OrderPlacedEvent orderPlacedEvent) {
        final OrderModel orderModel = orderPlacedEvent.getProcess().getOrder();
        
        
        final OrderProcessModel orderProcessModel = (OrderProcessModel) getBusinessProcessService()
                .createProcess("orderConfirmationEmailProcess-" + orderModel.getCode() + "-" + System.currentTimeMillis(),
                        "orderConfirmationEmailProcess");
       
        v2SmsService.sendSms(v2UserSmsDataMapPopulator.createV2OrderConfirmationSmsDataMap(orderModel),
                "Order_Confirmation_message_template",
                ((CustomerModel) orderModel.getUser()).getMobileNumber());
//       if( !phone.isEmpty()){
//        v2SmsService.sendSms(v2UserSmsDataMapPopulator.createV2OrderConfirmationSmsDataMap(orderModel),
//                "Order_Confirmation_message_template",
//                phone.get(size-1).getPhoneNumber().getNumber());
//       }

        // sends the sms to all the phone numbers of the current customer
//       for (UserPhoneNumberModel phoneNumbers : ((CustomerModel) orderModel.getUser()).getPhoneNumbers()) {
        
        
        UserPhoneNumberModel phone = null;
        Collection<UserPhoneNumberModel> phoneNoList = ((CustomerModel) orderModel.getUser()).getPhoneNumbers();
        if(!(phoneNoList==null)&& !(phoneNoList.isEmpty())){ 	
        int size= orderModel.getUser().getPhoneNumbers().size(); 
         phone = ((List<UserPhoneNumberModel>) ((CustomerModel) orderModel.getUser()).getPhoneNumbers()).get(size-1);
            v2SmsService.sendSms(v2UserSmsDataMapPopulator.createV2OrderConfirmationSmsDataMap(orderModel),
                   "Order_Confirmation_message_template",
                  phone.getPhoneNumber().getNumber());
          }
           
            

        orderProcessModel.setOrder(orderModel);
        getModelService().save(orderProcessModel);
        getBusinessProcessService().startProcess(orderProcessModel);
        
        CustomerModel customer = (CustomerModel)orderModel.getUser();
        customer.setPhoneNumbers(null);
        getModelService().save(customer);
    }
    

    @Override
    protected boolean shouldHandleEvent(final OrderPlacedEvent event) {
        final OrderModel order = event.getProcess().getOrder();
        ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
        final BaseSiteModel site = order.getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
        return SiteChannel.B2C.equals(site.getChannel());
    }
}
