package in.com.v2kart.core.cronjob;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class V2StockAvailabilityCronJob extends AbstractJobPerformable<CronJobModel> {

    private final Logger LOG = Logger.getLogger(V2StockAvailabilityCronJob.class);
    @Autowired
    protected FlexibleSearchService flexibleSearchService;
    private String fileDir;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
  
    	try {
           createExcel();
        } catch ( Exception ex ) {
            LOG.info("Stock Level excel file has Not been generated.cause :");
            LOG.error(ex);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }
     return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
   
 // this method is used for getting  stock level
    public List<StockLevelModel> getStockAvailablity() {
        final StringBuilder queryBuilder = new StringBuilder("SELECT {%s} FROM {%s}");
        final String formattedQuery = String.format(queryBuilder.toString(), ProductModel.PK, StockLevelModel._TYPECODE);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(formattedQuery);
        final SearchResult<StockLevelModel> stocks =this.flexibleSearchService.search(query);
   return stocks.getResult();
    }
	//this method is used for generating Excel files of Stock level availability
	void createExcel()throws Exception{
		   String filename = fileDir+"Stock_Level_Report.xls";
			List<StockLevelModel> stocks= getStockAvailablity();
					 
	    HSSFWorkbook wb = new HSSFWorkbook(); 
        Integer rowNumber = 1;
        Integer sheetNumber = 0;
        Integer count=new Integer(0);
        if (stocks != null && !stocks.isEmpty()) {           	
          	 LOG.info("Total stock level product is : "+stocks.size());
             HSSFSheet sheet = wb.createSheet("Sheet-"+sheetNumber); //Access the worksheet, so that we can update / modify it.  
          	 HSSFRow rowhead = sheet.createRow(0);
          	  rowhead.createCell(0).setCellValue("Product_Code");
         	   rowhead.createCell(1).setCellValue("Available");
         	   rowhead.createCell(2).setCellValue("Reserved");
           	 for (final StockLevelModel stock : stocks) {
          	 if(count==50000){
          		LOG.info("Sheet " + sheetNumber + " written; moving onto to sheet " + (sheetNumber + 1));
          	  sheetNumber++;
          	wb.createSheet("Sheet-"+sheetNumber);
          	  rowNumber = 1;
          	   sheet = wb.getSheetAt(sheetNumber); //Access the worksheet, so that we can update / modify it.  
          	 rowhead=sheet.createRow(0);
          	   rowhead.createCell(0).setCellValue("Product_Code");
          	   rowhead.createCell(1).setCellValue("Available");
          	   rowhead.createCell(2).setCellValue("Reserved");
            count=0;	
          	 }
          	 HSSFRow row = sheet.createRow(rowNumber);
   	            row.createCell(0).setCellValue(stock.getProductCode());
   	            row.createCell(1).setCellValue(stock.getAvailable());
   	            row.createCell(2).setCellValue(stock.getReserved());
   	            rowNumber++;
          count++;
          	 }  
             FileOutputStream fileOut = new FileOutputStream(filename);
             wb.write(fileOut);
             fileOut.close();
             LOG.info("Your Stock excel file has been generated!");
    }
}

public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	} 
	

}
