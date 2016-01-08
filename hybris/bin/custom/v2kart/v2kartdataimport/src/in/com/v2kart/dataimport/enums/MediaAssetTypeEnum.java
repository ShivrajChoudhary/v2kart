/**
 * 
 */
package in.com.v2kart.dataimport.enums;

/**
 * @author ashish2483
 * 
 */
public enum MediaAssetTypeEnum {
    PRODUCT_DETAIL_IMG("PD", MediaType.JPG, "Product Detail", "ProductConversionGroup"), PRODUCT_YOUTUBE_URL("VIDEO_URL", MediaType.URL,
            "Product Youtube Video URL", null);

    private String filenameSuffix;

    private MediaType mediaType;

    private String description;

    private String imageConversionGroupCode;

    private MediaAssetTypeEnum(final String filenameSuffix, final MediaType mediaType, final String description,
            final String conversionGroupCode) {
        this.filenameSuffix = filenameSuffix;
        this.mediaType = mediaType;
        this.description = description;
        this.imageConversionGroupCode = conversionGroupCode;
    }

    public String getFilenameSuffix() {
        return filenameSuffix;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getDescription() {
        return description;
    }

    public String getImageConversionGroupCode() {
        return imageConversionGroupCode;
    }

    public boolean isConversionRequired() {
        return imageConversionGroupCode != null;
    }

}
