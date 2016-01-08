package in.com.v2kart.core.dao.impl;

import in.com.v2kart.core.dao.V2ReturnRequestDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.returns.dao.impl.DefaultReturnRequestDao;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class V2ReturnRequestDaoImpl extends DefaultReturnRequestDao implements V2ReturnRequestDao{
    @Autowired
    private FlexibleSearchService flexibleSearchService;


	@Override
	public ReturnRequestModel getReturnRequestFromRMA(String RMACode) {
		final String query = "SELECT {pk} FROM {" + ReturnRequestModel._TYPECODE + "} WHERE {" + ReturnRequestModel.RMA +"}= ?RMACode";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("RMACode", RMACode);
		final SearchResult<ReturnRequestModel> result = flexibleSearchService
                .search(query,params);
        final List<ReturnRequestModel> returnRequestModels = result.getResult();
		return returnRequestModels.get(0);
	}

}
