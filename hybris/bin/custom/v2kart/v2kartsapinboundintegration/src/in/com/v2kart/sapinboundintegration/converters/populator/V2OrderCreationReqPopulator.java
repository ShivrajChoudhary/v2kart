package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq;
import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq.OrderCreationReq;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * <FGOrderCreationReqPopulator> populates data for sales Order request from HYBRIS to SAP
 * 
 * @author satvir_nagarro
 * 
 */
public class V2OrderCreationReqPopulator extends V2AbstractOrderCreationReqPopulator {

    private static final String PAYMENT_METHOD_CASH = "CASH";

    private static String ZERO_COD = "0.0";

    @Override
    public void populate(final OrderModel source, final OrderCreationReq target) throws ConversionException {
        target.setSONo(source.getCode());
        // TODO
        target.setSalesOrg(getBlankIfNull(""));
        // Hybris order creation date like 15092014(ddMMyyyy)
        target.setCreateDate(DateFormatUtils.format(source.getCreationtime(), SAP_INBOUND_DATE_FORMAT));
        target.setDeliveryType(source.getDeliveryMode().getSapCode());
        target.setFreightCondType(Config.getParameter("website.v2kart.FreightCondType"));
        target.setFreight(source.getDeliveryCost().toString());
        target.setCODCondType(Config.getParameter("website.v2kart.CODCondType"));
        if (source.getCodCharges() == null) {
            target.setCODCharges(ZERO_COD);
        } else {
            target.setCODCharges(source.getCodCharges().toString());
        }
        target.setSAPCustId(getBlankIfNull(((CustomerModel) source.getUser()).getSapCustomerId()));
        this.setShippingAddress(source, target);
        this.setBillingAddress(source, target);
        // target.setOrderTotal(source.getTotalPrice().toString());
        target.setCondTypeDisc(Config.getParameter("website.v2kart.HeaderCondTypeDisc"));
        target.setOrderTotalDis(source.getTotalDiscounts().toString());

        final PaymentInfoModel orderPaymentInfo = source.getPaymentInfo();
        if (null != orderPaymentInfo.getPaymentMode() && orderPaymentInfo.getPaymentMode().getMode().equalsIgnoreCase(PAYMENT_METHOD_CASH)) {
            if (source.getEntries().size() == 1) {
                target.setSingleEntryOrder(Config.getParameter("document.type.cod.single.order"));
            } else {
                target.setSingleEntryOrder(Config.getParameter("document.type.cod.multiple.order"));
            }
        } else {
            if (source.getEntries().size() == 1) {
                target.setSingleEntryOrder(Config.getParameter("document.type.prepaid.single.order"));
            } else {
                target.setSingleEntryOrder(Config.getParameter("document.type.prepaid.multi.order"));
            }
        }
    }

    private void setShippingAddress(final OrderModel source, final OrderCreationReq target) {
        SOCreateReq.OrderCreationReq.ShippingAddress shippAddress = target.getShippingAddress();
        if (shippAddress == null) {
            shippAddress = new SOCreateReq.OrderCreationReq.ShippingAddress();
        }
        final AddressModel deliveryAddress = source.getDeliveryAddress();
        shippAddress.setFName(deliveryAddress.getFirstname());
        shippAddress.setLName(deliveryAddress.getLastname());
        shippAddress.setAddLine1(getBlankIfNull(deliveryAddress.getLine1()));
        shippAddress.setAddLine2(getBlankIfNull(deliveryAddress.getLine2()));
        shippAddress.setAddLine3(getBlankIfNull(null));
        shippAddress.setCity(deliveryAddress.getTown());
        shippAddress.setCountry(deliveryAddress.getCountry().getIsocode());
        // shippAddress.setMobile(getBlankIfNull(deliveryAddress.getCellphone()));
        shippAddress.setMobile(getBlankIfNull(deliveryAddress.getPhone1()));
        shippAddress.setPostCode(deliveryAddress.getPostalcode());
        if (deliveryAddress.getRegion() != null) {
            shippAddress.setRegion(deliveryAddress.getRegion().getIsocodeShort());
        } else {
            shippAddress.setRegion(getBlankIfNull(null));
        }
        target.setShippingAddress(shippAddress);
    }

    private void setBillingAddress(final OrderModel source, final OrderCreationReq target) {
        SOCreateReq.OrderCreationReq.BillingAddress billingAddress = target.getBillingAddress();
        if (billingAddress == null) {
            billingAddress = new SOCreateReq.OrderCreationReq.BillingAddress();
        }
        AddressModel srcBillingAddress = null;
        if (source.getPaymentInfo() != null) {
            srcBillingAddress = source.getPaymentInfo().getBillingAddress();
            billingAddress.setAddLine1(getBlankIfNull(srcBillingAddress.getLine1()));
            billingAddress.setAddLine2(getBlankIfNull(srcBillingAddress.getLine2()));
            billingAddress.setAddLine3(getBlankIfNull(null));
            billingAddress.setCity(srcBillingAddress.getTown());
            billingAddress.setCountry(srcBillingAddress.getCountry().getIsocode());
            billingAddress.setMobile(getBlankIfNull(srcBillingAddress.getPhone1()));
            billingAddress.setPostCode(srcBillingAddress.getPostalcode());
            if (srcBillingAddress.getRegion() != null) {
                billingAddress.setRegion(srcBillingAddress.getRegion().getIsocodeShort());
            } else {
                billingAddress.setRegion(getBlankIfNull(null));
            }
        }
        target.setBillingAddress(billingAddress);
    }
}
