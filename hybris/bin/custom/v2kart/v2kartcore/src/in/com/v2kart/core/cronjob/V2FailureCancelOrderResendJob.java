package in.com.v2kart.core.cronjob;

import in.com.v2kart.core.order.cancel.releasestock.V2CancelOrderEntryStockHandler;
import in.com.v2kart.core.order.executor.impl.V2ImmediateCancelRequestExecutor;
import in.com.v2kart.core.process.V2RefundOrderProcessor;
import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService;
import in.com.v2kart.sapinboundintegration.services.V2StockService;
import in.com.v2kart.sapinboundintegration.strategies.V2kartSapOrderCancelStrategy;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelRes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.dao.OrderCancelDao;
import de.hybris.platform.ordercancel.exceptions.OrderCancelRecordsHandlerException;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;

/**
 * <V2FailureCancelOrderResendJob> fetches SAP FAILURE cancelled orders  and then send/post again then to SAP
 * 
 * @author Shivraj
 * @Version 2.0
 * 
 */
public class V2FailureCancelOrderResendJob extends
		AbstractJobPerformable<CronJobModel> {

	/** Logger Instance for this class */
	private final Logger LOG = Logger
			.getLogger(V2FailureCancelOrderResendJob.class);

	/** V2OrderDao bean injection */
	@Autowired
	private ModelService modelService;
	@Autowired 
	private V2StockService v2StockService;
	private FlexibleSearchService flexibleSearchService;
	private OrderCancelDao orderCancelDao;
	private V2kartSapOrderCancelStrategy v2kartSapOrderCancelStrategy;
	private V2ImmediateCancelRequestExecutor v2ImmediateCancelRequestExecutor;
	private V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService;
	private V2RefundOrderProcessor v2RefundOrderProcessor;
	private OrderCancelRecordsHandler orderCancelRecordsHandler;
	/**
	 * V2CancelOrderEntryStockHandler bean injection
	 */
	private V2CancelOrderEntryStockHandler v2CancelOrderEntryStockHandler;

	@Override
	public PerformResult perform(final CronJobModel cronJobModel) {

		try {
			retriggerPendingCancelOrder();
		} catch (OrderCancelException e) {
			LOG.error("pending order Cancel request get Exception :" + e);
			return new PerformResult(CronJobResult.ERROR,
					CronJobStatus.FINISHED);
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	public void retriggerPendingCancelOrder() throws OrderCancelException {
		LOG.debug("**********Retriggering Pending Cancel Order START **************");
	
		final List<OrderModel> orders = getOrders();
		if (orders != null && !orders.isEmpty()) {
			for (final OrderModel order : orders) {
				OrderCancelRecordEntryModel cancelRequestRecordEntry = getPendingCancelRecordEntry(order);
				if (cancelRequestRecordEntry != null) {
					
					OrderHistoryEntryModel rderHistoryEntry = cancelRequestRecordEntry.getOriginalVersion();
					OrderModel previewOrder =rderHistoryEntry.getPreviousOrderVersion();
					LOG.debug("Pending Cancel Record Found In :"+ previewOrder.getCode());
					LOG.debug("Order [{" + order.getCode()
							+ "}] cancelRequestRecordEntry status is [{"+cancelRequestRecordEntry.getStatus()+"}]");
					sendOrderCancelDataToSAP(cancelRequestRecordEntry,previewOrder);
					LOG.debug("cancelled order Record triggering Done of order number["+previewOrder.getCode()+"]");
				}
			}
		}
		LOG.debug("**********Retriggering Pending Cancel Order END **************");
	}

	private void sendOrderCancelDataToSAP(
			final OrderCancelRecordEntryModel result,
			final OrderModel refundOrderPreview) {
		LOG.debug("Sending Failure cancellation Order to SAP...");
		boolean success = false;
		try {
			final SOModifyCancelRes soModifyCancelRes = v2SapInboundSaleOrderIntegrationService
					.cancelModifyErpSales(result);
			for (final SOModifyCancelRes.OrderModifyCancelRes orderModRes : soModifyCancelRes
					.getOrderModifyCancelRes()) {

				if (StringUtils.isNotEmpty(orderModRes.getRespCode())) {
					LOG.info("Order cancellation SAP Response:["+orderModRes.getRespCode()+"] -"
							+ orderModRes.getRespMsg());
					if ("S".equalsIgnoreCase(orderModRes.getRespCode())) {
						success = true;
					}
				}
			} 
			if (success) {
				v2RefundOrderProcessor.process(refundOrderPreview, result
						.getOrderModificationRefundInfo()
						.getAmountTobeRefunded());// TODO
				this.updateRecordEntry(result);
				LOG.debug("sending to releasing stock !!!!!!!!!!!");
				this.releaseStockAfterOrderCancelling(result);
				LOG.debug("released stock process done !!!!!!!!!!!");
			}
		} catch (final V2SapConnectionException sape) {
			LOG.error("Error in sending Order cancellation data to SAP", sape);
		}
	}
public void updateRecordEntry(final OrderCancelRecordEntryModel currentEntry) {
currentEntry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);
for (OrderEntryModificationRecordEntryModel orderEntryRecord : currentEntry.getOrderEntriesModificationEntries()) {
	OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntry=(OrderEntryCancelRecordEntryModel)orderEntryRecord;
	orderEntryCancelRecordEntry.setCancelledQuantity(orderEntryCancelRecordEntry.getCancelRequestQuantity());

	modelService.save(orderEntryRecord);
}
modelService.save(currentEntry);

OrderModificationRecordModel record = currentEntry
		.getModificationRecord();
record.setInProgress(false);
modelService.save(record);

}

	public List<OrderModel> getOrders() {
		final StringBuilder queryBuilder = new StringBuilder("SELECT {o.%s} "
				+ "FROM {%s AS o} ");
		final String formattedQuery = String.format(queryBuilder.toString(),
				OrderModel.PK, OrderModel._TYPECODE);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				formattedQuery);
		final SearchResult<OrderModel> result = this.getFlexibleSearchService()
				.search(query);
		if (LOG.isDebugEnabled()) {
			LOG.debug(query);
		StringBuilder allOrder= new StringBuilder();
			for (final OrderModel salesOrder : result.getResult()) {
				allOrder.append(salesOrder.getCode()).append(",");
				}
			LOG.debug(allOrder);
		}
		return result.getResult();
	}

	public Collection<OrderCancelRecordEntryModel> getOrderCancelRecordEntries(
			OrderModel order) {
		return orderCancelDao.getOrderCancelRecordEntries(order);
	}

	public OrderCancelRecordEntryModel getPendingCancelRecordEntry(
			OrderModel order) throws OrderCancelRecordsHandlerException {
		OrderCancelRecordModel orderCancelRecord = this.orderCancelDao
				.getOrderCancelRecord(order);
		if ((orderCancelRecord == null)
				|| (!(orderCancelRecord.isInProgress()))) {
			LOG.debug("Order[" + order.getCode()
					+ "]: cancel is not currently in progress");
		return null;
		}
		Collection entries = orderCancelRecord.getModificationRecordEntries();
		if ((entries == null) || (entries.isEmpty())) {
			LOG.debug("Order[" + order.getCode()
					+ "]: has no cancel records");
		return null;
		}
		OrderCancelRecordEntryModel currentCancelEntry = null;
		for (Iterator iter = entries.iterator(); iter.hasNext();) {
			OrderCancelRecordEntryModel entry = (OrderCancelRecordEntryModel) iter
					.next();
			if (!(entry.getStatus()
					.equals(OrderModificationEntryStatus.INPROGRESS)))
				continue;
			if (currentCancelEntry == null) {
				currentCancelEntry = entry;
			} else {
				throw new IllegalStateException(
						"Order["
								+ order.getCode()
								+ "]: cancel record has more than one entries with status 'IN PROGRESS'");
			}
		}

		return currentCancelEntry;
	}

	public void releaseStockAfterOrderCancelling(final OrderCancelRecordEntryModel currentEntry) {
		try {
			for (OrderEntryModificationRecordEntryModel orderEntryRecord : currentEntry.getOrderEntriesModificationEntries()) {
				OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntry=(OrderEntryCancelRecordEntryModel)orderEntryRecord;
						LOG.info("current canceled entry is :"+orderEntryRecord!=null?orderEntryRecord.getCode():null);
					if (orderEntryCancelRecordEntry != null) {
							OrderEntryModel orderEntryModel = orderEntryCancelRecordEntry.getOrderEntry();
							Set<StockLevelModel> stocks = orderEntryModel.getProduct().getStockLevels();
							LOG.debug("Releasing the product ["
									+ orderEntryModel.getProduct().getCode()
									+ "] stock qty ["+ orderEntryCancelRecordEntry.getCancelledQuantity() + "]");
						for(StockLevelModel stock :stocks){
							this.v2StockService.release(stock, (int) orderEntryCancelRecordEntry.getCancelledQuantity());
								LOG.debug("reserve stock release successfully");
							}
						}
					}
		} catch (InsufficientStockLevelException e) {
			LOG.error("Error in stock level");
			e.printStackTrace();
		}
	}

	public OrderCancelDao getOrderCancelDao() {
		return orderCancelDao;
	}

	public void setOrderCancelDao(OrderCancelDao orderCancelDao) {
		this.orderCancelDao = orderCancelDao;
	}

	public V2kartSapOrderCancelStrategy getV2kartSapOrderCancelStrategy() {
		return v2kartSapOrderCancelStrategy;
	}

	public void setV2kartSapOrderCancelStrategy(
			V2kartSapOrderCancelStrategy v2kartSapOrderCancelStrategy) {
		this.v2kartSapOrderCancelStrategy = v2kartSapOrderCancelStrategy;
	}

	public V2ImmediateCancelRequestExecutor getV2ImmediateCancelRequestExecutor() {
		return v2ImmediateCancelRequestExecutor;
	}

	public void setV2ImmediateCancelRequestExecutor(
			V2ImmediateCancelRequestExecutor v2ImmediateCancelRequestExecutor) {
		this.v2ImmediateCancelRequestExecutor = v2ImmediateCancelRequestExecutor;
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(
			FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	public V2SapInboundSaleOrderIntegrationService getV2SapInboundSaleOrderIntegrationService() {
		return v2SapInboundSaleOrderIntegrationService;
	}

	public void setV2SapInboundSaleOrderIntegrationService(
			V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService) {
		this.v2SapInboundSaleOrderIntegrationService = v2SapInboundSaleOrderIntegrationService;
	}

	public V2RefundOrderProcessor getV2RefundOrderProcessor() {
		return v2RefundOrderProcessor;
	}

	public void setV2RefundOrderProcessor(
			V2RefundOrderProcessor v2RefundOrderProcessor) {
		this.v2RefundOrderProcessor = v2RefundOrderProcessor;
	}

	public OrderCancelRecordsHandler getOrderCancelRecordsHandler() {
		return orderCancelRecordsHandler;
	}

	public void setOrderCancelRecordsHandler(
			OrderCancelRecordsHandler orderCancelRecordsHandler) {
		this.orderCancelRecordsHandler = orderCancelRecordsHandler;
	}

	public V2CancelOrderEntryStockHandler getV2CancelOrderEntryStockHandler() {
		return v2CancelOrderEntryStockHandler;
	}

	public void setV2CancelOrderEntryStockHandler(
			V2CancelOrderEntryStockHandler v2CancelOrderEntryStockHandler) {
		this.v2CancelOrderEntryStockHandler = v2CancelOrderEntryStockHandler;
	}

}
