package in.com.v2kart.dataimport.services;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.ordersplitting.model.VendorModel;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;
import in.com.v2kart.dataimport.importengine.FileReader;

/**
 * base interface for all import services
 * 
 */
public interface BaseImportService<T>
{

    /**
     * Function to get CatalogVersionModel instance.
     * 
     * @return CatalogVersionModel
     */
    CatalogVersionModel getCatalogVersionModel();

    /**
     * Function to get CatalogVersionModel instance.
     * 
     * @return CatalogVersionModel
     */
    CatalogVersionModel getCatalogVersionModel(String siteCode);

    /**
     * Function to get default vendor from system
     * 
     * @return VendorModel
     */
    VendorModel getDefaultVendor();

    /**
     * Function to log product not found exception.
     * 
     * This adds the failure incidence to import summary info and log this to ImportLogService which internally persisted the log details to
     * database
     * 
     * 
     * @param productCode
     *        the product code
     * @param baseDto
     *        the base dto
     * @param importDataSummaryInfo
     *        the import data summary info
     */
    void logProductNotFoundException(String productCode, BaseDto baseDto, ImportDataSummaryInfo importDataSummaryInfo);

    /**
     * Function to log not found exception.
     * 
     * This adds the failure incidence to import summary info and log this to ImportLogService which internally persisted the log details to
     * database
     * 
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
    void logNotFoundException(String code, String entityTypeCode, String productUpc, BaseDto baseDto, FailureCause cause,
            ImportDataSummaryInfo importDataSummaryInfo);

    /**
     * Function to log not found exception.
     * 
     * This adds the failure incidence to import summary info and log this to ImportLogService which internally persisted the log details to
     * database
     * 
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
    void logNotFoundException(String code, String entityTypeCode, BaseDto baseDto, FailureCause cause,
            ImportDataSummaryInfo importDataSummaryInfo);

    /**
     * Imports data from CSV file to value container using <code>reader</code>, processes it and converts into corresponding model using
     * <code>converter</code> and saves it.
     * 
     * @param reader
     *        the reader
     * @param converter
     *        the converter
     * @return the import data summary info
     */
    ImportDataSummaryInfo importFromFeed(FileReader reader, AbstractConverter<T, ?> converter);

}
