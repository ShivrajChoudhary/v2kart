/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.data.response;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vikrant2480
 * 
 */
public class Shipment {

    @JsonProperty("Origin")
    private String origin;

    @JsonProperty("Status")
    private StatusDetail status;

    @JsonProperty("PickUpDate")
    private Date pickUpDate;

    @JsonProperty("OrderType")
    private String orderType;

    @JsonProperty("Destination")
    private String destination;

    @JsonProperty("Consignee")
    private ConsigneeDetails consignee;

    @JsonProperty("ReferenceNo")
    private String referenceNumber;

    @JsonProperty("ReturnedDate")
    private Date returnedDate;

    @JsonProperty("DestRecieveDate")
    private Date destinationRecievedDate;

    @JsonProperty("OriginRecieveDate")
    private Date originRecievedDate;

    @JsonProperty("OutDestinationDate")
    private Date outDestinationDate;

    @JsonProperty("CODAmount")
    private int codAmount;

    @JsonProperty("FirstAttemptDate")
    private Date firstAttemptDate;

    @JsonProperty("ReverseInTransit")
    private Boolean reverseInTransit;

    @JsonProperty("Scans")
    private Scans[] scans;

    @JsonProperty("SenderName")
    private String senderName;

    @JsonProperty("AWB")
    private String awb;

    @JsonProperty("DispatchCount")
    private Integer dispatchCount;

    @JsonProperty("InvoiceAmount")
    private Double invoiceAmount;

    /**
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin
     *        the origin to set
     */
    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    /**
     * @return the status
     */
    public StatusDetail getStatus() {
        return status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(final StatusDetail status) {
        this.status = status;
    }

    /**
     * @return the pickUpDate
     */
    public Date getPickUpDate() {
        return pickUpDate;
    }

    /**
     * @param pickUpDate
     *        the pickUpDate to set
     */
    public void setPickUpDate(final Date pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    /**
     * @return the orderType
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * @param orderType
     *        the orderType to set
     */
    public void setOrderType(final String orderType) {
        this.orderType = orderType;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination
     *        the destination to set
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }

    /**
     * @return the consignee
     */
    public ConsigneeDetails getConsignee() {
        return consignee;
    }

    /**
     * @param consignee
     *        the consignee to set
     */
    public void setConsignee(final ConsigneeDetails consignee) {
        this.consignee = consignee;
    }

    /**
     * @return the referenceNumber
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber
     *        the referenceNumber to set
     */
    public void setReferenceNumber(final String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return the returnedDate
     */
    public Date getReturnedDate() {
        return returnedDate;
    }

    /**
     * @param returnedDate
     *        the returnedDate to set
     */
    public void setReturnedDate(final Date returnedDate) {
        this.returnedDate = returnedDate;
    }

    /**
     * @return the destinationRecievedDate
     */
    public Date getDestinationRecievedDate() {
        return destinationRecievedDate;
    }

    /**
     * @param destinationRecievedDate
     *        the destinationRecievedDate to set
     */
    public void setDestinationRecievedDate(final Date destinationRecievedDate) {
        this.destinationRecievedDate = destinationRecievedDate;
    }

    /**
     * @return the originRecievedDate
     */
    public Date getOriginRecievedDate() {
        return originRecievedDate;
    }

    /**
     * @param originRecievedDate
     *        the originRecievedDate to set
     */
    public void setOriginRecievedDate(final Date originRecievedDate) {
        this.originRecievedDate = originRecievedDate;
    }

    /**
     * @return the outDestinationDate
     */
    public Date getOutDestinationDate() {
        return outDestinationDate;
    }

    /**
     * @param outDestinationDate
     *        the outDestinationDate to set
     */
    public void setOutDestinationDate(final Date outDestinationDate) {
        this.outDestinationDate = outDestinationDate;
    }

    /**
     * @return the codAmount
     */
    public int getCodAmount() {
        return codAmount;
    }

    /**
     * @param codAmount
     *        the codAmount to set
     */
    public void setCodAmount(final int codAmount) {
        this.codAmount = codAmount;
    }

    /**
     * @return the firstAttemptDate
     */
    public Date getFirstAttemptDate() {
        return firstAttemptDate;
    }

    /**
     * @param firstAttemptDate
     *        the firstAttemptDate to set
     */
    public void setFirstAttemptDate(final Date firstAttemptDate) {
        this.firstAttemptDate = firstAttemptDate;
    }

    /**
     * @return the reverseInTransit
     */
    public Boolean getReverseInTransit() {
        return reverseInTransit;
    }

    /**
     * @param reverseInTransit
     *        the reverseInTransit to set
     */
    public void setReverseInTransit(final Boolean reverseInTransit) {
        this.reverseInTransit = reverseInTransit;
    }

    /**
     * @return the scans
     */
    public Scans[] getScans() {
        return scans;
    }

    /**
     * @param scans
     *        the scans to set
     */
    public void setScans(final Scans[] scans) {
        this.scans = scans;
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName
     *        the senderName to set
     */
    public void setSenderName(final String senderName) {
        this.senderName = senderName;
    }

    /**
     * @return the awb
     */
    public String getAwb() {
        return awb;
    }

    /**
     * @param awb
     *        the awb to set
     */
    public void setAwb(final String awb) {
        this.awb = awb;
    }

    /**
     * @return the dispatchCount
     */
    public Integer getDispatchCount() {
        return dispatchCount;
    }

    /**
     * @param dispatchCount
     *        the dispatchCount to set
     */
    public void setDispatchCount(final Integer dispatchCount) {
        this.dispatchCount = dispatchCount;
    }

    /**
     * @return the invoiceAmount
     */
    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * @param invoiceAmount
     *        the invoiceAmount to set
     */
    public void setInvoiceAmount(final Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

}
