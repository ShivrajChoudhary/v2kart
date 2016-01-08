package in.com.v2kart.core.cronjob;

import in.com.v2kart.core.constants.GeneratedV2kartCoreConstants.Attributes.V2StoreCreditPaymentInfo;
import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * <V2OrderReportJob> fetches all orders which are Available in hybris
 * 
 * @author Shivraj
 * @Version 1.0
 * 
 */
public class V2OrderReportJob extends AbstractJobPerformable<CronJobModel> {

    /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(V2OrderReportJob.class);
    private FlexibleSearchService flexibleSearchService;
    private String fileDir;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
	try {
	    createExcel();
	} catch (Exception ex) {
	    System.out.println(ex);
	    System.out.println("Your Order excel file has Not been generated!");
	    return new PerformResult(CronJobResult.ERROR,
		    CronJobStatus.FINISHED);

	}
	return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    void createExcel() throws Exception {

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");// dd/MM/yyyy
	Date date = new Date();
	String fileWithDate = sdf.format(date);
	String filename = fileDir + "AllOrdersReport_" + fileWithDate + ".xls";
	final List<OrderModel> orders = findAllOrders();
	HSSFWorkbook wb = new HSSFWorkbook();
	Integer rowNumber = 1;
	Integer sheetNumber = 0;
	Integer count = new Integer(0);
	Integer sno = new Integer(0);

	if (orders != null && !orders.isEmpty()) {
	    LOG.info("Total Order [" + orders.size() + "]");
	    HSSFSheet sheet = wb.createSheet("Sheet-" + sheetNumber);
	    HSSFRow rowhead = sheet.createRow(0);
	    // Order Details
	    rowhead.createCell(0).setCellValue("S No");
	    rowhead.createCell(1).setCellValue("ORDER NO");
	    rowhead.createCell(2).setCellValue("ORDER DATE");
	    rowhead.createCell(3).setCellValue("ORDER TIME");
	    rowhead.createCell(4).setCellValue("NO OF ITEMS");
	    rowhead.createCell(5).setCellValue("ORDER TOTAL QTY.");
	    rowhead.createCell(6).setCellValue("ORDER Val");
	    rowhead.createCell(7).setCellValue("ORDER STATUS");
	    rowhead.createCell(8).setCellValue("SALES CHANNEL");
	    // Customer Details
	    rowhead.createCell(9).setCellValue("SAP CUSTOMER CODE");
	    rowhead.createCell(10).setCellValue("CUSTOMER ID");
	    rowhead.createCell(11).setCellValue("CUSTOMER NAME");
	    rowhead.createCell(12).setCellValue("MOBILE NO");
	    // PROMOTIONS&VOUCHER details
	    rowhead.createCell(13).setCellValue("VOUCHER REDEEMED");
	    rowhead.createCell(14).setCellValue("PROMOTIONS");
	    // PAYMENT DETAILS
	    rowhead.createCell(15).setCellValue("MOP");
	    rowhead.createCell(16).setCellValue("TRANSACTION ID");
	    rowhead.createCell(17).setCellValue("PG NAME");
	    rowhead.createCell(18).setCellValue("V2 MONEY REDEEMED");
	    rowhead.createCell(19).setCellValue("TOTAL DISCOUNT VAL");
	    rowhead.createCell(20).setCellValue("COD CHG");
	    rowhead.createCell(21).setCellValue("GIFT WRAP CHG");
	    rowhead.createCell(22).setCellValue("SHIPPING CHG");
	    // DELIVERY DETAILS
	    rowhead.createCell(23).setCellValue("CITY");
	    rowhead.createCell(24).setCellValue("STATE");
	    rowhead.createCell(25).setCellValue("DELIVERY MODE");
	    rowhead.createCell(26).setCellValue("DELIVERY ESTIMATION TIME ");
	    // CONSIGNMENT DETAILS
	    rowhead.createCell(27).setCellValue("CONSIGNMENT DATE");
	    rowhead.createCell(28).setCellValue("LSP/DEALER NAME");
	    rowhead.createCell(29).setCellValue("AWB NO");
	    rowhead.createCell(30).setCellValue("CONSIGNMENT STATUS");
	    rowhead.createCell(31).setCellValue("STATUS DATE");
	    // RETURN ORDER DETAILS
	    rowhead.createCell(32).setCellValue("CUSTOMER RETURN DATE");
	    rowhead.createCell(33).setCellValue("CUSTOMER RETURN TIME");
	    rowhead.createCell(34).setCellValue("CUSTOMER RETURN NUMBER ");
	    rowhead.createCell(35).setCellValue("CUSTOMER RETURN QTY");
	    rowhead.createCell(36).setCellValue("CUSTOMER RETURN VALUE");
	    rowhead.createCell(37).setCellValue("CUSTOMER RETURN REASON");
	    // CANCELLATION DETAILS
	    rowhead.createCell(38).setCellValue("CANCELLATION DATE");
	    rowhead.createCell(39).setCellValue("CANCELLATION TIME");
	    rowhead.createCell(40).setCellValue("CANCELLATION TYPE");
	    rowhead.createCell(41).setCellValue("CANCELLATION REQUEST NO");
	    rowhead.createCell(42).setCellValue("QTY");
	    rowhead.createCell(43).setCellValue("AMT");
	    rowhead.createCell(44).setCellValue("CANCELLATION REASON");
	    rowhead.createCell(45).setCellValue("REVISED ORDER DETAILS");
	    for (final OrderModel orderModel : orders) {
		if (LOG.isDebugEnabled()) {
		    LOG.debug("\t [" + sno + "] [" + orderModel.getCode()
			    + " | " + orderModel.getUser() != null ? orderModel
			    .getUser().getUid() : '-' + " | "
			    + orderModel.getTotalPrice() + " | "
			    + orderModel.getEntries() != null ? orderModel
			    .getEntries().size() : 0 + " | "
			    + orderModel.getStatus() + "|"
			    + orderModel.getDeliveryMode() != null ? orderModel
			    .getDeliveryMode().getDescription() : '-' + "]");
		}

		if (count == 30000) {
		    LOG.info("Sheet " + sheetNumber
			    + " written; moving onto to sheet "
			    + (sheetNumber + 1));
		    sheetNumber++;
		    wb.createSheet("Sheet-" + sheetNumber);
		    rowNumber = 1;
		    sheet = wb.getSheetAt(sheetNumber);
		    rowhead = sheet.createRow(0);
		    // Order Details
		    rowhead.createCell(0).setCellValue("S No");
		    rowhead.createCell(1).setCellValue("ORDER NO");
		    rowhead.createCell(2).setCellValue("ORDER DATE");
		    rowhead.createCell(3).setCellValue("ORDER TIME");
		    rowhead.createCell(4).setCellValue("NO OF ITEMS");
		    rowhead.createCell(5).setCellValue("ORDER TOTAL QTY.");
		    rowhead.createCell(6).setCellValue("ORDER Val");
		    rowhead.createCell(7).setCellValue("ORDER STATUS");
		    rowhead.createCell(8).setCellValue("SALES CHANNEL");
		    // Customer Details
		    rowhead.createCell(9).setCellValue("SAP CUSTOMER CODE");
		    rowhead.createCell(10).setCellValue("CUSTOMER ID");
		    rowhead.createCell(11).setCellValue("CUSTOMER NAME");
		    rowhead.createCell(12).setCellValue("MOBILE NO");
		    // PROMOTIONS&VOUCHER details
		    rowhead.createCell(13).setCellValue("VOUCHER REDEEMED");
		    rowhead.createCell(14).setCellValue("PROMOTIONS");
		    // PAYMENT DETAILS
		    rowhead.createCell(15).setCellValue("MOP");
		    rowhead.createCell(16).setCellValue("TRANSACTION ID");
		    rowhead.createCell(17).setCellValue("PG NAME");
		    rowhead.createCell(18).setCellValue("V2 MONEY REDEEMED");
		    rowhead.createCell(19).setCellValue("TOTAL DISCOUNT VAL");
		    rowhead.createCell(20).setCellValue("COD CHG");
		    rowhead.createCell(21).setCellValue("GIFT WRAP CHG");
		    rowhead.createCell(22).setCellValue("SHIPPING CHG");
		    // DELIVERY DETAILS
		    rowhead.createCell(23).setCellValue("CITY");
		    rowhead.createCell(24).setCellValue("STATE");
		    rowhead.createCell(25).setCellValue("DELIVERY MODE");
		    rowhead.createCell(26).setCellValue(
			    "DELIVERY ESTIMATION TIME ");
		    // CONSIGNMENT DETAILS
		    rowhead.createCell(27).setCellValue("CONSIGNMENT DATE");
		    rowhead.createCell(28).setCellValue("LSP/DEALER NAME");
		    rowhead.createCell(29).setCellValue("AWB NO");
		    rowhead.createCell(30).setCellValue("CONSIGNMENT STATUS");
		    rowhead.createCell(31).setCellValue("STATUS DATE");
		    // RETURN ORDER DETAILS
		    rowhead.createCell(32).setCellValue("CUSTOMER RETURN DATE");
		    rowhead.createCell(33).setCellValue("CUSTOMER RETURN TIME");
		    rowhead.createCell(34).setCellValue(
			    "CUSTOMER RETURN NUMBER ");
		    rowhead.createCell(35).setCellValue("CUSTOMER RETURN QTY");
		    rowhead.createCell(36)
			    .setCellValue("CUSTOMER RETURN VALUE");
		    rowhead.createCell(37).setCellValue(
			    "CUSTOMER RETURN REASON");
		    // CANCELLATION DETAILS
		    rowhead.createCell(38).setCellValue("CANCELLATION DATE");
		    rowhead.createCell(39).setCellValue("CANCELLATION TIME");
		    rowhead.createCell(40).setCellValue("CANCELLATION TYPE");
		    rowhead.createCell(41).setCellValue(
			    "CANCELLATION REQUEST NO");
		    rowhead.createCell(42).setCellValue("QTY");
		    rowhead.createCell(43).setCellValue("AMT");
		    rowhead.createCell(44).setCellValue("CANCELLATION REASON");
		    rowhead.createCell(45)
			    .setCellValue("REVISED ORDER DETAILS");
		    count = 0;
		}

		HSSFRow row = sheet.createRow(rowNumber);
		// ---------------------------START-------------------------------//

		// Order Details
		row.createCell(0).setCellValue(++sno);
		getOrderDetails(row, orderModel);
		// customer Detail
		getCustomerInfo(row, orderModel);
		// set payment Info
		// customer Detail
		if (orderModel.getAllPromotionResults() != null
			&& orderModel.getAllPromotionResults().size() > 0) {
		    getPromotionAndVoucherInfo(row, orderModel);
		}
		// set payment Info
		getPaymentInfo(row, orderModel);
		// set delivery Details
		getDeliveryInfo(row, orderModel);
		// Set Consignment Detail of Order
		if (orderModel.getConsignments() != null
			&& orderModel.getConsignments().size() > 0) {
		    getConsignmentDetails(row, orderModel);
		}
		// Set order return Details
		if (orderModel.getReturnRequests() != null
			&& orderModel.getReturnRequests().size() > 0) {
		    getReturnOrderDetails(row, orderModel);
		}
		// Set order cancellation Details
		if (orderModel.getModificationRecords() != null
			&& orderModel.getModificationRecords().size() > 0) {
		    getCancellationInfo(row, orderModel);
		}
		// ------------------------------END----------------------------//
		rowNumber++;
		count++;
	    }
	    FileOutputStream fileOut = new FileOutputStream(filename);
	    wb.write(fileOut);
	    fileOut.close();
	    LOG.info("Your Order excel file has been generated!");
	}
    }

    public List<OrderModel> findAllOrders() {
	final StringBuilder queryBuilder = new StringBuilder("SELECT {o.%s} "
		+ "FROM {%s AS o} ");
	final String formattedQuery = String.format(queryBuilder.toString(),
		OrderModel.PK, OrderModel._TYPECODE);
	final FlexibleSearchQuery query = new FlexibleSearchQuery(
		formattedQuery);
	final SearchResult<OrderModel> result = this.getFlexibleSearchService()
		.search(query);
	return result.getResult();
    }

    public String getTimeStamp(Date date) {
	if (date != null) {
	    DateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
	    String strDate = sdfDate.format(date);
	    return strDate;
	}
	return null;
    }

    public String getDateStamp(Date date) {
	if (date != null) {
	    DateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");// dd-MM-yyyy
	    String strDate = sdfDate.format(date);
	    return strDate;
	}
	return null;
    }

    public void getOrderDetails(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------ORDER DETAILS Start OF [" + orderModel.getCode()
		+ "]----------------------------------");
	row.createCell(1).setCellValue(orderModel.getCode());
	row.createCell(2).setCellValue(
		orderModel.getCreationtime() != null ? getDateStamp(orderModel
			.getCreationtime()) : "-");
	row.createCell(3).setCellValue(
		orderModel.getCreationtime() != null ? getTimeStamp(orderModel
			.getCreationtime()) : "-");
	// no of items
	row.createCell(4).setCellValue(
		orderModel.getEntries() != null ? orderModel.getEntries()
			.size() : '-');
	int totalQty = 0;
	for (AbstractOrderEntryModel orderEntry : orderModel.getEntries()) {
	    totalQty += orderEntry.getQuantity();
	}
	row.createCell(5).setCellValue(totalQty);
	row.createCell(6).setCellValue(
		orderModel.getTotalPrice() != null ? orderModel.getTotalPrice()
			: '-');
	row.createCell(7).setCellValue(
		orderModel.getStatus() != null ? orderModel.getStatus()
			.toString() : "-");
	row.createCell(8).setCellValue(
		orderModel.getSalesApplication() != null ? orderModel
			.getSalesApplication().getCode() : "-");
	LOG.debug("------------------------------------------ORDER DETAILS END----------------------------------");
    }

    public void getCustomerInfo(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------------------------------------CUSTOMER DETAILS START----------------------------------");
	if (orderModel.getUser() instanceof CustomerModel) {
	    CustomerModel customer = (CustomerModel) orderModel.getUser();
	    if (customer != null) {
		row.createCell(9).setCellValue(
			customer.getSapCustomerId() != null ? customer
				.getSapCustomerId() : "-");
		row.createCell(10).setCellValue(
			customer.getUid() != null ? customer.getUid() : "");
		row.createCell(11).setCellValue(
			customer.getDisplayName() != null ? customer
				.getDisplayName() : "");
		row.createCell(12).setCellValue(
			customer.getMobileNumber() != null ? customer
				.getMobileNumber() : "");
	    }
	}
	LOG.debug("------------------------------------------CUSTOMER DETAILS END----------------------------------");
    }

    public void getPromotionAndVoucherInfo(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------------------------------------PROMOTIONS AND VOUCHER DETAILS START----------------------------------");
	Set<PromotionResultModel> results = orderModel.getAllPromotionResults();
	if (results != null && results.isEmpty()) {
	    for (PromotionResultModel promotion : results) {
		row.createCell(13).setCellValue(
			promotion.getPromotion() != null ? promotion
				.getPromotion().getTitle() : "--");
		row.createCell(14).setCellValue("-");
	    }

	}
	LOG.debug("------------------------------------------PROMOTIONS AND VOUCHER DETAILS END----------------------------------");
    }

    public void getPaymentInfo(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------------------------------------Payment Info Start----------------------------------");
	if (orderModel.getPaymentInfo() != null) {
	    PaymentInfoModel paymentInfo = orderModel.getPaymentInfo();
	    if (paymentInfo instanceof V2PGPaymentInfoModel) {
		V2PGPaymentInfoModel pg = (V2PGPaymentInfoModel) paymentInfo;
		row.createCell(15).setCellValue(pg.getMode());
		row.createCell(16).setCellValue(pg.getTxnid());

	    } else {
		row.createCell(15).setCellValue("COD");
		row.createCell(16).setCellValue("-");
	    }
	    row.createCell(17).setCellValue(paymentInfo.getPaymentGateway());
	}

	V2StoreCreditPaymentInfoModel storecredit = (V2StoreCreditPaymentInfoModel) orderModel
		.getStoreCreditPaymentInfo();
	if (storecredit != null) {
	    row.createCell(18)
		    .setCellValue(
			    storecredit.getStoreCreditAmount() != null ? storecredit
				    .getStoreCreditAmount()
				    .doubleValue() : '-');
	}
	row.createCell(19).setCellValue(
		orderModel.getTotalDiscounts() != null ? orderModel
			.getTotalDiscounts() : '0');
	row.createCell(20).setCellValue(
		orderModel.getCodCharges() != null ? orderModel.getCodCharges()
			: '0');
	row.createCell(21).setCellValue(
		orderModel.getTotalGiftWrapPrice() != null ? orderModel
			.getTotalGiftWrapPrice() : '0');
	row.createCell(22).setCellValue(
		orderModel.getDeliveryCost() != null ? orderModel
			.getDeliveryCost() : '0');
	LOG.debug("------------------------------------------Payment Info End----------------------------------");
    }

    public void getDeliveryInfo(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------------------------------------Delhivery Info Start----------------------------------");
	if (orderModel.getDeliveryAddress() != null) {
	    row.createCell(23)
		    .setCellValue(
			    orderModel.getDeliveryAddress().getTown() != null ? orderModel
				    .getDeliveryAddress().getTown() : "-");
	    row.createCell(24)
		    .setCellValue(
			    orderModel.getDeliveryAddress().getRegion() != null ? orderModel
				    .getDeliveryAddress().getRegion().getName()
				    : "-");
	}
	if (orderModel.getDeliveryMode() != null) {
	    row.createCell(25).setCellValue(
		    orderModel.getDeliveryMode().getCode() != null ? orderModel
			    .getDeliveryMode().getCode() : "-");
	    row.createCell(26)
		    .setCellValue(
			    orderModel.getDeliveryMode().getDescription() != null ? orderModel
				    .getDeliveryMode().getDescription() : "-");
	}
	LOG.debug("------------------------------------------Delhivery Info END----------------------------------");
    }

    public void getConsignmentDetails(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------------------------------------Consignment Info Start----------------------------------");
	if (orderModel.getConsignments() != null
		&& orderModel.getConsignments().size() > 0) {
	    for (ConsignmentModel consignment : orderModel.getConsignments()) {
		row.createCell(27)
			.setCellValue(
				consignment.getShippingDate() != null ? getDateStamp(consignment
					.getShippingDate()) : "-");
		row.createCell(28).setCellValue(
			consignment.getCarrier() != null ? consignment
				.getCarrier() : "-");
		row.createCell(29).setCellValue(
			consignment.getTrackingID() != null ? consignment
				.getTrackingID() : "-");
		row.createCell(30).setCellValue(
			consignment.getStatus() != null ? consignment
				.getStatus().toString() : "-");
		row.createCell(31)
			.setCellValue(
				consignment.getCompletionDate() != null ? getDateStamp(consignment
					.getCompletionDate()) : "-");
	    }
	}
	LOG.debug("------------------------------------------Consignment Info End----------------------------------");
    }

    public void getReturnOrderDetails(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------------------------------------Return Order Info Start----------------------------------");
	int qty = 0;
	double value = 0;
	StringBuilder reasons = new StringBuilder();
	StringBuilder returnNumber = new StringBuilder();
	double basePrice = 0d;
	if (orderModel.getReturnRequests() != null
		&& orderModel.getReturnRequests().size() > 0) {
	    List<ReturnRequestModel> returnRequests = orderModel
		    .getReturnRequests();
	    for (ReturnRequestModel returnRequest : returnRequests) {
		returnNumber.append(returnRequest.getCode());
		returnNumber.append(",");
		row.createCell(32)
			.setCellValue(
				returnRequest.getCreationtime() != null ? getDateStamp(returnRequest
					.getCreationtime()) : "-");
		row.createCell(33)
			.setCellValue(
				returnRequest.getCreationtime() != null ? getTimeStamp(returnRequest
					.getCreationtime()) : "-");
		if (returnRequest.getReturnEntries() != null) {
		    for (ReturnEntryModel returnEntry : returnRequest
			    .getReturnEntries()) {
			if (returnEntry instanceof RefundEntryModel) {
			    RefundEntryModel refundEntryModel = (RefundEntryModel) returnEntry;
			    qty += (refundEntryModel.getExpectedQuantity() != null ? refundEntryModel
				    .getExpectedQuantity() : 0);
			    reasons.append(refundEntryModel.getReason() != null ? refundEntryModel
				    .getReason().toString() : "---");
			    reasons.append(",");
			    OrderEntryModel remainOrderEntry = (OrderEntryModel) refundEntryModel
				    .getOrderEntry();
			    basePrice = remainOrderEntry.getBasePrice();
			    // double
			    // bd=Double.parseDouble(refundEntryModel.getAmount()!=null?refundEntryModel.getAmount().toString():"0");
			    value += basePrice
				    * (refundEntryModel.getExpectedQuantity() != null ? refundEntryModel
					    .getExpectedQuantity() : 1);
			}
		    }
		}
	    }
	    row.createCell(34).setCellValue(returnNumber.toString());
	    row.createCell(35).setCellValue(qty);
	    row.createCell(36).setCellValue(value);
	    row.createCell(37).setCellValue(reasons.toString());
	}
	LOG.debug("------------------------------------------Return Order Info End----------------------------------");
    }

    public void getCancellationInfo(HSSFRow row, OrderModel orderModel) {
	LOG.debug("------------------------------------------Cancellation Info Start----------------------------------");
	int qty = 0;
	double refundAmount = 0d;
	String cancelType = new String();
	String cancelReason = new String();
	StringBuilder cancelRequestNos = new StringBuilder();
	StringBuilder revisedOrderEntries = new StringBuilder();

	if (orderModel.getModificationRecords() != null) {
	    for (OrderModificationRecordModel modifiedRecord : orderModel
		    .getModificationRecords()) {
		if (modifiedRecord instanceof OrderCancelRecordModel) {
		    OrderCancelRecordModel orderCancelRecord = (OrderCancelRecordModel) modifiedRecord;
		    cancelRequestNos.append(orderCancelRecord.getIdentifier());
		    cancelRequestNos.append(",");
		    row.createCell(38)
			    .setCellValue(
				    orderCancelRecord.getCreationtime() != null ? getDateStamp(orderCancelRecord
					    .getCreationtime()) : "-");
		    row.createCell(39)
			    .setCellValue(
				    orderCancelRecord.getCreationtime() != null ? getTimeStamp(orderCancelRecord
					    .getCreationtime()) : "-");
		    for (OrderModificationRecordEntryModel orderModificationRecordEntry : orderCancelRecord
			    .getModificationRecordEntries()) {
			if (orderModificationRecordEntry instanceof OrderCancelRecordEntryModel) {
			    OrderCancelRecordEntryModel orderCancelRecordEntryModel = (OrderCancelRecordEntryModel) orderModificationRecordEntry;
			    // for cancel type
			    cancelType = orderCancelRecordEntryModel
				    .getCancelResult() != null ? orderCancelRecordEntryModel
				    .getCancelResult().getCode() : "---";
			    // for cancel reason
			    cancelReason = orderCancelRecordEntryModel
				    .getCancelReason() != null ? orderCancelRecordEntryModel
				    .getCancelReason().getCode() : "---";
			    // for cancel qty
			    for (OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntryModel : orderCancelRecordEntryModel
				    .getOrderEntriesModificationEntries()) {
				if (orderEntryModificationRecordEntryModel instanceof OrderEntryCancelRecordEntryModel) {
				    OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntryModel = (OrderEntryCancelRecordEntryModel) orderEntryModificationRecordEntryModel;
				    qty += orderEntryCancelRecordEntryModel
					    .getCancelRequestQuantity() != null ? orderEntryCancelRecordEntryModel
					    .getCancelRequestQuantity() : 0;
				    // revised order details
				    OrderEntryModel orderEntryModel = orderEntryCancelRecordEntryModel
					    .getOrderEntry();
				    revisedOrderEntries
					    .append("|EntryNo=")
					    .append(orderEntryModel
						    .getProduct().getCode())
					    .append("| qty=")
					    .append(orderEntryModel
						    .getQuantity())
					    .append("| amount=")
					    .append(orderEntryModel
						    .getTotalPrice());
				    revisedOrderEntries.append("|");
				}
			    }
			    // for refund amount
			    V2OrderModificationRefundInfoModel v2OrderModificationRefundInfoModel = orderCancelRecordEntryModel
				    .getOrderModificationRefundInfo();
			    double refund = Double
				    .parseDouble(v2OrderModificationRefundInfoModel
					    .getAmountTobeRefunded() != null ? v2OrderModificationRefundInfoModel
					    .getAmountTobeRefunded().toString()
					    : "0");
			    refundAmount += refund;
			}
		    }
		}
	    }
	    row.createCell(40).setCellValue(cancelType);
	    row.createCell(41).setCellValue(cancelRequestNos.toString());
	    row.createCell(42).setCellValue(qty);
	    row.createCell(43).setCellValue(refundAmount);
	    row.createCell(44).setCellValue(cancelReason.toString());
	    row.createCell(45).setCellValue(revisedOrderEntries.toString());
	}
	LOG.debug("------------------------------------------Cancellation Info End----------------------------------");
    }

    public String getFileDir() {
	return fileDir;
    }

    public void setFileDir(String fileDir) {
	this.fileDir = fileDir;
    }

    public FlexibleSearchService getFlexibleSearchService() {
	return flexibleSearchService;
    }

    public void setFlexibleSearchService(
	    FlexibleSearchService flexibleSearchService) {
	this.flexibleSearchService = flexibleSearchService;
    }
}
