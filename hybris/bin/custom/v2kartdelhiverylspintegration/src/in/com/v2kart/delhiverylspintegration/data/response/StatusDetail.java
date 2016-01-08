/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.data.response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vikrant2480
 * 
 */
public class StatusDetail {

    @JsonProperty("Status")
    private String status;

    @JsonProperty("StatusLocation")
    private String statusLocation;

    @JsonProperty("StatusDateTime")
    private String statusDateTime;

    @JsonProperty("RecievedBy")
    private String recievedBy;

    @JsonProperty("Instructions")
    private String instructions;

    @JsonProperty("StatusType")
    private String statusType;

    @JsonProperty("StatusCode")
    private String statusCode;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * @return the statusLocation
     */
    public String getStatusLocation() {
        return statusLocation;
    }

    /**
     * @param statusLocation
     *        the statusLocation to set
     */
    public void setStatusLocation(final String statusLocation) {
        this.statusLocation = statusLocation;
    }

    /**
     * @return the statusDateTime
     */
    public String getStatusDateTime() {
        return statusDateTime;
    }

    /**
     * @param statusDateTime
     *        the statusDateTime to set
     */
    public void setStatusDateTime(final String statusDateTime) {
        this.statusDateTime = statusDateTime;
    }

    /**
     * @return the recievedBy
     */
    public String getRecievedBy() {
        return recievedBy;
    }

    /**
     * @param recievedBy
     *        the recievedBy to set
     */
    public void setRecievedBy(final String recievedBy) {
        this.recievedBy = recievedBy;
    }

    /**
     * @return the instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * @param instructions
     *        the instructions to set
     */
    public void setInstructions(final String instructions) {
        this.instructions = instructions;
    }

    /**
     * @return the statusType
     */
    public String getStatusType() {
        return statusType;
    }

    /**
     * @param statusType
     *        the statusType to set
     */
    public void setStatusType(final String statusType) {
        this.statusType = statusType;
    }

    /**
     * @return the statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     *        the statusCode to set
     */
    public void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }
}
