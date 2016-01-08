package in.com.v2kart.facades.seller.impl;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;

import in.com.v2kart.core.services.V2BeSellerService;
import in.com.v2kart.facades.core.data.V2SellerData;
import in.com.v2kart.facades.seller.SellerFacade;

public class SellerFacadeImpl implements SellerFacade {

    V2BeSellerService v2BeSellerService;

    @Override
    public void saveSellerData(final V2SellerData v2SellerData)
            throws DuplicateUidException {

        v2BeSellerService.saveBeSellerData(v2SellerData);
    }

    /**
     * @param v2BeSellerService
     *        the v2BeSellerService to set
     */
    public void setV2BeSellerService(final V2BeSellerService v2BeSellerService) {
        this.v2BeSellerService = v2BeSellerService;
    }

}
