/**
 *
 */
package in.com.v2kart.sapinboundintegration.strategies;

import de.hybris.platform.returns.model.ReturnRequestModel;

import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnRes;

/**
 * @author shailjagupta
 *
 */
public interface V2kartSapReturnOrderStrategy {

    SOReturnRes returnErpSales(final ReturnRequestModel returnRequestModel);

}
