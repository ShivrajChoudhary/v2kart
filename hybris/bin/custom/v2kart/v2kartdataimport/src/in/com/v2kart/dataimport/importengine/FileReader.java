package in.com.v2kart.dataimport.importengine;

import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.importengine.exception.FileReaderException;

import java.io.IOException;

/**
 * Interface to read feeds from files exported from ERP System. This basically act as client for import engine system to access feeds.
 * 
 * Implementation of this interface uses the SuperCSV reader and POI reader API to parse and process CSVs and Excel files. They read the
 * files sequentially(row by row) and keep track of current cursor position.
 * 
 */
public interface FileReader {

    /**
     * Returns the default file name
     * 
     * @return CSVFileName
     */
    String getDefaultFileName();

    /**
     * Gets the directory path.
     * 
     * @return the directory path
     */
    String getDirectoryPath();

    /**
     * Returns headers of file if any
     * 
     * @return header array
     */
    String[] getHeaders();

    /**
     * Function returning next row item.
     * 
     * @param <T>
     *        the generic type
     * @return DTO extending BaseDto
     * @throws FileReaderException
     *         the file reader exception
     */
    <T extends BaseDto> T getNext() throws FileReaderException;

    /**
     * Opens a connection to file system to read CSV file.
     * 
     * @throws FileReaderException
     *         the file reader exception
     */
    void open() throws FileReaderException;

    /**
     * Close function to close streams and release resources.
     * 
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     */
    void close() throws IOException;

    /**
     * Exposes management info about CSVReader and ExcelReader
     * 
     * @return ReaderManagementInfo instance
     */
    ReaderSummaryInfo getReaderSummaryInfo();

}
