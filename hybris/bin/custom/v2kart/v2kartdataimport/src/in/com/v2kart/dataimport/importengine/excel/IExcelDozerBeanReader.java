package in.com.v2kart.dataimport.importengine.excel;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * The Interface IExcelDozerBeanReader.
 * 
 * @author gaurav2007
 */
public interface IExcelDozerBeanReader extends IExcelReader {

    /**
     * Configure bean mapping.
     * 
     * @param paramClass
     *        the param class
     * @param fieldMappings
     *        the field mappings
     */
    void configureBeanMapping(Class<?> paramClass, String[] fieldMappings);

    /**
     * Configure bean mapping.
     * 
     * @param paramClass
     *        the param class
     * @param fieldMappings
     *        the param array of string
     * @param hintClasses
     *        the hint classes
     */
    void configureBeanMapping(Class<?> paramClass, String[] fieldMappings, Class<?>[] hintClasses);

    /**
     * Read.
     * 
     * @param <T>
     *        the generic type
     * @param paramClass
     *        the param class
     * @return the t
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws InvalidFormatException
     *         the invalid format exception
     */
    <T> T read(Class<T> paramClass) throws IOException, InvalidFormatException;

    /**
     * Read.
     * 
     * @param <T>
     *        the generic type
     * @param paramClass
     *        the param class
     * @param paramArrayOfCellProcessor
     *        the param array of cell processor
     * @return the t
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws InvalidFormatException
     *         the invalid format exception
     */
    <T> T read(Class<T> paramClass, CellProcessor[] paramArrayOfCellProcessor) throws IOException, InvalidFormatException;

}
