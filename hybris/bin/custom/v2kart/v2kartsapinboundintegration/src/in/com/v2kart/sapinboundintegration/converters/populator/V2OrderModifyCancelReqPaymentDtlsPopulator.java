/**
 *
 */
package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2CODPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;
import in.com.v2kart.sapinboundintegration.constants.V2kartsapinboundintegrationConstants;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelReq.OrderModifyCancelReq;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Populator class <Code>V2OrderModifyCancelReqPaymentDtlsPopulator</Code> is used for populating Order Model (Source)
 * of type {@link OrderModel} to {@link OrderModifyCancelReq} (Target).
 *
 * @author Shailja Gupta
 *
 */
public class V2OrderModifyCancelReqPaymentDtlsPopulator implements
        Populator<OrderCancelRecordEntryModel, SOModifyCancelReq.OrderModifyCancelReq> {

    protected final ObjectFactory objectFactory = new ObjectFactory();

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final OrderCancelRecordEntryModel source, final OrderModifyCancelReq target)
            throws ConversionException {

        final List<SOModifyCancelReq.OrderModifyCancelReq.PaymentDtls> cancelOrderPaymentList = target.getPaymentDtls();
        SOModifyCancelReq.OrderModifyCancelReq.PaymentDtls cancelOrderPayment = null;
        final OrderModel orderModel = source.getModificationRecord().getOrder();
        final PaymentInfoModel primaryPaymentInfo = orderModel.getPaymentInfo();
        final V2OrderModificationRefundInfoModel orderModificationRefundInfoModel = source
                .getOrderModificationRefundInfo(); // Logic for Payment gateway refund
        cancelOrderPayment = objectFactory.createSOModifyCancelReqOrderModifyCancelReqPaymentDtls();

        if (primaryPaymentInfo.getPaymentMode() != null) {
            if (primaryPaymentInfo instanceof V2PGPaymentInfoModel) {
                cancelOrderPayment.setMediaType(primaryPaymentInfo.getPaymentMode().getCode());
                //cancelOrderPayment.setPaymentGateway(((V2PGPaymentInfoModel) primaryPaymentInfo).getMode());
                cancelOrderPayment.setPaymentGateway(getBlankIfNull(((V2PGPaymentInfoModel) primaryPaymentInfo).getPaymentGateway()));
                cancelOrderPayment.setCardType(getBlankIfNull(((V2PGPaymentInfoModel) primaryPaymentInfo).getCardNumber()));
            } else if (primaryPaymentInfo instanceof V2CODPaymentInfoModel) {
                cancelOrderPayment.setPaymentGateway(getBlankIfNull(null));
                cancelOrderPayment.setMediaType(getBlankIfNull("CASH"));
                cancelOrderPayment.setCardType(getBlankIfNull(null));
            }
        } else if (primaryPaymentInfo instanceof V2StoreCreditPaymentInfoModel) {
            cancelOrderPayment.setPaymentGateway(getBlankIfNull(null));
            cancelOrderPayment.setMediaType(getBlankIfNull("V2-WALLET"));
            cancelOrderPayment.setCardType(getBlankIfNull(null));
        }

        cancelOrderPayment.setAmount(orderModificationRefundInfoModel.getAmountTobeRefunded());
        cancelOrderPaymentList.add(cancelOrderPayment);

    }

    /**
     * Returns Blank String if null
     *
     * @param str
     * @return String
     */
    public String getBlankIfNull(final String str) {
        if (StringUtils.isEmpty(str)) {
            return V2kartsapinboundintegrationConstants.BLANK_STR;
        } else {
            return str;
        }
    }
}
