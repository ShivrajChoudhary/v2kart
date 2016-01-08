/**
 * 
 */
package in.com.v2kart.dataimport.services;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.dto.V2MediaDto;

import java.util.List;

/**
 * @author shubhammaheshwari
 *
 */
public interface V2MediaAssetImportService extends BaseImportService {
    
    /**
     * Function returning Media info from feed
     * 
     * @return List of FGMediaDto
     */
    List<V2MediaDto> getMediaInfoFromFeed();

    /**
     * Function to import Media Info from feeds.
     * 
     * @return ImportDataSummaryInfo instance
     */
    ImportDataSummaryInfo importMediaDataFromFeed();

}
