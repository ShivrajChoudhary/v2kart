package in.com.v2kart.core.dao;

import java.util.List;

import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

public interface V2RefundEntryDao {
	
	List<RefundEntryModel> getRefundEntryList(ReturnRequestModel returnRequest);

}
