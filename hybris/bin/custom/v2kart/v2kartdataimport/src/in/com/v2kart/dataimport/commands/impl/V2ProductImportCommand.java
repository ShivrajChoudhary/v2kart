/**
 * 
 */
package in.com.v2kart.dataimport.commands.impl;

import org.springframework.beans.factory.annotation.Required;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.commands.AbstractImportCommand;
import in.com.v2kart.dataimport.services.V2ProductImportService;

public class V2ProductImportCommand extends AbstractImportCommand {

    /**
     * ProductImportService instance
     */
    private V2ProductImportService v2ProductImportService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.commands.AbstractImportCommand#importData()
     */
    @Override
    protected ImportDataSummaryInfo importData() throws Exception {
        return v2ProductImportService.importProductDataFromFeed();
    }

    /**
     * 
     * @param v2ProductImportService
     */
    @Required
    public void setV2ProductImportService(V2ProductImportService v2ProductImportService) {
        this.v2ProductImportService = v2ProductImportService;
    }
}
