package in.com.v2kart.dataimport.services;

import java.util.List;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.dto.V2PriceDto;

/**
 * @author arunkumar
 *
 */
public interface V2PriceImportService extends BaseImportService {

    /**
     * Function returning Price info from feed
     * 
     * @return List of ItemDto
     */
    List<V2PriceDto> getPriceDataFromFeed();

    /**
     * Function to import Price Info from feeds
     * 
     * @return ImportDataSummaryInfo instance
     */
    ImportDataSummaryInfo importPriceDataFromFeed();

}