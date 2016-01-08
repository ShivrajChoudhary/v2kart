/**
 * 
 */
package in.com.v2kart.dataimport.dto;

/**
 * @author shubhammaheshwari
 * 
 */
public class V2MediaDto extends BaseDto {

    private String code;

    private int sequence;

    private String contentType;

    private String fileName;

    private String altText;

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
     * @return the sequence
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * @param sequence
     *        the sequence to set
     */
    public void setSequence(final int sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType
     *        the contentType to set
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *        the fileName to set
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the altText
     */
    public String getAltText() {
        return altText;
    }

    /**
     * @param altText
     *        the altText to set
     */
    public void setAltText(final String altText) {
        this.altText = altText;
    }

}
