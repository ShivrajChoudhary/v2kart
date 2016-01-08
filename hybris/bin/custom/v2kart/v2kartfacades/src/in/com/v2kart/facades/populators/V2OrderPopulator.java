package in.com.v2kart.facades.populators;

import in.com.v2kart.facades.order.data.OrderEntryModificationRecordEntryData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2CODPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.refund.model.OrderRefundRecordEntryModel;
import de.hybris.platform.returns.model.OrderEntryReturnRecordEntryModel;
import de.hybris.platform.returns.model.OrderReturnRecordModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/*
 * 
 * Last updated By pravesh.gupta@nagarro.com 
 * Last updated Date 23/04/2015
 *
 */
public class V2OrderPopulator extends OrderPopulator {

    private final String NET_BANKING_CODE = "NB";
    private final String CREDIT_CARD_CODE = "CC";
    private final String DEBIT_CARD_CODE = "DC";
    private final String CASH_CARD_CODE = "CA";
    private static final String CREDIT_DEBIT_PAYMENT_MODE = "creditcard/debitcard";
    // private final String EMI_CODE = "EMI";
    private final String NET_BANKING = "Net Banking";
    private final String CREDIT_CARD = "Credit Card";
    private final String DEBIT_CARD = "Debit Card";
    private final String CREDIT_DEBIT_CARD = "Credit Card/Debit Card";
    private final String COD = "Cash On Delivery";
    private final String WALLET = "V2-Wallet";
    private final String CASHCARD ="Cash Card";
    // private final String EMI = "EMI";

    private Converter<PaymentInfoModel, CCPaymentInfoData> customPaymentInfoConverter;
    
    private Map<String,String> v2CancelReasons;
    
    private Map<String,String> v2RefundReasons;
    
    public Map<String,String> getV2CancelReasons() {
		return v2CancelReasons;
	}

	public void setV2CancelReasons(Map<String,String> v2CancelReasons) {
		this.v2CancelReasons = v2CancelReasons;
	}

    /**
     * @return the customPaymentInfoConverter
     */
    public Converter<PaymentInfoModel, CCPaymentInfoData> getCustomPaymentInfoConverter() {
        return customPaymentInfoConverter;
    }

    /**
     * @param customPaymentInfoConverter
     *        the customPaymentInfoConverter to set
     */
    @Required
    public void setCustomPaymentInfoConverter(Converter<PaymentInfoModel, CCPaymentInfoData> customPaymentInfoConverter) {
        this.customPaymentInfoConverter = customPaymentInfoConverter;
    }

    @Override
    public void populate(OrderModel source, OrderData target) {
        super.populate(source, target);
        addCancellationAndRefundData(source, target);
        addTotalGiftWrapPrice(source, target);
        //
        setCartSubTotal(source, target);
    }

    protected void addTotalGiftWrapPrice(final AbstractOrderModel source, final AbstractOrderData target)
    {
        if (source.getTotalGiftWrapPrice() != null && source.getTotalGiftWrapPrice() != 0.0) {

            target.setTotalGiftWrapPrice(createPrice(source, source.getTotalGiftWrapPrice()));
        }
    }

    /**
     * Adds the cancelled entries and refund entries data
     * 
     * @param order
     * @param orderData
     */
    protected void addCancellationAndRefundData(final OrderModel order, final OrderData orderData) {
        List<OrderEntryModificationRecordEntryData> cancelledEntryDatas = new ArrayList<OrderEntryModificationRecordEntryData>();
        List<OrderEntryModificationRecordEntryData> refundEntryDatas = new ArrayList<OrderEntryModificationRecordEntryData>();
        Set<OrderModificationRecordModel> orderModificationRecords = order.getModificationRecords();
        if (orderModificationRecords != null && orderModificationRecords.size() != 0) {
            for (OrderModificationRecordModel orderModificationRecord : orderModificationRecords) {
                if (orderModificationRecord instanceof OrderCancelRecordModel) {
                    populateForOrderModificationRecord(cancelledEntryDatas, orderModificationRecord);
                }
            }
        }
        orderData.setCancelledOrderEntries(cancelledEntryDatas);
        populateForOrderReturnRefundRecords(refundEntryDatas, order);
        orderData.setRefundOrderEntries(refundEntryDatas);
    }

    /**
     * Populates list of {@link OrderEntryModificationRecordEntryData} from {@link OrderModificationRecordModel}
     * 
     * @param modificationEntryDatas
     * @param orderModificationRecordModel
     */
    protected void populateForOrderModificationRecord(
            List<OrderEntryModificationRecordEntryData> modificationEntryDatas,
            final OrderModificationRecordModel orderModificationRecordModel) {
        Collection<OrderModificationRecordEntryModel> orderModificationRecordEntries = orderModificationRecordModel
                .getModificationRecordEntries();
        for (OrderModificationRecordEntryModel orderModificationRecordEntry : orderModificationRecordEntries) {
            populateForOrderModificationRecordEntry(modificationEntryDatas, orderModificationRecordEntry);
        }
    }

    /**
     * Populates list of {@link OrderEntryModificationRecordEntryData} from order for return refund datas
     * 
     * @param modificationEntryDatas
     * @param order
     */
    protected void populateForOrderReturnRefundRecords(final List<OrderEntryModificationRecordEntryData> modificationEntryDatas,
            final OrderModel order) {
        if (order.getReturnRequests() != null & order.getReturnRequests().size() != 0) {
            for (ReturnRequestModel returnRequest : order.getReturnRequests()) {
                for (ReturnEntryModel returnEntry : returnRequest.getReturnEntries()) {
                    if (returnEntry instanceof RefundEntryModel) {
                        OrderEntryModificationRecordEntryData orderEntryReturnRefundRecordEntryData = new OrderEntryModificationRecordEntryData();
                        populateForOrderReturnRefundRecord(orderEntryReturnRefundRecordEntryData, (RefundEntryModel) returnEntry);
                        modificationEntryDatas.add(orderEntryReturnRefundRecordEntryData);
                    }
                }
            }
        }
    }

    /**
     * Populates {@link OrderEntryModificationRecordEntryData} from {@link RefundEntryModel}
     * 
     * @param orderEntryReturnRefundRecordEntryData
     * @param returnEntry
     */
    protected void populateForOrderReturnRefundRecord(final OrderEntryModificationRecordEntryData orderEntryReturnRefundRecordEntryData,
            final RefundEntryModel returnEntry) {
        orderEntryReturnRefundRecordEntryData.setProductInfo(getProductInfoForModificationEntryData(returnEntry.getOrderEntry()));
        orderEntryReturnRefundRecordEntryData.setQuantity(returnEntry.getExpectedQuantity());
        orderEntryReturnRefundRecordEntryData.setReason(v2RefundReasons.get(returnEntry.getReason().getCode().toLowerCase()));
        orderEntryReturnRefundRecordEntryData.setStatus(returnEntry.getStatus().getCode());
    }

    protected String getProductInfoForModificationEntryData(final AbstractOrderEntryModel orderEntry) {
        ProductModel product = orderEntry.getProduct();
        return product.getCode() + "-" + product.getName();
    }

    /**
     * Populates list of {@link OrderEntryModificationRecordEntryData} from {@link OrderModificationRecordEntryModel}
     * 
     * @param modificationEntryDatas
     * @param orderModificationRecordEntry
     */
    protected void populateForOrderModificationRecordEntry(
            List<OrderEntryModificationRecordEntryData> modificationEntryDatas,
            final OrderModificationRecordEntryModel orderModificationRecordEntry) {
        Collection<OrderEntryModificationRecordEntryModel> orderEntryModificationRecordEntries = orderModificationRecordEntry
                .getOrderEntriesModificationEntries();
        for (OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntry : orderEntryModificationRecordEntries) {
            OrderEntryModificationRecordEntryData orderEntryModificationRecordEntryData = new OrderEntryModificationRecordEntryData();
            populateForOrderEntryModificationRecordEntry(
                    orderEntryModificationRecordEntry, orderEntryModificationRecordEntryData, orderModificationRecordEntry);
            modificationEntryDatas.add(orderEntryModificationRecordEntryData);
        }
    }

    /**
     * Populates {@link OrderEntryModificationRecordEntryData} from {@link OrderEntryModificationRecordEntryModel} and
     * {@link OrderModificationRecordEntryModel}
     * 
     * @param orderEntryModificationRecordEntry
     * @param orderEntryModificationRecordEntryData
     * @param orderModificationRecordEntry
     */
    protected void populateForOrderEntryModificationRecordEntry(
            final OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntry,
            OrderEntryModificationRecordEntryData orderEntryModificationRecordEntryData,
            final OrderModificationRecordEntryModel orderModificationRecordEntry) {
        orderEntryModificationRecordEntryData.setProductInfo(getProductInfoForModificationEntryData(orderEntryModificationRecordEntry
                .getOrderEntry()));
        orderEntryModificationRecordEntryData.setStatus(orderModificationRecordEntry.getStatus().getCode());

        if (orderEntryModificationRecordEntry instanceof OrderEntryCancelRecordEntryModel) {
            populateForOrderEntryCancelRecordEntry((OrderEntryCancelRecordEntryModel) orderEntryModificationRecordEntry,
                    orderEntryModificationRecordEntryData,(OrderCancelRecordEntryModel) orderModificationRecordEntry);
        } else if (orderEntryModificationRecordEntry instanceof OrderEntryReturnRecordEntryModel) {
            populateForOrderEntryRefundRecordEntry((OrderEntryReturnRecordEntryModel) orderEntryModificationRecordEntry,
                    orderEntryModificationRecordEntryData,(OrderRefundRecordEntryModel) orderModificationRecordEntry);
        }
    }

    /**
     * Populates cancel modification specific data in {@link OrderEntryModificationRecordEntryData} from
     * {@link OrderEntryCancelRecordEntryModel}
     * 
     * @param orderEntryCancelRecordEntry
     * @param orderEntryModificationRecordEntryData
     */
    protected void populateForOrderEntryCancelRecordEntry(OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntry,
            OrderEntryModificationRecordEntryData orderEntryModificationRecordEntryData,OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
        if (null != orderEntryCancelRecordEntry) {
            if (orderEntryCancelRecordEntry.getCancelledQuantity() != null) {
                orderEntryModificationRecordEntryData.setQuantity(Long.valueOf(orderEntryCancelRecordEntry.getCancelledQuantity()
                        .longValue()));
            }
            if (null != orderEntryCancelRecordEntry.getCancelReason() && !CancelReason.NA.equals(orderEntryCancelRecordEntry.getCancelReason())) {
                orderEntryModificationRecordEntryData.setReason(v2CancelReasons.get(orderEntryCancelRecordEntry.getCancelReason().getCode().toLowerCase()));
            } else {
                orderEntryModificationRecordEntryData.setReason(v2CancelReasons.get(orderCancelRecordEntryModel.getCancelReason().getCode().toLowerCase()));
            }
        }
    }

    /**
     * Populates refund specific data in {@link OrderEntryModificationRecordEntryData} from {@link OrderEntryReturnRecordEntryModel}
     * 
     * @param orderEntryReturnRecordEntry
     * @param orderEntryModificationRecordEntryData
     */
    protected void populateForOrderEntryRefundRecordEntry(OrderEntryReturnRecordEntryModel orderEntryReturnRecordEntry,
            OrderEntryModificationRecordEntryData orderEntryModificationRecordEntryData,OrderRefundRecordEntryModel orderRefundRecordEntryModel) {
        if (null != orderEntryReturnRecordEntry) {
            if (orderEntryReturnRecordEntry.getReturnedQuantity() != null) {
                orderEntryModificationRecordEntryData.setQuantity(Long.valueOf(orderEntryReturnRecordEntry.getReturnedQuantity()
                        .longValue()));
            }
        }
    }

    @Override
    protected void addPaymentInformation(AbstractOrderModel source, AbstractOrderData prototype) {
        // TODO Auto-generated method stub
        super.addPaymentInformation(source, prototype);

        final PaymentInfoModel paymentInfo = source.getPaymentInfo();
        PaymentInfoModel storeCreditPaymentInfo=source.getStoreCreditPaymentInfo();
        if(storeCreditPaymentInfo != null && storeCreditPaymentInfo instanceof V2StoreCreditPaymentInfoModel){
            prototype.setPartialWalletMode(WALLET);
        }else{
            prototype.setPartialWalletMode(null);
        }
        // payment made using some external mode
        if (paymentInfo instanceof V2PGPaymentInfoModel) {

            final CCPaymentInfoData paymentInfoData = getCustomPaymentInfoConverter().convert((V2PGPaymentInfoModel) paymentInfo);

            prototype.setPaymentInfo(paymentInfoData);

            final String paymentMode = ((V2PGPaymentInfoModel) paymentInfo).getMode();

            // In case of external payment mode, the payment could have been made by net banking, credit card, debit card or EMI.
            if (null != paymentMode) {
                if (NET_BANKING_CODE.equalsIgnoreCase(paymentMode) || NET_BANKING.equalsIgnoreCase(paymentMode)) {
                    prototype.setPaymentMode(NET_BANKING);
                } else if (CREDIT_CARD_CODE.equalsIgnoreCase(paymentMode) || CREDIT_CARD.equalsIgnoreCase(paymentMode)) {
                    prototype.setPaymentMode(CREDIT_CARD);
                } else if (DEBIT_CARD_CODE.equalsIgnoreCase(paymentMode) || DEBIT_CARD.equalsIgnoreCase(paymentMode)) {
                    prototype.setPaymentMode(DEBIT_CARD);
                } else if (paymentMode.equalsIgnoreCase(CREDIT_DEBIT_PAYMENT_MODE)) {
                    prototype.setPaymentMode(CREDIT_DEBIT_CARD);
                }else if (CASH_CARD_CODE.equalsIgnoreCase(paymentMode) || CASHCARD.equalsIgnoreCase(paymentMode)) {
                    prototype.setPaymentMode(CASHCARD);
                }
                
                /*
                   * else if (paymentMode.contains(EMI_CODE)) { prototype.setPaymentMode(EMI); }
                   */
            }
            // cash on delivery
        } else if (paymentInfo instanceof V2CODPaymentInfoModel) {
            final CCPaymentInfoData paymentInfoData = getCustomPaymentInfoConverter().convert((V2CODPaymentInfoModel) paymentInfo);

            prototype.setPaymentInfo(paymentInfoData);
            prototype.setPaymentMode(COD);
            // set COD charges
            if (null != source.getCodCharges()) {
                prototype.setCodCharges(getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getCodCharges()),
                        source.getCurrency().getIsocode()));
            }
        } else if (paymentInfo instanceof V2StoreCreditPaymentInfoModel) {
            final CCPaymentInfoData paymentInfoData = getCustomPaymentInfoConverter().convert((V2StoreCreditPaymentInfoModel) paymentInfo);

            prototype.setPaymentInfo(paymentInfoData);
            prototype.setPaymentMode(WALLET);
        }

    }
    
    /**
     * 
     * @param source
     * @param target
     */
    protected void setCartSubTotal(final AbstractOrderModel source,final AbstractOrderData target)
	{
	    if (target.getProductDiscounts() != null &&  target.getSubTotal() !=null) {
	    	BigDecimal productDiscount = target.getProductDiscounts().getValue();
	    	if(productDiscount != null) {
	    		target.setSubTotal(createPrice(source, productDiscount.doubleValue() + target.getSubTotal().getValue().doubleValue()));
	    	}
	    }
	}

	public Map<String,String> getV2RefundReasons() {
		return v2RefundReasons;
	}

	public void setV2RefundReasons(Map<String,String> v2RefundReasons) {
		this.v2RefundReasons = v2RefundReasons;
	}

	

}
