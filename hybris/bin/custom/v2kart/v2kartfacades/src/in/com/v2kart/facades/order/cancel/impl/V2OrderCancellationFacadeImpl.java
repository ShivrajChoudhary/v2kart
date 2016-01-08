package in.com.v2kart.facades.order.cancel.impl;

import in.com.v2kart.core.services.V2RefundAmountService;
import in.com.v2kart.facades.cancel.data.CancelData;
import in.com.v2kart.facades.cancel.data.CancelEntryData;
import in.com.v2kart.facades.order.cancel.V2OrderCancellationFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.dao.OrderCancelDao;
import de.hybris.platform.ordercancel.exceptions.OrderCancelDaoException;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class V2OrderCancellationFacadeImpl implements V2OrderCancellationFacade {

	private UserService userService;
	private OrderCancelDao orderCancelDao;
	private ModelService modelService;
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private EnumerationService enumerationService;
	private OrderCancelService orderCancelService;
	private V2RefundAmountService v2RefundAmountService;
	private PriceDataFactory priceDataFactory;
	private Map<String, String> v2CancelReasons;	

	protected Map<String, String> getV2CancelReasons() {
		return v2CancelReasons;
	}
	@Required
	public void setV2CancelReasons(final Map<String, String> v2CancelReasons) {
		this.v2CancelReasons = v2CancelReasons;
	}

	public PriceDataFactory getPriceDataFactory() {
		return priceDataFactory;
	}

	public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
		this.priceDataFactory = priceDataFactory;
	}

	public V2RefundAmountService getV2RefundAmountService() {
		return v2RefundAmountService;
	}

	public void setV2RefundAmountService(
			V2RefundAmountService v2RefundAmountService) {
		this.v2RefundAmountService = v2RefundAmountService;
	}

	private static final Logger LOG = Logger
			.getLogger(V2OrderCancellationFacadeImpl.class);

	public OrderCancelService getOrderCancelService() {
		return orderCancelService;
	}

	public void setOrderCancelService(OrderCancelService orderCancelService) {
		this.orderCancelService = orderCancelService;
	}

	public EnumerationService getEnumerationService() {
		return enumerationService;
	}

	public void setEnumerationService(EnumerationService enumerationService) {
		this.enumerationService = enumerationService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CustomerAccountService getCustomerAccountService() {
		return customerAccountService;
	}

	public void setCustomerAccountService(
			CustomerAccountService customerAccountService) {
		this.customerAccountService = customerAccountService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
		return checkoutCustomerStrategy;
	}

	public void setCheckoutCustomerStrategy(
			CheckoutCustomerStrategy checkoutCustomerStrategy) {
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
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
	private OrderCancelRequest buildPartialCancellationRequest(
			final OrderModel order, List<OrderCancelEntry> cancelledEntries) {
		OrderCancelRequest orderCancelRequest = new OrderCancelRequest(order,
				cancelledEntries);
		orderCancelRequest.setCancelReason(cancelledEntries.get(
				cancelledEntries.size() - 1).getCancelReason());
		orderCancelRequest.setNotes(cancelledEntries.get(
				cancelledEntries.size() - 1).getNotes());
		return orderCancelRequest;
	}

	private OrderCancelRequest buildFullCancellationRequest(
			final OrderModel order, CancelData cancelData) {
		OrderCancelRequest orderCancelRequest = new OrderCancelRequest(order,getEnumerationService().getEnumerationValue(
                CancelReason.class, cancelData.getReason()),cancelData.getNote());
		return orderCancelRequest;
	}

	private OrderModel getOrderForCode(final String code) {
		final BaseStoreModel baseStoreModel = getBaseStoreService()
				.getCurrentBaseStore();
		final OrderModel orderModel = getCheckoutCustomerStrategy()
				.isAnonymousCheckout() ? getCustomerAccountService()
				.getOrderDetailsForGUID(code, baseStoreModel)
				: getCustomerAccountService().getOrderForCode(
						(CustomerModel) getUserService().getCurrentUser(),
						code, baseStoreModel);
		return orderModel;
	}

	private List<OrderCancelEntry> getEntriesToBeCancelled(
			List<CancelEntryData> cancelledEntries, OrderModel order) {
		List<AbstractOrderEntryModel> orderEntries = order.getEntries();
		List<OrderCancelEntry> orderCancelledEntries = new ArrayList<OrderCancelEntry>();
		for (CancelEntryData cancelEntryData : cancelledEntries) {
			for (AbstractOrderEntryModel entry : orderEntries) {
				if (entry.getEntryNumber().equals(
						cancelEntryData.getOrderEntry())) {
					CancelReason reason = getEnumerationService()
							.getEnumerationValue(CancelReason.class,
									cancelEntryData.getReason());
					if (cancelEntryData.getQuantity() == null) {
						cancelEntryData.setQuantity(0);
					}
					OrderCancelEntry orderCancelEntry = new OrderCancelEntry(
							entry, cancelEntryData.getQuantity(),
							cancelEntryData.getNote(), reason);
					orderCancelledEntries.add(orderCancelEntry);
				}
			}
		}
		return orderCancelledEntries;
	}

	@Override
	public OrderCancelEntryStatus cancelPartialOrder(CancelData cancelData) {
		final OrderModel order = getOrderForCode(cancelData.getOrderCode());
		OrderCancelRecordEntryModel orderRequestRecord = null;
		if(isCancelationValid(order)){
		try {
			if (cancelData.getCancelledEntries() != null
					&& cancelData.getCancelledEntries().size() != 0) {
				List<OrderCancelEntry> cancelledEntries = getEntriesToBeCancelled(
						cancelData.getCancelledEntries(), order);
				final OrderCancelRequest request = buildPartialCancellationRequest(
						order, cancelledEntries);
				if (request != null) {
					orderRequestRecord = orderCancelService.requestOrderCancel(
							request, userService.getCurrentUser());

					if (OrderCancelEntryStatus.DENIED.equals(orderRequestRecord
							.getCancelResult())) {
						final String orderCode = order.getCode();
						StringBuilder message = new StringBuilder();
						if (orderRequestRecord.getRefusedMessage() != null) {
							message.append(orderRequestRecord
									.getRefusedMessage());
						}
						if (orderRequestRecord.getFailedMessage() != null) {
							message.append(orderRequestRecord
									.getFailedMessage());
						}

						throw new OrderCancelException(orderCode,
								message.toString());
					}

				}
			}

		} catch (final OrderCancelException ocEx) {
			LOG.warn("Failed to cancel [" + order.getCode() + "]", ocEx);
		} catch (final Exception e) {
			LOG.warn("Failed to cancel [" + order.getCode() + "]", e);
		}
		}
		return (orderRequestRecord != null) ? orderRequestRecord
				.getCancelResult() : null;
	}
	

	@Override
	public OrderCancelEntryStatus cancelFullOrder(CancelData cancelData) {
		final OrderModel order = getOrderForCode(cancelData.getOrderCode());
		OrderCancelRecordEntryModel orderRequestRecord = null;
		if(isCancelationValid(order)){
		try {
			final OrderCancelRequest request = buildFullCancellationRequest(
					order, cancelData);
			if (request != null) {
				orderRequestRecord = orderCancelService.requestOrderCancel(
						request, userService.getCurrentUser());

				if (OrderCancelEntryStatus.DENIED.equals(orderRequestRecord
						.getCancelResult())) {
					final String orderCode = order.getCode();
					StringBuilder message = new StringBuilder();
					if (orderRequestRecord.getRefusedMessage() != null) {
						message.append(orderRequestRecord.getRefusedMessage());
					}
					if (orderRequestRecord.getFailedMessage() != null) {
						message.append(orderRequestRecord.getFailedMessage());
					}
					throw new OrderCancelException(orderCode,
							message.toString());
				}

			}

		} catch (final OrderCancelException ocEx) {
			LOG.warn("Failed to cancel [" + order.getCode() + "]", ocEx);
		} catch (final Exception e) {
			LOG.warn("Failed to cancel [" + order.getCode() + "]", e);
		}
		}
		return (orderRequestRecord != null) ? orderRequestRecord
				.getCancelResult() : null;
	}

	@Override
	public PriceData getRefundAmount(CancelData cancelData) {
		Double refund = 0d;
		final OrderModel order = getOrderForCode(cancelData.getOrderCode());
		if (cancelData.getIsFull()) {
			refund = getV2RefundAmountService().getRefundAmount(null, order,
					cancelData.getIsFull());
		} else {
			if (cancelData.getCancelledEntries() != null
					&& cancelData.getCancelledEntries().size() != 0) {
				List<OrderCancelEntry> cancelledEntries = getEntriesToBeCancelled(
						cancelData.getCancelledEntries(), order);
				refund = getV2RefundAmountService().getRefundAmount(
						cancelledEntries, order, cancelData.getIsFull());
			}
		}
		return getPriceDataFactory().create(PriceDataType.BUY,
				BigDecimal.valueOf(refund), order.getCurrency());
	}

	@Override
	public Map<String, String> filterCancelReasons(
			Map<String, String> cancelReasonMap) {
		 Map<String, String> cancelReasons = new TreeMap<String, String>();
		 
		 for (Map.Entry<String, String> entry : cancelReasonMap.entrySet())
		 {
		    if(getV2CancelReasons().containsKey(entry.getKey())){
		    	cancelReasons.put(entry.getKey(),getV2CancelReasons().get(entry.getKey()));
		    }
		    
		 }		return cancelReasons;
	}
		
	@Override
		public Boolean isCancelationValid(OrderModel order) throws OrderCancelDaoException {
			OrderCancelRecordModel cancelRecord = this.orderCancelDao
					.getOrderCancelRecord(order);
			if (cancelRecord == null) {
			cancelRecord = createCancelRecord(order);
			}
			return (!cancelRecord.isInProgress());
		}
		
	protected OrderCancelRecordModel createCancelRecord(OrderModel order) {
			OrderCancelRecordModel cancelRecord = (OrderCancelRecordModel) getModelService()
					.create(OrderCancelRecordModel.class);
			cancelRecord.setOrder(order);
			cancelRecord.setOwner(order);
			cancelRecord.setInProgress(false);
			getModelService().save(cancelRecord);
			return cancelRecord;
		}
}
