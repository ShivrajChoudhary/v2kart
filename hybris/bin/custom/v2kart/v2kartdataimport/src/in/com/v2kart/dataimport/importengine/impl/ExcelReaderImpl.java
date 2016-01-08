package in.com.v2kart.dataimport.importengine.impl;

import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.exceptions.FeedProcessingException.FailureCause;
import in.com.v2kart.dataimport.importengine.ExcelReader;
import in.com.v2kart.dataimport.importengine.excel.IExcelBeanReader;
import in.com.v2kart.dataimport.importengine.excel.impl.ExcelBeanReaderImpl;
import in.com.v2kart.dataimport.importengine.exception.ExcelReaderException;
import in.com.v2kart.dataimport.importengine.exception.FileReaderException;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;

/**
 * Concrete implementation of ExcelReader extending AbstractFileReader. It reads excel file and convert row items to Java Object.
 * 
 */
public class ExcelReaderImpl extends AbstractFileReader implements ExcelReader {

    private static final Logger LOG = Logger.getLogger(ExcelReaderImpl.class);

    /**
     * IExcelBeanReader instance
     */
    private IExcelBeanReader beanReader;

    /** The excel sheet name. */
    private String excelSheetName;

    /** The no of columns. */
    private int noOfColumns;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.FileReader#getNext()
     */
    @Override
    public final <T extends BaseDto> T getNext() throws FileReaderException {
        BaseDto dto = null;
        final Class<? extends BaseDto> dtoClass = this.getDtoType();
        try {
            if (beanReader == null) {
                this.open();
            }
            this.readerSummary.incrementCursorPosition();
            dto = dtoClass.cast(beanReader.read(dtoClass, this.getHeaders(), this.getCellProcessors()));
        } catch (final SuperCsvConstraintViolationException exp) {
            this.logException(FailureCause.CONSTRAINT_VIOLATION, exp, this.readerSummary.getCursorPosition(), exp.getCsvContext()
                    .getColumnNumber());
            dto = this.getNext();
        } catch (final SuperCsvCellProcessorException csve) {
            this.logException(FailureCause.PARSING_ERROR, csve, this.readerSummary.getCursorPosition(), csve.getCsvContext()
                    .getColumnNumber());
            dto = this.getNext();
        } catch (final ExcelReaderException | SuperCsvException screwedRow) {
            // un-recoverable exception, propagate further
            throw this.logException(FailureCause.OTHERS, screwedRow, this.readerSummary.getCursorPosition(), 0);
        } catch (InvalidFormatException | IOException e) {
            throw new FileReaderException(e);
        }
        if (dto != null) {
            dto.setRowIndex(this.readerSummary.getCursorPosition());
            dto.setUnTokenizedRowString(beanReader.getUntokenizedRow());
        } else {
            this.readerSummary.sanitizeSummaryReportForLastNullRecord();
        }
        return (T) dto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.FileReader#open()
     */
    @Override
    public final void open() throws FileReaderException {
        if (beanReader == null) {
            try {
                beanReader = new ExcelBeanReaderImpl(getFileToProcess(), excelSheetName, noOfColumns);
                this.initManagementInfo();
                if (this.isHasFileHeader()) {
                    beanReader.getHeader(true);
                    this.readerSummary.incrementCursorPosition();
                }
            } catch (final ExcelReaderException screwedRow) {
                // un-recoverable exception, propagate further
                throw this.logException(FailureCause.OTHERS, screwedRow, this.readerSummary.getCursorPosition(), 0);
            } catch (InvalidFormatException | IOException e) {
                throw new FileReaderException(e);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.FileReader#close()
     */
    @PreDestroy
    @Override
    public final void close() throws IOException {
        if (beanReader != null) {
            beanReader.close();
            beanReader = null;
            moveToProcessedDirectory();
        }
    }

    /**
     * @return the beanReader
     */
    public final IExcelBeanReader getBeanReader() {
        return beanReader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.ExcelReader#getExcelSheetName()
     */
    @Override
    public final String getExcelSheetName() {
        return excelSheetName;
    }

    /**
     * @param excelSheetName
     *        the excelSheetName to set
     */
    public final void setExcelSheetName(final String excelSheetName) {
        this.excelSheetName = excelSheetName;
    }

    /**
     * @param noOfColumns
     *        the noOfColumns to set
     */
    public final void setNoOfColumns(final int noOfColumns) {
        this.noOfColumns = noOfColumns;
    }

}
