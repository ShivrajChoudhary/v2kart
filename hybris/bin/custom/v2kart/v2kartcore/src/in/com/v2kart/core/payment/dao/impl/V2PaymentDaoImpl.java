/**
 * 
 */
package in.com.v2kart.core.payment.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import in.com.v2kart.core.model.V2PaymentModeModel;
import in.com.v2kart.core.model.V2PaymentResponseInfoModel;
import in.com.v2kart.core.payment.dao.V2PaymentDao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

/**
 * The Class V2PaymentDaoImpl.
 * 
 * @author Ànuj
 */
public class V2PaymentDaoImpl implements V2PaymentDao {

    @Resource(name = "flexibleSearchService")
    private FlexibleSearchService flexibleSearchService;

    @Override
    public V2PaymentModeModel getV2PaymentMode(String code) {
        validateParameterNotNull(code, "code must not be null!");

        V2PaymentModeModel v2PaymentModeModel = new V2PaymentModeModel();
        v2PaymentModeModel.setMode(code);
        final List<V2PaymentModeModel> list = getFlexibleSearchService().getModelsByExample(v2PaymentModeModel);
        if (!list.isEmpty()) {
            v2PaymentModeModel = list.get(0);
        }
        return v2PaymentModeModel;
    }

    @Override
    public V2PaymentResponseInfoModel getV2PaymentResponseInfoModel(final String txnId) {
        validateParameterNotNull(txnId, "transaction id musV2not be null!");

        V2PaymentResponseInfoModel v2PaymentResponseInfo = new V2PaymentResponseInfoModel();
        v2PaymentResponseInfo.setTxnid(txnId);
        final List<V2PaymentResponseInfoModel> list = getFlexibleSearchService().getModelsByExample(v2PaymentResponseInfo);
        if (!list.isEmpty()) {
            v2PaymentResponseInfo = list.get(0);
        } else {
            v2PaymentResponseInfo = null;
        }
        return v2PaymentResponseInfo;
    }

    /**
     * @return the flexibleSearchService
     */
    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    /**
     * @param flexibleSearchService
     *        the flexibleSearchService to set
     */
    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
