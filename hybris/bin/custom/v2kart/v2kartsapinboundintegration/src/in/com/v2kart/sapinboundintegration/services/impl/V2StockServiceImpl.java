package in.com.v2kart.sapinboundintegration.services.impl;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.basecommerce.enums.StockLevelUpdateType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.stock.impl.DefaultStockService;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.stock.model.StockLevelHistoryEntryModel;
import de.hybris.platform.stock.strategy.StockLevelProductStrategy;
import de.hybris.platform.util.Utilities;

import in.com.v2kart.sapinboundintegration.dao.V2StockLevelDao;
import in.com.v2kart.sapinboundintegration.services.V2StockService;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * <FGStockServiceImpl> implements <FGStockService>
 *
 * @author satvir_nagarro
 *
 */
public class V2StockServiceImpl extends DefaultStockService implements V2StockService {

    /** The logger. */
    private final Logger LOG = LoggerFactory.getLogger(V2StockServiceImpl.class);

    /** property reference for SpotStockLevelDao bean injection . */
    private V2StockLevelDao stockLevelDao;

    /** property reference for StockLevelProductStrategy bean injection . */
    private StockLevelProductStrategy stockLevelProductStrategy;

    private StockLevelModel checkAndGetStockLevel(final ProductModel product, final WarehouseModel warehouse) {
        final StockLevelModel stockLevel = (this.stockLevelDao).findStockLevel(this.stockLevelProductStrategy.convert(product), warehouse);
        if (stockLevel == null) {
            throw new StockLevelNotFoundException("no stock level for product [" + product + "] in warehouse [" + warehouse.getName()
                    + "] found.");
        }
        return stockLevel;
    }

    private void clearCacheForItem(final StockLevelModel stockLevel) {
        Utilities.invalidateCache(stockLevel.getPk());
        getModelService().refresh(stockLevel);
    }

    private StockLevelHistoryEntryModel createStockLevelHistoryEntry(final StockLevelModel stockLevel,
            final StockLevelUpdateType updateType, final int reserved, final String comment) {
        final StockLevelHistoryEntryModel historyEntry = (StockLevelHistoryEntryModel) getModelService().create(
                StockLevelHistoryEntryModel.class);
        historyEntry.setStockLevel(stockLevel);
        historyEntry.setActual(stockLevel.getAvailable());
        historyEntry.setReserved(reserved);
        historyEntry.setUpdateType(updateType);
        if (comment != null) {
            historyEntry.setComment(comment);
        }
        historyEntry.setUpdateDate(new Date());
        getModelService().save(historyEntry);
        return historyEntry;
    }

    /**
     * Same as in <DefaultStockService>, only a slight change that if the stock level is not available then create it and with reserve
     * amount.
     */
    @Override
    public void reserve(final ProductModel product, final WarehouseModel warehouse, final int amount, final String comment)
            throws InsufficientStockLevelException {
        reserveStock(product, warehouse, amount, comment, true);

    }

    @Override
    public void reserveWithNegativeStockLevel(final ProductModel product, final WarehouseModel warehouse, final int amount,
            final String comment) throws InsufficientStockLevelException {
        reserveStock(product, warehouse, amount, comment, false);
    }

    private void reserveStock(final ProductModel product, final WarehouseModel warehouse, final int amount, final String comment,
            final boolean treatNegativeAsZero) throws InsufficientStockLevelException {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero.");
        }

        StockLevelModel currentStockLevel = null;
        boolean existingStockLevelFound = true;
        try {
            currentStockLevel = checkAndGetStockLevel(product, warehouse);
        } catch (final StockLevelNotFoundException localStockLevelNotFoundException) {
            // create stockLevel with available amount 0 and reserved as same as amount
            createStockLevel(product, warehouse, 0, 0, amount, InStockStatus.NOTSPECIFIED, -1, treatNegativeAsZero);
            existingStockLevelFound = false;
        }

        if (existingStockLevelFound) {
            final InStockStatus realInStockStatus = currentStockLevel.getInStockStatus();
            currentStockLevel.setInStockStatus(InStockStatus.FORCEINSTOCK);
            final Integer reserved = this.stockLevelDao.reserve(currentStockLevel, amount);
            currentStockLevel.setInStockStatus(realInStockStatus);
            if (reserved == null) {
                throw new InsufficientStockLevelException("insufficient available amount for stock level [" + currentStockLevel.getPk()
                        + "]");
            }

            clearCacheForItem(currentStockLevel);
            createStockLevelHistoryEntry(currentStockLevel, StockLevelUpdateType.CUSTOMER_RESERVE, reserved.intValue(), comment);
        }
    }

    @Override
    public void commit(final ProductModel product, final WarehouseModel warehouse, final int amount, final String comment) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero.");
        }
        final StockLevelModel currentStockLevel = checkAndGetStockLevel(product, warehouse);
        this.stockLevelDao.commit(currentStockLevel, amount);
        clearCacheForItem(currentStockLevel);
        // createStockLevelHistoryEntry(currentStockLevel, StockLevelUpdateType.ORDER_READY_TO_DISPATCH, amount, comment);
    }

    @Override
    @Required
    public void setStockLevelProductStrategy(final StockLevelProductStrategy stockLevelProductStrategy) {
        this.stockLevelProductStrategy = stockLevelProductStrategy;
        super.setStockLevelProductStrategy(stockLevelProductStrategy);
    }

    /**
     * @param stockLevelDao
     *        the stockLevelDao to set
     */
    @Override
    @Required
    public void setStockLevelDao(final StockLevelDao stockLevelDao) {
        this.stockLevelDao = (V2StockLevelDao) stockLevelDao;
        super.setStockLevelDao(stockLevelDao);
    }

    /*
     * (non-Javadoc) This method is used for releasing the reserve stock from product after cancel that product
     *
     * @see in.com.v2kart.sapinboundintegration.services.V2StockService#release(de.hybris.platform.core.model.product.ProductModel,
     * de.hybris.platform.ordersplitting.model.WarehouseModel, int)
     */
    @Override
    public void release(final StockLevelModel currentStockLevel, final int amount) throws InsufficientStockLevelException {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero.");
        }
        this.stockLevelDao.releaseStockOfCancelEntry(currentStockLevel, amount);
        clearCacheForItem(currentStockLevel);
    }

}
