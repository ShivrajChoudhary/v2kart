package in.com.v2kart.dataimport.services.impl;

import in.com.v2kart.core.dao.CatalogAwareModelDao;
import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.converters.V2PriceConverter;
import in.com.v2kart.dataimport.dto.V2PriceDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;
import in.com.v2kart.dataimport.importengine.CSVReader;
import in.com.v2kart.dataimport.services.V2PriceImportService;
import in.com.v2kart.dataimport.strategies.FeedReaderStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.util.Config;

/**
 * @author arunkumar
 * 
 */
public class V2PriceImportServiceImpl extends BaseImportServiceImpl implements V2PriceImportService {

    private static final Logger LOG = Logger.getLogger(V2PriceImportServiceImpl.class);

    @Resource(name = "v2PriceCsvReader")
    private CSVReader v2PriceCsvReader;

    @Autowired
    private FeedReaderStrategy feedReaderStrategy;

    @Resource(name = "v2PriceConverter")
    private V2PriceConverter v2PriceConverter;

    @Resource(name = "productModelDao")
    private CatalogAwareModelDao<ProductModel> productModelDao;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.services.V2PriceImportService#getPriceDataFromFeed()
     */
    @Override
    public List<V2PriceDto> getPriceDataFromFeed() {
        return feedReaderStrategy.<V2PriceDto> readFeed(v2PriceCsvReader);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.services.V2PriceImportService#importPriceDataFromFeed()
     */
    @Override
    public ImportDataSummaryInfo importPriceDataFromFeed() {
        final List<V2PriceDto> v2PriceDtos = this.getPriceDataFromFeed();
        final ImportDataSummaryInfo summaryInfo = new ImportDataSummaryInfo();
        summaryInfo.setTotalRecords(v2PriceDtos.size());
        for (final V2PriceDto v2PriceDto : v2PriceDtos) {
            try {
                summaryInfo.recordDtoProcessing(v2PriceDto);
                this.createOrUpdateV2Price(v2PriceDto, summaryInfo);
            } catch (final Exception e) {
                LOG.error("Error persisting price Dto:" + v2PriceDto.getVerboseToString(), e);
                summaryInfo.addFailureIncidence(FailureCause.OTHERS);
            }
        }
        return summaryInfo;
    }

    /**
     * Creates the or update V2 price.
     * 
     * @param v2PriceDto
     *        the V2 price DTO
     * @param summaryInfo
     *        the summary info
     */
    private void createOrUpdateV2Price(final V2PriceDto v2PriceDto, final ImportDataSummaryInfo summaryInfo) {
        final CatalogVersionModel catalogVersionModel = getCatalogVersionModel();
        if (null == catalogVersionModel) {
            throw new FeedPersistanceException(String.format("No Catalog found for organization unit identifier %1$s for row: %2$s",
                    getCatalogVersionModel(), Integer.valueOf(v2PriceDto.getRowIndex())),
                    FailureCause.CATALOG_VERSION_NOT_FOUND, v2PriceDto.getRowIndex());
        }
        final ProductModel productModel = productModelDao.findByCodeAndCatalogVersion(v2PriceDto.getCode(), catalogVersionModel);
        if (productModel == null) {
            this.logProductNotFoundException(v2PriceDto.getCode(), v2PriceDto, summaryInfo);
            return;
        }
        boolean found = false;
        LOG.debug("Initiating Price Check for : " + v2PriceDto.getCode());

        // Filter prices on the basis of price type
        Predicate<PriceRowModel> predicate = new Predicate<PriceRowModel>() {
            @Override
            public boolean apply(final PriceRowModel price) {
                return price.getPriceType().equals(v2PriceDto.getPriceType())
                        && price.getUnit().getCode().equals(Config.getParameter("price.unit"));
            }
        };
        getModelService().refresh(productModel);
        Iterable<PriceRowModel> priceRows = Iterables.filter(productModel.getEurope1Prices(), predicate);
        // Find the active price row
        PriceRowModel activePriceRow = getActivePriceRow(priceRows, v2PriceDto);
        // Price row to be deleted if delete flag is on.

        PriceRowModel updatedPriceRow = null;
        boolean isActive = true;
        if (null != activePriceRow) {
            updatedPriceRow = updateActivePriceRow(v2PriceDto, productModel, activePriceRow);
            found = true;
        } else {
            isActive = false;
            predicate = new Predicate<PriceRowModel>() {
                @Override
                public boolean apply(final PriceRowModel price) {
                    return price.getPriceType().equals(v2PriceDto.getPriceType()) && price.getEndTime().after(v2PriceDto.getValidFrom());
                }
            };
            priceRows = Iterables.filter(productModel.getEurope1Prices(), predicate);
            activePriceRow = getFuturePriceRow(priceRows);
            if (null != activePriceRow) {
                updatedPriceRow = updateActivePriceRow(v2PriceDto, productModel, activePriceRow);
                found = true;
            }
        }
        if (!found) {
            final PriceRowModel newPriceRow = v2PriceConverter.convert(v2PriceDto, productModel);
            getModelService().save(newPriceRow);
            getModelService().refresh(newPriceRow);
            updatedPriceRow = newPriceRow;
            LOG.debug("creating a new Price row for Product" + v2PriceDto.getCode());
        }
        getModelService().refresh(productModel);
        priceRows = Iterables.filter(productModel.getEurope1Prices(), predicate);
        if (isActive) {
            activePriceRow = getActivePriceRow(priceRows, v2PriceDto);
        } else {
            activePriceRow = getFuturePriceRow(priceRows);
        }

        if (null != activePriceRow && null != updatedPriceRow) {
            for (final PriceRowModel priceRow : priceRows) {
                if (!priceRow.equals(activePriceRow)
                        && !priceRow.equals(updatedPriceRow)
                        && !activePriceRow.getStartTime().equals(priceRow.getEndTime())
                        && !activePriceRow.getEndTime().equals(priceRow.getStartTime())
                        && isOverlap(activePriceRow.getStartTime(), activePriceRow.getEndTime(), priceRow.getStartTime(),
                                priceRow.getEndTime())) {
                    LOG.info("Removing overlapping price row for Product" + priceRow.getProduct().getCode() + " with start date"
                            + priceRow.getStartTime() + " and end date"
                            + priceRow.getEndTime());
                    getModelService().remove(priceRow);
                }
            }
            /*
             * updatedPriceRow.setIsBlocked(v2PriceDto.getBlock()); if (null != v2PriceDto.getDelete() &&
             * v2PriceDto.getDelete().booleanValue()) { LOG.info("Deleting active price row for Product" +
             * updatedPriceRow.getProduct().getCode() + " with start date" + updatedPriceRow.getStartTime() + " and end date" +
             * updatedPriceRow.getEndTime()); getModelService().remove(updatedPriceRow); }
             */
        }
    }

    /**
     * Update active price row.
     * 
     * @param v2PriceDto
     *        the v2 price dto
     * @param productModel
     *        the product model
     * @param activePriceRow
     *        the active price row
     * @return the price row model
     */
    private PriceRowModel updateActivePriceRow(final V2PriceDto v2PriceDto, final ProductModel productModel,
            final PriceRowModel activePriceRow) {
        PriceRowModel updatedPriceRow = activePriceRow;
        if (activePriceRow.getStartTime().before(v2PriceDto.getValidFrom())) {
            activePriceRow.setEndTime(v2PriceDto.getValidFrom());
            getModelService().save(activePriceRow);
            LOG.debug("Changing the end time of Price row for Product" + v2PriceDto.getCode() + " with start date"
                    + v2PriceDto.getValidFrom() + " and end date"
                    + v2PriceDto.getValidTo());
            LOG.debug("\n Setting its end time to" + v2PriceDto.getValidFrom());
            final PriceRowModel newPriceRow = v2PriceConverter.convert(v2PriceDto, productModel);
            getModelService().save(newPriceRow);
            getModelService().refresh(newPriceRow);
            LOG.debug("creating a new Price row for Product" + v2PriceDto.getCode() + " with start date" + v2PriceDto.getValidFrom()
                    + " and end date" + v2PriceDto.getValidTo());
            updatedPriceRow = newPriceRow;
        } else if (activePriceRow.getStartTime().equals(v2PriceDto.getValidFrom())) {
            activePriceRow.setEndTime(v2PriceDto.getValidTo());
            v2PriceConverter.populate(v2PriceDto, activePriceRow);
            getModelService().save(activePriceRow);
            LOG.debug("Populating existing price for product: " + v2PriceDto.getCode() + " with price value:" + activePriceRow.getPrice());
        } else {
            activePriceRow.setStartTime(v2PriceDto.getValidFrom());
            activePriceRow.setEndTime(v2PriceDto.getValidTo());
            v2PriceConverter.populate(v2PriceDto, activePriceRow);
            getModelService().save(activePriceRow);
            LOG.debug("Populating existing price for product: " + v2PriceDto.getCode() + " with price value:" + activePriceRow.getPrice());
        }
        getModelService().refresh(activePriceRow);
        return updatedPriceRow;
    }

    /**
     * Is Overlap. Will check if the range one overlaps with range two.
     * 
     * Let ConditionA Mean DateRange A Completely After DateRange B (True if StartA > EndB)
     * 
     * Let ConditionB Mean DateRange A Completely Before DateRange B (True if EndA < StartB)
     * 
     * Then Overlap exists if Neither A Nor B is true ( If one range is neither completely after the other, nor completely before the other,
     * then they must overlap)
     * 
     * @param startDate1
     *        the start date1
     * @param endDate1
     *        the end date1
     * @param startDate2
     *        the start date2
     * @param endDate2
     *        the end date2
     * @return true, if successful
     */
    private boolean isOverlap(final Date startDate1, final Date endDate1, final Date startDate2, final Date endDate2) {
        return !((startDate1.after(endDate2)) || (startDate2.after(endDate1)));
    }

    /**
     * Gets the future price row. In case two future price rows are present, one modified last is picked.
     * 
     * @param priceRows
     *        the price rows
     * @return the active price row
     */
    private PriceRowModel getFuturePriceRow(final Iterable<PriceRowModel> priceRows) {
        if (priceRows.iterator().hasNext()) {
            final List<PriceRowModel> futurePriceRows = Lists.newArrayList(priceRows);
            Collections.sort(futurePriceRows, Collections.reverseOrder(new Comparator<PriceRowModel>() {

                @Override
                public int compare(final PriceRowModel price1, final PriceRowModel price2) {
                    return price1.getModifiedtime().compareTo(price2.getModifiedtime());
                }
            }));
            return futurePriceRows.get(0);
        }
        return null;
    }

    /**
     * Gets the active price row. It checks the price rows available with start date before or equal to current date and end date after or
     * equal to current date. In case two active price rows are present, one modified last is picked. Active rows must also overlap with the
     * input price row
     * 
     * @param priceRows
     *        the price rows
     * @param v2PriceDto
     *        the V2 price dto
     * @return the active price row
     */
    private PriceRowModel getActivePriceRow(final Iterable<PriceRowModel> priceRows, final V2PriceDto v2PriceDto) {
        final List<PriceRowModel> activePriceRows = new ArrayList<PriceRowModel>();
        final Date currentDate = new Date();
        for (final PriceRowModel priceRow : priceRows) {
            if (!priceRow.getStartTime().after(currentDate) && !priceRow.getEndTime().before(currentDate)
                    && v2PriceDto.getValidFrom().before(priceRow.getEndTime())) {
                activePriceRows.add(priceRow);
            }
        }
        Collections.sort(activePriceRows, Collections.reverseOrder(new Comparator<PriceRowModel>() {

            @Override
            public int compare(final PriceRowModel price1, final PriceRowModel price2) {
                return price1.getModifiedtime().compareTo(price2.getModifiedtime());
            }
        }));
        return activePriceRows.isEmpty() ? null : activePriceRows.get(0);
    }

}
