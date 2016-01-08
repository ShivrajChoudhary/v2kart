/**
 * 
 */
package in.com.v2kart.core.services.impl;

import in.com.v2kart.core.services.V2CalculationService;
import in.com.v2kart.core.services.V2RefundService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.refund.impl.DefaultRefundService;
import de.hybris.platform.returns.OrderReturnRecordHandler;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.model.OrderReturnRecordEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.TaxValue;

/**
 * The class <Code>V2RefundServiceImpl</Code> is implementation class of <code>V2RefundService </code> it contains services related to order
 * refund calculation.
 * 
 * @author Nagarro_devraj802
 * @since 1.2
 * 
 */
public class V2RefundServiceImpl extends DefaultRefundService implements
        V2RefundService {

    /** Logger bean injection */
    private final static Logger LOG = Logger
            .getLogger(V2RefundServiceImpl.class);

    /** CalculationService bean injection */
    private V2CalculationService calculationService;

    /** PromotionsService bean injection */
    private PromotionsService v2PromotionsService;

    private ModelService modelService;

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public V2CalculationService getCalculationService() {
        return calculationService;
    }

    /** Constants used for v2kartPromoGrp */
    private String V2_KART_PROMO_GRP = "v2kartPromoGrp";

    public PromotionsService getV2PromotionsService() {
        return v2PromotionsService;
    }

    public void setV2PromotionsService(PromotionsService v2PromotionsService) {
        this.v2PromotionsService = v2PromotionsService;
    }

    /** OrderReturnRecordHandler bean injection **/
    private OrderReturnRecordHandler orderReturnRecordsHandler;

    /** OrderService bean injection **/
    private OrderService orderService;

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderReturnRecordHandler getOrderReturnRecordsHandler() {
        return orderReturnRecordsHandler;
    }

    public void setOrderReturnRecordsHandler(
            OrderReturnRecordHandler orderReturnRecordsHandler) {
        this.orderReturnRecordsHandler = orderReturnRecordsHandler;
    }

    /** {@inheritDoc} */
    @Override
    public OrderModel createFullOrderRefundPreview(
            final OrderModel refundOrderPreview) {
        // final OrderModel refundOrderPreview =
        // this.createRefundOrderPreview(originalOrderModel);
        // refundOrderPreview.setDeliveryCost(0d);
        for (final AbstractOrderEntryModel orderEntry : refundOrderPreview
                .getEntries()) {
            if (orderEntry == null) {
                continue;
            }
            final long newQuantity = 0l;
            orderEntry.setQuantity(Long.valueOf(newQuantity));
            orderEntry.setCalculated(Boolean.TRUE);

            if (newQuantity <= 0L) {
                orderEntry.setQuantityStatus(OrderEntryStatus.DEAD);
            }
            getModelService().save(orderEntry);
        }
        refundOrderPreview.setCalculated(Boolean.FALSE);
        getModelService().save(refundOrderPreview);

        // Recalculate Promotion details
        this.recalculatePromotionsForPreview(refundOrderPreview);
        return refundOrderPreview;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public OrderModel createPartialOrderRefundPreview(
            final OrderModel refundOrderPreview,
            final OrderCancelRequest orderCancelRequest) {
        for (OrderCancelEntry oce : orderCancelRequest.getEntriesToCancel()) {
            AbstractOrderEntryModel oem = oce.getOrderEntry();
            AbstractOrderEntryModel orderEntry = getOrderEntryByEntryId(
                    refundOrderPreview, oem.getEntryNumber());
            long previousQuantity = orderEntry.getQuantity().longValue();

            if (oce.getCancelQuantity() <= oce.getOrderEntry().getQuantity()
                    .longValue()) {
                orderEntry.setQuantity(Long.valueOf(previousQuantity
                        - oce.getCancelQuantity()));

                if (previousQuantity == oce.getCancelQuantity()) {
                    orderEntry.setQuantityStatus(OrderEntryStatus.DEAD);
                }
                getModelService().save(orderEntry);
            }
        }
        refundOrderPreview.setCalculated(Boolean.FALSE);
        getModelService().save(refundOrderPreview);
        // Recalculate Promotion details
        this.recalculatePromotionsForReturnPreview(refundOrderPreview);
        return refundOrderPreview;
    }

    /**
     * Return {@link AbstractOrderEntryModel } from OrderModel by its entryId.
     * 
     * @param orderModel
     * 
     * @param entryId
     *        to get AbstractOrderEntryModel
     * @return AbstractOrderEntryModel
     */
    private AbstractOrderEntryModel getOrderEntryByEntryId(
            final OrderModel orderModel, final Integer entryId) {
        ServicesUtil.validateParameterNotNull(orderModel,
                "Order must not be null");

        for (final AbstractOrderEntryModel oem : orderModel.getEntries()) {
            if (entryId == oem.getEntryNumber()) {
                return oem;
            }
        }
        return null;
    }

    /* *//** {@inheritDoc} */
    /*
     * @Override public void apply(final OrderModel previewOrder, final ReturnRequestModel request) throws
     * OrderReturnRecordsHandlerException { }
     */

    /** {@inheritDoc} */
    @Override
    public OrderModel createOrderPreview(OrderModel originalOrderModel) {
        final OrderModel refundOrderPreview = this
                .createRefundOrderPreview(originalOrderModel);

        return refundOrderPreview;
    }

    /** {@inheritDoc} */
    @Override
    public OrderModel createOrderPreviewAsPerCancellationRequest(
            final OrderCancelRequest orderCancelRequest) {
        OrderModel refundOrderPreview = createOrderPreview(orderCancelRequest
                .getOrder());
        refundOrderPreview = updateOrderAsPerCancellationRequest(
                refundOrderPreview, orderCancelRequest);
        return refundOrderPreview;
    }

    /**
     * API is used to Update Order As Per cancellation Request parameter.
     * 
     * @param refundOrderPreview
     * @param orderCancelRequest
     * @return
     */
    private OrderModel updateOrderAsPerCancellationRequest(
            final OrderModel refundOrderPreview,
            final OrderCancelRequest orderCancelRequest) {
        for (OrderCancelEntry oce : orderCancelRequest.getEntriesToCancel()) {
            AbstractOrderEntryModel oem = oce.getOrderEntry();
            AbstractOrderEntryModel orderEntry = getOrderEntryByEntryId(
                    refundOrderPreview, oem.getEntryNumber());
            long previousQuantity = orderEntry.getQuantity().longValue();

            if (oce.getCancelQuantity() <= oce.getOrderEntry().getQuantity()
                    .longValue()) {
                orderEntry.setQuantity(Long.valueOf(previousQuantity
                        - oce.getCancelQuantity()));

                if (previousQuantity == oce.getCancelQuantity()) {
                    orderEntry.setQuantityStatus(OrderEntryStatus.DEAD);
                }
                getModelService().save(orderEntry);
            }
        }

        refundOrderPreview.setCalculated(Boolean.FALSE);
        getModelService().save(refundOrderPreview);
        // Recalculate Promotion details
        this.recalculatePromotionsForPreview(refundOrderPreview);
        getCalculationService().calculateTotalsForCancellation(
                refundOrderPreview, orderCancelRequest.isPartialCancel());

        /*
         * if (refundOrderPreview.getDeliveryCost() != null && (refundOrderPreview.getDeliveryCost() - 0) > 0) { if
         * (refundOrderPreview.getTotalPrice() - refundOrderPreview.getDeliveryCost() < 0.001) { refundOrderPreview.setTotalPrice(0.0); } }
         */

        return refundOrderPreview;
    }

    /**
     * @param calculationService
     *        the calculationService to set
     */
    @Required
    public void setCalculationService(
            final V2CalculationService calculationService) {
        this.calculationService = calculationService;
    }
    
    @Override
    public OrderReturnRecordEntryModel getOrderReturnRecordEntry(OrderModel previewOrder, ReturnRequestModel request) throws OrderReturnRecordsHandlerException {
        OrderModel finalOrder = request.getOrder();
        OrderReturnRecordEntryModel record = getOrderReturnRecordsHandler()
                .createRefundEntry(
                        finalOrder,
                        getRefunds(request),
                        (new StringBuilder("Refund request for order: "))
                                .append(finalOrder.getCode()).toString());
        return record;
    }

    @Override
    public void applyRefunds(OrderModel previewOrder,
            ReturnRequestModel request){
        OrderModel finalOrder = request.getOrder();
        AbstractOrderEntryModel originalEntry;
        for (Iterator<AbstractOrderEntryModel> iterator = previewOrder.getEntries().iterator(); iterator.hasNext(); getModelService().save(originalEntry)) {
            AbstractOrderEntryModel previewEntry = (AbstractOrderEntryModel) iterator.next();
            originalEntry = getEntry(finalOrder, previewEntry.getEntryNumber());
            long newQuantity = previewEntry.getQuantity().longValue();
            originalEntry.setQuantity(Long.valueOf(newQuantity));
            originalEntry.setCalculated(Boolean.FALSE);
            if (newQuantity <= 0L)
                originalEntry.setQuantityStatus(OrderEntryStatus.DEAD);
        }

        finalOrder.setCalculated(Boolean.FALSE);
        getModelService().save(finalOrder);
        try {
            getCalculationService().calculateRefundOrder(finalOrder);
        } catch (CalculationException exception) {
            LOG.debug(exception);
        }
    }

    /**
     * API used to re- calculate order's promotion as per current order state.
     * 
     * @param order
     *        To re-calculate promotions
     * @return
     */
    @Override
    public OrderModel recalculatePromotionsForPreview(OrderModel order) {
        final PromotionGroupModel promotionGroup = v2PromotionsService
                .getPromotionGroup(V2_KART_PROMO_GRP);
        final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
        promotionGroups.add(promotionGroup);
        try {
            if (null != promotionGroups) {
                this.v2PromotionsService.updatePromotions(promotionGroups,
                        order);
            }
            ((CalculationService) this.calculationService).calculateTotals(
                    order, false);
        } catch (final CalculationException e) {
            LOG.error(
                    "Could not only calculate totals only on order, trying to do it using jalo calculation",
                    e);
        }
        return order;
    }

    public OrderModel recalculatePromotionsForReturnPreview(OrderModel order) {
        final PromotionGroupModel promotionGroup = v2PromotionsService
                .getPromotionGroup(V2_KART_PROMO_GRP);
        double deliveryCost = order.getDeliveryCost();
        final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
        promotionGroups.add(promotionGroup);
        if (null != promotionGroups) {
            this.v2PromotionsService.updatePromotions(promotionGroups, order);
        }
        order.setDeliveryCost(deliveryCost);
        getCalculationService().calculateTotalsForReturn(order, false);
        return order;
    }

    @Override
    public void apply(List<RefundEntryModel> refunds, OrderModel order) {
        for (Iterator it = refunds.iterator(); it.hasNext();) {
            RefundEntryModel refund = (RefundEntryModel) it.next();
            AbstractOrderEntryModel orderEntry = getOrderEntry(refund, order);
            if (orderEntry == null)
                continue;
            long newQuantity = orderEntry.getQuantity().longValue()
                    - refund.getExpectedQuantity().longValue();
            orderEntry.setQuantity(Long.valueOf(newQuantity));
            orderEntry.setCalculated(Boolean.FALSE);

            if (newQuantity <= 0L) {
                orderEntry.setQuantityStatus(OrderEntryStatus.DEAD);
            }

            getModelService().save(orderEntry);
        }

        order.setCalculated(Boolean.FALSE);
        getModelService().save(order);
        try {
            getCalculationService().calculateRefundOrder(order);
        } catch (CalculationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // this.orderService.calculateOrder(order);
    }
}
