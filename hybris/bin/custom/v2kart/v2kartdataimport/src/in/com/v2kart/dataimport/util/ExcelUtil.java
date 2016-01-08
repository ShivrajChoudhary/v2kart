package in.com.v2kart.dataimport.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * The Class ExcelUtil.
 */
public class ExcelUtil {

    private static final String TRUE = "TRUE";
    private static final String HEADER_ROW_NOT_PRESENT = "HEADER_ROW_NOT_PRESENT";
    private static final String HEADER_ROW_NOT_STRING = "HEADER_ROW_NOT_STRING";
    private static final String NUM_OF_COLS_INVALID = "NUM_OF_COLS_INVALID";

    /**
     * Checks the length of file.
     * 
     * @param file
     *        file uploaded
     * @return - true if file is empty.
     */
    public static boolean isEmptyFile(final File file) {
        return (file.length() == 0);
    }

    /**
     * Checks if row is empty.
     * 
     * @param row
     *        the row
     * @param noOfColumns
     *        the no of columns
     * @return true, if is valid row
     */
    public static boolean isRowEmpty(final Row row, final int noOfColumns) {
        boolean isValidRow = false;
        if (row != null) {
            Cell cell = null;
            for (int colCount = 0; colCount < noOfColumns; colCount++) {
                cell = row.getCell(colCount);
                // if any single cell in the row has data, then the row is
                // considered valid and its data will be read
                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                    isValidRow = true;
                    break;
                }
            }
        }
        return isValidRow;
    }

    /**
     * Read columns.
     * 
     * @param row
     *        the row
     * @param noOfColumns
     *        the no of columns
     * @param columns
     *        the columns
     * @return true, if successful
     */
    public static boolean readColumns(final Row row, final int noOfColumns, final List<String> columns) {
        if (columns == null) {
            throw new NullPointerException("columns should not be null");
        }
        if (row == null) {
            throw new NullPointerException("row should not be null");
        }
        columns.clear();
        for (int colCount = 0; colCount < noOfColumns; colCount++) {
            final Cell cell = row.getCell(colCount);
            if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                columns.add(null);
            } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                columns.add(cell.getStringCellValue());
            } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                switch (cell.getCachedFormulaResultType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    evaluteNumericValue(columns, cell);
                    break;
                case Cell.CELL_TYPE_STRING:
                    columns.add(cell.getStringCellValue());
                    break;
                default:
                    break;
                }
            } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                evaluteNumericValue(columns, cell);
            } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                columns.add(new Boolean(cell.getBooleanCellValue()).toString());
            }
        }
        return true;
    }

    /**
     * Evalute numeric value.
     * 
     * @param columns
     *        the columns
     * @param cell
     *        the cell
     */
    private static void evaluteNumericValue(final List<String> columns, final Cell cell) {
        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            final Date date = cell.getDateCellValue();
            columns.add(DateUtil.getDateStringForView(date));
        } else {
            final Double cellVal = new Double(cell.getNumericCellValue());
            final NumberFormat format = NumberFormat.getInstance();
            if (format instanceof DecimalFormat) {
                ((DecimalFormat) format).setDecimalSeparatorAlwaysShown(false);
            }
            format.setParseIntegerOnly(true);
            format.setGroupingUsed(false);
            format.setMaximumFractionDigits(Integer.MAX_VALUE);
            format.setMaximumIntegerDigits(Integer.MAX_VALUE);
            columns.add(format.format(cellVal));
        }
    }

    /**
     * This API checks the header row. It add errors in dataDto if sub-header row is invalid
     * 
     * @param headerRow
     *        the header row
     * @param colCount
     *        number of columns expected in the excel sheet
     * @return true if headers in the file are correct, else error message
     */
    public static String checkHeaders(final Row headerRow, final int colCount) {
        final List<String> headerList = new ArrayList<String>(0);
        if (headerRow == null) {
            return HEADER_ROW_NOT_PRESENT;
        }
        final int cols = headerRow.getLastCellNum();
        for (int colNum = 0; colNum < cols; colNum++) {
            // this will return null for a blank column
            final Cell caption = headerRow.getCell(colNum, Row.RETURN_BLANK_AS_NULL);

            // when any column value is null
            if (caption == null && colNum < colCount) {
                return HEADER_ROW_NOT_STRING;
            } else if (caption == null && colNum >= colCount) {
                // when empty cells exist after the desired number of columns,
                // ignore them
                continue;
            } else {
                // when the cell contains a value
                if (caption.getCellType() != Cell.CELL_TYPE_STRING) {
                    return HEADER_ROW_NOT_STRING;
                }

                // If header is blank, it should not be considered as a header.
                if (caption.getStringCellValue() == null || caption.getStringCellValue().isEmpty()) {
                    break;
                }
                headerList.add(caption.getStringCellValue().trim());
            }
        }
        if (headerList.size() != colCount) {
            return NUM_OF_COLS_INVALID;
        }
        return TRUE;
    }

}
