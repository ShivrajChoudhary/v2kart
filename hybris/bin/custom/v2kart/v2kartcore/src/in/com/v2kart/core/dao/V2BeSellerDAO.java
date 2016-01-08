/**
 * 
 */

package in.com.v2kart.core.dao;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;

import in.com.v2kart.core.model.V2SellerModel;

public interface V2BeSellerDAO {

    /**
     * @param saveBeSellerData
     *        Saves Customer Saves the Be A Seller data in the DB
     */

    void saveBeSellerData(V2SellerModel v2SellerModel)
            throws DuplicateUidException;
}
