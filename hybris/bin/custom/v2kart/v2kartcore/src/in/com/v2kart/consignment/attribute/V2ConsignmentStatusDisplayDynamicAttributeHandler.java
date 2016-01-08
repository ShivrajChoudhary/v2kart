/**
 * 
 */
package in.com.v2kart.consignment.attribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;

/**
 * @author shubhammaheshwari
 * 
 */
public class V2ConsignmentStatusDisplayDynamicAttributeHandler implements DynamicAttributeHandler<String, ConsignmentModel> {

    private final Map<String, String> statusDisplayMap = new HashMap<>();

    private String defaultStatus;

    public String getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(final String defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public void setStatusDisplayMap(final Map<String, String> statusDisplayMap) {
        this.statusDisplayMap.putAll(statusDisplayMap);
    }

    protected Map<String, String> getStatusDisplayMap() {
        if (statusDisplayMap == null) {
            return Collections.emptyMap();
        } else {
            return statusDisplayMap;
        }
    }

    @Override
    public String get(final ConsignmentModel consignment) {
        String statusLocalisationKey = getDefaultStatus();

        if (consignment != null && consignment.getStatus() != null) {
            final ConsignmentStatus statusCode = consignment.getStatus();
            final String statusDisplayEntry = getStatusDisplayMap().get(statusCode.getCode());
            if (statusDisplayEntry != null) {
                statusLocalisationKey = statusDisplayEntry;
            }
        }

        if (statusLocalisationKey == null || statusLocalisationKey.isEmpty()) {
            return "";
        }
        return Localization.getLocalizedString(statusLocalisationKey);
    }

    @Override
    public void set(final ConsignmentModel model, final String value) {
        throw new UnsupportedOperationException();
    }

}
