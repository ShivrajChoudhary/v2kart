package de.hybris.platform.promotions.jalo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.promotions.util.Helper;
import de.hybris.platform.tx.Transaction;

public class V2kartPromotionsManager extends PromotionsManager {

    private static final Logger LOG = Logger.getLogger(V2kartPromotionsManager.class.getName());
    
    @Override
    public PromotionOrderResults updatePromotions(SessionContext ctx, Collection<PromotionGroup> promotionGroups, AbstractOrder order,
            boolean evaluateRestrictions, AutoApplyMode productPromotionMode, AutoApplyMode orderPromotionMode, Date date)
    {
        try
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("updatePromotions for [" + order + "] promotionGroups=[" + Helper.join(promotionGroups) +
                        "] evaluateRestrictions=[" + evaluateRestrictions + "] productPromotionMode=[" + productPromotionMode +
                        "] orderPromotionMode=[" + orderPromotionMode + "] date=[" + date + "]");
            }

            if ((promotionGroups == null) || (order == null))
                return null;
            if (date == null)
            {
                date = Helper.getDateNowRoundedToMinute();
            }

            if (!(order.isCalculated(ctx).booleanValue()))
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("updatePromotions order [" + order + "] not calculated, calculating");
                }
                order.calculate(date);
            }

            List<String> promotionResultsToKeepApplied = new ArrayList();

            List<PromotionResult> currResults = getPromotionResultsInternal(ctx, order);
            double oldTotalAppliedDiscount = 0.0D;
            if ((currResults != null) && (!(currResults.isEmpty())))
            {
                for (PromotionResult pr : currResults)
                {
                    if (!(pr.getFired(ctx)))
                        continue;
                    boolean prApplied = pr.isApplied(ctx);
                    if (prApplied)
                    {
                        oldTotalAppliedDiscount += pr.getTotalDiscount(ctx);
                    }

                    if ((!(pr.isValid(ctx)))
                            || (
                            (((productPromotionMode != AutoApplyMode.KEEP_APPLIED) || (!(pr.getPromotion(ctx) instanceof ProductPromotion)))) && (((orderPromotionMode != AutoApplyMode.KEEP_APPLIED)
                                    ||
                                    (!(pr
                                            .getPromotion(ctx) instanceof OrderPromotion)) || (!(prApplied))))))
                        continue;
                    String prKey = pr.getDataUnigueKey(ctx);
                    if ((prKey == null) || (prKey.length() <= 0))
                        continue;
                    if (LOG.isDebugEnabled())
                    {
                        LOG.debug("updatePromotions found applied PromotionResult [" + pr + "] key [" + prKey +
                                "] that should be reapplied");
                    }
                    promotionResultsToKeepApplied.add(prKey);
                }

            }

            deleteStoredPromotionResults(ctx, order, true);

            Collection products = getBaseProductsForOrder(ctx, order);

            List<AbstractPromotion> activePromotions = findOrderAndProductPromotionsSortByPriority(ctx, getSession(),
                    promotionGroups, products, date);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("updatePromotions found [" + activePromotions.size() + "] promotions to run");
            }

            List results = new LinkedList();

            double newTotalAppliedDiscount = 0.0D;
            PromotionEvaluationContext promoContext;
            if (!(activePromotions.isEmpty()))
            {
                //ArrayList vouchers = fixupVouchersRemoveVouchers(ctx, order);

                promoContext = new PromotionEvaluationContext(order, evaluateRestrictions, date);

                for (AbstractPromotion promotion : activePromotions)
                {
                    if (LOG.isDebugEnabled())
                    {
                        LOG.debug("updatePromotions evaluating promotion [" + promotion + "]");
                    }
                    final List<PromotionResult> promoResults = evaluatePromotion(ctx, promoContext, promotion);
                    if (LOG.isDebugEnabled())
                    {
                        LOG.debug("updatePromotions promotion [" + promotion + "] returned [" + promoResults.size() + "] results");
                    }

                    boolean autoApply = false;
                    boolean keepApplied = false;

                    if (((productPromotionMode == AutoApplyMode.APPLY_ALL) && (orderPromotionMode == AutoApplyMode.APPLY_ALL)) ||
                            ((productPromotionMode == AutoApplyMode.APPLY_ALL) && (promotion instanceof ProductPromotion)) || (
                            (orderPromotionMode == AutoApplyMode.APPLY_ALL) && (promotion instanceof OrderPromotion)))
                    {
                        autoApply = true;
                    }
                    else if (((productPromotionMode == AutoApplyMode.KEEP_APPLIED) && (orderPromotionMode == AutoApplyMode.KEEP_APPLIED)) ||
                            ((productPromotionMode == AutoApplyMode.KEEP_APPLIED) && (promotion instanceof ProductPromotion)) || (
                            (orderPromotionMode == AutoApplyMode.KEEP_APPLIED) && (promotion instanceof OrderPromotion)))
                    {
                        keepApplied = true;
                    }

                    boolean needsCalculateTotals = false;

                    if ((autoApply) || (keepApplied))
                    {
                        for (PromotionResult pr : promoResults)
                        {
                            if (!(pr.getFired(ctx)))
                                continue;
                            if (autoApply)
                            {
                                if (LOG.isDebugEnabled())
                                {
                                    LOG.debug("updatePromotions auto applying result [" + pr + "] from promotion [" + promotion +
                                            "]");
                                }
                                needsCalculateTotals |= pr.apply(ctx);

                                newTotalAppliedDiscount += pr.getTotalDiscount(ctx);
                            } else {
                                if (!(keepApplied))
                                    continue;
                                String prKey = pr.getDataUnigueKey(ctx);
                                if ((prKey == null) || (prKey.length() == 0))
                                {
                                    LOG.error("updatePromotions promotion result [" + pr + "] from promotion [" + promotion +
                                            "] returned NULL or Empty DataUnigueKey");
                                }
                                else
                                {
                                    if (!(promotionResultsToKeepApplied.remove(prKey))) {
                                        continue;
                                    }
                                    if (LOG.isDebugEnabled())
                                    {
                                        LOG.debug("updatePromotions keeping applied the result [" + pr + "] from promotion [" +
                                                promotion + "]");
                                    }
                                    needsCalculateTotals |= pr.apply(ctx);

                                    newTotalAppliedDiscount += pr.getTotalDiscount(ctx);
                                }
                            }

                        }

                    }

                    if (needsCalculateTotals)
                    {
                        order.calculateTotals(true);
                    }

                    results.addAll(promoResults);
                }

                //fixupVouchersReapplyVouchers(ctx, order, vouchers);
            }

            if (LOG.isDebugEnabled())
            {
                for (String prKey : promotionResultsToKeepApplied)
                {
                    LOG.debug("updatePomrotions PromotionResult not reapplied because it did not fire [" + prKey + "]");
                }
            }

            double appliedDiscountChange = newTotalAppliedDiscount - oldTotalAppliedDiscount;

            if (LOG.isDebugEnabled())
            {
                LOG.debug("updatePromotions for [" + order + "] returned [" + results.size() +
                        "] PromotionResults appliedDiscountChange=[" + appliedDiscountChange + "]");
            }

            return new PromotionOrderResults(ctx, order, Collections.unmodifiableList(results), appliedDiscountChange);
        } catch (Exception ex)
        {
            LOG.error("Failed to updatePromotions", ex);
        }
        label1263: return null;
    }
    private List<PromotionResult> evaluatePromotion(final SessionContext ctx, final PromotionEvaluationContext promoContext,
            final AbstractPromotion promotion) {
        final List results = promotion.evaluate(ctx, promoContext);
        if (Transaction.current().isRunning()) {
            Transaction.current().flushDelayedStore();
        }
        return results;
    }

}