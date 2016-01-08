/**
 * 
 */
package in.com.v2kart.sapinboundintegration.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;

import in.com.v2kart.sapinboundintegration.ws.order.ObjectFactory;
import in.com.v2kart.sapinboundintegration.ws.order.SOCreateReq;

import org.apache.commons.lang.StringUtils;

/**
 * @author satvir_nagarro
 * 
 */
public abstract class V2AbstractOrderCreationReqPopulator implements Populator<OrderModel, SOCreateReq.OrderCreationReq> {

    protected final ObjectFactory objectFactory = new ObjectFactory();

    private final static String BLANK_STR = "";

    protected final static String SAP_INBOUND_DATE_FORMAT = "ddMMyyyy";

    /**
     * Returns Blank String if null
     * 
     * @param str
     *        str
     * @return str
     */
    public String getBlankIfNull(final String str) {
        if (StringUtils.isEmpty(str)) {
            return BLANK_STR;
        } else {
            return str;
        }
    }

}
