package in.com.v2kart.dataimport.importengine.excel;

import java.io.Closeable;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * The Interface IExcelReader.
 * 
 * @author gaurav2007
 */
public interface IExcelReader extends Closeable {

    /**
     * Gets the.
     * 
     * @param paramInt
     *        the param int
     * @return the string
     */
    String get(int paramInt);

    /**
     * Gets the header.
     * 
     * @param paramBoolean
     *        the param boolean
     * @return the header
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws InvalidFormatException
     *         the invalid format exception
     */
    String[] getHeader(boolean paramBoolean) throws IOException, InvalidFormatException;

    /**
     * Gets the untokenized row.
     * 
     * @return the untokenized row
     */
    String getUntokenizedRow();

    /**
     * Gets the row number.
     * 
     * @return the row number
     */
    int getRowNumber();

    /**
     * Length.
     * 
     * @return the int
     */
    int length();

}
