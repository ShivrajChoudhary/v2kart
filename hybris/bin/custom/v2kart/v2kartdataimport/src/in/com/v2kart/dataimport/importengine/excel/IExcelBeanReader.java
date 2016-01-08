package in.com.v2kart.dataimport.importengine.excel;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * The Interface IExcelBeanReader.
 * 
 * @author gaurav2007
 */
public interface IExcelBeanReader extends IExcelReader {

    /**
     * Read.
     * 
     * @param <T>
     *        the generic type
     * @param paramClass
     *        the param class
     * @param paramArrayOfString
     *        the param array of string
     * @return the t
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws InvalidFormatException
     *         the invalid format exception
     */
    <T> T read(Class<T> paramClass, String[] paramArrayOfString) throws IOException, InvalidFormatException;

    /**
     * Read.
     * 
     * @param <T>
     *        the generic type
     * @param paramClass
     *        the param class
     * @param paramArrayOfString
     *        the param array of string
     * @param paramArrayOfCellProcessor
     *        the param array of cell processor
     * @return the t
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws InvalidFormatException
     *         the invalid format exception
     */
    <T> T read(Class<T> paramClass, String[] paramArrayOfString, CellProcessor[] paramArrayOfCellProcessor) throws IOException,
            InvalidFormatException;

}
