/**
 * 
 */
package in.com.v2kart.dataimport.enums;

/**
 * @author ashish2483
 * 
 */
public enum MediaType {
    JPG(".jpg", "image", "image/jpeg"), URL("url", "video", "youtube");

    private String filenameExtension;

    private String fileType;

    private String contentType;

    private MediaType(final String filenameExtension, final String fileType, final String contentType) {
        this.filenameExtension = filenameExtension;
        this.contentType = contentType;
        this.fileType = fileType;
    }

    public String getFilenameExtension() {
        return filenameExtension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileTye() {
        return fileType;
    }

}
