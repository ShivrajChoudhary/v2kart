package in.com.v2kart.core.serviceability.impl;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import in.com.v2kart.core.model.ServiceabilityAreaModel;
import in.com.v2kart.core.serviceability.V2ServiceabilityService;
import in.com.v2kart.core.serviceability.daos.V2ServiceabilityDao;

public class V2ServiceabilityServiceImpl implements V2ServiceabilityService {

    private V2ServiceabilityDao serviceabilityDao;

    private CartService cartService;

    private CommonI18NService commonI18NService;

    @Required
    public void setServiceabilityDao(final V2ServiceabilityDao serviceabilityDao) {
        this.serviceabilityDao = serviceabilityDao;
    }

    /**
     * @param cartService
     *        the cartService to set
     */
    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * @param commonI18NService
     *        the commonI18NService to set
     */
    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Override
    public boolean isProductServicableForPinCode(final String pinCode, final String countryIsoCode) {
        final CartModel cartModel = cartService.getSessionCart();
        return serviceabilityDao.checkDeliveryOptionForPinCode(pinCode, cartModel.getNet(), commonI18NService.getCountry(countryIsoCode),
                cartModel.getCurrency(), cartModel.getStore());

    }

    @Override
    public boolean isProductServicableForPinCode(String pinCode) {

        return serviceabilityDao.checkDeliveryOptionForPinCode(pinCode);

    }
    
    @Override
    public boolean isCodAvailableForArea(final String pincode)
    {
        boolean isCodAvailable;
        final ServiceabilityAreaModel serviceArea =  serviceabilityDao.getServiceabilityAreaForPincode(pincode);

        if (serviceArea != null)
        {
            isCodAvailable = true;
        }
        else
        {
            isCodAvailable = false;
        }
        return isCodAvailable;
    }
}
