package in.com.v2kart.core.dao;

import de.hybris.platform.returns.model.ReturnRequestModel;

public interface V2ReturnRequestDao {
	
	ReturnRequestModel getReturnRequestFromRMA(String RMACode);

}
