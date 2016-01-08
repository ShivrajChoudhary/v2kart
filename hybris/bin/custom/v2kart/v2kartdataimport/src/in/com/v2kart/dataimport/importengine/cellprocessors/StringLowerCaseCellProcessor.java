/**
 * 
 */
package in.com.v2kart.dataimport.importengine.cellprocessors;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * @author shubhammaheshwari
 * 
 */
public class StringLowerCaseCellProcessor extends CellProcessorAdaptor implements StringCellProcessor {

    public StringLowerCaseCellProcessor() {
    }

    public StringLowerCaseCellProcessor(StringCellProcessor next) {
        super(next);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.supercsv.cellprocessor.ift.CellProcessor#execute(java.lang.Object, org.supercsv.util.CsvContext)
     */
    @Override
    public Object execute(final Object value, final CsvContext context) {
        if (null != value) {
            return next.execute(((String) value).toLowerCase(), context);
        } else {
            return null;
        }
    }
}
