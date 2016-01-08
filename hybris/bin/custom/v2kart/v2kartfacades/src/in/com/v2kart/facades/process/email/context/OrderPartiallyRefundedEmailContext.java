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

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.returns.model.OrderEntryReturnRecordEntryModel;
import in.com.v2kart.facades.order.data.OrderEntryCancelRecordEntryData;
import in.com.v2kart.facades.order.data.OrderEntryModificationRecordEntryData;
import in.com.v2kart.facades.order.data.OrderEntryRefundRecordEntryData;
import in.com.v2kart.facades.order.data.OrderModificationRecordEntryData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Velocity context for email about partially order refund
 */
public class OrderPartiallyRefundedEmailContext extends OrderPartiallyModifiedEmailContext {
    private OrderModificationRecordEntryData orderModificationRecordEntryData;


    public OrderModificationRecordEntryData getOrderRefundRecordEntryData() {
        return this.orderModificationRecordEntryData;
    }

    protected void fillModifiedEntries(final OrderModificationProcessModel orderProcessModel)
    {
        orderModificationRecordEntryData = new OrderModificationRecordEntryData();
        List<OrderEntryModificationRecordEntryData> refundRecordEntryEntryDatas = new ArrayList<>();

        for (final OrderEntryModificationRecordEntryModel modificationEntry : orderProcessModel.getOrderModificationRecordEntry()
                .getOrderEntriesModificationEntries()) {
           // OrderEntryCancelRecordEntryData cancelRecordEntryEntryData = new OrderEntryCancelRecordEntryData();
            
            OrderEntryRefundRecordEntryData refundRecordEntryEntryData =new OrderEntryRefundRecordEntryData();
           // OrderEntryCancelRecordEntryModel cancelRecordEntryEntry = (OrderEntryCancelRecordEntryModel) modificationEntry;
            
            OrderEntryReturnRecordEntryModel refundRecordEntryEntry = (OrderEntryReturnRecordEntryModel)modificationEntry;
            final OrderEntryModel orderEntryModel = modificationEntry.getOriginalOrderEntry();
            final OrderEntryData orderEntryData = getOrderEntryConverter().convert(orderEntryModel);

            refundRecordEntryEntryData.setQuantity(refundRecordEntryEntry.getReturnedQuantity());
            refundRecordEntryEntryData.setOrderEntryData(orderEntryData);

            refundRecordEntryEntryDatas.add(refundRecordEntryEntryData);
        }
        orderModificationRecordEntryData.setOrderEntryModificationRecordEntries(refundRecordEntryEntryDatas);
    }
}
