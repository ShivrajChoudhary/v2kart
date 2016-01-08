/**
 *
 */
package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import in.com.v2kart.sapinboundintegration.constants.V2kartsapinboundintegrationConstants;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq.OrderModifyCancelReq;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * Populator class <Code>V2OrderModifyCancelReqPopulator</Code> is used for converting Order Model (Source) of type {@link OrderModel} to
 * {@link OrderModifyCancelReq} (Target).
 *
 * @author Shailja Gupta
 *
 */
public class V2OrderModifyCancelReqPopulator implements Populator<OrderCancelRecordEntryModel, SOModifyCancelReq.OrderModifyCancelReq> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final OrderCancelRecordEntryModel source, final OrderModifyCancelReq target) throws ConversionException {
        final Date dt = new Date();
        OrderModel orderModel;
        if (source.getModificationRecord() != null) {
            if (source.getModificationRecord().getOrder() != null) {
                orderModel = source.getModificationRecord().getOrder(); // hybris customerID

                target.setSAPCustId(((CustomerModel) orderModel.getUser()).getSapCustomerId());
                target.setSONo(orderModel.getCode());
                target.setCreateDate(dt.toString());
                target.setSalesOrg(getBlankIfNull(null));
                if (orderModel.getDeliveryAddress() != null) {
                    target.setCODCharges(orderModel.getCodCharges() != null ? orderModel.getCodCharges().toString() : "");
                    target.setCODCondType(getBlankIfNull(null));
                    target.setFreight(getBlankIfNull(orderModel.getDeliveryCost().toString()));
                    target.setFreightCondType(getBlankIfNull(Config.getParameter("website.v2kart.FreightCondType")));
                    // target.setCancelReason(source.getCancelReason().toString());
                }
                if (orderModel.getDeliveryMode() != null) {
                    final String deliveryMode = orderModel.getDeliveryMode().getSapCode();
                    target.setDeliveryType(getBlankIfNull(deliveryMode));
                } else {
                    target.setDeliveryType(getBlankIfNull(null));
                }
                /*
                 * if (deliveryMode != null && !deliveryMode.isEmpty()) { if
                 * (V2kartsapinboundintegrationConstants.STD_DEL_MODE_1.equals(deliveryMode) ||
                 * V2kartsapinboundintegrationConstants.STD_DEL_MODE_2.equals(deliveryMode) ||
                 * V2kartsapinboundintegrationConstants.STD_DEL_MODE_3.equals(deliveryMode) ||
                 * V2kartsapinboundintegrationConstants.STD_DEL_MODE_4.equals(deliveryMode)) {
                 * target.setDeliveryType(V2kartsapinboundintegrationConstants.SAP_DELV_MODE_CODE_1); } else if
                 * (V2kartsapinboundintegrationConstants.EXP_DEL_MODE_1.equals(deliveryMode) ||
                 * V2kartsapinboundintegrationConstants.EXP_DEL_MODE_2.equals(deliveryMode) ||
                 * V2kartsapinboundintegrationConstants.EXP_DEL_MODE_3.equals(deliveryMode)) {
                 * target.setDeliveryType(V2kartsapinboundintegrationConstants.SAP_DELV_MODE_CODE_2); } else if
                 * (V2kartsapinboundintegrationConstants.STD_PICKUP.equals(deliveryMode) ||
                 * V2kartsapinboundintegrationConstants.EXP_PICKUP.equals(deliveryMode)) {
                 * target.setDeliveryType(V2kartsapinboundintegrationConstants.SAP_PICKUP_MODE_CODE_3); } } else {
                 * target.setDeliveryType(V2kartsapinboundintegrationConstants.BLANK_STR); }
                 */

            }
        }

    }

    /**
     * Returns Blank String if null
     *
     * @param str
     * @return str
     */
    public String getBlankIfNull(final String str) {
        if (StringUtils.isEmpty(str)) {
            return V2kartsapinboundintegrationConstants.BLANK_STR;
        } else {
            return str;
        }
    }
}
