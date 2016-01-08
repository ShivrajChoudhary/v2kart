/**
 * 
 */
package in.com.v2kart.dataimport.importengine.cellprocessors;

import de.hybris.platform.core.HybrisEnumValue;

import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * @author ashish2483
 *
 */
/**
 * EnumCellProcessor
 * 
 */
public class EnumCellProcessor extends CellProcessorAdaptor implements StringCellProcessor {

    private Map<String, Enum<? extends HybrisEnumValue>> enumMap;

    public void setEnumMap(final Map<String, Enum<? extends HybrisEnumValue>> enumMap) {
        this.enumMap = enumMap;
    }

    public Enum<? extends HybrisEnumValue> getEnumValue(final String code) {
        return enumMap.get(code);
    }

    @Override
    public Object execute(final Object value, final CsvContext context) {
        Object result;
        if (value == null) {
            result = getEnumValue("");
        } else {
            result = getEnumValue((String) value);
        }
        return next.execute(result, context);
    }
}
