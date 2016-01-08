/**
 * 
 */
package in.com.v2kart.dataimport.importengine.cellprocessors;

import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * The Class StringMapperCellProcessor.
 * 
 * @author ashishgrover
 */
public class StringMapperCellProcessor extends CellProcessorAdaptor implements StringCellProcessor {

    private Map<String, String> mapping;

    /**
     * Instantiates a new string mapper cell processor.
     */
    public StringMapperCellProcessor() {
    }

    /**
     * Instantiates a new string mapper cell processor.
     * 
     * @param mapping
     *        the mapping
     * @param next
     *        the next
     */
    public StringMapperCellProcessor(final Map<String, String> mapping, final CellProcessorAdaptor next) {
        super(next);
        this.mapping = mapping;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.supercsv.cellprocessor.ift.CellProcessor#execute(java.lang.Object, org.supercsv.util.CsvContext)
     */
    @Override
    public final Object execute(final Object paramObject, final CsvContext paramCsvContext) {
        if (paramObject != null) {
            return this.next.execute(mapping.get(paramObject.toString()), paramCsvContext);
        } else {
            return null;
        }
    }

    /**
     * Gets the mapping.
     * 
     * @return the mapping
     */
    public final Map<String, String> getMapping() {
        return mapping;
    }

    /**
     * Sets the mapping.
     * 
     * @param mapping
     *        the mapping to set
     */
    public final void setMapping(final Map<String, String> mapping) {
        this.mapping = mapping;
    }

}
