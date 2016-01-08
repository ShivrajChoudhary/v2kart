package in.com.v2kart.dataimport.services;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.dto.V2StoreInventoryDto;

import java.util.List;

/**
 * @author arunkumar
 * 
 */
public interface V2InventoryImportService extends BaseImportService {

    /**
     * Function returning Product info from feed
     * 
     * @return List of ItemDto
     */
    List<V2StoreInventoryDto> geStoreInventoryDataFromFeed();

    /**
     * Function to import Product Info from feeds
     * 
     * @return ImportDataSummaryInfo instance
     */
    ImportDataSummaryInfo importStoreInventoryDataFromFeed();


}
