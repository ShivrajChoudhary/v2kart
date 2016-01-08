/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.data.response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vikrant2480
 * 
 */
public class ShipmentData {

    @JsonProperty("Shipment")
    private Shipment shipment;

    /**
     * @return the shipment
     */
    public Shipment getShipment() {
        return shipment;
    }

    /**
     * @param shipment
     *        the shipment to set
     */
    public void setShipment(final Shipment shipment) {
        this.shipment = shipment;
    }
}
