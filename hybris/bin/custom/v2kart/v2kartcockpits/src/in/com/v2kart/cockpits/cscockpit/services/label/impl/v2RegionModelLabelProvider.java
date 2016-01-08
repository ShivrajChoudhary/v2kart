package in.com.v2kart.cockpits.cscockpit.services.label.impl;

import de.hybris.platform.cockpit.services.label.AbstractModelLabelProvider;
import de.hybris.platform.core.model.c2l.RegionModel;

/**
 * This class extends the AbstractModelLabelProvider to override the default regions populated in region field while setting customer adds.
 * By Default the Region Names are ISO-Code so to change the default behavior we have override the getItemLabel method.
 * 
 * @author shivanichopra
 */
public class v2RegionModelLabelProvider extends AbstractModelLabelProvider<RegionModel> {

    /**
     * Get the icon path
     *  @param region
     *        the region model
     */
    @Override
    protected String getIconPath(RegionModel region) {
        return null;
    }

    /**
     * Get the icon path
     *  @param region
     *        the region model
     *  @param arg1

     */
    @Override
    protected String getIconPath(RegionModel region, String arg1) {
        return null;
    }

    /**
     * Return the item description
     * @param region
     *        the region model
     */
    @Override
    protected String getItemDescription(RegionModel region) {
        return "";
    }
    /**
     * Get the icon path
     *  @param region
     *        the region model
     *  @param arg1

     */
    @Override
    protected String getItemDescription(RegionModel region, String arg1) {
        return "";
    }

    /**
     * This method returns the region label
     * 
     * @param region
     *        region for which label is to be created
     * @param arg1
     * @return string the formed label
     * 
     */
    @Override
    protected String getItemLabel(RegionModel region, String arg1) {
        return getItemLabel(region);
    }

    /**
     * Prepare the item label for given region
     * 
     * @param region
     *        the region model
     * @return itemLabel
     */
    @Override
    protected String getItemLabel(RegionModel region) {
        String name = region.getName();
        String symbol = region.getIsocode();
        return (new StringBuilder(String.valueOf(name != null ? ((Object) (name)) : ""))).append(
                symbol != null && symbol.length() >= 1 ? (new StringBuilder(" (")).append(symbol).append(")").toString() : "").toString();
    }
}
