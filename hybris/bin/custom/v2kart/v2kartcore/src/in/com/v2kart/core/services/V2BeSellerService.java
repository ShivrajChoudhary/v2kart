/**
 * 
 */
package in.com.v2kart.core.services;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;

import in.com.v2kart.facades.core.data.V2SellerData;

/**
 * 
 * handles customer notification request from facade
 * 
 * @author himanshumehta
 * 
 */
public interface V2BeSellerService {
    /**
     * @param V2SellerModel
     *        saves the be Seller data in the database
     */
    void saveBeSellerData(V2SellerData v2SellerData)
            throws DuplicateUidException;

}
