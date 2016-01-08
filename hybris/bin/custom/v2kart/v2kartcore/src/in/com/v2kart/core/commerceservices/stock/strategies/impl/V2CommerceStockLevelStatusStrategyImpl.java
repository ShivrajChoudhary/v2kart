package in.com.v2kart.core.commerceservices.stock.strategies.impl;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.strategies.impl.CommerceStockLevelStatusStrategy;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import java.util.Collection;

/**
 * The Class FGCommerceStockLevelStatusStrategyImpl.
 * 
 * @author gaurav2007
 */
public class V2CommerceStockLevelStatusStrategyImpl extends CommerceStockLevelStatusStrategy {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.stock.strategies.impl.CommerceStockLevelStatusStrategy#checkStatus(java.util.Collection)
     */
    @Override
    public StockLevelStatus checkStatus(final Collection<StockLevelModel> stockLevels) {
        StockLevelStatus resultStatus = StockLevelStatus.OUTOFSTOCK;
        final Long result = getCommerceStockLevelCalculationStrategy().calculateAvailability(stockLevels);
        if (null == result) {
            return StockLevelStatus.INSTOCK;
        }
        final long resultValue = result.longValue();
        if (resultValue <= 0) {
            resultStatus = StockLevelStatus.OUTOFSTOCK;
        } else if (resultValue > getDefaultLowStockThreshold()) {
            resultStatus = StockLevelStatus.INSTOCK;
        } else {
            resultStatus = StockLevelStatus.LOWSTOCK;
        }
        return resultStatus;
    }

}
