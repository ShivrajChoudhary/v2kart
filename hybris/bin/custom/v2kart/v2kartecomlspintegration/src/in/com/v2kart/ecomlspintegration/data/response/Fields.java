/**
 * 
 */
package in.com.v2kart.ecomlspintegration.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author arunkumar
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "awbNumber", "orderId", "actualWeight", "origin", "destination", "locationName", "currentLocationName",
        "customer", "consignee", "pickupdate", "status", "reasonCode", "reasonCodeNumber", "reasonCodeDescription", "receiver",
        "expectedDate", "lastUpdateDate", "deliveryDate", "refAwb", "rtsShipment" })
public class Fields {

    @XmlElement(name = "awb_number")
    private long awbNumber;
    @XmlElement(name = "orderid")
    private String orderId;
    @XmlElement(name = "actual_weight")
    private double actualWeight;
    @XmlElement(name = "origin")
    private String origin;
    @XmlElement(name = "destination")
    private String destination;
    @XmlElement(name = "location_name")
    private String locationName;
    @XmlElement(name = "current_location_name")
    private String currentLocationName;
    @XmlElement(name = "customer")
    private String customer;
    @XmlElement(name = "consignee")
    private String consignee;
    @XmlElement(name = "pickupdate")
    private String pickupdate;
    @XmlElement(name = "status")
    private String status;
    @XmlElement(name = "reason_code")
    private String reasonCode;
    @XmlElement(name = "reason_code_number")
    private String reasonCodeNumber;
    @XmlElement(name = "reason_code_description")
    private String reasonCodeDescription;
    @XmlElement(name = "receiver")
    private String receiver;
    @XmlElement(name = "expected_date")
    private String expectedDate;
    @XmlElement(name = "last_update_date")
    private String lastUpdateDate;
    @XmlElement(name = "delivery_date")
    private String deliveryDate;
    @XmlElement(name = "ref_awb")
    private String refAwb;
    @XmlElement(name = "rts_shipment")
    private String rtsShipment;

    public long getAwbNumber() {
        return awbNumber;
    }

    public void setAwbNumber(final long awbNumber) {
        this.awbNumber = awbNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public double getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(final double actualWeight) {
        this.actualWeight = actualWeight;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(final String locationName) {
        this.locationName = locationName;
    }

    public String getCurrentLocationName() {
        return currentLocationName;
    }

    public void setCurrentLocationName(final String currentLocationName) {
        this.currentLocationName = currentLocationName;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(final String customer) {
        this.customer = customer;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(final String consignee) {
        this.consignee = consignee;
    }

    public String getPickupdate() {
        return pickupdate;
    }

    public void setPickupdate(final String pickupdate) {
        this.pickupdate = pickupdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(final String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonCodeNumber() {
        return reasonCodeNumber;
    }

    public void setReasonCodeNumber(final String reasonCodeNumber) {
        this.reasonCodeNumber = reasonCodeNumber;
    }

    public String getReasonCodeDescription() {
        return reasonCodeDescription;
    }

    public void setReasonCodeDescription(final String reasonCodeDescription) {
        this.reasonCodeDescription = reasonCodeDescription;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(final String receiver) {
        this.receiver = receiver;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(final String expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(final String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getRefAwb() {
        return refAwb;
    }

    public void setRefAwb(final String refAwb) {
        this.refAwb = refAwb;
    }

    public String getRtsShipment() {
        return rtsShipment;
    }

    public void setRtsShipment(final String rtsShipment) {
        this.rtsShipment = rtsShipment;
    }

}
