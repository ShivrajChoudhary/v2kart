package in.com.v2kart.dataimport.importengine.impl;

import in.com.v2kart.dataimport.constants.V2kartdataimportConstants;
import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.exceptions.FeedProcessingException.FailureCause;
import in.com.v2kart.dataimport.importengine.CSVReader;
import in.com.v2kart.dataimport.importengine.exception.FileReaderException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.ICsvReader;
import org.supercsv.io.Tokenizer;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.io.dozer.ICsvDozerBeanReader;

/**
 * Concrete implementation of CSVReader extending AbstractFileReader. It wraps to SuperCSV BeanReader to read csv file and convert row items
 * to Java Object using Dozer library.
 * 
 * SuperCSV provides a fine integration with Dozer for converting row item to java object
 * 
 */
public class CSVDozerReaderImpl extends AbstractFileReader implements CSVReader {

    private static final Logger LOG = Logger.getLogger(CSVDozerReaderImpl.class);

    /**
     * ICsvDozerBeanReader instance
     */
    private ICsvDozerBeanReader beanReader;

    /**
     * DozerBeanMapper instance
     */
    private DozerBeanMapper beanMapper;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.FileReader#getNext()
     */
    @Override
    public <T extends BaseDto> T getNext() throws FileReaderException {
        BaseDto dto = null;
        final Class<? extends BaseDto> dtoClass = this.getDtoType();
        try {
            if (beanReader == null) {
                this.open();
            }
            this.readerSummary.incrementCursorPosition();
            dto = dtoClass.cast(beanReader.read(dtoClass, this.getCellProcessors()));

        } catch (final SuperCsvConstraintViolationException exp) {
            this.logException(FailureCause.CONSTRAINT_VIOLATION, exp, this.readerSummary.getCursorPosition(), exp.getCsvContext()
                    .getColumnNumber());
            dto = this.getNext();
        } catch (final SuperCsvCellProcessorException csve) {
            this.logException(FailureCause.PARSING_ERROR, csve, this.readerSummary.getCursorPosition(), csve.getCsvContext()
                    .getColumnNumber());
            dto = this.getNext();
        } catch (final SuperCsvException screwedRow) {
            // un-recoverable exception, propagate further
            throw this.logException(FailureCause.OTHERS, screwedRow, this.readerSummary.getCursorPosition(), 0);
        } catch (final IOException e) {
            throw new FileReaderException(e);
        }
        if (dto != null) {
            dto.setUnTokenizedRowString(beanReader.getUntokenizedRow());
            dto.setRowIndex(this.readerSummary.getCursorPosition());
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
    public void open() throws FileReaderException {
        if (beanReader == null) {
            try {
                beanMapper = new DozerBeanMapper();
                final Tokenizer tokenizer = new Tokenizer(new InputStreamReader(new FileInputStream(getFileToProcess()),
                        this.getCharacterEncoding()), V2kartdataimportConstants.PIPE_DELIMITED);
                beanReader = new CsvDozerBeanReader(tokenizer, V2kartdataimportConstants.PIPE_DELIMITED, beanMapper);
                final Class<? extends BaseDto> dtoClass = this.getDtoType();
                beanReader.configureBeanMapping(dtoClass, getHeaders());
                this.initManagementInfo();
                if (this.isHasFileHeader()) {
                    beanReader.getHeader(true);
                    this.readerSummary.incrementCursorPosition();
                }
            } catch (final IOException e) {
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
    public void close() throws IOException {
        if (beanReader != null) {
            beanReader.close();
            beanReader = null;
            beanMapper.destroy();
            moveToProcessedDirectory();
            beanMapper = null;
        }
    }

    /**
     * @return the beanReader
     */
    public ICsvReader getBeanReader() {
        return beanReader;
    }

}
