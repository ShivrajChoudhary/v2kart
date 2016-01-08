package in.com.v2kart.dataimport.converters;

import in.com.v2kart.dataimport.dto.V2StoreInventoryDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;
import in.com.v2kart.dataimport.services.BaseImportService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class V2StoreInventoryConverter extends DefaultDozerBasedConverter<V2StoreInventoryDto, StockLevelModel> {

    @Autowired
    @Qualifier("baseImportService")
    private BaseImportService baseImportService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.converters.DefaultDozerBasedConverter#createTarget()
     */
    @Override
    protected StockLevelModel createTarget() {
        final StockLevelModel stockLevelModel = getModelService().create(StockLevelModel.class);
        stockLevelModel.setTreatNegativeAsZero(false);
        return stockLevelModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#convert(java.lang.Object)
     */
    @Override
    public StockLevelModel convert(final V2StoreInventoryDto source) throws ConversionException {
        final StockLevelModel stockLevelModel = createTarget();
        final CatalogVersionModel catalogVersionModel = baseImportService.getCatalogVersionModel();
        stockLevelModel.setProduct(productService.getProductForCode(catalogVersionModel, source.getCode()));
        stockLevelModel.setProductCode(source.getCode());

        // in stock status added for pick up in store functionality
        stockLevelModel.setInStockStatus(InStockStatus.NOTSPECIFIED);

        // TODO
        // stockLevelModel.setCreationtime();
        try {
            stockLevelModel.setWarehouse(warehouseService.getWarehouseForCode(source.getSiteCode()));
        } catch (final UnknownIdentifierException e) {
            throw new FeedPersistanceException(FailureCause.WAREHOUSE_NOT_FOUND, source.getRowIndex(), e);
        }
        populate(source, stockLevelModel);
        return stockLevelModel;
    }

    public StockLevelModel convert(final V2StoreInventoryDto source, final ProductModel productModel) throws ConversionException {
        final StockLevelModel stockLevelModel = createTarget();
        stockLevelModel.setProduct(productModel);
        stockLevelModel.setProductCode(productModel.getCode());

        // in stock status added for pick up in store functionality
        stockLevelModel.setInStockStatus(InStockStatus.NOTSPECIFIED);

        // TODO
        // stockLevelModel.setCreationtime();
        try {
            stockLevelModel.setWarehouse(warehouseService.getWarehouseForCode(source.getSiteCode()));
        } catch (final UnknownIdentifierException e) {
            throw new FeedPersistanceException(FailureCause.WAREHOUSE_NOT_FOUND, source.getRowIndex(), e);
        }
        populate(source, stockLevelModel);
        return stockLevelModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.converters.DefaultDozerBasedConverter#populate(in.com.v2kart.dataimport.dto .BaseDto,
     * de.hybris.platform.core.model.ItemModel)
     */
    @Override
    public void populate(final V2StoreInventoryDto source, final StockLevelModel target) {
        try {
            target.setReleaseDate(new SimpleDateFormat("ddMMyyyyHHmmss").parse(source.getCreationDate() + source.getCreationTime()));
        } catch (final ParseException e) {
            throw new FeedPersistanceException(FailureCause.STOCK_CREATION_DATE_ERROR, source.getRowIndex(), e);
        }

        target.setAvailable(source.getAvailable()+target.getReserved());
        target.setTreatNegativeAsZero(true);
        // target.setIsBlock(source.getBlock());
    }

}
