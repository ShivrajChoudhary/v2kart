package in.com.v2kart.cockpits.cscockpit.utils;

import de.hybris.platform.cockpit.model.listview.impl.DefaultColumnDescriptor;
import de.hybris.platform.cockpit.services.config.ColumnConfiguration;
import de.hybris.platform.cscockpit.utils.PropertyRendererHelper;

/**
 * Class override to change the label for Principal.Uid
 * 
 * @author busamkumar
 *
 */
public class V2PropertyRendererHelper extends PropertyRendererHelper{
    
    @Override
    public String getPropertyDescriptorName(ColumnConfiguration columnConfiguration) {
        if (columnConfiguration != null) {
            DefaultColumnDescriptor descriptor = columnConfiguration.getColumnDescriptor();
            if (descriptor != null) {
                String text = descriptor.getName();
                if (text.equals("ID")) {
                    text = "Email";
                }
                if (text == null || text.isEmpty()) {
                    return (new StringBuilder("[")).append(columnConfiguration.getName()).append("]").toString();
                } else {
                    return text;
                }
            }
        }
        return "";
    }

}
