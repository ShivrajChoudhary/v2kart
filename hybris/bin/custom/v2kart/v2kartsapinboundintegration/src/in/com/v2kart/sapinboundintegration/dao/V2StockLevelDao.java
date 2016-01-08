package in.com.v2kart.sapinboundintegration.dao;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.stock.impl.StockLevelDao;

/**
 * <FGStockLevelDao> extends hybris default <StockLevelDao> contains some custom APIs for commit of stock as database side.
 *
 * @author satvir_nagarro
 *
 */

public interface V2StockLevelDao extends StockLevelDao {

    /**
     * Final update/commit of a stock level for an consignment/order before sending it to SAP. It decreases the commit amount from reserved
     * and available both for a stock level..
     *
     * @param stockLevel
     *        stockLevel
     * @param amount
     *        commit amount
     */
    void commit(final StockLevelModel stockLevel, final int commitAmount);

    /**
     * Final update of a stock level for an order cancel before sending it to SAP. It decreases the commit amount from reserved
     * 
     * @author Shivraj
     * @param stockLevel
     * @param releaseAmount
     */
    void releaseStockOfCancelEntry(final StockLevelModel stockLevel, final int releaseAmount);

}
