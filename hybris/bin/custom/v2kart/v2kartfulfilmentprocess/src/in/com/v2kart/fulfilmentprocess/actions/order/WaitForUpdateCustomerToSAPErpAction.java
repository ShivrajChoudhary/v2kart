package in.com.v2kart.fulfilmentprocess.actions.order;

import in.com.v2kart.sapinboundintegration.services.V2CustomerSapIntegrationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.util.Config;

/**
 *
 */
public class WaitForUpdateCustomerToSAPErpAction extends AbstractAction<OrderProcessModel> {

    private final Logger LOG = Logger.getLogger(WaitForUpdateCustomerToSAPErpAction.class);
    
    /**  V2CustomerSapIntegrationService bean injection */
    private V2CustomerSapIntegrationService v2CustomerSapIntegrationService;

    public enum Transition {
        OK, NOK, FAILED;

        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<String>();
            for (final Transition transitions : Transition.values()) {
                res.add(transitions.toString());
            }
            return res;
        }
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    @Override
    public final String execute(final OrderProcessModel process) throws RetryLaterException, Exception {
        return executeAction(process).toString();
    }

    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();
        Transition transition = Transition.OK;
        try {
            final CustomerModel customerModel = (CustomerModel) order.getUser();
            // IF the customer is not synced/created on SAP ERP
            if (StringUtils.isEmpty(customerModel.getSapCustomerId())) {
               this.addCustomerBillingAddress(customerModel, order);
               customerModel.setOrderPlaced(true);
               v2CustomerSapIntegrationService.createCustomer(customerModel);
                if (StringUtils.isEmpty(customerModel.getSapCustomerId())) {
                    setOrderStatus(order, OrderStatus.UPDATE_CUSTOMER_TO_SAPERP_FAILED);
                    transition = Transition.FAILED;
                } else {
                    updateStatus(order);
                    Thread.sleep(2000);
                    transition = Transition.NOK;
                }
            }
            if (stillWait(order)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Process [" + process.getCode() + "] : Order [" + order.getCode() + "] in a wait node");
                }
                Thread.sleep(2000);
                transition = Transition.NOK;
            }
        } catch (final InterruptedException e) {
            LOG.error("Sleep interrupted", e);
            // TODO
            // transition = Transition.NOK;
        }
        return transition;
    }
    /**
     * API is used to add customer billing address from order.
     * 
     * @param customerModel
     *        to set billing address
     * @param orderModel
     *        to get billing address
     */
    private void addCustomerBillingAddress(final CustomerModel customerModel, final OrderModel orderModel) {

        AddressModel billingAdrress = null;
        if (orderModel.getPaymentInfo() != null) {
            billingAdrress = orderModel.getPaymentInfo().getBillingAddress();
        }
        final AddressModel customerPresentAddress = modelService.clone(billingAdrress);
        final List<AddressModel> addresses = new ArrayList<AddressModel>(customerModel.getAddresses());
        addresses.add(customerPresentAddress);
        customerModel.setAddresses(addresses);
    }
    private boolean stillWait(final OrderModel order) {
        final long orderPlacedMilis = order.getCreationtime().getTime();
        final long timetowait = Config.getLong("order.WaittimeInSec.ForUpdateCustomerToSAPErp", 1) * 1000;

        return (new Date().getTime() < (orderPlacedMilis + timetowait))
                && OrderStatus.WAITING_FOR_UPDATE_CUSTOMER_TO_SAPERP.equals(order.getStatus());
    }
    /**
     * @param v2CustomerSapIntegrationService the v2CustomerSapIntegrationService to set
     */
    @Required
    public void setV2CustomerSapIntegrationService(V2CustomerSapIntegrationService v2CustomerSapIntegrationService) {
        this.v2CustomerSapIntegrationService = v2CustomerSapIntegrationService;
    }
    
    private void updateStatus(final OrderModel order) {
        getModelService().refresh(order);
        if (!OrderStatus.WAITING_FOR_UPDATE_CUSTOMER_TO_SAPERP.equals(order.getStatus())) {
            setOrderStatus(order, OrderStatus.WAITING_FOR_UPDATE_CUSTOMER_TO_SAPERP);
        }
    }

}
