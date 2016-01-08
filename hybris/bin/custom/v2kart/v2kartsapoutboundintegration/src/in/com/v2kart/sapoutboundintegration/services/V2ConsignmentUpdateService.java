package in.com.v2kart.sapoutboundintegration.services;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.ConsignmentCreationException;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import in.com.v2kart.core.model.V2LogisticServiceProviderModel;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateReq;

/**
 * 
 * @author satvir_nagarro
 * 
 */
public interface V2ConsignmentUpdateService {
    /**
     * Creates new Consignment
     * 
     * @param order
     *        order
     * @param orderEntries
     *        orderEntries
     * @param orderStatusUpdate
     *        orderStatusUpdate
     * @return
     */
    ConsignmentModel createConsignment(final AbstractOrderModel order, final SoOrderStatusUpdateReq orderStatusUpdate)
            throws ConsignmentCreationException;

    /**
     * Update consignment details and line items status and consignment staus.
     * 
     * @param order
     *        order
     * @param consignment
     *        consignment
     * @param orderStatusUpdate
     *        orderStatusUpdate
     */
    ConsignmentStatus updateConsignment(final AbstractOrderModel order, final ConsignmentModel consignment,
            final SoOrderStatusUpdateReq orderStatusUpdate);

    /**
     * Commits stock for this consignment permannently
     * 
     * @param consignment
     */
    void commitStock(final ConsignmentModel consignment);

    /**
     * 
     * @param lspCode
     * @return
     */
    V2LogisticServiceProviderModel findLspByCode(final String lspCode);
}
