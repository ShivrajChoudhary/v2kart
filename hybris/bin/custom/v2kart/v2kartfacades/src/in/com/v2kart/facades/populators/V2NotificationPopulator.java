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
package in.com.v2kart.facades.populators;

import in.com.v2kart.facades.notification.data.V2NotificationData;
import de.hybris.platform.converters.Populator;
import in.com.v2kart.core.model.V2NotificationModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates Notification Data with description and active flag
 */
public class V2NotificationPopulator implements Populator<V2NotificationModel, V2NotificationData> {

    @Override
    public void populate(final V2NotificationModel source, final V2NotificationData target) throws ConversionException {
    	
    	if(source!=null && target!=null){
    		target.setActive(source.getActive());
    		target.setDescription(source.getDescription());
    		target.setLink(source.getLink());
    		if(source.getIsPdp()!=null){
    		target.setIsPdp(source.getIsPdp());
    		}
    		if(source.getIsPlp()!=null){
    		target.setIsPlp(source.getIsPlp());
    		}
    		target.setId(source.getId());
    		target.setStartDate(source.getStartDate());
    		target.setEndDate(source.getEndDate());
    target.setTitle(source.getTitle());
    
    	}

    }

}
