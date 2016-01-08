package in.com.v2kart.dataimport.commands;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.services.V2InventoryImportService;

/**
 * @author arunkumar
 * 
 */
public class StoreInventoryImportCommand extends AbstractImportCommand {

    /**
     * Inventory ImportService instance
     */
    private V2InventoryImportService v2StoreImportService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2.dataimport.commands.AbstractImportCommand#importData()
     */
    @Override
    protected ImportDataSummaryInfo importData() throws Exception {
        return v2StoreImportService.importStoreInventoryDataFromFeed();
    }

    /**
     * @param v2StoreImportService
     *        the v2SiteImportService to set
     */
    public void setV2StoreImportService(final V2InventoryImportService v2StoreImportService) {
        this.v2StoreImportService = v2StoreImportService;
    }
}
