package in.com.v2kart.core.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import in.com.v2kart.core.dao.V2RefundEntryDao;

public class V2RefundEntryDaoImpl implements V2RefundEntryDao{
	 @Autowired
	    private FlexibleSearchService flexibleSearchService;


	@Override
	public List<RefundEntryModel> getRefundEntryList(
			ReturnRequestModel returnRequest) {
		
		final String query = "SELECT {pk} FROM {" + RefundEntryModel._TYPECODE + " as rEM JOIN " + ReturnRequestModel._TYPECODE + " as rRM ON {rEM.returnRequest}={rRM.pk}} WHERE  {rRM.pk} = ?returnRequest";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("returnRequest", returnRequest.getPk());
		final SearchResult<RefundEntryModel> result = flexibleSearchService
                .search(query,params);
        final List<RefundEntryModel> returnRequestModels = result.getResult();
		
		return returnRequestModels;
	}

}
