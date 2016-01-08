package in.com.v2kart.dataimport.importengine.excel.impl;

import in.com.v2kart.dataimport.importengine.excel.IExcelReader;
import in.com.v2kart.dataimport.importengine.exception.ExcelReaderException;
import in.com.v2kart.dataimport.util.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.Util;

/**
 * The Class AbstractExcelReader.
 * 
 * @author gaurav2007
 */
public abstract class AbstractExcelReader implements IExcelReader {

    /** The file. */
    private File file;

    /** The sheet name. */
    private final String sheetName;

    /** Number of columns in the file. */
    private final int noOfColumns;

    /** The columns. */
    private final List<String> columns = new ArrayList<>(0);

    /** The row number. */
    private int rowNumber = 0;

    /** The untokenized row. */
    private String untokenizedRow;

    /** The sheet. */
    private Sheet sheet;

    /**
     * Instantiates a new abstract excel reader.
     * 
     * @param file
     *        the file
     * @param sheetName
     *        the sheet name
     * @param noOfColumns
     *        the no of columns
     */
    public AbstractExcelReader(final File file, final String sheetName, final int noOfColumns) {
        if (file == null) {
            throw new NullPointerException("file should not be null");
        }
        if (sheetName == null) {
            throw new NullPointerException("sheet name should not be null");
        }
        this.file = file;
        this.sheetName = sheetName;
        this.noOfColumns = noOfColumns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.excelimportengine.services.IExcelReader#getHeader(boolean)
     */
    @Override
    public String[] getHeader(final boolean firstLineCheck) throws IOException, InvalidFormatException {
        if ((firstLineCheck) && (this.getRowNumber() != 0)) {
            throw new ExcelReaderException(String.format(
                    "Header must be fetched as the first read operation, but %d lines have already been read",
                    new Object[] { Integer.valueOf(this.getRowNumber()) }));
        }
        if (readRow()) {
            /*
             * final String returnValue = ExcelUtil.checkHeaders(getCurrentRow(), noOfColumns); if
             * (!Boolean.valueOf(returnValue).booleanValue()) { throw new ExcelReaderException(returnValue); }
             */
            return (this.columns.toArray(new String[this.columns.size()]));
        }
        return null;
    }

    /**
     * Gets the.
     * 
     * @param n
     *        the n
     * @return the string
     */
    @Override
    public String get(final int n) {
        return (this.columns.get(n - 1));
    }

    /**
     * Gets the untokenized row.
     * 
     * @return the untokenized row
     */
    @Override
    public String getUntokenizedRow() {
        return untokenizedRow;
    }

    /**
     * Gets the row number.
     * 
     * @return the row number
     */
    @Override
    public int getRowNumber() {
        return this.rowNumber;
    }

    /**
     * Length.
     * 
     * @return the int
     */
    @Override
    public int length() {
        return this.columns.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        this.rowNumber = 0;
        this.sheet = null;
        this.file = null;
        this.columns.clear();
    }

    /**
     * Gets the columns.
     * 
     * @return the columns
     */
    protected List<String> getColumns() {
        return this.columns;
    }

    /**
     * Read row.
     * 
     * @return true, if successful
     * @throws InvalidFormatException
     *         the invalid format exception
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     */
    protected boolean readRow() throws InvalidFormatException, IOException {
        if (sheet == null) {
            initiateSheet();
        }
        final Row row = getCurrentRow();
        if (!ExcelUtil.isRowEmpty(row, this.noOfColumns)) {
            // if row is empty it will assumed as end of data
            return false;
        }
        if (ExcelUtil.readColumns(row, this.noOfColumns, this.columns)) {
            untokenizedRow = columns.toString();
            incrementRowNumber();
            return true;
        }
        return false;
    }

    /**
     * Execute processors.
     * 
     * @param processedColumns
     *        the processed columns
     * @param processors
     *        the processors
     * @return the list
     */
    protected List<Object> executeProcessors(final List<Object> processedColumns, final CellProcessor[] processors) {
        Util.executeCellProcessors(processedColumns, getColumns(), processors, getRowNumber(), getRowNumber());
        return processedColumns;
    }

    /**
     * Initiate.
     * 
     * @throws ExcelReaderException
     *         the excel reader exception
     * @throws InvalidFormatException
     *         the invalid format exception
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     */
    private void initiateSheet() throws InvalidFormatException, IOException {
        if (ExcelUtil.isEmptyFile(this.file)) {
            throw new ExcelReaderException("Invalid File length");
        }
        this.sheet = this.getSheet();
        if (this.sheet == null) {
            throw new ExcelReaderException(String.format("No sheet present with name:  %s", this.sheetName));
        }
    }

    /**
     * Gets the current row.
     * 
     * @return the current row
     */
    private Row getCurrentRow() {
        return this.sheet.getRow(this.rowNumber);
    }

    /**
     * Increment row number.
     */
    private void incrementRowNumber() {
        this.rowNumber++;
    }

    /**
     * Gets the sheet.
     * 
     * @return the sheet
     * @throws InvalidFormatException
     *         the invalid format exception
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     */
    private Sheet getSheet() throws InvalidFormatException, IOException {
        if (file == null) {
            throw new NullPointerException("file should not be null");
        }
        if (sheetName == null) {
            throw new NullPointerException("sheet name should not be null");
        }
        final Workbook workbook = this.createWorkbook();
        sheet = workbook.getSheet(sheetName);
        return sheet;
    }

    /**
     * Creates the workbook.
     * 
     * @return the workbook
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws InvalidFormatException
     *         the invalid format exception
     */
    private Workbook createWorkbook() throws IOException, InvalidFormatException {
        if (!(file.exists())) {
            throw new FileNotFoundException(file.toString());
        }
        OPCPackage opcPackage = null;
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file);) {
                return new HSSFWorkbook(fileInputStream);
            } catch (final OfficeXmlFileException e) {
                opcPackage = OPCPackage.open(file);
            }
            return new XSSFWorkbook(opcPackage);
        } finally {
            if (opcPackage != null) {
                opcPackage.close();
            }
        }
    }

}
