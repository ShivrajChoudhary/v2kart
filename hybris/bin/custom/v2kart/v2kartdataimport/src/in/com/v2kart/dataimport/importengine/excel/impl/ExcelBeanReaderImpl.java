package in.com.v2kart.dataimport.importengine.excel.impl;

import in.com.v2kart.dataimport.importengine.excel.IExcelBeanReader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.util.BeanInterfaceProxy;
import org.supercsv.util.MethodCache;

/**
 * The Class ExcelBeanReaderImpl.
 * 
 * @author gaurav2007
 */
public class ExcelBeanReaderImpl extends AbstractExcelReader implements IExcelBeanReader {

    /** The processed columns. */
    private final List<Object> processedColumns = new ArrayList<>(0);

    /** The cache. */
    private final MethodCache cache = new MethodCache();

    /**
     * Instantiates a new excel bean reader impl.
     * 
     * @param file
     *        the file
     * @param sheetName
     *        the sheet name
     * @param noOfColumns
     *        the no of columns
     */
    public ExcelBeanReaderImpl(final File file, final String sheetName, final int noOfColumns) {
        super(file, sheetName, noOfColumns);
    }

    /**
     * Instantiate bean.
     * 
     * @param <T>
     *        the generic type
     * @param clazz
     *        the clazz
     * @return the t
     */
    private static <T> T instantiateBean(final Class<T> clazz) {
        T bean;
        if (clazz.isInterface()) {
            bean = BeanInterfaceProxy.createProxy(clazz);
        } else {
            try {
                bean = clazz.newInstance();
            } catch (final InstantiationException e) {
                throw new SuperCsvReflectionException(String.format(
                        "error instantiating bean, check that %s has a default no-args constructor", new Object[] { clazz.getName() }), e);
            } catch (final IllegalAccessException e) {
                throw new SuperCsvReflectionException("error instantiating bean", e);
            }
        }
        return bean;
    }

    /**
     * Invoke setter.
     * 
     * @param bean
     *        the bean
     * @param setMethod
     *        the set method
     * @param fieldValue
     *        the field value
     */
    private static void invokeSetter(final Object bean, final Method setMethod, final Object fieldValue) {
        try {
            setMethod.invoke(bean, new Object[] { fieldValue });
        } catch (final Exception e) {
            throw new SuperCsvReflectionException(String.format("error invoking method %s()", new Object[] { setMethod.getName() }), e);
        }
    }

    /**
     * Populate bean.
     * 
     * @param <T>
     *        the generic type
     * @param clazz
     *        the clazz
     * @param nameMapping
     *        the name mapping
     * @return the t
     */
    private <T> T populateBean(final Class<T> clazz, final String[] nameMapping) {
        final T resultBean = instantiateBean(clazz);

        for (int i = 0; i < nameMapping.length; ++i) {
            final Object fieldValue = this.processedColumns.get(i);

            if (nameMapping[i] == null) {
                continue;
            }
            if (fieldValue == null) {
                continue;
            }

            final Method setMethod = this.cache.getSetMethod(resultBean, nameMapping[i], fieldValue.getClass());
            invokeSetter(resultBean, setMethod, fieldValue);

        }
        return resultBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.excel.services.IExcelBeanReader#read(java.lang.Class, java.lang.String[])
     */
    @Override
    public <T> T read(final Class<T> clazz, final String[] nameMapping) throws IOException, InvalidFormatException {
        if (clazz == null) {
            throw new NullPointerException("clazz should not be null");
        }
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }

        if (readRow()) {
            if (nameMapping.length != length()) {
                throw new IllegalArgumentException(
                        String.format(
                                "the nameMapping array and the number of columns read should be the same size (nameMapping length = %d, columns = %d)",
                                new Object[] { Integer.valueOf(nameMapping.length), Integer.valueOf(length()) }));
            }

            this.processedColumns.clear();
            this.processedColumns.addAll(getColumns());
            return populateBean(clazz, nameMapping);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.importengine.excelimportengine.services.IExcelBeanReader#read(java.lang.Class, java.lang.String[],
     * org.supercsv.cellprocessor.ift.CellProcessor[])
     */
    @Override
    public <T> T read(final Class<T> clazz, final String[] nameMapping, final CellProcessor[] processors) throws IOException,
            InvalidFormatException {
        if (clazz == null) {
            throw new NullPointerException("clazz should not be null");
        }
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        if (processors == null) {
            throw new NullPointerException("processors should not be null");
        }
        if (readRow()) {
            executeProcessors(this.processedColumns, processors);
            return populateBean(clazz, nameMapping);
        }
        return null;
    }

}
