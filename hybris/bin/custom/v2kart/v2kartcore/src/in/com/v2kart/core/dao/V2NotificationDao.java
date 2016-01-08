/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package in.com.v2kart.core.dao;

import java.util.List;

import  in.com.v2kart.core.model.V2NotificationModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;


/**
 * The {@link DeliveryModeModel} DAO.
 * 
 * @spring.bean deliveryModeDao
 */
public interface V2NotificationDao
{

	/**
	 * Finds the {@link NotificationModel}s.
	 * 
	 * @param code
	 * @return the found {@link V2NotificationModel}s
	 */
	List<V2NotificationModel> findAllNotifications();

	/**
	 * Finds active {@link V2NotificationModel}s.
	 * 
	 * @return a <code>Collection</code> of active {@link V2NotificationModel}s, or empty list if not found.
	 */
	List<V2NotificationModel> findActiveNotfications();


}
