package in.com.v2kart.core.order;

import in.com.v2kart.core.order.strategies.calculation.V2FindCODCostStrategy;
import in.com.v2kart.core.services.V2CalculationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.V2CODPaymentInfoModel;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

/**
 * 
 * @author vikrant2480
 * 
 */
public class V2CalculationServiceImpl extends DefaultCalculationService implements V2CalculationService {

    /** Logger bean injection */
    private final static Logger LOG = Logger.getLogger(V2CalculationServiceImpl.class);

    private final static String GIFTWRAP_COST = "giftwrap.cost";
    private V2FindCODCostStrategy findCODCostStrategy;
    private CommonI18NService v2CommonI18NService;
    private OrderRequiresCalculationStrategy v2OrderRequiresCalculationStrategy;
    private double codChargesValue = 0.0;
    private ModelService modelService;
    private PromotionsService promotionsService;
    private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;

    public PromotionsService getPromotionsService() {
	return promotionsService;
    }

    public void setPromotionsService(PromotionsService promotionsService) {
	this.promotionsService = promotionsService;
    }

    /** Constants used for v2kartPromoGrp */
    private String V2_KART_PROMO_GRP = "v2kartPromoGrp";

    public ModelService getModelService() {
	return modelService;
    }

    public void setModelService(ModelService modelService) {
	this.modelService = modelService;
    }

    @Override
    protected void resetAdditionalCosts(AbstractOrderModel order, Collection<TaxValue> relativeTaxValues) {
	super.resetAdditionalCosts(order, relativeTaxValues);
	final PriceValue codCharges = findCODCostStrategy.getCODCharges(order,
		calculateDiscountValuesForCODCharges(order, true));

	if (codCharges != null) {
	    codChargesValue = convertPriceIfNecessary(codCharges, order.getNet().booleanValue(), order.getCurrency(),
		    relativeTaxValues).getValue();
	} else {
	    codChargesValue = 0.0;
	}
    }

    @Override
    public void calculateTotalsForReturn(AbstractOrderModel order, boolean recalculate) {
	double subtotal = 0.0;
	double payment = 0.0;
	// double deliveryCharges=0.0;

	final Double giftwrap = order.getTotalGiftWrapPrice();

	try {
	    calculateTotals(order, recalculate, calculateSubtotal(order, recalculate));
	    if (order.getSubtotal() != null) {
		subtotal = order.getSubtotal().doubleValue();
	    }
	    if (order.getPaymentCost() != null) {
		payment = order.getPaymentCost().doubleValue();
	    }
	    order.setTotalGiftWrapPrice(giftwrap);
	    order.setTotalPrice(subtotal + payment + order.getDeliveryCost().doubleValue() + giftwrap);
	    getModelService().save(order);
	} catch (CalculationException e) {
	    e.printStackTrace();
	}

    }

    @Override
    protected void calculateTotals(AbstractOrderModel order, boolean recalculate,
	    Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException {
	super.calculateTotals(order, recalculate, taxValueMap);
	final Collection<TaxValue> relativeTaxValues = new LinkedList<TaxValue>();
	for (final Map.Entry<TaxValue, ?> e : taxValueMap.entrySet())
	{
	    final TaxValue taxValue = e.getKey();
	    if (!taxValue.isAbsolute())
	    {
		relativeTaxValues.add(taxValue);
	    }
	}
	final PriceValue codCharges = findCODCostStrategy.getCODCharges(order,
		calculateDiscountValuesForCODCharges(order, true));
	if (codCharges != null) {
	    codChargesValue = convertPriceIfNecessary(codCharges, order.getNet().booleanValue(), order.getCurrency(),
		    relativeTaxValues).getValue();
	} else {
	    codChargesValue = 0.0;
	}

	List<AbstractOrderEntryModel> orderEntries = order.getEntries();
	OrderModel orderModel = null;
	if (order instanceof OrderModel) {
	    orderModel = (OrderModel) order;
	}
	double giftWrapCost = 0.0;
	if (!(orderModel != null && orderModel.getReturnRequests() != null
	&& orderModel.getReturnRequests().size() != 0)) {
	    final double giftWrapCostPerProduct = Double.parseDouble(Config
		    .getParameter(GIFTWRAP_COST));
	    if (CollectionUtils.isNotEmpty(orderEntries)) {
		for (AbstractOrderEntryModel orderEntry : orderEntries) {
		    if (!OrderEntryStatus.DEAD.equals(orderEntry
			    .getQuantityStatus())) {
			if (orderEntry.isGiftWrap()) {
			    giftWrapCost = giftWrapCost
				    + (giftWrapCostPerProduct * orderEntry
					    .getQuantity());
			}
		    }
		}
		order.setTotalGiftWrapPrice(giftWrapCost);
	    }
	} else {
	    giftWrapCost = order.getTotalGiftWrapPrice();
	}
	double subtotal = 0.0;
	double payment = 0.0;
	if (order.getSubtotal() != null) {
	    subtotal = order.getSubtotal().doubleValue();
	}
	if (order.getPaymentCost() != null) {
	    payment = order.getPaymentCost().doubleValue();
	}
	final double promotions = calculateDiscountValues(order, recalculate);

	if (null != order.getPaymentInfo() && order.getPaymentInfo() instanceof V2CODPaymentInfoModel
		&& !order.getDeliveryMode().getIsPickUp()) {
	    final CurrencyModel curr = order.getCurrency();
	    final int digits = curr.getDigits().intValue();
	    if (order.getSubtotal().doubleValue() > 0) {
		order.setCodCharges(Double.valueOf(codChargesValue));
	    } else {
		order.setCodCharges(Double.valueOf(0));
	    }
	    if (null != order.getTotalPrice() && null != order.getCodCharges()) {

		// set total to total payable which is total minus wallet money
		// if applied
		// set total order price to be total price from super + cod
		// charges + gift wrap charges
		// final double subtotal = order.getSubtotal().doubleValue();
		// final double promotions = calculateDiscountValues(order,
		// recalculate);
		// final double payment = order.getPaymentCost().doubleValue();
		final double total = subtotal + payment + giftWrapCost - promotions
			+ order.getCodCharges().doubleValue() + order.getDeliveryCost().doubleValue();
		final double totalRounded = v2CommonI18NService.roundCurrency(total, digits);
		order.setTotalPrice(Double.valueOf(totalRounded));
		getModelService().save(order);
	    }
	} else {
	    final CurrencyModel curr = order.getCurrency();
	    final int digits = curr.getDigits().intValue();
	    if (null != order.getTotalPrice()) {
		// final double subtotal = order.getSubtotal().doubleValue();
		order.setCodCharges(Double.valueOf(0));
		// final double payment = order.getPaymentCost().doubleValue();
		final double total = subtotal + payment + order.getDeliveryCost().doubleValue() + giftWrapCost
			- promotions;
		// final double total = order.getTotalPrice().doubleValue() +
		// giftWrapCost;
		final double totalRounded = v2CommonI18NService.roundCurrency(total, digits);
		order.setTotalPrice(Double.valueOf(totalRounded));
		order.setCalculated(Boolean.TRUE);
		getModelService().save(order);
	    }
	}
    }

    /*
     * To calculate discount values in case of COD charges.
     */
    @SuppressWarnings("rawtypes")
    public double calculateDiscountValuesForCODCharges(final AbstractOrderModel order, final boolean recalculate) {
	if (recalculate || v2OrderRequiresCalculationStrategy.requiresCalculation(order)) {
	    final List<DiscountValue> discountValues = order.getGlobalDiscountValues();
	    if (discountValues != null && !discountValues.isEmpty()) {
		final CurrencyModel curr = order.getCurrency();
		final String iso = curr.getIsocode();

		final int digits = curr.getDigits().intValue();

		final double discountablePrice = order.getSubtotal().doubleValue()
			+ (order.isDiscountsIncludeDeliveryCost() ? order.getDeliveryCost().doubleValue() : 0.0)
			+ (order.isDiscountsIncludePaymentCost() ? order.getPaymentCost().doubleValue() : 0.0);

		final List appliedDiscounts = DiscountValue.apply(1.0, discountablePrice, digits,
			convertDiscountValues(order, discountValues), iso);
		return DiscountValue.sumAppliedValues(appliedDiscounts);
	    }
	    return 0.0;
	} else {
	    return DiscountValue.sumAppliedValues(order.getGlobalDiscountValues());
	}
    }

    @Required
    public void setFindCODCostStrategy(V2FindCODCostStrategy findCODCostStrategy) {
	this.findCODCostStrategy = findCODCostStrategy;
    }

    @Required
    public void setV2CommonI18NService(CommonI18NService v2CommonI18NService) {
	this.v2CommonI18NService = v2CommonI18NService;
    }

    @Required
    public void setV2OrderRequiresCalculationStrategy(
	    OrderRequiresCalculationStrategy v2OrderRequiresCalculationStrategy) {
	this.v2OrderRequiresCalculationStrategy = v2OrderRequiresCalculationStrategy;
    }

    @Override
    public OrderModel recalculatePromotionsForOriginalOrder(OrderModel originalOrder) {
	final PromotionGroupModel promotionGroup = promotionsService.getPromotionGroup(V2_KART_PROMO_GRP);
	final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
	promotionGroups.add(promotionGroup);
	try {
	    if (null != promotionGroups) {
		this.promotionsService.updatePromotions(promotionGroups, originalOrder);
	    }
	    calculateTotals(originalOrder, false);
	} catch (final CalculationException e) {
	    LOG.error("Could not only calculate totals only on order, trying to do it using jalo calculation", e);
	}
	return originalOrder;
    }

    @Override
    public OrderModel calculateTotalsForCancellation(OrderModel order, Boolean isPartialCancel) {
	double defaultPickUpDeliveryCost = Double.parseDouble(Config
		.getParameter("deliverycost.default.pickup.store"));
	try {
	    if (!isPartialCancel) {
		order.setDeliveryCost(0d);
	    } else {
		PriceValue deliCost = null;
		final AbstractOrder orderItem = getModelService().getSource(order);
		final DeliveryMode dMode = orderItem.getDeliveryMode();
		
		if(isAllEntriesCanceled(order.getEntries())){
			order.setDeliveryCost(0d);
		} else {
			// if order is pick in store, delivery charges should be
						// retrieved from local.properties
						if (order.getDeliveryMode().getIsPickUp()) {
						    LOG.info("Order [" + order.getCode() + "] is pick-up in store ...setting default delivery cost : "
							    + defaultPickUpDeliveryCost);
						    deliCost = new PriceValue(order.getCurrency().getIsocode(), defaultPickUpDeliveryCost, order
							    .getNet().booleanValue());
						}
						deliCost = dMode.getCost(orderItem);
						order.setDeliveryCost(Double.valueOf(deliCost.getValue()));
					    }
		}
		
	    getModelService().save(order);
	    calculateTotals(order, isPartialCancel);
	} catch (CalculationException e) {
	    LOG.error("Could not only calculate totals only on order, trying to do it using jalo calculation", e);
	} catch (JaloDeliveryModeException e) {
	    LOG.warn("Could not find deliveryCost for order [" + order.getCode() + "] due to : " + e.getMessage());
	}
	return order;
    }
    
    private boolean isAllEntriesCanceled(List<AbstractOrderEntryModel> orderEntries){
    	boolean isAllEntriesCanceled = true;
    	if (CollectionUtils.isNotEmpty(orderEntries)) {
    		for (AbstractOrderEntryModel orderEntry : orderEntries) {
    		    if (OrderEntryStatus.DEAD.equals(orderEntry
    			    .getQuantityStatus())) {
    		    	isAllEntriesCanceled = true;
    		    } else {
    		    	isAllEntriesCanceled = false;
    		    	break;
    		    }
    		}
    }
		return isAllEntriesCanceled;
    }

    @Override
    public void calculateRefundOrder(AbstractOrderModel order) throws CalculationException {
	// if (orderRequiresCalculationStrategy.requiresCalculation(order))
	// {
	// -----------------------------
	// first calc all entries
	calculateEntries(order, false);
	// -----------------------------
	// reset own values
	// final Map taxValueMap = resetAllValues(order);
	// -----------------------------
	// now calculate all totals
	calculateTotalsForReturn(order, false);
	// notify manual discouns - needed?
	// notifyDiscountsAboutCalculation();
	// }
    }
}
