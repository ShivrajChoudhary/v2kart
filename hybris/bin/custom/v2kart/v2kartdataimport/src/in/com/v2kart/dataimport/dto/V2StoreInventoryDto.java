package in.com.v2kart.dataimport.dto;

/**
 * @author arunkumar
 * 
 */
public class V2StoreInventoryDto extends BaseDto {
    private String siteCode;

    private String code;

    private int available;

    private String creationDate;

    private String creationTime;

    /**
     * @return the siteCode
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**
     * @param siteCode
     *        the siteCode to set
     */
    public void setSiteCode(final String siteCode) {
        this.siteCode = siteCode;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *        the code to set
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * @return the available
     */
    public int getAvailable() {
        return available;
    }

    /**
     * @param available
     *        the available to set
     */
    public void setAvailable(final int available) {
        this.available = available;
    }

    /**
     * @return the creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate
     *        the creationDate to set
     */
    public void setCreationDate(final String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the creationTime
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime
     *        the creationTime to set
     */
    public void setCreationTime(final String creationTime) {
        this.creationTime = creationTime;
    }
}
