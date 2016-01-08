package in.com.v2kart.core.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.promotions.impl.DefaultPromotionsService;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.jalo.V2kartPromotionsManager;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

public class V2PromotionService extends DefaultPromotionsService {

    @Override
    protected V2kartPromotionsManager getPromotionsManager() {
        return (V2kartPromotionsManager) PromotionsManager.getInstance();
    }

    @Override
    public PromotionOrderResults updatePromotions(Collection promotionGroups,
            AbstractOrderModel order) {
        PromotionOrderResults result = getPromotionsManager().updatePromotions(
                getModelService().getAllSources(promotionGroups,
                        new ArrayList()), getOrder(order));
        refreshOrder(order);
        return result;
    }

    @Override
    public PromotionOrderResults updatePromotions(
            Collection<PromotionGroupModel> promotionGroups,
            AbstractOrderModel order, boolean evaluateRestrictions,
            PromotionsManager.AutoApplyMode productPromotionMode,
            PromotionsManager.AutoApplyMode orderPromotionMode, Date date) {
        Double giftWrapPrice = 0.0;
        if (order.getTotalGiftWrapPrice() != null) {
            giftWrapPrice = order.getTotalGiftWrapPrice();
        }
        PromotionOrderResults result = getPromotionsManager().updatePromotions(
                getSessionContext(),
                getModelService().getAllSources(promotionGroups,
                        new ArrayList()), getOrder(order),
                evaluateRestrictions, productPromotionMode, orderPromotionMode,
                date);
        refreshOrder(order);
        
        if(result.getAllProductPromotions().size()!=0){
        order.setTotalGiftWrapPrice(giftWrapPrice);
        order.setTotalPrice(order.getTotalPrice() + giftWrapPrice);
        getModelService().save(order);
        }
        return result;
    }

}
