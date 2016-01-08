/**
 * 
 */
package in.com.v2kart.dataimport.services;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.dto.V2ProductDto;

import java.util.List;

/**
 * Interface <Code>V2ProductImportService</code> exposing APIs for Product data imports.
 * 
 * Implementation of these internally invokes CSVReaders to get feeds out of csv files and persist them to Hybris platform
 * 
 */
public interface V2ProductImportService extends BaseImportService {

    /**
     * Function returning Product info from feed
     * 
     * @return List of V2ProductDto
     */
    List<V2ProductDto> getProductsDataFromFeed();

    /**
     * Function to import Product Info from feeds
     * 
     * @return ImportDataSummaryInfo instance
     */
    ImportDataSummaryInfo importProductDataFromFeed();
}