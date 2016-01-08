package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq;
import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq.OrderCreationReq;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * <FGOrderCreationReqItemDtlsPopulator> populates data for sales Order entries request from HYBRIS to SAP
 *
 * @author satvir_nagarro
 *
 */
public class V2OrderCreationReqItemDtlsPopulator extends V2AbstractOrderCreationReqPopulator {
    private static final Logger LOG = Logger.getLogger(V2OrderCreationReqItemDtlsPopulator.class);

    @Override
    public void populate(final OrderModel source, final OrderCreationReq target) throws ConversionException {
        final List<SOCreateReq.OrderCreationReq.ItemDtls> orderItemsList = target.getItemDtls();
        SOCreateReq.OrderCreationReq.ItemDtls orderItemDtls = null;
        for (final AbstractOrderEntryModel abstractOrderEntryModel : source.getEntries()) {
            final OrderEntryModel orderEntryModel = (OrderEntryModel) abstractOrderEntryModel;
            orderItemDtls = objectFactory.createSOCreateReqOrderCreationReqItemDtls();
            // Hybris generate entry number like 0, 1,2 ,3 ,4 etc
            if (orderEntryModel.getEntryNumber() != null) {
                // orderItemDtls.setItmNumber(orderEntryModel.getEntryNumber().toString());
                orderItemDtls.setItmNumber(changingItemNumber(orderEntryModel.getEntryNumber()).toString());
            }
            // Product code. This product must be available on SAP. Otherwise while placing an order on SAP it will throw error.
            // orderItemDtls.setItmNumber(abstractOrderEntryModel.getEntryNumber().toString());
            orderItemDtls.setItmNumber(changingItemNumber(abstractOrderEntryModel.getEntryNumber()).toString());
            orderItemDtls.setArtNumber(orderEntryModel.getProduct().getCode());
            orderItemDtls.setItmName(orderEntryModel.getProduct().getName());
            orderItemDtls.setReqQty(BigDecimal.valueOf(orderEntryModel.getQuantity().longValue()));
            orderItemDtls.setUOM(orderEntryModel.getUnit().getCode());
            orderItemDtls.setSingleUnitMRP(String.valueOf(orderEntryModel.getBasePrice()));
            if (abstractOrderEntryModel.getDiscountValues() != null && abstractOrderEntryModel.getDiscountValues().size() > 0) {
                orderItemDtls.setCondTypeDisc(Config.getParameter("website.v2kart.ItemCondTypeDisc"));
                final double allUnitDis = abstractOrderEntryModel.getDiscountValues().get(0).getAppliedValue();
                final String singleUnitDis = String.valueOf(allUnitDis / orderEntryModel.getQuantity().longValue());
                orderItemDtls.setSingleUnitDis(singleUnitDis);
            }

            // add to list
            orderItemsList.add(orderItemDtls);
        }
    }

    public Integer changingItemNumber(final Integer itmNo) {
        Integer in;
        if (itmNo.intValue() == 0) {
            in = Integer.valueOf(itmNo.intValue() + 10);
        } else {
            in = Integer.valueOf(itmNo.intValue() * 10 + 10);
        }
        LOG.debug("ArtNumber" + in);
        return in;
    }
}
