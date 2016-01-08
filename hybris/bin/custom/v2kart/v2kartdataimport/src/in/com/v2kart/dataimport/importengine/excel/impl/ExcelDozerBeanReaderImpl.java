/**
 * 
 */
package in.com.v2kart.dataimport.importengine.excel.impl;

import in.com.v2kart.dataimport.importengine.excel.IExcelDozerBeanReader;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOption;
import org.dozer.loader.api.FieldsMappingOptions;
import org.dozer.loader.api.TypeMappingBuilder;
import org.dozer.loader.api.TypeMappingOption;
import org.dozer.loader.api.TypeMappingOptions;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanData;

/**
 * The Class ExcelDozerBeanReaderImpl.
 * 
 * @author gaurav2007
 */
public class ExcelDozerBeanReaderImpl extends AbstractExcelReader implements IExcelDozerBeanReader {

    private final DozerBeanMapper dozerBeanMapper;
    private final CsvDozerBeanData beanData = new CsvDozerBeanData();

    /**
     * Instantiates a new excel dozer bean reader impl.
     * 
     * @param file
     *        the file
     * @param sheetName
     *        the sheet name
     * @param noOfColumns
     *        the no of columns
     */
    public ExcelDozerBeanReaderImpl(final File file, final String sheetName, final int noOfColumns) {
        super(file, sheetName, noOfColumns);
        this.dozerBeanMapper = new DozerBeanMapper();
    }

    /**
     * Instantiates a new excel dozer bean reader impl.
     * 
     * @param file
     *        the file
     * @param sheetName
     *        the sheet name
     * @param noOfColumns
     *        the no of columns
     * @param dozerBeanMapper
     *        the dozer bean mapper
     */
    public ExcelDozerBeanReaderImpl(final File file, final String sheetName, final int noOfColumns, final DozerBeanMapper dozerBeanMapper) {
        super(file, sheetName, noOfColumns);
        this.dozerBeanMapper = dozerBeanMapper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.excel.IExcelDozerBeanReader#configureBeanMapping(java.lang.Class, java.lang.String[])
     */
    @Override
    public void configureBeanMapping(final Class<?> paramClass, final String[] fieldMapping) {
        this.dozerBeanMapper.addMapping(new MappingBuilder(paramClass, fieldMapping));
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.excel.IExcelDozerBeanReader#configureBeanMapping(java.lang.Class, java.lang.String[],
     * java.lang.Class[])
     */
    @Override
    public void configureBeanMapping(final Class<?> paramClass, final String[] fieldMapping, final Class<?>[] hintTypes) {
        this.dozerBeanMapper.addMapping(new MappingBuilder(paramClass, fieldMapping, hintTypes));

    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.excel.IExcelDozerBeanReader#read(java.lang.Class)
     */
    @Override
    public <T> T read(final Class<T> paramClass) throws IOException, InvalidFormatException {
        if (paramClass == null) {
            throw new NullPointerException("clazz should not be null");
        }

        if (readRow()) {
            this.beanData.getColumns().clear();
            this.beanData.getColumns().addAll(getColumns());
            return this.dozerBeanMapper.map(this.beanData, paramClass);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.excel.IExcelDozerBeanReader#read(java.lang.Class,
     * org.supercsv.cellprocessor.ift.CellProcessor[])
     */
    @Override
    public <T> T read(final Class<T> paramClass, final CellProcessor[] paramArrayOfCellProcessor) throws IOException,
            InvalidFormatException {
        if (paramClass == null) {
            throw new NullPointerException("clazz should not be null");
        }
        if (paramArrayOfCellProcessor == null) {
            throw new NullPointerException("processors should not be null");
        }

        if (readRow()) {
            executeProcessors(this.beanData.getColumns(), paramArrayOfCellProcessor);
            return this.dozerBeanMapper.map(this.beanData, paramClass);
        }

        return null;
    }

    /**
     * The Class MappingBuilder.
     */
    private static class MappingBuilder extends BeanMappingBuilder {
        private final Class<?> clazz;
        private final String[] fieldMapping;
        private final Class<?>[] hintTypes;

        /**
         * Instantiates a new mapping builder.
         * 
         * @param clazz
         *        the clazz
         * @param fieldMapping
         *        the field mapping
         */
        public MappingBuilder(final Class<?> clazz, final String[] fieldMapping) {
            if (clazz == null) {
                throw new NullPointerException("clazz should not be null");
            }
            if (fieldMapping == null) {
                throw new NullPointerException("fieldMapping should not be null");
            }
            this.clazz = clazz;
            this.fieldMapping = fieldMapping;
            this.hintTypes = null;
        }

        /**
         * Instantiates a new mapping builder.
         * 
         * @param clazz
         *        the clazz
         * @param fieldMapping
         *        the field mapping
         * @param hintTypes
         *        the hint types
         */
        public MappingBuilder(final Class<?> clazz, final String[] fieldMapping, final Class<?>[] hintTypes) {
            if (clazz == null) {
                throw new NullPointerException("clazz should not be null");
            }
            if (fieldMapping == null) {
                throw new NullPointerException("fieldMapping should not be null");
            }
            if (hintTypes == null) {
                throw new NullPointerException("fieldMapping should not be null");
            }
            if (fieldMapping.length != hintTypes.length) {
                throw new IllegalArgumentException(String.format("hintTypes length(%d) should match fieldMapping length(%d)", new Object[] {
                        Integer.valueOf(hintTypes.length), Integer.valueOf(fieldMapping.length) }));
            }

            this.clazz = clazz;
            this.fieldMapping = fieldMapping;
            this.hintTypes = hintTypes;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.dozer.loader.api.BeanMappingBuilder#configure()
         */
        @Override
        protected void configure() {
            final TypeMappingBuilder mappingBuilder = mapping(CsvDozerBeanData.class, this.clazz, new TypeMappingOption[] {
                    TypeMappingOptions.oneWay(), TypeMappingOptions.wildcard(false), TypeMappingOptions.mapNull(true) });

            for (int i = 0; i < this.fieldMapping.length; ++i) {
                final String mapping = this.fieldMapping[i];

                if (mapping == null) {
                    continue;
                }

                if ((this.hintTypes != null) && (this.hintTypes[i] != null)) {
                    mappingBuilder.fields("columns[" + i + "]", mapping,
                            new FieldsMappingOption[] { FieldsMappingOptions.hintB(new Class[] { this.hintTypes[i] }) });
                } else {
                    mappingBuilder.fields("columns[" + i + "]", mapping, new FieldsMappingOption[0]);
                }
            }
        }
    }

}
