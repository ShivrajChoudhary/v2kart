/**
 *
 */
package in.com.v2kart.core.process.impl;

import in.com.v2kart.core.payment.services.V2PaymentService;
import in.com.v2kart.core.process.V2RefundOrderProcessor;
import in.com.v2kart.core.services.V2CalculationService;
import in.com.v2kart.core.services.V2RefundService;
import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Class <Code>V2RefundOrderProcessorImpl</Code> contains methods related to refund amount calculation. It also contains API that is
 * responsible for actual refunding.
 * 
 * @author Nagarro_Devraj802
 * @Since 1.2
 * 
 */
public class V2RefundOrderProcessorImpl implements V2RefundOrderProcessor {

    /** Application log file. */
    private static final Logger LOG = Logger.getLogger(V2RefundOrderProcessorImpl.class);

    /** Injecting bean V2PaymentService */
    private V2PaymentService v2PaymentService;

    /** Injecting bean ModelService */
    private ModelService modelService;

    /** V2RefundService bean injection */
    private V2RefundService refundService;

    private V2CalculationService calculationService;

    public V2CalculationService getCalculationService() {
        return calculationService;
    }

    public void setCalculationService(V2CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public V2RefundService getRefundService() {
        return refundService;
    }

    public void setRefundService(V2RefundService refundService) {
        this.refundService = refundService;
    }

    /** Constants used for StoreCredit */
    public final static String STORE_CREDIT = "StoreCredit";

    /**
     * API is used to process the refunding of an order. This method will refund payments to the store wallet.
     * 
     * @param orderPreview
     *        The order to be refunded.
     */
    @Override
    public void process(OrderModel orderPreview, BigDecimal refundAmount) {

        final OrderModel originalOrder = orderPreview.getOriginalVersion();
        final PaymentTransactionModel transaction = createPaymentTransactionForStoreCredit(originalOrder);
        // Debugging...
        if (LOG.isDebugEnabled()) {
            logRefundDetails(orderPreview, originalOrder);
        }

        v2PaymentService.refundFollowOn(orderPreview, transaction, refundAmount,
                Currency.getInstance(originalOrder.getCurrency().getIsocode()));
    }

    /**
     * API used to create new Payment Transaction For Store Credit.
     * 
     * @param order
     *        To create payment transaction
     * @return
     */
    private PaymentTransactionModel createPaymentTransactionForStoreCredit(final OrderModel order) {
        final PaymentTransactionModel transaction = modelService.create(PaymentTransactionModel.class);
        transaction.setCode("storecredit" + order.getUser().getUid() + "-" + UUID.randomUUID());
        this.modelService.save(transaction);
        transaction.setOrder(order);
        transaction.setPaymentProvider(STORE_CREDIT);
        this.modelService.save(transaction);
        return transaction;
    }

    /**
     * API is used to log refund details ie. refund amount , original order value. </P> Note use this API only if detail information
     * required for refund.
     * 
     * @param refundOrderPreview
     *        - preview order
     * @param originalOrder
     *        - order to process
     */
    @SuppressWarnings("boxing")
    private void logRefundDetails(final OrderModel refundOrderPreview, final OrderModel originalOrder) {

        LOG.debug("Refund details:");
        LOG.debug("  Order (refund preview) PK ................ " + refundOrderPreview.getPk());
        LOG.debug("  Order (refund preview) value ............. " + refundOrderPreview.getTotalPrice());
        LOG.debug("  Original order PK ........................ " + originalOrder.getPk());
        LOG.debug("  Original order value ..................... " + originalOrder.getTotalPrice());
        LOG.debug("  Amount to refund ......................... " + (originalOrder.getTotalPrice() - refundOrderPreview.getTotalPrice()));
        // LOG.debug("  Refund to ................................ " +
        // (refundToStoreCredit ? "store credit" : "source"));
        LOG.debug("  Order (refund preview) return requests:");
        for (final ReturnRequestModel returnRequestModel : refundOrderPreview.getReturnRequests()) {
            LOG.debug("    PK .................... " + returnRequestModel.getPk());
            LOG.debug("    Order PK .............. " + returnRequestModel.getOrder().getPk());
            LOG.debug("    Creation time ......... " + returnRequestModel.getCreationtime().toString());
            // .debug("    Refund to store credit. " +
            // returnRequestModel.getRefundToStoreCredit());
            LOG.debug("    Return entries:");
            for (final ReturnEntryModel returnEntry : returnRequestModel.getReturnEntries()) {
                LOG.debug("      PK .................. " + returnEntry.getPk());
                LOG.debug("      Expected quantity ... " + returnEntry.getExpectedQuantity());
                LOG.debug("      Action .............. " + returnEntry.getAction().toString());
                LOG.debug("      Status .............. " + returnEntry.getStatus().toString());
                LOG.debug("      Notes ............... " + returnEntry.getNotes());
            }
        }
        LOG.debug("  Original Order return requests:");
        for (final ReturnRequestModel returnRequestModel : originalOrder.getReturnRequests()) {
            LOG.debug("    PK .................... " + returnRequestModel.getPk());
            LOG.debug("    Order PK .............. " + returnRequestModel.getOrder().getPk());
            LOG.debug("    Creation time ......... " + returnRequestModel.getCreationtime().toString());
            // LOG.debug("    Refund to store credit. " +
            // returnRequestModel.getRefundToStoreCredit());
            LOG.debug("    Return entries:");
            for (final ReturnEntryModel returnEntry : returnRequestModel.getReturnEntries()) {
                LOG.debug("      PK .................. " + returnEntry.getPk());
                LOG.debug("      Expected quantity ... " + returnEntry.getExpectedQuantity());
                LOG.debug("      Action .............. " + returnEntry.getAction().toString());
                LOG.debug("      Status .............. " + returnEntry.getStatus().toString());
                LOG.debug("      Notes ............... " + returnEntry.getNotes());
            }
        }
        LOG.debug("  Payment service:");
        /*
         * LOG.debug("    Original funds ........ " + paymentServiceFunds); LOG.debug("    Remaining funds ....... " +
         * remainingPaymentServiceFunds); LOG.debug("    Refund amount ......... " + paymentServiceRefund); LOG.debug("  Store credit:");
         * LOG.debug("    Original funds ........ " + storeCreditFunds); LOG.debug("    Refund amount ......... " + storeCreditRefund);
         */
    }

    @Required
    public void setV2PaymentService(V2PaymentService v2PaymentService) {
        this.v2PaymentService = v2PaymentService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public V2OrderModificationRefundInfoModel calculateRefundInfoForCancellation(OrderModel refundOrderPreview, boolean refundToWallet,
            boolean isFullCancel) {
        V2OrderModificationRefundInfoModel refundInfo = this.modelService.create(V2OrderModificationRefundInfoModel.class);
        if (refundToWallet) {
            refundInfo.setRefundToPaymentGateway(Boolean.FALSE);
            refundInfo.setRefundToWallet(Boolean.TRUE);
        } else {
            refundInfo.setRefundToPaymentGateway(Boolean.TRUE);
            refundInfo.setRefundToWallet(Boolean.FALSE);
        }
        // Find the original version of order we're processing; if we can't find it, fail fast
        final OrderModel originalOrder = refundOrderPreview.getOriginalVersion();
        if (originalOrder == null) {
            LOG.error("No order found for refund payment");
            throw new AdapterException("No order found for refund payment");
        }

        // In case payment made using COD
        final PaymentInfoModel orderPaymentInfo = originalOrder.getPaymentInfo();
        /*
         * if( orderPaymentInfo.getPaymentMode()!=null && isCancelRequest){
         * if((orderPaymentInfo.getPaymentMode().getMode()).equals("CASH")){ LOG.info("No payment transactions found for order " +
         * originalOrder.getCode()); return null; } }
         */

        // Find the list of transactions; abandon if there aren't any
        final List<PaymentTransactionModel> paymentTransactions = originalOrder.getPaymentTransactions();
        if (paymentTransactions == null || paymentTransactions.isEmpty()) {
            if (orderPaymentInfo.getPaymentMode() != null) {
                if (!(orderPaymentInfo.getPaymentMode().getMode()).equals("CASH")) {
                    LOG.error("No payment transactions found for order " + originalOrder.getCode());
                    throw new AdapterException("No payment transactions found for order " + originalOrder.getCode());
                }
            }
        }
        // Determine how much we have to refund in total
        getCalculationService().recalculatePromotionsForOriginalOrder(originalOrder);
        // getRefundService().recalculatePromotions(originalOrder);
        BigDecimal refundAmount = BigDecimal.valueOf(0);

        if (orderPaymentInfo.getPaymentMode() != null) {
            if ((orderPaymentInfo.getPaymentMode().getMode()).equals("CASH")) {
                PaymentInfoModel storeCreditPaymentInfo = originalOrder.getStoreCreditPaymentInfo();
                if (storeCreditPaymentInfo != null && storeCreditPaymentInfo instanceof V2StoreCreditPaymentInfoModel
                        && originalOrder.getTotalPayableBalance() > 0) {
                    V2StoreCreditPaymentInfoModel v2StoreCreditPaymentInfoModel = ((V2StoreCreditPaymentInfoModel) storeCreditPaymentInfo);
                    if (isFullCancel) {
                        refundAmount = new BigDecimal(v2StoreCreditPaymentInfoModel.getStoreCreditAmount().doubleValue());
                    } else {
                        if (v2StoreCreditPaymentInfoModel.getStoreCreditAmount().doubleValue() <= refundOrderPreview.getTotalPrice()) {
                            refundAmount = BigDecimal.valueOf(0); // TODO
                            // TODO set COD payable to zero
                        } else {
                            refundAmount = new BigDecimal(v2StoreCreditPaymentInfoModel.getStoreCreditAmount().doubleValue()
                                    - refundOrderPreview.getTotalPrice());
                            // TODO update payable amount by COD
                        }
                    }
                } else {
                    refundAmount = BigDecimal.valueOf(0);
                }
            } else {
                refundAmount = new BigDecimal(originalOrder.getTotalPrice() - refundOrderPreview.getTotalPrice());
            }
        } else {
            refundAmount = new BigDecimal(originalOrder.getTotalPrice() - refundOrderPreview.getTotalPrice());
        }

        if (refundAmount.compareTo(BigDecimal.valueOf(0)) < 0) {
            refundAmount = BigDecimal.valueOf(0);
        }
        refundInfo.setAmountTobeRefunded(refundAmount);
        this.modelService.save(refundInfo);
        return refundInfo;
    }

    @Override
    public V2OrderModificationRefundInfoModel calculateRefundInfoForReturn(OrderModel refundOrderPreview, boolean refundToWallet) {
        V2OrderModificationRefundInfoModel refundInfo = this.modelService.create(V2OrderModificationRefundInfoModel.class);

        for (ReturnRequestModel returnRequestModel : refundOrderPreview.getReturnRequests()) {

            if (returnRequestModel.getReturnMethod() != null) {

                if (returnRequestModel.getReturnMethod().equalsIgnoreCase("gateway")) {
                    refundInfo.setRefundToPaymentGateway(Boolean.TRUE);
                    refundInfo.setRefundToWallet(Boolean.FALSE);
                } else if (returnRequestModel.getReturnMethod().equalsIgnoreCase("wallet")) {
                    refundInfo.setRefundToWallet(Boolean.TRUE);
                    refundInfo.setRefundToPaymentGateway(Boolean.FALSE);
                }
            }
            if (returnRequestModel.getReturnMethod() == null) {
                refundInfo.setRefundToWallet(Boolean.TRUE);
                refundInfo.setRefundToPaymentGateway(Boolean.FALSE);
            }
        }
        /*
         * if (refundToWallet) { refundInfo.setRefundToPaymentGateway(Boolean.FALSE); refundInfo.setRefundToWallet(Boolean.TRUE); }
         */
        // Find the original version of order we're processing; if we can't find it, fail fast
        final OrderModel originalOrder = refundOrderPreview.getOriginalVersion();
        if (originalOrder == null) {
            LOG.error("No order found for refund payment");
            throw new AdapterException("No order found for refund payment");
        }

        // In case payment made using COD
        final PaymentInfoModel orderPaymentInfo = originalOrder.getPaymentInfo();
        /*
         * if( orderPaymentInfo.getPaymentMode()!=null ){ if((orderPaymentInfo.getPaymentMode().getMode()).equals("CASH")){
         * LOG.info("No payment transactions found for order " + originalOrder.getCode()); return null; } }
         */

        // Find the list of transactions; abandon if there aren't any
        final List<PaymentTransactionModel> paymentTransactions = originalOrder.getPaymentTransactions();
        if (paymentTransactions == null || paymentTransactions.isEmpty()) {
            if (orderPaymentInfo.getPaymentMode() != null) {
                if (!(orderPaymentInfo.getPaymentMode().getMode()).equals("CASH")) {
                    LOG.error("No payment transactions found for order " + originalOrder.getCode());
                    throw new AdapterException("No payment transactions found for order " + originalOrder.getCode());
                }
            }
        }
        // Determine how much we have to refund in total
        getCalculationService().recalculatePromotionsForOriginalOrder(originalOrder);
        // getRefundService().recalculatePromotions(originalOrder);
        BigDecimal refundAmount = BigDecimal.valueOf(0);

        refundAmount = new BigDecimal(originalOrder.getTotalPrice() - refundOrderPreview.getTotalPrice());
        if (refundAmount.compareTo(BigDecimal.valueOf(0)) < 0) {
            refundAmount = BigDecimal.valueOf(0);
        }
        refundInfo.setAmountTobeRefunded(refundAmount);
        this.modelService.save(refundInfo);
        return refundInfo;
    }
}
