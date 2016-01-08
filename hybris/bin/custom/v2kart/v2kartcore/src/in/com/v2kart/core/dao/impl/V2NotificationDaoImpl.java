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
package in.com.v2kart.core.dao.impl;

import in.com.v2kart.core.dao.V2NotificationDao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import in.com.v2kart.core.model.V2NotificationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;



public class V2NotificationDaoImpl implements V2NotificationDao
{
	
	@Autowired
	private FlexibleSearchService flexibleSearchService;
	

	@Override
	public List<V2NotificationModel> findAllNotifications()
	{
		final String query = "SELECT {pk} FROM {" + V2NotificationModel._TYPECODE + "}";
		final SearchResult<V2NotificationModel> result = flexibleSearchService.search(query);
		final List<V2NotificationModel> notifications = result.getResult();
		return notifications;
	}


	@Override
	public List<V2NotificationModel> findActiveNotfications()
	{
		final Date currentDate = new Date();
		final String query = "SELECT {pk} FROM {" + V2NotificationModel._TYPECODE + "} WHERE ?currentDate between { "
				+ V2NotificationModel.STARTDATE + "} and {" + V2NotificationModel.ENDDATE + "} AND {" + V2NotificationModel.ACTIVE
				+ "} = ?active";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentDate", currentDate);
		params.put("active", Boolean.TRUE);
		final SearchResult<V2NotificationModel> result = flexibleSearchService.search(query, params);
		final List<V2NotificationModel> notifications = result.getResult();
		return notifications;
	}

}
