package in.com.v2kart.core.order.strategies.calculation.impl;

import in.com.v2kart.core.order.strategies.calculation.V2FindCODCostStrategy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.util.PriceValue;

public class V2FindCODCostStrategyImpl extends AbstractBusinessService implements V2FindCODCostStrategy {

    private static final Logger LOG = Logger.getLogger(V2FindCODCostStrategyImpl.class);

    private CMSSiteService cmsSiteService;
    private CommonI18NService commonI18NService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.order.strategies.calculation.V2FindCODCostStrategy#getCODCharges(de.hybris.platform.core.model.order.
     * AbstractOrderModel)
     */
    @Override
    public PriceValue getCODCharges(AbstractOrderModel order, double totalDiscounts) {
        final CMSSiteModel cmsSite = getCmsSiteService().getCurrentSite();

        final CurrencyModel curr = order.getCurrency();
        final int digits = curr.getDigits().intValue();
        // subtotal
        final double subtotal = order.getSubtotal().doubleValue();
        // discounts
        final double roundedTotalDiscounts = commonI18NService.roundCurrency(totalDiscounts, digits);
        // set total
        final double total = subtotal + order.getPaymentCost().doubleValue() + order.getDeliveryCost().doubleValue()
                - roundedTotalDiscounts;
        final double totalRounded = commonI18NService.roundCurrency(total, digits);
        double minCodCharges=0.0;
        if(cmsSite!=null){
        minCodCharges =  cmsSite.getCodCharges()!=null?cmsSite.getCodCharges().doubleValue():0.0;
        }
        double codChargesOnOrder = totalRounded * 0.02 ;
        
        double codCharges = (minCodCharges < codChargesOnOrder) ? codChargesOnOrder : minCodCharges ;
           
        double codChargesRounded=commonI18NService.roundCurrency(codCharges, digits);
        return new PriceValue(order.getCurrency().getIsocode(), codChargesRounded, order.getNet().booleanValue());
        
        
     /*   // if order total price is less than COD charges applicable threshold, apply COD charges to that order
        if (null != cmsSite.getCodChargesApplicableThreshold() && null != cmsSite.getCodCharges()
                && totalRounded < cmsSite.getCodChargesApplicableThreshold().doubleValue()) {
            return new PriceValue(order.getCurrency().getIsocode(), cmsSite.getCodCharges().doubleValue(), order.getNet().booleanValue());
        }*/
       // return null;
    }

    @Required
    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    @Required
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

	public CMSSiteService getCmsSiteService() {
		return cmsSiteService;
	}

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}
}
