package in.com.v2kart.sapinboundintegration.services;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;

/**
 * <V2StockService> extends <StockService> contains custom API that updates, commit and transfer stock
 *
 * @author satvir_nagarro
 *
 */
public interface V2StockService extends StockService {

    /**
     * Commit (permanently decrease from both reserved and available) stock at sales request to SAP.
     *
     * @param product
     *        product
     * @param warehouse
     *        warehouse
     * @param commitAmount
     *        commitAmount
     * @param comment
     *        comment
     */
    void commit(ProductModel product, WarehouseModel warehouse, int commitAmount, String comment);

    /**
     *
     * @param product
     * @param warehouse
     * @param amount
     * @param comment
     * @throws InsufficientStockLevelException
     */
    void reserveWithNegativeStockLevel(final ProductModel product, final WarehouseModel warehouse, final int amount, final String comment)
            throws InsufficientStockLevelException;

    /**
     * @author Shivraj
     * @param stock
     * @param amount
     * @throws InsufficientStockLevelException
     */
    public void release(final StockLevelModel stock, final int amount) throws InsufficientStockLevelException;

}
