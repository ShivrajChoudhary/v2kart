package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.sapinboundintegration.ws.orderreturn.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnReq;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnReq.OrderReturnReq;

import java.util.List;

/**
 * <FGOrderCreationReqItemDtlsPopulator> populates data for sales Order return entries request from HYBRIS to SAP
 * 
 * @author shailjagupta
 * 
 */
public class V2OrderReturnReqItemDtlsPopulator implements Populator<ReturnRequestModel, SOReturnReq.OrderReturnReq> {

    private final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public void populate(final ReturnRequestModel source, final OrderReturnReq target) throws ConversionException {
        final List<SOReturnReq.OrderReturnReq.SOReturnItemDtls> returnOrderItemsList = target.getSOReturnItemDtls();
        SOReturnReq.OrderReturnReq.SOReturnItemDtls returnOrderItemDtls = null;
        for (final ReturnEntryModel returnEntryModel : source.getReturnEntries()) {
            final OrderEntryModel orderEntryModel = (OrderEntryModel) returnEntryModel.getOrderEntry();
            returnOrderItemDtls = objectFactory.createSOReturnReqOrderReturnReqSOReturnItemDtls();
            if (returnEntryModel.getOrderEntry() != null) {
                returnOrderItemDtls.setItemNo(orderEntryModel.getEntryNumber().toString());
            }
            if (returnEntryModel.getExpectedQuantity() != null) {
                returnOrderItemDtls.setTargetQty(returnEntryModel.getExpectedQuantity().toString());
            } else {
                returnOrderItemDtls.setTargetQty("0");
            }
            returnOrderItemDtls.setArticleNo(orderEntryModel.getProduct().getCode());
            returnOrderItemDtls.setUnit(orderEntryModel.getUnit().getCode());

            // Add to list
            returnOrderItemsList.add(returnOrderItemDtls);
        }

    }
}
