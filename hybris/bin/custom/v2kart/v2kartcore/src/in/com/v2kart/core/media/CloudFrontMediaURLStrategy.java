/**
 *
 */
package in.com.v2kart.core.media;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jets3t.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import de.hybris.platform.amazon.media.services.S3StorageServiceFactory;
import de.hybris.platform.amazon.media.url.S3MediaURLStrategy;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.media.exceptions.ExternalStorageServiceException;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * CloudFrontMediaURLStrategy - Strategy to generate cloudfront media url
 * 
 * It allows to specify media folder specific CDN urls
 * 
 */
public class CloudFrontMediaURLStrategy extends S3MediaURLStrategy {

    public static final Logger LOG = Logger.getLogger(CloudFrontMediaURLStrategy.class);

    public static final String SIGNED_KEY = "url.signed";
    public static final String SIGNED_VALID_FOR_KEY = "url.signed.validFor";
    public static final String USE_HTTPS_KEY = "url.unsigned.https";
    public static final String VIRTUAL_HOST_KEY = "url.unsigned.virtualHost";
    private static final Integer DEFAULT_TIME_TO_LIVE = Integer.valueOf(10);
    private static final Boolean DEFAULT_USE_SIGNED = Boolean.TRUE;
    private static final Boolean DEFAULT_USE_HTTPS = Boolean.TRUE;
    private static final Boolean DEFAULT_VIRTUAL_HOST = Boolean.FALSE;
    private static final String CDN_URL_FORMAT_FOR_MEDIA_FOLDER = "media.folder.%1$s.cdn.url";
    private static final String CDN_URL_FORMAT_FOR_MEDIA_FOLDER_A = "media.folder.%1$s.cdn.url.a";
    private static final String CDN_URL_FORMAT_FOR_MEDIA_FOLDER_B = "media.folder.%1$s.cdn.url.b";
    private static final String CDN_URL_FORMAT_FOR_MEDIA_FOLDER_C = "media.folder.%1$s.cdn.url.c";
    private static final String CDN_URL_FORMAT_FOR_MEDIA_FOLDER_D = "media.folder.%1$s.cdn.url.d";

    private final S3StorageServiceFactory s3StorageServiceFactory;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
	return configurationService;
    }

    /**
     * @param s3StorageServiceFactory
     */
    public CloudFrontMediaURLStrategy(final S3StorageServiceFactory s3StorageServiceFactory) {
	super(s3StorageServiceFactory);
	this.s3StorageServiceFactory = s3StorageServiceFactory;
    }

    /**
     * Function returning CDN url for media
     * 
     * @param config
     * @param bucketName
     * @param s3URL
     * @return String - url of media
     */
    private String getCloudFrontURL(final MediaStorageConfigService.MediaFolderConfig config, final String bucketName,
	    final String s3URL) {
	final Configuration configuration = this.getConfigurationService().getConfiguration();
	List<String> allCNAME = new LinkedList<String>();
	allCNAME.add(configuration.getString(String.format(CDN_URL_FORMAT_FOR_MEDIA_FOLDER_A,
		config.getFolderQualifier())));
	allCNAME.add(configuration.getString(String.format(CDN_URL_FORMAT_FOR_MEDIA_FOLDER_B,
		config.getFolderQualifier())));
	allCNAME.add(configuration.getString(String.format(CDN_URL_FORMAT_FOR_MEDIA_FOLDER_C,
		config.getFolderQualifier())));
	allCNAME.add(configuration.getString(String.format(CDN_URL_FORMAT_FOR_MEDIA_FOLDER_D,
		config.getFolderQualifier())));
	allCNAME.add(configuration.getString(String.format(CDN_URL_FORMAT_FOR_MEDIA_FOLDER,
		config.getFolderQualifier())));
	Random random = new Random();
	int randomNumber = random.nextInt(allCNAME.size());

	final StringBuilder cloudFrontURL = new StringBuilder(allCNAME.get(randomNumber));
	cloudFrontURL.append(s3URL.substring(s3URL.indexOf("://") + (3 + bucketName.length())));
	LOG.debug(cloudFrontURL.toString());
	return cloudFrontURL.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.amazon.media.url.S3MediaURLStrategy#getUrlForMedia
     * (de.hybris.platform.media.storage.
     * MediaStorageConfigService.MediaFolderConfig,
     * de.hybris.platform.media.MediaSource)
     */
    @Override
    public String getUrlForMedia(final MediaStorageConfigService.MediaFolderConfig config, final MediaSource media) {
	Preconditions.checkArgument(config != null, "Folder config is required to perform this operation");
	Preconditions.checkArgument(media != null, "MediaSource is required to perform this operation");

	String url = "";
	try {
	    final S3Service s3Service = this.s3StorageServiceFactory.getS3ServiceForFolder(config);
	    final String bucket = this.getBucketId(config);

	    if (isConfiguredForSignedUrls(config)) {
		url = s3Service.createSignedGetUrl(bucket, media.getLocation(), getTimeToLiveForUrl(config));
	    } else {
		url = s3Service.createUnsignedObjectUrl(bucket, media.getLocation(), isVirtualHost(config),
			useHttpsForUnsignedUrls(config), true);
	    }
	    url = this.getCloudFrontURL(config, bucket, url);

	} catch (final org.jets3t.service.ServiceException e) {
	    logDebug(media, e);
	}
	return url;
    }

    /**
     * Function returning secure media configuration
     * 
     * @param config
     * @return boolean
     */
    private boolean isConfiguredForSignedUrls(final MediaStorageConfigService.MediaFolderConfig config) {
	return config.getParameter(SIGNED_KEY, Boolean.class, DEFAULT_USE_SIGNED).booleanValue();
    }

    /**
     * Function returning secure channel configuration
     * 
     * @param config
     * @return boolean
     */
    private boolean useHttpsForUnsignedUrls(final MediaStorageConfigService.MediaFolderConfig config) {
	return config.getParameter(USE_HTTPS_KEY, Boolean.class, DEFAULT_USE_HTTPS).booleanValue();
    }

    /**
     * Function return virtual host configuration
     * 
     * @param config
     * @return boolean
     */
    private boolean isVirtualHost(final MediaStorageConfigService.MediaFolderConfig config) {
	return config.getParameter(VIRTUAL_HOST_KEY, Boolean.class, DEFAULT_VIRTUAL_HOST).booleanValue();
    }

    /**
     * Function returning TTL configuration
     * 
     * @param config
     * @return String
     */
    private Date getTimeToLiveForUrl(final MediaStorageConfigService.MediaFolderConfig config) {
	final Calendar cal = Calendar.getInstance();

	final Integer configuredTimeToLive = config.getParameter(SIGNED_VALID_FOR_KEY, Integer.class,
		DEFAULT_TIME_TO_LIVE);
	cal.add(12, configuredTimeToLive.intValue());
	return cal.getTime();
    }

    /**
     * Function returning S3 bucket id from configurations
     * 
     * @param config
     * @return String
     */
    private String getBucketId(final MediaStorageConfigService.MediaFolderConfig config) {
	final String bucketId = config.getParameter("bucketId");
	if (StringUtils.isBlank(bucketId)) {
	    throw new ExternalStorageServiceException("Bucket ID not found in S3 configuration");
	}
	return bucketId;
    }

    /**
     * Function to write debug log
     * 
     * @param media
     * @param exc
     */
    private void logDebug(final MediaSource media, final Exception exc) {
	final String msg = "Cannot render public url for media location: " + media.getLocation()
		+ " stored in Amazon S3 storage.";
	LOG.error(msg);
	if (!(LOG.isDebugEnabled())) {
	    return;
	}
	LOG.debug(msg, exc);
    }

}
