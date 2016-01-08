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

import in.com.v2kart.core.dao.V2HexCodesDao;
import in.com.v2kart.core.jalo.V2HexCodes;
import in.com.v2kart.core.model.V2HexCodesModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class V2HexCodesDaoImpl implements V2HexCodesDao {

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<V2HexCodesModel> findHexCodeForColor() {
        final String query = "SELECT {pk} FROM {" + V2HexCodesModel._TYPECODE
                + "}";
//        V2HexCodesModel v2HexCode = null;
        final SearchResult<V2HexCodesModel> result = flexibleSearchService
                .search(query);
        final List<V2HexCodesModel> hexCodes = result.getResult();
//        for (V2HexCodesModel hexCodeModel : hexCodes) {
//            v2HexCode = hexCodeModel;
//        }
        return hexCodes;
    }

}
