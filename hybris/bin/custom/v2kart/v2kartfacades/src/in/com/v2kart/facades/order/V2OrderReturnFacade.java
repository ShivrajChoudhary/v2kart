package in.com.v2kart.facades.order;

import in.com.v2kart.facades.order.data.ReturnData;

import java.util.Map;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
/**
 * APIs for Order Return from Storefront.
 * @author shailjagupta
 *
 */
public interface V2OrderReturnFacade {
	
    /**
     * API to get tentative amount to be refunded after return
     * @param returnData
     * @return Price data
     */
	public  PriceData getRefundAmount(final ReturnData returnData);
	
	/**
	 * API to process Full Order Return
	 * @param returnData
	 */
	public void processFullOrderReturn(final ReturnData returnData);
	
	/**
	 * API to process Partial Order Return
	 * @param returnData
	 */
	public void processPartialOrderReturn(final ReturnData returnData);
	
	/**
	 * API to get all the returnable Entries for given ordercode
	 * @param orderCode
	 * @return
	 */
	public Map<OrderEntryData,Long> getAllReturnableOrderEntries(final String orderCode);
	
	/**
	 * API to check whether order is returnable or not.
	 * @param orderCode
	 * @return
	 */
	public Boolean setOrderReturnable(final String orderCode);
	
	public Map<String, String> filterRefundReasons(Map<String, String> refundReasonMap);

}
