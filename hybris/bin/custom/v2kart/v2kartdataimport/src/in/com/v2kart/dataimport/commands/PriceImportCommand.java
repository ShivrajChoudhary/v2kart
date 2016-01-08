package in.com.v2kart.dataimport.commands;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.services.V2PriceImportService;

public class PriceImportCommand extends AbstractImportCommand {

    /**
     * ProductImportService instance
     */
    private V2PriceImportService v2PriceImportService;
    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.commands.AbstractImportCommand#importData()
     */
    @Override
    protected ImportDataSummaryInfo importData() throws Exception {
        return v2PriceImportService.importPriceDataFromFeed();
    }

    /**
     * @param v2ProductImportService
     *        the v2ProductImportService to set
     */
    public void setV2PriceImportService(final V2PriceImportService v2PriceImportService) {
        this.v2PriceImportService = v2PriceImportService;
    }

}