/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.data.response;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vikrant2480
 * 
 */
@JsonIgnoreProperties
public class DelhiveryOrderTrackerResponse
{
    @JsonProperty("ShipmentData")
    private ShipmentData[] shipmentData;

    @JsonProperty("Error")
    private String error;

    /**
     * @return the shipmentData
     */
    public ShipmentData[] getShipmentData() {
        return shipmentData;
    }

    /**
     * @param shipmentData
     *        the shipmentData to set
     */
    public void setShipmentData(final ShipmentData[] shipmentData) {
        this.shipmentData = shipmentData;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error
     *        the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

}
