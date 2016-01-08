/**
 * 
 */
package in.com.v2kart.sapinboundintegration.order.daos;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * @author satvir_nagarro
 * 
 */
public interface V2AcceleratorConsignmentDao {

    /**
     * 
     * @param consignmentCode
     * @param orderCode
     * @param sites
     * @return
     */
    ConsignmentModel findConsignmentByCodeAndOrderCode(final String consignmentCode, final String orderCode);

}
