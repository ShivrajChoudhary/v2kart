package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq;
import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq.OrderCreationReq;

import java.math.BigDecimal;

/**
 * <FGOrderCreationReqPaymentDtlsPopulator> populates data for sales Order payment request from HYBRIS to SAP
 * 
 * @author satvir_nagarro
 * 
 */
public class V2OrderCreationReqPaymentDtlsPopulator extends V2AbstractOrderCreationReqPopulator {

    @Override
    public void populate(final OrderModel source, final OrderCreationReq target) throws ConversionException {
        SOCreateReq.OrderCreationReq.PaymentDtls orderPaymentDtls = target.getPaymentDtls();
        if (orderPaymentDtls == null) {
            orderPaymentDtls = new SOCreateReq.OrderCreationReq.PaymentDtls();
        }
        final PaymentInfoModel primaryPaymentInfo = source.getPaymentInfo();

        // if there is a primary payment info
        if (primaryPaymentInfo != null) {
            orderPaymentDtls = objectFactory.createSOCreateReqOrderCreationReqPaymentDtls();
            // TODO
            orderPaymentDtls.setPaymentGateway(getBlankIfNull(primaryPaymentInfo.getPaymentGateway()));
            // orderPaymentDtls.setPaymentGateway(getBlankIfNull("PAYU"));
            // TODO
            // orderPaymentDtls.setMediaType(primaryPaymentInfo.getPaymentMode().getCode());
            orderPaymentDtls.setMediaType("ZCAR");
            if (primaryPaymentInfo instanceof CreditCardPaymentInfoModel) {
                orderPaymentDtls.setCardType(getBlankIfNull(((CreditCardPaymentInfoModel) primaryPaymentInfo).getType().getType()));
            } else {
                orderPaymentDtls.setCardType(getBlankIfNull(null));
            }
            orderPaymentDtls.setAmount(BigDecimal.valueOf(source.getTotalPrice().doubleValue()));
            target.setPaymentDtls(orderPaymentDtls);
        }
    }
}
