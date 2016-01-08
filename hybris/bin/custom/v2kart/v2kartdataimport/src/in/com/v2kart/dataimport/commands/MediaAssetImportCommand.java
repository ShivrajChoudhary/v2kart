/**
 * 
 */
package in.com.v2kart.dataimport.commands;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.services.V2MediaAssetImportService;

/**
 * @author shubhammaheshwari
 *
 */
public class MediaAssetImportCommand extends AbstractImportCommand {
    
    private V2MediaAssetImportService v2MediaAssetImportService;

    /* (non-Javadoc)
     * @see in.com.v2kart.dataimport.commands.AbstractImportCommand#importData()
     */
    @Override
    protected ImportDataSummaryInfo importData() throws Exception {
        return v2MediaAssetImportService.importMediaDataFromFeed();
    }

    public void setV2MediaAssetImportService(V2MediaAssetImportService v2MediaAssetImportService) {
        this.v2MediaAssetImportService = v2MediaAssetImportService;
    }

}
