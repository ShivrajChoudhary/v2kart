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

package in.com.v2kart.sapoutboundintegration.hmc;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.hmc.util.action.ActionEvent;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.util.action.ItemAction;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.ordersplitting.jalo.Consignment;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.core.sms.V2SmsService;
import in.com.v2kart.core.sms.populator.V2UserSmsDataMapPopulator;
import in.com.v2kart.sapoutboundintegration.services.V2ConsignmentUpdateService;

/**
 * 
 */
public class PickupConfirmationHMCAction extends ItemAction {
    private static final long serialVersionUID = -5500487889164110964L;

    @SuppressWarnings({ "deprecation" })
    @Override
    public ActionResult perform(final ActionEvent event) throws JaloBusinessException {
        final Item item = getItem(event);
        if (item instanceof Consignment) {
            ((Consignment) item).setStatus(EnumerationManager.getInstance().getEnumerationValue(ConsignmentStatus._TYPECODE,
                    ConsignmentStatus.READY_TO_DISPATCH.getCode()));
            final ConsignmentModel consignmentModel = (ConsignmentModel) this.getModelService().get(((Consignment) item).getPK());
            consignmentModel.setLsp(this.getV2ConsignmentUpdateService().findLspByCode(consignmentModel.getCarrier()));
            this.getV2ConsignmentUpdateService().commitStock(consignmentModel);
            this.getModelService().save(consignmentModel);
            // TODO sent mail and message here if required
            triggerDeliverySentNotificationToCustomer(consignmentModel);
            return new ActionResult(ActionResult.OK, true, false);
        }
        return new ActionResult(ActionResult.FAILED, false, false);
    }

    /**
     * @param consignment
     */
    private void triggerDeliverySentNotificationToCustomer(final ConsignmentModel consignmentModel) {
        final AbstractOrderModel orderModel = consignmentModel.getOrder();
        // triggering sms for ORDER_DISPATCHED
        getV2SmsService().sendSms(getV2UserSmsDataMapPopulator().createV2OrderDispatchedSmsDataMap(consignmentModel),
                "Order_Dispatched_message_template", ((CustomerModel) orderModel.getUser()).getMobileNumber());

        final ConsignmentProcessModel consignmentProcessModel = getBusinessProcessService().createProcess(
                "sendDeliveryEmailProcess-" + consignmentModel.getCode() + "-" + System.currentTimeMillis(), "sendDeliveryEmailProcess");
        consignmentProcessModel.setConsignment(consignmentModel);
        getModelService().save(consignmentProcessModel);
        getBusinessProcessService().startProcess(consignmentProcessModel);

    }

    protected V2ConsignmentUpdateService getV2ConsignmentUpdateService() {
        return Registry.getApplicationContext().getBean("v2ConsignmentUpdateService", V2ConsignmentUpdateService.class);
    }

    protected ModelService getModelService() {
        return Registry.getApplicationContext().getBean("modelService", ModelService.class);
    }

    protected V2SmsService getV2SmsService() {
        return Registry.getApplicationContext().getBean("v2SmsService", V2SmsService.class);
    }

    protected V2UserSmsDataMapPopulator getV2UserSmsDataMapPopulator() {
        return Registry.getApplicationContext().getBean("v2UserSmsDataMapPopulator", V2UserSmsDataMapPopulator.class);
    }

    protected BusinessProcessService getBusinessProcessService() {
        return Registry.getApplicationContext().getBean("businessProcessService", BusinessProcessService.class);
    }
}
