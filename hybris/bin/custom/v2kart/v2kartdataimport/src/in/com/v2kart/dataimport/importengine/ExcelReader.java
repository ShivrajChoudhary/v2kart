package in.com.v2kart.dataimport.importengine;

/**
 * Interface to read feeds from excel files exported from ERP System. This basically act as client for import engine system to access feeds.
 * 
 * Implementation of this interface read the excel files sequentially(row by row) and keep track of current cursor position.
 * 
 */
public interface ExcelReader extends FileReader {

    /**
     * Returns the Excel sheet name
     * 
     * @return ExcelSheetName
     */
    String getExcelSheetName();

}
