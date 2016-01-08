package in.com.v2kart.fulfilmentprocess.actions.order;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 */
public class SendOrderToSAPAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

    private final Logger LOG = Logger.getLogger(SendOrderToSAPAction.class);

    /** FGSapSaleOrderIntegrationService bean injection */
    private V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService;

    @Override
    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();
        try {
            v2SapInboundSaleOrderIntegrationService.updateErpSales(order);
        } catch (final V2SapConnectionException fgSCE) {
            // TODO
            // LOG.error(fgSCE.getMessage(), fgSCE);
        }
        return Transition.OK;
    }

    /**
     * @param v2SapInboundSaleOrderIntegrationService the v2SapInboundSaleOrderIntegrationService to set
     */
    @Required
    public void setV2SapInboundSaleOrderIntegrationService(V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService) {
        this.v2SapInboundSaleOrderIntegrationService = v2SapInboundSaleOrderIntegrationService;
    }

}
