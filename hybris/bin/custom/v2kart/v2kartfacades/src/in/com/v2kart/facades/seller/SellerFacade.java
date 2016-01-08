package in.com.v2kart.facades.seller;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;

import in.com.v2kart.facades.core.data.V2SellerData;

public interface SellerFacade {

	public void saveSellerData(V2SellerData v2SellerData)
			throws DuplicateUidException;

}
