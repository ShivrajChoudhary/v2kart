package in.com.v2kart.sapoutboundintegration.services.Impl;

import static org.springframework.util.Assert.notNull;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.ConsignmentCreationException;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import in.com.v2kart.core.model.V2LogisticServiceProviderModel;
import in.com.v2kart.sapinboundintegration.services.V2StockService;
import in.com.v2kart.sapoutboundintegration.services.V2ConsignmentUpdateService;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateReq;

import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author satvir_nagarro
 * 
 */
public class V2ConsignmentUpdateServiceImpl implements V2ConsignmentUpdateService {

    private final Logger LOG = LoggerFactory.getLogger(V2ConsignmentUpdateServiceImpl.class);

    /** ModelService bean injection */
    private ModelService modelService;

    /** WarehouseService bean injection */
    private WarehouseService warehouseService;

    /** StockService bean injection */
    private V2StockService stockService;

    /** flexibleSearchService bean injection */
    private FlexibleSearchService flexibleSearchService;

    @Override
    public ConsignmentModel createConsignment(final AbstractOrderModel order, final SoOrderStatusUpdateReq orderStatusUpdate)
            throws ConsignmentCreationException {
        notNull(order, "order may not be null!");

        final ConsignmentModel consignment = modelService.create(ConsignmentModel.class);

        consignment.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
        consignment.setCode(orderStatusUpdate.getDeliveryNo());
        consignment.setOrder(order);
        if (order != null) {
            consignment.setShippingAddress(order.getDeliveryAddress());
        }
        consignment.setCarrier(orderStatusUpdate.getLSPName());
        consignment.setTrackingID(orderStatusUpdate.getAWBNo());
        // TODO
        consignment.setLsp(this.findLspByCode(orderStatusUpdate.getLSPName()));
        for (final AbstractOrderEntryModel orderEntry : order.getEntries()) {
            final ConsignmentEntryModel entry = modelService.create(ConsignmentEntryModel.class);
            entry.setOrderEntry(orderEntry);
            entry.setQuantity(orderEntry.getQuantity());
            entry.setConsignment(consignment);
            consignment.setDeliveryMode(orderEntry.getDeliveryMode());
            entry.setShippedQuantity(orderEntry.getQuantity());
            consignment.getConsignmentEntries().add(entry);
        }
        WarehouseModel warehouseModel = null;
        try {
            warehouseModel = warehouseService.getWarehouseForCode(orderStatusUpdate.getWH());
        } catch (final ModelNotFoundException e) {
            LOG.error(String.format("No warehouse found for warehouse code [%s] for consignment [%s]", orderStatusUpdate.getWH(),
                    orderStatusUpdate.getDeliveryNo()), e);
            throw new ConsignmentCreationException(String.format("No warehouse found for warehouse code [%s] for consignment [%s]",
                    orderStatusUpdate.getWH(), orderStatusUpdate.getDeliveryNo()));
        }

        consignment.setWarehouse(warehouseModel);
        // TODO
        consignment.setStatus(ConsignmentStatus.READY_TO_DISPATCH);
        // TODO
        consignment.setShippingDate(new Date());
        modelService.save(consignment);
        modelService.refresh(consignment);
        return consignment;

    }

    @Override
    public ConsignmentStatus updateConsignment(final AbstractOrderModel order, final ConsignmentModel consignment,
            final SoOrderStatusUpdateReq orderStatusUpdate) {
        consignment.setStatus(ConsignmentStatus.DISPATCHED);
        consignment.setShippingDate(new Date());
        modelService.save(consignment);
        modelService.refresh(consignment);
        return consignment.getStatus();
    }

    @Override
    public void commitStock(final ConsignmentModel consignment) {
        notNull(consignment, "consignment may not be null!");
        if (!consignment.isStockCommited()) {
            for (final ConsignmentEntryModel consignmentEntryModel : consignment.getConsignmentEntries()) {
                stockService.commit(consignmentEntryModel.getOrderEntry().getProduct(), consignment.getWarehouse(), consignmentEntryModel
                        .getOrderEntry().getQuantity().intValue(),
                        String.format("Commit stock for consignment code [%s]", consignment.getCode()));
            }
            consignment.setStockCommited(true);
            modelService.save(consignment);
        }
    }

    /**
     * 
     * @param lspCode
     * @return
     */
    public V2LogisticServiceProviderModel findLspByCode(final String lspCode) {
        final V2LogisticServiceProviderModel tempLSPModel = modelService.create(V2LogisticServiceProviderModel._TYPECODE);
        tempLSPModel.setCode(lspCode);
        V2LogisticServiceProviderModel lspModel = null;
        try {
            lspModel = flexibleSearchService.getModelByExample(tempLSPModel);
        } catch (final ModelNotFoundException mnEx) {
            LOG.error("No LSP exists for this LSP name/code [{}]", lspCode);
        } catch (final AmbiguousIdentifierException aiEx) {
            LOG.error("No LSP exists for this LSP name/code [{}]", lspCode);
        }
        return lspModel;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param warehouseService
     *        the warehouseService to set
     */
    @Required
    public void setWarehouseService(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * @param stockService
     *        the stockService to set
     */
    @Required
    public void setStockService(final V2StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * @param flexibleSearchService
     *        the flexibleSearchService to set
     */
    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
