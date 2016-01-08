package in.com.v2kart.dataimport.importengine;

/**
 * Interface to read feeds from CSV files exported kfrom ERP System. This basically act as client for import engine system to access feeds.
 * 
 * Implementation of this interface uses the SuperCSV reader API to parse and process CSVs. They read the CSV files sequentially(row by row)
 * and keep track of current cursor position.
 * 
 */
public interface CSVReader extends FileReader {

    // No API's defined currently
}
