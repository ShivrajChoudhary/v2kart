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
public class ScansDetail {

    @JsonProperty("ScanDateTime")
    private Date scanDateTime;

    @JsonProperty("ScanType")
    private String scanType;

    @JsonProperty("Scan")
    private String scan;

    @JsonProperty("StatusDateTime")
    private Date statusDateTime;

    @JsonProperty("ScannedLocation")
    private String scannedLocation;

    @JsonProperty("Instructions")
    private String instructions;

    @JsonProperty("StatusCode")
    private String statusCode;

    /**
     * @return the scanDateTime
     */
    public Date getScanDateTime() {
        return scanDateTime;
    }

    /**
     * @param scanDateTime
     *        the scanDateTime to set
     */
    public void setScanDateTime(final Date scanDateTime) {
        this.scanDateTime = scanDateTime;
    }

    /**
     * @return the scanType
     */
    public String getScanType() {
        return scanType;
    }

    /**
     * @param scanType
     *        the scanType to set
     */
    public void setScanType(final String scanType) {
        this.scanType = scanType;
    }

    /**
     * @return the scan
     */
    public String getScan() {
        return scan;
    }

    /**
     * @param scan
     *        the scan to set
     */
    public void setScan(final String scan) {
        this.scan = scan;
    }

    /**
     * @return the statusDateTime
     */
    public Date getStatusDateTime() {
        return statusDateTime;
    }

    /**
     * @param statusDateTime
     *        the statusDateTime to set
     */
    public void setStatusDateTime(final Date statusDateTime) {
        this.statusDateTime = statusDateTime;
    }

    /**
     * @return the scannedLocation
     */
    public String getScannedLocation() {
        return scannedLocation;
    }

    /**
     * @param scannedLocation
     *        the scannedLocation to set
     */
    public void setScannedLocation(final String scannedLocation) {
        this.scannedLocation = scannedLocation;
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
