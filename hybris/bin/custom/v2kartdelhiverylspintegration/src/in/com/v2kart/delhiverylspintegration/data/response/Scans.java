/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.data.response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vikrant2480
 * 
 */
public class Scans {

    @JsonProperty("ScanDetail")
    private ScansDetail scansDetail;

    /**
     * @return the scansDetail
     */
    public ScansDetail getScansDetail() {
        return scansDetail;
    }

    /**
     * @param scansDetail
     *        the scansDetail to set
     */
    public void setScansDetail(final ScansDetail scansDetail) {
        this.scansDetail = scansDetail;
    }

}
