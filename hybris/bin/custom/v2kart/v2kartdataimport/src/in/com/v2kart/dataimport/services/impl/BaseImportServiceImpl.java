/**
 *
 */
package in.com.v2kart.dataimport.services.impl;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.context.ImportCommandContext;
import in.com.v2kart.dataimport.context.ImportCommandContextHolder;
import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;
import in.com.v2kart.dataimport.importengine.FileReader;
import in.com.v2kart.dataimport.services.BaseImportService;
import in.com.v2kart.dataimport.strategies.FeedReaderStrategy;
import in.com.v2kart.importlog.data.ImportLogEventData;
import in.com.v2kart.importlog.enums.ImportLogEventHint;
import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.services.ImportLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.validation.enums.Severity;

/**
 * BaseImportServiceImpl - Base service for all import engine services.
 * 
 */
public class BaseImportServiceImpl<T extends BaseDto> extends AbstractBusinessService implements BaseImportService
{

    private static final Logger LOG = Logger.getLogger(BaseImportServiceImpl.class);

    protected static final String NOT_FOUND_EXCEPTION_MESSAGE = "%4$S ERROR : Code[%1$s], RowNo[%2$s], Error[%3$S]";

    protected static final String PRODUCT_ASSOCIATE_NOT_FOUND_EXCEPTION_MESSAGE = "%4$S ERROR : ProductUpc[%5$s], Code[%1$s], RowNo[%2$s], Error[%3$S]";

    @Autowired
    private Mapper dozerBeanMapper;

    @Resource(name = "flexibleSearchService")
    private FlexibleSearchService flexibleSearchService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "categoryService")
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private FeedReaderStrategy feedReaderStrategy;

    private CatalogVersionModel catalogVersionModel;

    private String catalogVersion;

    private String defaultVendorCode;

    private VendorModel defaultVendor;

    private int productMaxOrderQuantity;

    private String masterCatalogName;

    @Autowired
    protected ImportLogService importLogService;

    private Map<String, String> siteCatalogMap;
    private final Map<String, CatalogVersionModel> formatCatalogVersionModelMap = new HashMap<String, CatalogVersionModel>(0);

    /**
     * Function to log product not found exception.
     * 
     * @param productCode
     *        the product code
     * @param baseDto
     *        the base dto
     * @param importDataSummaryInfo
     *        the import data summary info
     */
    @Override
    public final void logProductNotFoundException(final String productCode, final BaseDto baseDto,
            final ImportDataSummaryInfo importDataSummaryInfo)
    {
        this.logNotFoundException(productCode, ProductModel._TYPECODE, baseDto, FailureCause.PRODUCT_NOT_FOUND,
                importDataSummaryInfo);
    }

    /**
     * Function to log not found exception.
     * 
     * @param code
     *        the code
     * @param entityTypeCode
     *        the entity type code
     * @param productUpc
     *        the product upc
     * @param baseDto
     *        the base dto
     * @param cause
     *        the cause
     * @param importDataSummaryInfo
     *        the import data summary info
     */
    @Override
    public final void logNotFoundException(final String code, final String entityTypeCode, final String productUpc,
            final BaseDto baseDto, final FailureCause cause, final ImportDataSummaryInfo importDataSummaryInfo)
    {
        final ImportCommandContext context = ImportCommandContextHolder.getContext();
        importDataSummaryInfo.addFailureIncidence(cause, baseDto);
        final Integer rowIndex = Integer.valueOf(baseDto.getRowIndex());
        final String exceptionMessage = String.format(PRODUCT_ASSOCIATE_NOT_FOUND_EXCEPTION_MESSAGE, code, rowIndex, cause,
                context.getLogEventType(), productUpc);
        LOG.warn(exceptionMessage);
        logExceptionToService(context, exceptionMessage, cause);
    }

    /**
     * Function to log not found exception.
     * 
     * @param code
     *        the code
     * @param entityTypeCode
     *        the entity type code
     * @param baseDto
     *        the base dto
     * @param cause
     *        the cause
     * @param importDataSummaryInfo
     *        the import data summary info
     */
    @Override
    public final void logNotFoundException(final String code, final String entityTypeCode, final BaseDto baseDto,
            final FailureCause cause, final ImportDataSummaryInfo importDataSummaryInfo)
    {
        final ImportCommandContext context = ImportCommandContextHolder.getContext();
        importDataSummaryInfo.addFailureIncidence(cause, baseDto);
        final Integer rowIndex = Integer.valueOf(baseDto.getRowIndex());
        final String exceptionMessage = String
                .format(NOT_FOUND_EXCEPTION_MESSAGE, code, rowIndex, cause, context.getLogEventType());
        LOG.warn(exceptionMessage);
        logExceptionToService(context, exceptionMessage, cause);
    }

    /**
     * Log exception to console and service. Add failure cause to import data summary.
     * 
     * @param exception
     *        the exception
     * @param cause
     *        the cause
     * @param failureCauseReference
     *        the failure cause reference
     * @param importDataSummaryInfo
     *        the import data summary info
     */
    protected final void logExceptionToService(final Exception exception, final FailureCause cause,
            final Integer failureCauseReference, final ImportDataSummaryInfo importDataSummaryInfo)
    {
        LOG.warn(exception.getMessage());
        final ImportCommandContext context = ImportCommandContextHolder.getContext();
        importDataSummaryInfo.addFailureIncidence(cause, failureCauseReference);
        logExceptionToService(context, exception.getMessage(), cause);
    }

    /**
     * Function to log exception in service.
     * 
     * @param context
     *        the context
     * @param exceptionMessage
     *        the exception message
     * @param cause
     *        the cause
     */
    protected final void logExceptionToService(final ImportCommandContext context, final String exceptionMessage,
            final FailureCause cause)
    {
        final ImportLogEventData eventData = new ImportLogEventData();
        eventData.setMessage(exceptionMessage);
        eventData.setSeverity(Severity.ERROR);
        eventData.setEventStatus(ImportLogEventStatus.ERROR);
        eventData.setEventType(context.getLogEventType());
        eventData.setThreadIdentifier(context.getIdentifier());
        eventData.setEventHint(ImportLogEventHint.valueOf(cause.name()));
        importLogService.logEvent(eventData);
    }

    /**
     * Provides hook method to support processing of value container list via {@link BaseImportServiceImpl#processPostRead(List)} Provides
     * hook method to support to add custom logic before saving the model via
     * {@link BaseImportServiceImpl#saveOrUpdateModel(BaseDto, AbstractConverter, ImportDataSummaryInfo)}.
     * 
     * @param reader
     *        the reader
     * @param converter
     *        the converter
     * @return the import data summary info
     */
    @Override
    public final ImportDataSummaryInfo importFromFeed(final FileReader reader, final AbstractConverter converter)
    {
        final List<T> dtoList = getFeedReaderStrategy().readFeed(reader);
        final ImportDataSummaryInfo summaryInfo = new ImportDataSummaryInfo();
        summaryInfo.setTotalRecords(dtoList.size());

        processPostRead(dtoList);

        for (final T dto : dtoList)
        {
            try
            {
                LOG.debug("Processing data with row number: " + dto.getRowIndex());
                summaryInfo.recordDtoProcessing(dto);
                saveOrUpdateModel(dto, converter, summaryInfo);
            } catch (final Exception e)
            {
                LOG.error("Error persisting Dto:" + dto, e);
                summaryInfo.addFailureIncidence(FailureCause.OTHERS);
            }
        }
        return summaryInfo;
    }

    /**
     * Process post read.
     * 
     * @param dtoList
     *        List of DTOs to process
     */
    protected void processPostRead(final List<T> dtoList)
    {
        // DO NOTHING
    }

    /**
     * Save or update model.
     * 
     * @param dto
     *        the dto
     * @param converter
     *        the converter
     * @param summaryInfo
     *        the summary info
     */
    protected void saveOrUpdateModel(final T dto, final AbstractConverter converter, final ImportDataSummaryInfo summaryInfo)
    {
        try
        {
            getModelService().save(converter.convert(dto));
            LOG.debug("Model saved successfully for row number: " + dto.getRowIndex());
        } catch (final FeedPersistanceException fpe)
        {
            this.logExceptionToService(fpe, fpe.getFailureCause(), Integer.valueOf(fpe.getFeedRowNumber()), summaryInfo);
        }
    }

    /**
     * Gets the catalog version.
     * 
     * @return the catalog version
     */
    public final String getCatalogVersion()
    {
        return catalogVersion;
    }

    /**
     * Sets the catalog version.
     * 
     * @param catalogVersion
     *        the new catalog version
     */
    @Required
    public final void setCatalogVersion(final String catalogVersion)
    {
        this.catalogVersion = catalogVersion;
    }

    /**
     * Gets the flexible search service.
     * 
     * @return the flexibleSearchService
     */
    public final FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    /**
     * Gets the category service.
     * 
     * @return the categoryService
     */
    public final CategoryService getCategoryService()
    {
        return categoryService;
    }

    /**
     * Gets the user service.
     * 
     * @return the userService
     */
    public final UserService getUserService()
    {
        return userService;
    }

    /**
     * Gets the common i18 n service.
     * 
     * @return the commonI18NService
     */
    public final CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    /**
     * Gets the catalog version service.
     * 
     * @return the catalogVersionService
     */
    public final CatalogVersionService getCatalogVersionService()
    {
        return catalogVersionService;
    }

    /**
     * Gets the catalog version model.
     * 
     * @return the catalogVersionModel
     */
    @Override
    public final CatalogVersionModel getCatalogVersionModel()
    {
        if (catalogVersionModel == null)
        {
            this.catalogVersionModel = catalogVersionService.getCatalogVersion(getMasterCatalogName(), getCatalogVersion());
        }
        return catalogVersionModel;
    }

    /**
     * Gets the feed reader strategy.
     * 
     * @return the feedReaderStrategy
     */
    public final FeedReaderStrategy getFeedReaderStrategy()
    {
        return feedReaderStrategy;
    }

    /**
     * Sets the feed reader strategy.
     * 
     * @param feedReaderStrategy
     *        the feedReaderStrategy to set
     */
    public final void setFeedReaderStrategy(final FeedReaderStrategy feedReaderStrategy)
    {
        this.feedReaderStrategy = feedReaderStrategy;
    }

    /**
     * Gets the dozer bean mapper.
     * 
     * @return the dozerBeanMapper
     */
    public final Mapper getDozerBeanMapper()
    {
        return dozerBeanMapper;
    }

    /**
     * Sets the dozer bean mapper.
     * 
     * @param dozerBeanMapper
     *        the dozerBeanMapper to set
     */
    public final void setDozerBeanMapper(final Mapper dozerBeanMapper)
    {
        this.dozerBeanMapper = dozerBeanMapper;
    }

    /**
     * Gets the default vendor code.
     * 
     * @return the defaultVendorCode
     */
    public final String getDefaultVendorCode()
    {
        return defaultVendorCode;
    }

    /**
     * Sets the default venodr code.
     * 
     * @param defaultVendorCode
     *        the defaultVenorCode to set
     */
    @Value("${importEngine.defaultVendorCode}")
    @Required
    public final void setDefaultVenodrCode(final String defaultVendorCode)
    {
        this.defaultVendorCode = defaultVendorCode;
    }

    /**
     * Gets the default vendor.
     * 
     * @return the defaultVendor
     */
    @Override
    public final VendorModel getDefaultVendor()
    {
        if (defaultVendor == null)
        {
            defaultVendor = new VendorModel();
            defaultVendor.setCode(getDefaultVendorCode());
            try
            {
                defaultVendor = getFlexibleSearchService().getModelByExample(defaultVendor);
            } catch (final ModelNotFoundException e)
            {
                LOG.error("Default Vendor Not Found", e);
            }
        }
        return defaultVendor;
    }

    /**
     * Sets the default vendor.
     * 
     * @param defaultVendor
     *        the defaultVendor to set
     */
    public final void setDefaultVendor(final VendorModel defaultVendor)
    {
        this.defaultVendor = defaultVendor;
    }

    /**
     * Gets the catalog version model.
     * 
     * @param siteCode
     *        the format
     * @return the catalogVersionModel
     */
    @Override
    public final CatalogVersionModel getCatalogVersionModel(final String siteCode)
    {
        if (null == siteCode)
        {
            throw new IllegalArgumentException("Format must not be null");
        }
        if (null == siteCatalogMap.get(siteCode))
        {
            return null;
        }
        CatalogVersionModel requiredCatalogVersionModel = formatCatalogVersionModelMap.get(siteCode);
        if (requiredCatalogVersionModel == null)
        {
            requiredCatalogVersionModel = catalogVersionService.getCatalogVersion(siteCatalogMap.get(siteCode), getCatalogVersion());
            formatCatalogVersionModelMap.put(siteCode, requiredCatalogVersionModel);
        }
        return requiredCatalogVersionModel;
    }

    /**
     * Gets the product max order quantity.
     * 
     * @return the productMaxOrderQuantity
     */
    public final int getProductMaxOrderQuantity()
    {
        return productMaxOrderQuantity;
    }

    /**
     * Sets the product max order quantity.
     * 
     * @param productMaxOrderQuantity
     *        the productMaxOrderQuantity to set
     */
    @Value("${maxOrderQuantityPerProduct}")
    @Required
    public final void setProductMaxOrderQuantity(final int productMaxOrderQuantity)
    {
        this.productMaxOrderQuantity = productMaxOrderQuantity;
    }

    /**
     * @return the formatCatalogVersionModelMap
     */
    public Map<String, CatalogVersionModel> getFormatCatalogVersionModelMap()
    {
        return formatCatalogVersionModelMap;
    }

    /**
     * @return the siteCatalogMap
     */
    public Map<String, String> getSiteCatalogMap()
    {
        return siteCatalogMap;
    }

    /**
     * @param siteCatalogMap
     *        the siteCatalogMap to set
     */
    @Required
    public void setSiteCatalogMap(final Map<String, String> siteCatalogMap)
    {
        this.siteCatalogMap = siteCatalogMap;
    }

    /**
     * @return the masterCatalogName
     */
    public String getMasterCatalogName()
    {
        return masterCatalogName;
    }

    /**
     * @param masterCatalogName
     *        the masterCatalogName to set
     */
    public void setMasterCatalogName(final String masterCatalogName)
    {
        this.masterCatalogName = masterCatalogName;
    }
}
