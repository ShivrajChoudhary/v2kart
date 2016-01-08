package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.sapinboundintegration.constants.V2kartsapinboundintegrationConstants;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnReq;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnReq.OrderReturnReq;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * <FGOrderCreationReqPopulator> populates data for sales Order return request from HYBRIS to SAP
 *
 * @author shailjagupta
 *
 */
public class V2OrderReturnReqPopulator implements Populator<ReturnRequestModel, SOReturnReq.OrderReturnReq> {

    private Map<String, String> returnReasonCodeMap;

    public Map<String, String> getReturnReasonCodeMap() {
        return returnReasonCodeMap;
    }

    public void setReturnReasonCodeMap(final Map<String, String> returnReasonCodeMap) {
        this.returnReasonCodeMap = returnReasonCodeMap;
    }

    @Override
    public void populate(final ReturnRequestModel source, final OrderReturnReq target) throws ConversionException {
        final OrderModel orderModel = source.getOrder();
        target.setReturnOrderNo(source.getRMA());// request Id/code for return
        target.setDocType(getDocTypeForOrder(orderModel));
        target.setSalesOrg(getBlankIfNull(null));
        target.setDistrChannel(getBlankIfNull(null));
        target.setOriginalSONo(orderModel.getCode());
        if (source.getReturnEntries() != null && source.getReturnEntries().size() != 0) {
            target.setReturnReason(((RefundEntryModel) source.getReturnEntries().get(0)).getReason().getCode());
        }
    }

    // API is used to return SAP DocType. It will return ZEDR for COD order otherwise ZECR.
    private String getDocTypeForOrder(final OrderModel orderModel) {
        final PaymentInfoModel primaryPaymentInfo = orderModel.getPaymentInfo();
        if (primaryPaymentInfo != null && primaryPaymentInfo.getPaymentGateway() != null
                && primaryPaymentInfo.getPaymentGateway().equals("COD")) {
            return "ZEDR";
        } else {
            return "ZECR";
        }
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
