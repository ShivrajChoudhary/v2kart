package in.com.v2kart.core.price;

import in.com.v2kart.core.enums.V2PriceTypeEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.platform.catalog.jalo.CatalogAwareEurope1PriceFactory;
import de.hybris.platform.europe1.channel.strategies.RetrieveChannelStrategy;
import de.hybris.platform.europe1.enums.PriceRowChannel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * @author arunkumar
 *
 */
public class V2CatalogAwareEurope1PriceFactory extends CatalogAwareEurope1PriceFactory {

    private static final Logger LOG = Logger.getLogger(V2CatalogAwareEurope1PriceFactory.class);
    private ModelService modelService;
    private RetrieveChannelStrategy retrieveChannelStrategy;

/*    
     * Converts price row to price information
     * 
     * @param priceRow
     * 
     * @param ctx
     * 
     * @param product
     * 
     * @param forDate
     * 
     * @param net
     * 
     * @return PriceInformation
     * 
     * @throws JaloPriceFactoryException
     
    @SuppressWarnings("deprecation")
    private PriceInformation convertPriceRowToPriceInformation(final PriceRow priceRow, final SessionContext ctx, final Product product,
            final Date forDate, final boolean net) throws JaloPriceFactoryException {
        PriceInformation priceInformation = null;
        Collection<?> theTaxValues = getTaxInformations(product, getPTG(ctx, product), ctx.getUser(), getUTG(ctx, ctx.getUser()), forDate);
        priceInformation = Europe1Tools.createPriceInformation(priceRow, ctx.getCurrency());

        if (priceInformation.getPriceValue().isNet() != net) {
            if (theTaxValues == null) {
                theTaxValues = Europe1Tools.getTaxValues(getTaxInformations(product, getPTG(ctx, product), ctx.getUser(),
                        getUTG(ctx, ctx.getUser()), forDate));
            }

            priceInformation = new PriceInformation(priceInformation.getQualifiers(), priceInformation.getPriceValue().getOtherPrice(
                    theTaxValues));
        }

        return priceInformation;
    }
*/
    /**
     * 
     * The comparator below is an exact copy of the original PriceRowInfoComparator except for the price addition, and the match value was
     * commented out since the methods were not visible from this package structure.
     * 
     * @author shashank2484
     * 
     */
    protected class V2PriceRowInfoComparator implements Comparator<PriceRow> {
        private final Currency curr;
        private final boolean net;

        protected V2PriceRowInfoComparator(final Currency curr, final boolean net) {
            this.curr = curr;
            this.net = net;
        }

        @Override
        public int compare(final PriceRow row1, final PriceRow row2) {

            // unit
            final long u1Match = row1.getUnit().getPK().getLongValue();
            final long u2Match = row2.getUnit().getPK().getLongValue();
            if (u1Match != u2Match) {
                // just sort by unit pk
                return u1Match < u2Match ? -1 : 1;
            }
            // min quantity
            final long min1 = row1.getMinqtdAsPrimitive();
            final long min2 = row2.getMinqtdAsPrimitive();
            if (min1 != min2) {
                // the row with larger min quantity is better
                return min1 > min2 ? -1 : 1;
            }

            // currency
            final boolean c1Match = curr.equals(row1.getCurrency());
            final boolean c2Match = curr.equals(row2.getCurrency());
            if (c1Match != c2Match) {
                // the row matching currency exactly is better
                return c1Match ? -1 : 1;
            }
            // net
            final boolean n1Match = this.net == row1.isNetAsPrimitive();
            final boolean n2Match = this.net == row2.isNetAsPrimitive();
            if (n1Match != n2Match) {
                // the row matching net flag exactly is better
                return n1Match ? -1 : 1;
            }

            final PriceRowModel row1PriceModel = modelService.get(row1.getPK());
            final PriceRowModel row2PriceModel = modelService.get(row2.getPK());
            final V2PriceTypeEnum row1PriceType = row1PriceModel.getPriceType();
            final V2PriceTypeEnum row2PriceType = row2PriceModel.getPriceType();
            if (row1PriceType != null && row2PriceType != null && !row1PriceType.equals(row2PriceType)) {
                // the row matching MRP exactly is better
                return row1PriceType.equals(V2PriceTypeEnum.MRP) ? -1 : 1;
            }

            final Double row1Price = row1.getPrice();
            final Double row2Price = row2.getPrice();
            if (!row1Price.equals(row2Price)) {
                return row1Price.compareTo(row2Price);
            }
            // date range
            final boolean row1Range = row1.getStartTime() != null;
            final boolean row2Range = row2.getStartTime() != null;

            if (row1Range != row2Range) {
                // rows with range are better
                return row1Range ? -1 : 1;
            }
            return row1.getPK().compareTo(row2.getPK());
        }
    }

    /**
     * The comparator below is an exact copy of the original V2PriceRowMatchComparator except for the price addition, and the match value
     * was commented out since the methods were not visible from this package structure.
     * 
     * @author arunkumar
     * 
     */
    protected class V2PriceRowMatchComparator implements Comparator<PriceRow> {
        private final Currency curr;
        private final boolean net;
        private final Unit unit;

        protected V2PriceRowMatchComparator(final Currency curr, final boolean net, final Unit u) {
            this.curr = curr;
            this.net = net;
            this.unit = u;
        }

        @Override
        public int compare(final PriceRow row1, final PriceRow row2) {

            // currency
            final boolean c1Match = curr.equals(row1.getCurrency());
            final boolean c2Match = curr.equals(row2.getCurrency());
            if (c1Match != c2Match) {
                // the row matching currency exactly is better
                return c1Match ? -1 : 1;
            }

            // net
            final boolean n1Match = this.net == row1.isNetAsPrimitive();
            final boolean n2Match = this.net == row2.isNetAsPrimitive();
            if (n1Match != n2Match) {
                // the row matching net flag exactly is better
                return n1Match ? -1 : 1;
            }

            // unit
            final boolean u1Match = unit.equals(row1.getUnit());
            final boolean u2Match = unit.equals(row2.getUnit());
            if (u1Match != u2Match) {
                // the row matching unit exactly is better
                return u1Match ? -1 : 1;
            }

            // min quantity
            final long min1 = row1.getMinqtdAsPrimitive();
            final long min2 = row2.getMinqtdAsPrimitive();
            if (min1 != min2) {
                // the row with larger min quantity is better
                return min1 > min2 ? -1 : 1;
            }
            final PriceRowModel row1PriceModel = modelService.get(row1.getPK());
            final PriceRowModel row2PriceModel = modelService.get(row2.getPK());
            final V2PriceTypeEnum row1PriceType = row1PriceModel.getPriceType();
            final V2PriceTypeEnum row2PriceType = row2PriceModel.getPriceType();
            if (row1PriceType != null && row2PriceType != null && !row1PriceType.equals(row2PriceType)) {
                // the row matching MRP exactly is better
                return row1PriceType.equals(V2PriceTypeEnum.MRP) ? -1 : 1;
            }
            final Double row1Price = row1.getPrice();
            final Double row2Price = row2.getPrice();
            if (!row1Price.equals(row2Price)) {
                return row1Price.compareTo(row2Price);
            }

            // date range
            final boolean row1Range = row1.getStartTime() != null;
            final boolean row2Range = row2.getStartTime() != null;

            if (row1Range != row2Range) {
                // rows with range are better
                return row1Range ? -1 : 1;
            }

            LOG.warn("found ambigous price rows " + row1 + " and " + row2 + " - using PK to distinguish");

            return row1.getPK().compareTo(row2.getPK());
        }
    }

    /**
     * This method is a copy of the original method in all aspects except for the referenced comparator.
     */
    @SuppressWarnings("deprecation")
    @Override
    public List<PriceRow> matchPriceRowsForInfo(final SessionContext ctx, final Product product, final EnumerationValue pg,
            final User user, final EnumerationValue ug, final Currency currency, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        if (product == null && pg == null) {
            throw new JaloPriceFactoryException("cannot match price info without product and product group - at least one must be present",
                    0);
        }
        if (user == null && ug == null) {
            throw new JaloPriceFactoryException("cannot match price info without user and user group - at least one must be present", 0);
        }
        if (currency == null) {
            throw new JaloPriceFactoryException("cannot match price info without currency", 0);
        }
        if (date == null) {
            throw new JaloPriceFactoryException("cannot match price info without date", 0);
        }
        final Collection<PriceRow> rows = queryPriceRows4Price(ctx, product, pg, user, ug);
        if (!rows.isEmpty()) {
            final PriceRowChannel channel = retrieveChannelStrategy.getChannel(ctx);
            final List<PriceRow> ret = new ArrayList<PriceRow>(rows);
            if (ret.size() > 1) {
                Collections.sort(ret, new V2PriceRowInfoComparator(currency, net));
            }
            return filterPriceRows4Info(ret, currency, date, channel);
        }
        return Collections.emptyList();
    }

    /**
     * This is a copy of the original method in all aspects except for the referenced comparator.
     */
    @Override
    public PriceRow matchPriceRowForPrice(final SessionContext ctx, final Product product, final EnumerationValue pg, final User user,
            final EnumerationValue ug, final long qtd, final Unit unit, final Currency currency, final Date date, final boolean net,
            final boolean giveAwayMode) throws JaloPriceFactoryException {
        if (product == null && pg == null) {
            throw new JaloPriceFactoryException("cannot match price without product and product group - at least one must be present", 0);
        }
        if (user == null && ug == null) {
            throw new JaloPriceFactoryException("cannot match price without user and user group - at least one must be present", 0);
        }
        if (currency == null) {
            throw new JaloPriceFactoryException("cannot match price without currency", 0);
        }
        if (date == null) {
            throw new JaloPriceFactoryException("cannot match price without date", 0);
        }
        if (unit == null) {
            throw new JaloPriceFactoryException("cannot match price without unit", 0);
        }

        final Collection<PriceRow> rows = queryPriceRows4Price(ctx, product, pg, user, ug);
        if (!rows.isEmpty()) {
            final PriceRowChannel channel = retrieveChannelStrategy.getChannel(ctx);
            final List<PriceRow> l = filterPriceRows4Price(//
                    rows, //
                    qtd, //
                    unit, //
                    currency, //
                    date, //
                    giveAwayMode, //
                    channel);
            if (l.isEmpty()) {
                return null;
            } else if (l.size() == 1) {
                return l.get(0);
            } else {
                Collections.sort(l, new V2PriceRowMatchComparator(currency, net, unit));
                return l.get(0);
            }
        }
        return null;
    }

    @Override
    public void setRetrieveChannelStrategy(final RetrieveChannelStrategy retrieveChannelStrategy) {
        super.setRetrieveChannelStrategy(retrieveChannelStrategy);
        this.retrieveChannelStrategy = retrieveChannelStrategy;
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
