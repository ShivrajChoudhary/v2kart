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
package in.com.v2kart.facades.process.email.context;

import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import in.com.v2kart.facades.order.data.OrderEntryCancelRecordEntryData;
import in.com.v2kart.facades.order.data.OrderEntryModificationRecordEntryData;
import in.com.v2kart.facades.order.data.OrderModificationRecordEntryData;

import java.util.ArrayList;
import java.util.List;

/**
 * Velocity context for email about partially order cancellation.
 */
public class OrderPartiallyCanceledEmailContext extends OrderPartiallyModifiedEmailContext {
    private OrderModificationRecordEntryData orderModificationRecordEntryData;


    public OrderModificationRecordEntryData getOrderCancelRecordEntryData() {
        return this.orderModificationRecordEntryData;
    }

    protected void fillModifiedEntries(final OrderModificationProcessModel orderProcessModel)
    {
        orderModificationRecordEntryData = new OrderModificationRecordEntryData();
        List<OrderEntryModificationRecordEntryData> cancelRecordEntryEntryDatas = new ArrayList<>();

        for (final OrderEntryModificationRecordEntryModel modificationEntry : orderProcessModel.getOrderModificationRecordEntry()
                .getOrderEntriesModificationEntries()) {
            OrderEntryCancelRecordEntryData cancelRecordEntryEntryData = new OrderEntryCancelRecordEntryData();
            OrderEntryCancelRecordEntryModel cancelRecordEntryEntry = (OrderEntryCancelRecordEntryModel) modificationEntry;

            final OrderEntryModel orderEntryModel = modificationEntry.getOriginalOrderEntry();
            final OrderEntryData orderEntryData = getOrderEntryConverter().convert(orderEntryModel);

            cancelRecordEntryEntryData.setQuantity(cancelRecordEntryEntry.getCancelledQuantity().longValue());
            cancelRecordEntryEntryData.setOrderEntryData(orderEntryData);

            cancelRecordEntryEntryDatas.add(cancelRecordEntryEntryData);
        }
        orderModificationRecordEntryData.setOrderEntryModificationRecordEntries(cancelRecordEntryEntryDatas);
    }
    
  


}
