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

import java.util.List;

/**
 * Velocity context for email about partially order cancellation.
 */
public class OrderCancelledEmailContext extends OrderPartiallyModifiedEmailContext {

    @Override
    public void init(final OrderModificationProcessModel orderProcessModel, final EmailPageModel emailPageModel) {
        super.init(orderProcessModel, emailPageModel);
    }
    
    
    public List<OrderEntryData> getCanceledEntries() {
        return super.getModifiedEntries();
    }
}
