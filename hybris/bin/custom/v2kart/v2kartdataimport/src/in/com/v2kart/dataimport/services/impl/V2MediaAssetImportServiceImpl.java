/**
 * 
 */
package in.com.v2kart.dataimport.services.impl;

import in.com.v2kart.core.dao.CatalogAwareModelDao;
import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.dto.V2MediaDto;
import in.com.v2kart.dataimport.enums.MediaAssetTypeEnum;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;
import in.com.v2kart.dataimport.importengine.ExcelReader;
import in.com.v2kart.dataimport.services.V2MediaAssetImportService;
import in.com.v2kart.dataimport.strategies.FeedReaderStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;

/**
 * @author shubhammaheshwari
 * 
 */
public class V2MediaAssetImportServiceImpl extends BaseImportServiceImpl implements V2MediaAssetImportService {

    private static final String MEDIA_IMPORT_EXCEPTION_MESSAGE = "Error processing Media Dto[%1$s] for row number: %2$d";

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(V2MediaAssetImportServiceImpl.class);

    /** The Constant CONTAINER_SUFFIX. */
    private static final String CONTAINER_SUFFIX = "CONTAINER";

    /** The Constant DEFAULT_SEQUENCE. */
    private static final int DEFAULT_SEQUENCE = -1;

    /** The Constant DEFAULT_PD_IMAGE_FILENAME. */
    public static final String DEFAULT_PD_IMAGE_FILENAME = "pd_default";

    /** The Constant BASE_FORMAT. */
    private static final String BASE_FORMAT = "original";

    /** The Constant HYBRIS_IMG_ASSET_DIR. */
    private static final String HYBRIS_IMG_ASSET_DIR = "images";

    /** The default media container. */
    private MediaContainerModel defaultMediaContainer = null;

    /** The media service. */
    private MediaService mediaService;

    /** The media conversion service. */
    private MediaConversionService mediaConversionService;

    /** The processed root directory. */
    private String processedRootDirectory;

    /** The product image directory path. */
    private String productImageDirectoryPath;

    /** The should move asset files. */
    private boolean shouldMoveAssetFiles;

    /** The should add default media. */
    private boolean shouldAddDefaultMedia;

    /** The media excel reader. */
    @Resource(name = "v2MediaExcelReader")
    private ExcelReader v2MediaExcelReader;

    /** The product model dao. */
    @Resource(name = "productModelDao")
    private CatalogAwareModelDao<ProductModel> productModelDao;

    /** The feed reader strategy. */
    @Autowired
    private FeedReaderStrategy feedReaderStrategy;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.services.FGMediaAssetImportService#getMediaInfoFromFeed()
     */
    @Override
    public List<V2MediaDto> getMediaInfoFromFeed() {
        return feedReaderStrategy.<V2MediaDto> readFeed(v2MediaExcelReader);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.services.V2MediaAssetImportService#importMediaDataFromFeed()
     */
    @Override
    public ImportDataSummaryInfo importMediaDataFromFeed() {
        final List<V2MediaDto> mediaDtoList = this.getMediaInfoFromFeed();
        LOG.info("No. of media DTO's: " + mediaDtoList.size());
        final ImportDataSummaryInfo summaryInfo = new ImportDataSummaryInfo();
        summaryInfo.setTotalRecords(mediaDtoList.size());
        for (final V2MediaDto mediaInfoDto : mediaDtoList) {
            try {
                summaryInfo.recordDtoProcessing(mediaInfoDto);
                final ProductModel productModel = (ProductModel) productModelDao.findByCodeAndCatalogVersion(getProductCode(mediaInfoDto),getCatalogVersionModel());
                if (productModel == null) {
                    LOG.error("Product Not Found!!!");
                    throw new FeedPersistanceException(FailureCause.PRODUCT_NOT_FOUND, mediaInfoDto.getRowIndex());
                } else {
                    LOG.debug("Processing image[" + mediaInfoDto.getFileName() + "] for product with code :" + getProductCode(mediaInfoDto)
                            + " for row: " + mediaInfoDto.getRowIndex());
                    processMedia(mediaInfoDto, productModel);
                }
            } catch (final FeedPersistanceException e) {
                this.logExceptionToService(e, e.getFailureCause(), Integer.valueOf(e.getFeedRowNumber()), summaryInfo);
            } catch (final Exception e) {
                LOG.error(
                        String.format(MEDIA_IMPORT_EXCEPTION_MESSAGE, mediaInfoDto.getVerboseToString(),
                                new Integer(mediaInfoDto.getRowIndex())), e);
                summaryInfo.addFailureIncidence(FailureCause.OTHERS);
            }
        }
        return summaryInfo;
    }

    /**
     * @param v2MediaDto
     * @return
     */
    private final String getProductCode(V2MediaDto v2MediaDto) {
        return v2MediaDto.getCode().replace("-", "_");
    }

    /**
     * Process media.
     * 
     * @param mediaInfoDto
     *        the media info dto
     * @param productModel
     *        the product model
     */
    private void processMedia(final V2MediaDto mediaInfoDto, final ProductModel productModel) {
        try {
            final List<MediaContainerModel> galleryImagesFromDb = productModel.getGalleryImages();
            final Set<MediaContainerModel> galleryImages = new HashSet<MediaContainerModel>();
            if (galleryImagesFromDb != null) {
                galleryImages.addAll(galleryImagesFromDb);
            }
            final MediaContainerModel mediaContainer = createOrRetrieveMediaContainer(getProductCode(mediaInfoDto), mediaInfoDto.getFileName(),
                    MediaAssetTypeEnum.PRODUCT_DETAIL_IMG, mediaInfoDto.getSequence(), mediaInfoDto.getAltText());
            if (mediaContainer != null) {
                galleryImages.add(mediaContainer);
            } else {
                throw new FeedPersistanceException("Image File not found for imageFile name :" + mediaInfoDto.getFileName(),
                        FailureCause.IMAGE_NOT_FOUND, mediaInfoDto.getRowIndex());
            }
            // let's remove the default media container
            final MediaContainerModel defaultMediaContainer = getDefaultMediaContainer();
            if (defaultMediaContainer != null) {
                galleryImages.remove(defaultMediaContainer);
            }
            if (!galleryImages.isEmpty()) {
                // save the images to product model
                final List<MediaContainerModel> sortedGalleryImages = new ArrayList<>(galleryImages);
                Collections.sort(sortedGalleryImages, new MediaContainerModelComparator());
                // if want to set first gallery image as Thumbnail
                productModel.setThumbnail(sortedGalleryImages.get(0).getMaster());
                productModel.setGalleryImages(new ArrayList<MediaContainerModel>(sortedGalleryImages));
                getModelService().save(productModel);
                moveToProcessedDirectory(mediaInfoDto.getFileName());
            } else {
                // no gallery images found, put a default image.
                if (defaultMediaContainer != null) {
                    galleryImages.add(defaultMediaContainer);
                    productModel.setGalleryImages(new ArrayList<MediaContainerModel>(galleryImages));
                    getModelService().save(productModel);
                }
            }
        } catch (final ModelSavingException e) {
            LOG.debug("Failed to save:" + productModel.getCode() + " during browseAndSaveGalleryImages()");
        }
    }

    /**
     * Gets the default media container.
     * 
     * @return the default media container
     */
    private MediaContainerModel getDefaultMediaContainer() {

        if (shouldAddDefaultMedia && defaultMediaContainer == null) {
            defaultMediaContainer = createOrRetrieveDefaultProductDetailMediaContainer();
        }
        return defaultMediaContainer;
    }

    /**
     * Creates the or retrieve default product detail media container.
     * 
     * @return the media container model
     */
    private MediaContainerModel createOrRetrieveDefaultProductDetailMediaContainer() {
        final StringBuilder filenameSb = new StringBuilder(productImageDirectoryPath);
        filenameSb.append(File.separator).append(DEFAULT_PD_IMAGE_FILENAME);
        filenameSb.append(MediaAssetTypeEnum.PRODUCT_DETAIL_IMG.getMediaType().getFilenameExtension());
        filenameSb.toString();

        final File sourceFile = new File(filenameSb.toString());

        if (sourceFile.exists()) {
            return createOrRetrieveMediaContainer(null, MediaAssetTypeEnum.PRODUCT_DETAIL_IMG, DEFAULT_SEQUENCE, sourceFile, null);
        } else {
            LOG.warn("Default image 'pd_default.jpg' not found, product image gallery will be empty.");
            return null;
        }
    }

    /**
     * Creates the or retrieve media container.
     * 
     * @param productCode
     *        the product code
     * @param assetCode
     *        the asset code
     * @param mediaAssetTypeEnum
     *        the media asset type enum
     * @param sequence
     *        the sequence
     * @param altText
     *        the alt text
     * @return the media container model
     */
    private MediaContainerModel createOrRetrieveMediaContainer(final String productCode, final String assetCode,
            final MediaAssetTypeEnum mediaAssetTypeEnum, final int sequence, final String altText) {
        final File sourceFile = getSourceFile(assetCode);
        if (sourceFile != null) {
            return createOrRetrieveMediaContainer(productCode, mediaAssetTypeEnum, sequence, sourceFile, altText);
        } else {
            return null;
        }
    }

    /**
     * Creates the or retrieve media container.
     * 
     * @param productCode
     *        the product code
     * @param mediaAssetTypeEnum
     *        the media asset type enum
     * @param sequence
     *        the sequence
     * @param sourceFile
     *        the source file
     * @param altText
     *        the alt text
     * @return the media container model
     */
    private MediaContainerModel createOrRetrieveMediaContainer(final String productCode, final MediaAssetTypeEnum mediaAssetTypeEnum,
            final int sequence, final File sourceFile, final String altText) {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        } else {
            boolean isNew = false;

            final String containerName;
            if (StringUtils.isBlank(productCode) && (sequence == DEFAULT_SEQUENCE)) {
                containerName = DEFAULT_PD_IMAGE_FILENAME.toUpperCase();
            } else {
                containerName = getMediaContainerName(productCode, mediaAssetTypeEnum, sequence);
            }

            MediaContainerModel mediaContainer = getMediaContainer(containerName);

            if (mediaContainer == null) {
                LOG.debug("Creating media container: " + containerName);

                mediaContainer = getModelService().create(MediaContainerModel.class);
                isNew = true;

                mediaContainer.setCatalogVersion(getCatalogVersionModel());
                mediaContainer.setQualifier(containerName);
                mediaContainer.setName(containerName);
                if (mediaAssetTypeEnum.isConversionRequired()) {
                    mediaContainer.setConversionGroup(getConversionGroup(mediaAssetTypeEnum));
                }
            }
            final MediaModel originalMedia = createOrUpdateMediaModel(productCode, containerName, mediaAssetTypeEnum, sourceFile);
            if (altText != null) {
                originalMedia.setAltText(altText);
            }
            final Collection<MediaModel> medias = new ArrayList<MediaModel>();
            medias.add(originalMedia);
            mediaContainer.setMedias(medias);
            saveMediaContainerAndGenerateMediaModels(mediaContainer, isNew);
            return mediaContainer;
        }
    }

    /**
     * Save media container and generate media models.
     * 
     * @param mediaContainer
     *        the media container
     * @param isNew
     *        the is new
     */
    private void saveMediaContainerAndGenerateMediaModels(final MediaContainerModel mediaContainer, final boolean isNew) {
        if (!isNew) {
            mediaConversionService.deleteConvertedMedias(mediaContainer);
        }
        try {
            getModelService().save(mediaContainer);
        } catch (final ModelSavingException e) {
            LOG.debug("Failed to save:" + mediaContainer.getName() + " media container during saveMediaContainerAndGenerateMediaModels()");
        }
    }

    /**
     * Gets the media container name.
     * 
     * @param productCode
     *        the product code
     * @param mediaAssetTypeEnum
     *        the media asset type enum
     * @param sequence
     *        the sequence
     * @return the media container name
     */
    private String getMediaContainerName(final String productCode, final MediaAssetTypeEnum mediaAssetTypeEnum, final int sequence) {
        final StringBuilder filenameSb = new StringBuilder(productCode);
        if (StringUtils.isNotBlank(mediaAssetTypeEnum.getFilenameSuffix())) {
            filenameSb.append('_').append(mediaAssetTypeEnum.getFilenameSuffix());
        }
        if (sequence != DEFAULT_SEQUENCE) {
            filenameSb.append('_').append(sequence);
        }
        filenameSb.append('_').append(CONTAINER_SUFFIX);
        return filenameSb.toString();
    }

    /**
     * Gets the media container.
     * 
     * @param qualifier
     *        the qualifier
     * @return the media container
     */
    private MediaContainerModel getMediaContainer(final String qualifier) {
        MediaContainerModel mediaContainerModel = getModelService().create(MediaContainerModel.class);
        mediaContainerModel.setQualifier(qualifier);
        mediaContainerModel.setCatalogVersion(getCatalogVersionModel());
        try {
            mediaContainerModel = getFlexibleSearchService().getModelByExample(mediaContainerModel);
            return mediaContainerModel;
        } catch (final ModelNotFoundException e) {
            LOG.debug("MediaContainerModel: " + qualifier + " not found!");
            return null;
        }
    }

    /**
     * Gets the conversion group.
     * 
     * @param mediaAssetTypeEnum
     *        the media asset type enum
     * @return the conversion group
     */
    private ConversionGroupModel getConversionGroup(final MediaAssetTypeEnum mediaAssetTypeEnum) {
        ConversionGroupModel conversionGroupModel = getModelService().create(ConversionGroupModel.class);
        conversionGroupModel.setCode(mediaAssetTypeEnum.getImageConversionGroupCode());
        try {
            conversionGroupModel = getFlexibleSearchService().getModelByExample(conversionGroupModel);
            return conversionGroupModel;
        } catch (final ModelNotFoundException e) {
            LOG.error("ConversionGroupModel: " + mediaAssetTypeEnum.getImageConversionGroupCode() + " not found!");
            return null;
        }
    }

    /**
     * Creates the or update media model.
     * 
     * @param productCode
     *        the product code
     * @param containerName
     *        the container name
     * @param mediaAssetTypeEnum
     *        the media asset type enum
     * @param file
     *        the file
     * @return the media model
     */
    private MediaModel createOrUpdateMediaModel(final String productCode, final String containerName,
            final MediaAssetTypeEnum mediaAssetTypeEnum, final File file) {
        final MediaFormatModel originalFormat = mediaService.getFormat(BASE_FORMAT);
        final String mediaModelCode = containerName + '_' + originalFormat.getQualifier();
        MediaModel mediaModel;

        try {
            mediaModel = mediaService.getMedia(getCatalogVersionModel(), mediaModelCode);
        } catch (final UnknownIdentifierException exception) {
            // Expected output.
            final MediaFolderModel mediaFolder = createOrRetrieveMediaFolder(productCode);

            mediaModel = getModelService().create(MediaModel.class);

            mediaModel.setCode(mediaModelCode);
            mediaModel.setDescription(mediaAssetTypeEnum.getDescription());
            mediaModel.setCatalogVersion(getCatalogVersionModel());

            mediaModel.setMediaFormat(originalFormat);
            mediaModel.setFolder(mediaFolder);
            mediaModel.setMime(mediaAssetTypeEnum.getMediaType().getContentType());
            // need to save first before calling setStreamForMedia
            getModelService().save(mediaModel);
        }

        try {
            mediaService.setStreamForMedia(mediaModel, new FileInputStream(file));
            getModelService().save(mediaModel);
        } catch (final MediaIOException e) {
            LOG.error("Failed to access:" + file.getAbsolutePath() + " during createMediaModel()");
        } catch (final IllegalArgumentException e) {
            LOG.error("Failed to stream:" + file.getAbsolutePath() + " during createMediaModel()");
        } catch (final FileNotFoundException e) {
            LOG.error("Failed to find:" + file.getAbsolutePath() + " during createMediaModel()");
        }
        return mediaModel;
    }

    /**
     * Creates the or retrieve media folder.
     * 
     * @param productCode
     *        the product code
     * @return the media folder model
     */
    private MediaFolderModel createOrRetrieveMediaFolder(final String productCode) {
        StringBuilder mediaFolderName;
        mediaFolderName = new StringBuilder(HYBRIS_IMG_ASSET_DIR);
        // mediaFolderName.append(File.separator).append(productCode);
        MediaFolderModel mediaFolder;
        try {
            mediaFolder = mediaService.getFolder(mediaFolderName.toString());
        } catch (final UnknownIdentifierException exception) {
            mediaFolder = getModelService().create(MediaFolderModel.class);
            mediaFolder.setQualifier(mediaFolderName.toString());
            mediaFolder.setPath(mediaFolderName.toString());
        }
        return mediaFolder;
    }

    /**
     * Gets the source file.
     * 
     * @param assetCode
     *        the asset code
     * @return the source file
     */
    private File getSourceFile(final String assetCode) {
        final StringBuilder filenameSb = new StringBuilder(productImageDirectoryPath);
        filenameSb.append(File.separator).append(assetCode);
        filenameSb.toString();
        final File sourceFile = new File(filenameSb.toString());

        if (sourceFile.exists()) {
            return sourceFile;
        } else {
            return null;
        }
    }

    /**
     * Move to processed directory.
     * 
     * @param assetCode
     *        the asset code
     */
    private void moveToProcessedDirectory(final String assetCode) {
        if (shouldMoveAssetFiles) {
            final File mediaFile = getSourceFile(assetCode);
            if (mediaFile != null) {
                moveFile(mediaFile);
            }
        }
    }

    /**
     * Move file.
     * 
     * @param mediaFile
     *        the media file
     */
    private void moveFile(final File mediaFile) {
        final Date today = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            if (mediaFile != null && mediaFile.exists()) {
                final File destinationDirectory = new File(processedRootDirectory + File.separator + sdf.format(today)
                        + StringUtils.difference(processedRootDirectory, mediaFile.getParent()));
                try {
                    final File destinationFile = new File(destinationDirectory + File.separator
                            + FilenameUtils.getName(mediaFile.getAbsolutePath()));
                    if (destinationFile.exists()) {
                        destinationFile.delete();
                    }

                    FileUtils.moveFileToDirectory(mediaFile, destinationDirectory, true);
                } catch (final IOException e) {
                    LOG.error("Unable to move: " + mediaFile.getName() + " to: " + destinationDirectory.getAbsolutePath(), e);
                }
                final File sourceDirectory = mediaFile.getParentFile();
                if (sourceDirectory.list().length == 0) {
                    try {
                        FileUtils.deleteDirectory(sourceDirectory);
                    } catch (final IOException e) {
                        LOG.error("Unable to delete directory: " + sourceDirectory.getAbsolutePath(), e);
                    }
                }
            }
        } catch (final Exception e) {
            final String mediaFileName;
            if (mediaFile != null && StringUtils.isNotBlank(mediaFile.getName())) {
                mediaFileName = mediaFile.getName();
            } else {
                mediaFileName = "Unknown media file name.";
            }
            LOG.error("Unable to move: " + mediaFileName, e);
        }
    }

    /**
     * Sets the media service.
     * 
     * @param mediaService
     *        the new media service
     */
    @Required
    @Autowired
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * Sets the media conversion service.
     * 
     * @param mediaConversionService
     *        the new media conversion service
     */
    @Required
    @Autowired
    public void setMediaConversionService(final MediaConversionService mediaConversionService) {
        this.mediaConversionService = mediaConversionService;
    }

    /**
     * Sets the should move asset files.
     * 
     * @param shouldMoveAssetFiles
     *        the new should move asset files
     */
    @Value("${import.media.moveFiles}")
    public void setShouldMoveAssetFiles(final boolean shouldMoveAssetFiles) {
        this.shouldMoveAssetFiles = shouldMoveAssetFiles;
        if (this.shouldMoveAssetFiles) {
            LOG.debug("Asset files will be moved to 'processed' directory as per local.properties setting.");
        } else {
            LOG.debug("Asset files will NOT be moved to 'processed' directory as per local.properties setting.");
        }

    }

    /**
     * Sets the should add default media.
     * 
     * @param shouldAddDefaultMedia
     *        the new should add default media
     */
    @Value("${import.media.addDefaultMedia}")
    public void setShouldAddDefaultMedia(final boolean shouldAddDefaultMedia) {
        this.shouldAddDefaultMedia = shouldAddDefaultMedia;
        if (this.shouldAddDefaultMedia) {
            LOG.debug("Default media will be assigned to products with no media as per local.properties setting.");
        } else {
            LOG.debug("Default media will NOT be assigned to products with no media as per local.properties setting.");
        }
    }

    /**
     * Sets the product image directory path.
     * 
     * @param productImageDirectoryPath
     *        the productImageDirectoryPath to set
     */
    @Value("${mediaAssetHelper.product.imageDirectory}")
    public void setProductImageDirectoryPath(final String productImageDirectoryPath) {
        this.productImageDirectoryPath = productImageDirectoryPath;
    }

    /**
     * Sets the processed root directory.
     * 
     * @param processedRootDirectory
     *        the processedRootDirectory to set
     */
    public void setProcessedRootDirectory(final String processedRootDirectory) {
        this.processedRootDirectory = processedRootDirectory;
    }

    /**
     * The Class MediaContainerModelComparator.
     */
    class MediaContainerModelComparator implements Comparator<MediaContainerModel> {

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final MediaContainerModel m1, final MediaContainerModel m2) {
            return m1.getQualifier().compareTo(m2.getQualifier());
        }
    }
}
