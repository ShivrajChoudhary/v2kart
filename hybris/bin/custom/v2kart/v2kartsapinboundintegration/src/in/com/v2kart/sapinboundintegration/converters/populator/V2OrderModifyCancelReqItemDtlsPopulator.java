/**
 *
 */
package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.sapinboundintegration.constants.V2kartsapinboundintegrationConstants;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq.OrderModifyCancelReq;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Populator class <Code>V2OrderModifyCancelReqItemDtlsPopulator</Code> is used for populating Order Model (Source) of type
 * {@link OrderModel} to {@link OrderModifyCancelReq} (Target).
 *
 * @author Shailja Gupta
 *
 */
public class V2OrderModifyCancelReqItemDtlsPopulator implements
        Populator<OrderCancelRecordEntryModel, SOModifyCancelReq.OrderModifyCancelReq> {

    protected final ObjectFactory objectFactory = new ObjectFactory();

    // protected final static String GIVE_AWAY_PRICE = "0.01";//TODO

    // protected final static String GIVE_AWAY_PRICE_DISC = "0";//TODO

    private Map<String, String> cancelReasonCodeMap;

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final OrderCancelRecordEntryModel source, final OrderModifyCancelReq target) throws ConversionException {

        final List<SOModifyCancelReq.OrderModifyCancelReq.ItemDtls> cancelOrderItemList = target.getItemDtls();
        SOModifyCancelReq.OrderModifyCancelReq.ItemDtls orderCancelItem = null;
        OrderEntryModel orderEntryModel;
        for (final OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntryModel : source
                .getOrderEntriesModificationEntries()) {
            orderEntryModel = orderEntryModificationRecordEntryModel.getOrderEntry();
            orderCancelItem = objectFactory.createSOModifyCancelReqOrderModifyCancelReqItemDtls();
            if (orderEntryModel.getEntryNumber() != null) {
                orderCancelItem.setItmNumber(changingItemNumber(orderEntryModel.getEntryNumber()).toString());
            }
            orderCancelItem.setArtNumber(orderEntryModel.getProduct().getCode());
            // if (orderEntryModel.getQuantity() != null && orderEntryModel.getQuantity().doubleValue() > 0) {
            if (orderEntryModel.getQuantity() != null) {
                orderCancelItem.setReqQty(new BigDecimal(orderEntryModel.getQuantity().doubleValue()));
                orderCancelItem.setSingleUnitMRP(orderEntryModel.getBasePrice().toString());
                orderCancelItem.setCondTypeDisc(getBlankIfNull(null));
                orderCancelItem.setSingleUnitDis(getBlankIfNull(null));
            }
            if (orderEntryModel.getProduct().getUnit() != null && !orderEntryModel.getProduct().getUnit().getCode().isEmpty()) {
                orderCancelItem.setUOM(orderEntryModel.getProduct().getUnit().getCode());
            } else {
                orderCancelItem.setUOM(getBlankIfNull(null));
            }

            cancelOrderItemList.add(orderCancelItem);
        }

    }

    /**
     * Returns Blank String if null
     *
     * @param str
     *        str
     * @return str
     */
    public String getBlankIfNull(final String str) {
        if (StringUtils.isEmpty(str)) {
            return V2kartsapinboundintegrationConstants.BLANK_STR;
        } else {
            return str;
        }
    }

    public Map<AbstractOrderEntryModel, Double> calaculatePromoEntriesDiscount(final OrderModel source) {
        final Map<AbstractOrderEntryModel, Double> promoEntriesTotalDiscountMap = new HashMap<AbstractOrderEntryModel, Double>();
        final Set<PromotionResultModel> allPromotionResults = source.getAllPromotionResults();
        for (final PromotionResultModel promotionResultModel : allPromotionResults) {
            if (!promotionResultModel.getCertainty().equals(Float.valueOf(1))) {
                // if this promotion is not fully fired but only could fired skip this .
                continue;
            }
            final List<PromotionOrderEntryConsumedModel> consumedEntries = (List<PromotionOrderEntryConsumedModel>) promotionResultModel
                    .getConsumedEntries();

            for (final PromotionOrderEntryConsumedModel promotionOrderEntryConsumedModel : consumedEntries) {
                final AbstractOrderEntryModel orderEntry = promotionOrderEntryConsumedModel.getOrderEntry();
                final Double entryBasePrice = orderEntry.getBasePrice();
                final Double entryAdjustedUnitPrice = promotionOrderEntryConsumedModel.getAdjustedUnitPrice();
                final Long qty = promotionOrderEntryConsumedModel.getQuantity();
                final double entryTotalAdjustedUnitPrice = entryAdjustedUnitPrice.doubleValue() * qty.doubleValue();
                final double entryTotalDiscountValue = (entryBasePrice.doubleValue() * qty.doubleValue()) - (entryTotalAdjustedUnitPrice);

                final Double entryTotalDiscount = promoEntriesTotalDiscountMap.get(orderEntry);
                if (entryTotalDiscount == null) {
                    promoEntriesTotalDiscountMap.put(orderEntry, Double.valueOf(entryTotalDiscountValue));
                } else {
                    promoEntriesTotalDiscountMap
                            .put(orderEntry, Double.valueOf(entryTotalDiscount.doubleValue() + entryTotalDiscountValue));
                }
            }

        }
        return promoEntriesTotalDiscountMap;
    }

    public Integer changingItemNumber(final Integer itmNo) {
        Integer in;
        if (itmNo.intValue() == 0) {
            in = Integer.valueOf(itmNo.intValue() + 10);
        } else {
            in = Integer.valueOf(itmNo.intValue() * 10 + 10);
        }
        return in;
    }

    @Required
    public void setCancelReasonCodeMap(final Map<String, String> cancelReasonCodeMap) {
        this.cancelReasonCodeMap = cancelReasonCodeMap;
    }
}
