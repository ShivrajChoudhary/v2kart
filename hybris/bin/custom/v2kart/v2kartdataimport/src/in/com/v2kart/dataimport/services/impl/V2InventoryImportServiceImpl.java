package in.com.v2kart.dataimport.services.impl;

import in.com.v2kart.core.dao.CatalogAwareModelDao;
import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.converters.V2StoreInventoryConverter;
import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.dto.V2StoreInventoryDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException;
import in.com.v2kart.dataimport.importengine.CSVReader;
import in.com.v2kart.dataimport.services.V2InventoryImportService;
import in.com.v2kart.dataimport.strategies.FeedReaderStrategy;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.services.BaseStoreService;

class V2InventoryImportServiceImpl extends BaseImportServiceImpl<BaseDto> implements V2InventoryImportService {

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(V2InventoryImportServiceImpl.class);

    
    /** The feed reader strategy. */
    @Autowired
    private FeedReaderStrategy feedReaderStrategy;

    @Resource(name = "v2StoreInventoryCsvReader")
    private CSVReader v2StoreInventoryCsvReader;

    @Resource(name = "v2StoreInventoryConverter")
    private V2StoreInventoryConverter v2StoreInventoryConverter;

    @Resource(name = "productModelDao")
    private CatalogAwareModelDao<ProductModel> productModelDao;

    @Autowired
    private BaseStoreService baseStoreService;

    @Override
    public List<V2StoreInventoryDto> geStoreInventoryDataFromFeed() {
        return feedReaderStrategy.<V2StoreInventoryDto> readFeed(v2StoreInventoryCsvReader);
    }

    @Override
    public ImportDataSummaryInfo importStoreInventoryDataFromFeed() {
        final List<V2StoreInventoryDto> v2StoreInventoryDtos = this.geStoreInventoryDataFromFeed();
        final ImportDataSummaryInfo summaryInfo = new ImportDataSummaryInfo();
        summaryInfo.setTotalRecords(v2StoreInventoryDtos.size());
        for (final V2StoreInventoryDto v2StoreInventoryDto : v2StoreInventoryDtos) {
            try {
                summaryInfo.recordDtoProcessing(v2StoreInventoryDto);
                this.createOrUpdateV2StoreInventory(v2StoreInventoryDto, summaryInfo);
            } catch (final FeedPersistanceException e) {
                // LOG.error("Error persisting Store Inventory Dto:" + fgStoreInventoryDto + "Cause "+);
                logNotFoundException(v2StoreInventoryDto.getSiteCode(), WarehouseModel._TYPECODE, v2StoreInventoryDto, e.getFailureCause(),
                        summaryInfo);
                summaryInfo.addFailureIncidence(e.getFailureCause());
            }
        }
        return summaryInfo;
    }
    
    /**
     * @param v2StoreInventoryDto
     * @param summaryInfo
     */
    private void createOrUpdateV2StoreInventory(final V2StoreInventoryDto v2StoreInventoryDto, final ImportDataSummaryInfo summaryInfo) {
        final ProductModel productModel = productModelDao.findByCodeAndCatalogVersion(v2StoreInventoryDto.getCode(),
                getCatalogVersionModel());
        if (productModel == null) {
            this.logProductNotFoundException(v2StoreInventoryDto.getCode(), v2StoreInventoryDto, summaryInfo);
            return;
        }
        boolean found = false;
        for (final StockLevelModel stockLevelModel : productModel.getStockLevels()) {
            if (stockLevelModel.getWarehouse().getCode().equals(v2StoreInventoryDto.getSiteCode())) {
                v2StoreInventoryConverter.populate(v2StoreInventoryDto, stockLevelModel);
                LOG.debug("Saving inventory details for row: " + v2StoreInventoryDto.getRowIndex());
                getModelService().save(stockLevelModel);
                found = true;
                break;
            }
        }
        if (!found) {
            final StockLevelModel newStockLevelModel = v2StoreInventoryConverter.convert(v2StoreInventoryDto, productModel);
            LOG.debug("Saving inventory details for row: " + v2StoreInventoryDto.getRowIndex());
            getModelService().save(newStockLevelModel);
        }
    }

}