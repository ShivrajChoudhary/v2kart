/**
 * 
 */
package in.com.v2kart.core.services.impl;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.core.dao.V2BeSellerDAO;
import in.com.v2kart.core.model.V2SellerModel;
import in.com.v2kart.core.services.V2BeSellerService;
import in.com.v2kart.facades.core.data.V2SellerData;

/**
 * @author himanshumehta
 */
public class V2BeSellerServiceImpl implements V2BeSellerService {

    private V2BeSellerDAO v2BeSellerDAO;

    /**
     * modelService bean Injection
     */
    private ModelService modelService;

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public void saveBeSellerData(final V2SellerData v2SellerData)
            throws DuplicateUidException {

        final V2SellerModel v2SellerModel = modelService
                .create(V2SellerModel.class);

        populateNotifyCustomerModel(v2SellerData, v2SellerModel);

        v2BeSellerDAO.saveBeSellerData(v2SellerModel);
    }

    /**
     * @param customerNotificationData
     * @param notifyCustomerModel
     * 
     *        populate notify customer model
     */
    private void populateNotifyCustomerModel(final V2SellerData v2SellerData,
            final V2SellerModel v2SellerModel) {

        v2SellerModel.setName(v2SellerData.getName());
        v2SellerModel.setPhone(v2SellerData.getPhone());
        v2SellerModel.setEmail(v2SellerData.getEmail());
        v2SellerModel.setMessage(v2SellerData.getMessage());
        v2SellerModel.setCategory(v2SellerData.getCategory());
    }

    /**
     * @param v2BeSellerDAO
     *        the v2BeSellerDAO to set
     */
    public void setV2BeSellerDAO(final V2BeSellerDAO v2BeSellerDAO) {
        this.v2BeSellerDAO = v2BeSellerDAO;
    }
}
