package in.com.v2kart.core.dao.impl;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.core.dao.V2BeSellerDAO;
import in.com.v2kart.core.model.V2SellerModel;

public class V2BeSellerDAOImpl implements V2BeSellerDAO {
    /**
     * model service bean injection
     */
    private ModelService modelService;

    @Override
    public void saveBeSellerData(final V2SellerModel v2SellerModel)
            throws DuplicateUidException {

        try {
            modelService.save(v2SellerModel);
        } catch (final ModelSavingException e) {
            throw new DuplicateUidException(v2SellerModel.getEmail(), e);
        }

    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
