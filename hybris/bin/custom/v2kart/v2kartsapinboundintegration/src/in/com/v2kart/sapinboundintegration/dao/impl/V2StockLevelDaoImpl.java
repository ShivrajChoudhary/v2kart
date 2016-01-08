package in.com.v2kart.sapinboundintegration.dao.impl;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.stock.impl.DefaultStockLevelDao;

import in.com.v2kart.sapinboundintegration.dao.V2StockLevelDao;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * <FGStockLevelDaoImpl> implementation for <FGStockLevelDao>
 *
 * @author satvir_nagarro
 *
 */
public class V2StockLevelDaoImpl extends DefaultStockLevelDao implements V2StockLevelDao {

    /** property reference for TransactionTemplate bean injection . */
    private TransactionTemplate transactionTemplate;

    /** property reference for JdbcTemplate bean injection . */
    private JdbcTemplate jdbcTemplate;

    /** property reference for TypeService bean injection . */
    private TypeService typeService;

    /** instance of StockLevelColumns. */
    private StockLevelColumns stockLevelColumns1;

    @Override
    public void commit(final StockLevelModel stockLevel, final int amount) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus arg0) {
                try {
                    final String commitQuery = V2StockLevelDaoImpl.this.assembleDecreaseReserveAvailableQuery();
                    final Integer _amount = Integer.valueOf(amount);
                    final Long _pk = Long.valueOf(stockLevel.getPk().getLongValue());
                    final int rows = V2StockLevelDaoImpl.this.jdbcTemplate.update(commitQuery, new Object[] { _amount, _amount, _pk });
                    if (rows <= 1) {
                        return;
                    }
                    throw new IllegalStateException("more stock level rows found for the update: [" + stockLevel.getPk() + "]");
                } catch (final DataAccessException dae) {
                    throw new SystemException(dae);
                }
            }
        });
    }

    /*
     * (non-Javadoc) Dao method for decreasing reserve qty after cancellation order
     *
     * @author Shivraj
     *
     * @see
     * in.com.v2kart.sapinboundintegration.dao.V2StockLevelDao#releaseStockOfCancelEntry(de.hybris.platform.ordersplitting.model.StockLevelModel
     * , int)
     */
    @Override
    public void releaseStockOfCancelEntry(final StockLevelModel stockLevel, final int releaseAmount) {
        //
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus arg0) {
                try {
                    final String commitQuery = V2StockLevelDaoImpl.this.assembleDecreaseReserveQuery();
                    final Integer _amount = Integer.valueOf(releaseAmount);
                    final Long _pk = Long.valueOf(stockLevel.getPk().getLongValue());
                    final int rows = V2StockLevelDaoImpl.this.jdbcTemplate.update(commitQuery, new Object[] { _amount, _pk });
                    if (rows <= 1) {
                        return;
                    }
                    throw new IllegalStateException("more stock level rows found for the update: [" + stockLevel.getPk() + "]");
                } catch (final DataAccessException dae) {
                    throw new SystemException(dae);
                }
            }
        });
    }

    /**
     * Query for decreasing reserve and available.
     *
     * @param inStockStatus
     * @return query
     */
    private String assembleDecreaseReserveAvailableQuery() {
        prepareStockLevelColumns();
        final StringBuilder query = new StringBuilder("UPDATE " + this.stockLevelColumns1.tableName);
        query.append(" SET " + this.stockLevelColumns1.reservedCol + " = " + this.stockLevelColumns1.reservedCol + " - ?,  ");
        query.append(this.stockLevelColumns1.availableCol + " = " + this.stockLevelColumns1.availableCol + " - ? ");
        query.append(" WHERE " + this.stockLevelColumns1.pkCol + "=?");
        return query.toString();
    }

    /**
     * Query for decreasing reserve only
     *
     * @author Shivraj
     * @return query
     */
    private String assembleDecreaseReserveQuery() {
        prepareStockLevelColumns();
        final StringBuilder query = new StringBuilder("UPDATE " + this.stockLevelColumns1.tableName);
        query.append(" SET " + this.stockLevelColumns1.reservedCol + " = " + this.stockLevelColumns1.reservedCol + " - ? ");
        query.append(" WHERE " + this.stockLevelColumns1.pkCol + "=?");
        return query.toString();
    }

    /**
     * Private class for StockLevel columns
     *
     * @author satvir_nagarro
     *
     */
    private class StockLevelColumns {
        private final String tableName;
        private final String pkCol;
        private final String availableCol;
        private final String reservedCol;

        /**
         * Constructor
         *
         * @param typeService
         */
        private StockLevelColumns(final TypeService typeService) {
            final ComposedTypeModel stockLevelType = typeService.getComposedTypeForClass(StockLevelModel.class);
            this.tableName = stockLevelType.getTable();
            this.pkCol = typeService.getAttributeDescriptor(stockLevelType, StockLevelModel.PK).getDatabaseColumn();
            this.availableCol = typeService.getAttributeDescriptor(stockLevelType, StockLevelModel.AVAILABLE).getDatabaseColumn();
            this.reservedCol = typeService.getAttributeDescriptor(stockLevelType, StockLevelModel.RESERVED).getDatabaseColumn();
        }
    }

    private void prepareStockLevelColumns() {
        if (this.stockLevelColumns1 != null) {
            return;
        }
        this.stockLevelColumns1 = new StockLevelColumns(this.typeService);
    }

    /**
     * @param transactionTemplate
     *        the transactionTemplate1 to set
     */
    @Override
    @Required
    public void setTransactionTemplate(final TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
        super.setTransactionTemplate(transactionTemplate);
    }

    /**
     * @param jdbcTemplate
     *        the jdbcTemplate1 to set
     */
    @Override
    @Required
    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        super.setJdbcTemplate(jdbcTemplate);
    }

    /**
     * @param typeService
     *        the typeService1 to set
     */

    @Override
    @Required
    public void setTypeService(final TypeService typeService) {
        this.typeService = typeService;
        super.setTypeService(typeService);
    }
}
