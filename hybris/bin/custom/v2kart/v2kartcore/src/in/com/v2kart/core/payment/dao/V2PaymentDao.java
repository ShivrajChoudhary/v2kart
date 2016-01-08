/**
 * 
 */
package in.com.v2kart.core.payment.dao;

import in.com.v2kart.core.model.V2PaymentModeModel;
import in.com.v2kart.core.model.V2PaymentResponseInfoModel;

/**
 * The Interface V2PaymentDao.
 * 
 * @author Anuj
 */
public interface V2PaymentDao {

    /**
     * Returns payment mode based on the code
     * 
     * @param code
     *        , code received by PayU transaction
     * @return V2PaymentMode specific for the combination given
     */
    V2PaymentModeModel getV2PaymentMode(String code);

    /**
     * For getting the response received from payment gateway for the specific transaction
     * 
     * @param txnId
     *        , transaction id for which PayU response info required
     * @return PayU response Info
     */
    V2PaymentResponseInfoModel getV2PaymentResponseInfoModel(String txnId);

}
