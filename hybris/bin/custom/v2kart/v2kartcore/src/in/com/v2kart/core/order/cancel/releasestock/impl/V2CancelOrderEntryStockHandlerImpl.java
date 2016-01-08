package in.com.v2kart.core.order.cancel.releasestock.impl;

import in.com.v2kart.core.order.cancel.releasestock.V2CancelOrderEntryStockHandler;
import in.com.v2kart.sapinboundintegration.services.V2StockService;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.dao.OrderCancelDao;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;

/**
 * V2CancelOrderEntryStockHandlerImpl for release the cancel order entry
 * reserved stock.
 * 
 * @author Shivraj
 *
 */
public class V2CancelOrderEntryStockHandlerImpl implements
		V2CancelOrderEntryStockHandler {
	private static final Logger LOG = Logger
			.getLogger(V2CancelOrderEntryStockHandlerImpl.class);
	private OrderCancelDao orderCancelDao;
	private ModelService modelService;
	private V2StockService v2StockService;

	@Override
	public void releaseStockAfterOrderCancelling(final OrderCancelRecordEntryModel currentEntry,
            final OrderCancelRequest orderCancelRequest) {
		try {
				for (OrderCancelEntry responseEntry : orderCancelRequest.getEntriesToCancel()){
					OrderEntryCancelRecordEntryModel orderEntryRecord = this.orderCancelDao
								.getOrderEntryCancelRecord((OrderEntryModel) responseEntry.getOrderEntry(), currentEntry);
					LOG.info("current canceled entry is :"+orderEntryRecord!=null?orderEntryRecord.getCode():null);
					if (orderEntryRecord != null) {
							OrderEntryModel orderEntryModel = orderEntryRecord.getOrderEntry();
							Set<StockLevelModel> stocks = orderEntryModel.getProduct().getStockLevels();
							LOG.debug("Releasing the product ["
									+ orderEntryModel.getProduct().getCode()
									+ "] stock qty ["+ responseEntry.getCancelQuantity() + "]");
						for(StockLevelModel stock :stocks){
							this.v2StockService.release(stock, (int) responseEntry.getCancelQuantity());
								LOG.debug("reserve stock release successfully");
							}
						}
					}
		} catch (InsufficientStockLevelException e) {
			LOG.error("Error in stock level");
			e.printStackTrace();
		}
	}

	public V2StockService getV2StockService() {
		return v2StockService;
	}

	public void setV2StockService(V2StockService v2StockService) {
		this.v2StockService = v2StockService;
	}

	public OrderCancelDao getOrderCancelDao() {
		return orderCancelDao;
	}

	public void setOrderCancelDao(OrderCancelDao orderCancelDao) {
		this.orderCancelDao = orderCancelDao;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
}
