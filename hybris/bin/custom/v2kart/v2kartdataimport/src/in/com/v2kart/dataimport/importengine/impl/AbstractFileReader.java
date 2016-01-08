package in.com.v2kart.dataimport.importengine.impl;

import in.com.v2kart.dataimport.context.ImportCommandContext;
import in.com.v2kart.dataimport.context.ImportCommandContextHolder;
import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.exceptions.FeedProcessingException;
import in.com.v2kart.dataimport.exceptions.FeedProcessingException.FailureCause;
import in.com.v2kart.dataimport.exceptions.FeedSourceReadException;
import in.com.v2kart.dataimport.importengine.FileReader;
import in.com.v2kart.dataimport.importengine.ReaderSummaryInfo;
import in.com.v2kart.importlog.data.ImportLogEventData;
import in.com.v2kart.importlog.enums.ImportLogEventHint;
import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.services.ImportLogService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.supercsv.cellprocessor.ift.CellProcessor;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.validation.enums.Severity;

/**
 * Abstract Implementation of FileReader.
 * 
 */
public abstract class AbstractFileReader implements FileReader {

    private static final Logger LOG = Logger.getLogger(AbstractFileReader.class);

    /**
     * Unix based path separator
     */
    private static final String PATH_SEPARATOR = "/";

    private static final String FILE_READER_CHARSET_DECODING_FORMAT = "importEngine.filereader.charset";

    /**
     * ReaderManagementInfo instance
     */
    protected ReaderSummaryInfo readerSummary;

    @Autowired
    protected ImportLogService importLogService;

    /**
     * Attribute to store default file name
     */
    protected String defaultFileName;

    /** The directory path. */
    protected String directoryPath;

    /**
     * Array of cell processors to parse cell values
     */
    protected CellProcessor[] cellProcessors;

    /**
     * Array of headers
     */
    protected String[] headers;

    /**
     * Type info representing individual row
     */
    protected Class<? extends BaseDto> dtoType;

    /**
     * Status to indicate header row presence
     */
    private boolean hasFileHeader = false;

    private String processedRootDirectory;

    private boolean shouldMoveFiles;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Checks if is checks for file header.
     * 
     * @return the hasFileHeader
     */
    public final boolean isHasFileHeader() {
        return hasFileHeader;
    }

    /**
     * Sets the checks for file header.
     * 
     * @param hasFileHeader
     *        the hasFileHeader to set
     */
    public final void setHasFileHeader(final boolean hasFileHeader) {
        this.hasFileHeader = hasFileHeader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.FileReader#getDefaultFileName()
     */
    @Override
    public final String getDefaultFileName() {
        return defaultFileName;
    }

    /**
     * Sets the default file name.
     * 
     * @param defaultFileName
     *        the new default file name
     */
    public final void setDefaultFileName(final String defaultFileName) {
        this.defaultFileName = defaultFileName;
        this.createFolderStructure(defaultFileName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.FileReader#getDirectoryPath()
     */
    @Override
    public final String getDirectoryPath() {
        return directoryPath;
    }

    /**
     * Sets the directory path.
     * 
     * @param directoryPath
     *        the directoryPath to set
     */
    public final void setDirectoryPath(final String directoryPath) {
        this.directoryPath = directoryPath;
        this.createFolderStructure(directoryPath + "/sample.csv");
    }

    /**
     * Creates the folder structure.
     * 
     * @param fileName
     *        the file name
     */
    private void createFolderStructure(final String fileName) {
        Path path = Paths.get(fileName).getParent();
        if (!Files.exists(path)) {
            try {
                path = Files.createDirectories(path);
                LOG.info("Folder structure doesn't exist, creating directory structure  : " + path);
            } catch (final IOException e) {
                LOG.error("Exception while creating directory structure", e);
            }

        }
    }

    /**
     * Gets the headers.
     * 
     * @return the headerList
     */
    @Override
    public final String[] getHeaders() {
        return headers;
    }

    /**
     * Sets the headers.
     * 
     * @param headers
     *        the headers to set
     */
    public final void setHeaders(final String[] headers) {
        this.headers = headers;
    }

    /**
     * Gets the dto type.
     * 
     * @return the dtoType
     */
    public final Class<? extends BaseDto> getDtoType() {
        return dtoType;
    }

    /**
     * Sets the dto type.
     * 
     * @param dtoType
     *        the dtoType to set
     */
    public final void setDtoType(final Class<? extends BaseDto> dtoType) {
        this.dtoType = dtoType;
    }

    /**
     * Gets the cell processors.
     * 
     * @return the cell processors
     */
    public final CellProcessor[] getCellProcessors() {
        return cellProcessors != null ? cellProcessors : new CellProcessor[0];
    }

    /**
     * Sets the cell processors.
     * 
     * @param cellProcessors
     *        the cellProcessors to set
     */
    public final void setCellProcessors(final CellProcessor[] cellProcessors) {
        this.cellProcessors = cellProcessors;
    }

    /**
     * Gets the reader summary info.
     * 
     * @return the managementInfo
     */
    @Override
    public final ReaderSummaryInfo getReaderSummaryInfo() {
        return readerSummary;
    }

    /**
     * Gets the configuration service.
     * 
     * @return the configurationService
     */
    public final ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Sets the configuration service.
     * 
     * @param configurationService
     *        the configurationService to set
     */
    public final void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Inits the management info.
     */
    protected final void initManagementInfo() {
        this.readerSummary = new ReaderSummaryInfo(this.defaultFileName);
    }

    /**
     * Gets the character encoding.
     * 
     * @return the character encoding
     */
    protected final String getCharacterEncoding() {
        final Configuration configuration = this.getConfigurationService().getConfiguration();
        return configuration.getString(FILE_READER_CHARSET_DECODING_FORMAT, "UTF-8");
    }

    /**
     * Move to processed directory.
     */
    protected final void moveToProcessedDirectory() {
        if (shouldMoveFiles) {
            final File sourceFile = new File(getDefaultFileName());
            if (sourceFile.exists()) {
                moveFile(sourceFile);
            }
        } else {
            return;
        }
    }

    /**
     * Gets the file to process. If default file is not provided or it doesn't exists then the oldest file in the directory path will be
     * retrieved.
     * 
     * @return the file to process
     */
    protected final File getFileToProcess() {
        String errorPath = getDefaultFileName();
        File file = null;
        if (null != getDefaultFileName() && (new File(getDefaultFileName())).exists()) {
            file = new File(getDefaultFileName());
        } else if (null != getDirectoryPath()) {
            errorPath = getDirectoryPath();
            final File dir = new File(getDirectoryPath());
            if (dir.isDirectory()) {
                final File[] files = dir.listFiles();
                if (!isEmpty(files)) {
                    /** The oldest file comes first **/
                    Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
                    file = files[0];
                    // this is done for logging purposes
                    setDefaultFileName(file.getPath());
                }
            }
        }
        if (null == file) {
            throw new FeedSourceReadException(errorPath);
        }
        return file;
    }

    /**
     * Move file.
     * 
     * @param sourceFile
     *        the source file
     */
    private void moveFile(final File sourceFile) {
        final Date today = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final SimpleDateFormat fdf = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");

        try {
            if (sourceFile != null && sourceFile.exists()) {
                final File destinationDirectory = new File(processedRootDirectory + File.separator + sdf.format(today)
                        + StringUtils.difference(processedRootDirectory, sourceFile.getParent()));
                try {

                    final StringBuilder newFilePath = new StringBuilder(sourceFile.getParent());
                    newFilePath.append(File.separator)
                            .append(StringUtils.substringBeforeLast(FilenameUtils.getName(sourceFile.getAbsolutePath()), "."))
                            .append(fdf.format(today)).append(".")
                            .append(StringUtils.substringAfterLast(FilenameUtils.getName(sourceFile.getAbsolutePath()), "."));
                    sourceFile.renameTo(new File(newFilePath.toString()));
                    FileUtils.moveFileToDirectory(new File(newFilePath.toString()), destinationDirectory, true);
                } catch (final IOException e) {
                    LOG.error("Unable to move: " + sourceFile.getName() + " to: " + destinationDirectory.getAbsolutePath(), e);
                }
                // commenting the code to remove the folder from dataexchange directory if empty
                /*
                 * final File sourceDirectory = sourceFile.getParentFile(); if (sourceDirectory.list().length == 0) { try {
                 * FileUtils.deleteDirectory(sourceDirectory); } catch (final IOException e) { LOG.error("Unable to delete directory: " +
                 * sourceDirectory.getAbsolutePath(), e); } }
                 */
            }
        } catch (final Exception e) {
            final String sourceFileName;
            if (sourceFile != null && StringUtils.isNotBlank(sourceFile.getName())) {
                sourceFileName = sourceFile.getName();
            } else {
                sourceFileName = "Unknown file name.";
            }
            LOG.error("Unable to move: " + sourceFileName, e);
        }
    }

    /**
     * Sets the should move asset files.
     * 
     * @param shouldMoveAssetFiles
     *        the new should move asset files
     */
    @Value("${import.csv.moveFiles}")
    public final void setShouldMoveAssetFiles(final boolean shouldMoveAssetFiles) {
        this.shouldMoveFiles = shouldMoveAssetFiles;
        if (this.shouldMoveFiles) {
            LOG.debug("Asset files will be moved to 'processed' directory as per local.properties setting.");
        } else {
            LOG.debug("Asset files will NOT be moved to 'processed' directory as per local.properties setting.");
        }
    }

    /**
     * Sets the processed root directory.
     * 
     * @param processedRootDirectory
     *        the processedRootDirectory to set
     */
    @Value("${processed.rootDirectory}")
    public final void setProcessedRootDirectory(final String processedRootDirectory) {
        this.processedRootDirectory = processedRootDirectory;
    }

    /**
     * Function to log exception.
     * 
     * @param cause
     *        the cause
     * @param throwable
     *        the throwable
     * @param lineNo
     *        the line no
     * @param columnNumber
     *        the column number
     * @return FeedProcessingException
     */
    protected final FeedProcessingException logException(final FailureCause cause, final Throwable throwable, final int lineNo,
            final int columnNumber) {
        final FeedProcessingException exp = new FeedProcessingException(cause, lineNo, columnNumber, throwable);
        logException(exp);
        return exp;
    }

    /**
     * Function to log exception and persisting failure event to database.
     * 
     * @param exception
     *        the exception
     */
    private void logException(final FeedProcessingException exception) {
        final FailureCause failureCause = exception.getFailureCause();
        this.readerSummary.addFailure(failureCause);
        final String message = String.format("PARSING ERROR: Cause[%1S], RowNo[%2s], ColNo[%3s], Message[%3s]", failureCause, Integer
                .valueOf(exception.getFeedRowNumber()), Integer.valueOf(exception.getColumnNumber()), exception.getCause().getMessage());
        LOG.error(message);
        LOG.debug(exception);
        logToService(exception, failureCause, message);
    }

    /**
     * logToService method to persist log event to database.
     * 
     * @param exception
     *        the exception
     * @param failureCause
     *        the failure cause
     * @param message
     *        the message
     */
    private void logToService(final FeedProcessingException exception, final FailureCause failureCause, final String message) {
        final Severity severity = (failureCause == FailureCause.OTHERS ? Severity.ERROR : Severity.WARN);
        final ImportLogEventStatus status = (failureCause == FailureCause.OTHERS ? ImportLogEventStatus.FAILED
                : ImportLogEventStatus.ERROR);
        final ImportLogEventData eventData = new ImportLogEventData();
        eventData.setException(exception);
        eventData.setMessage(message);
        eventData.setSeverity(severity);
        eventData.setEventStatus(status);
        final ImportCommandContext commandContext = ImportCommandContextHolder.getContext();
        eventData.setEventType(commandContext.getLogEventType());
        eventData.setEventHint(ImportLogEventHint.valueOf(failureCause.name()));
        eventData.setThreadIdentifier(commandContext.getIdentifier());
        importLogService.logEvent(eventData);
    }

    /**
     * Gets the import log service.
     * 
     * @return the ImportLogService
     */
    public final ImportLogService getImportLogService() {
        return importLogService;
    }

    /**
     * Sets the import log service.
     * 
     * @param ImportLogService
     *        the ImportLogService to set
     */
    public final void setImportLogService(final ImportLogService importLogService) {
        this.importLogService = importLogService;
    }

    /**
     * Checks if is empty.
     * 
     * @param files
     *        the files
     * @return true, if is empty
     */
    private boolean isEmpty(final File[] files) {
        return files == null || files.length == 0;
    }

}
