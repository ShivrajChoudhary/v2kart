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

import in.com.v2kart.core.model.V2HexCodesModel;



public interface V2HexCodesDao
{

    List<V2HexCodesModel> findHexCodeForColor();
}
