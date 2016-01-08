package in.com.v2kart.core.cronjob;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * @author Shivraj
 *
 *this cronJob used for Generating Excel file for all product where GalleryImage is null
 */
public class V2AllProductWithoutImageJob extends AbstractJobPerformable<CronJobModel> {

	
	 /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(V2AllProductWithoutImageJob.class);
    @Autowired
    protected FlexibleSearchService flexibleSearchService;
    private String fileDir;
    /** {@inheritDoc} */
    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
  
    	try {
           createExcel();
        } catch ( Exception ex ) {
            LOG.info("Product excel file has Not been generated.cause :");
            LOG.error(ex);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }
     return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
   
 // this method is used for getting  All product where gallery Image not available
    public List<ProductModel> findAllProductwithoutGalleryImage() {
        final StringBuilder queryBuilder = new StringBuilder("select {%s} from {%s as p "
        		+ "LEFT JOIN CatalogVersion AS cv ON {p:catalogVersion} = {cv.PK} "
        		+ "LEFT JOIN Catalog AS c ON {cv.catalog} = {c.PK}} "
        		+ "WHERE ( {cv.version} = 'Staged' AND {c.id} = 'v2kartProductCatalog' AND {p.galleryImages} IS NULL AND {p.variantType} = ({{SELECT {%s} FROM {%s} where {"+ComposedTypeModel.CODE+"} = 'V2kartSizeVariantProduct'}}))");
        final String formattedQuery = String.format(queryBuilder.toString(), ProductModel.PK, ProductModel._TYPECODE,ComposedTypeModel.PK,ComposedTypeModel._TYPECODE);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(formattedQuery);
        final SearchResult<ProductModel> products =this.flexibleSearchService.search(query);
   return products.getResult();
    }
	//this method is used for generating Excel files of all product where gallery Image not available
	void createExcel()throws Exception{
		   String filename = fileDir+"AllProductWithout_GalleryImage.xls";
			List<ProductModel> products= findAllProductwithoutGalleryImage();
					 
	    HSSFWorkbook wb = new HSSFWorkbook(); //Access the workbook
        Integer rowNumber = 1;
        Integer sheetNumber = 0;
        Integer count=new Integer(0);
        if (products != null && !products.isEmpty()) {           	
          	 LOG.info("Total Products without gallery Image is : "+products.size());
             HSSFSheet sheet = wb.createSheet("Sheet-"+sheetNumber); //Access the worksheet, so that we can update / modify it.  
          	 HSSFRow rowhead = sheet.createRow(0);
             rowhead.createCell(0).setCellValue("Product code");
          	 for (final ProductModel ProductModel : products) {
          	 if(count==50000){
          		LOG.info("Sheet " + sheetNumber + " written; moving onto to sheet " + (sheetNumber + 1));
          	  sheetNumber++;
          	wb.createSheet("Sheet-"+sheetNumber);
          	  rowNumber = 1;
          	   sheet = wb.getSheetAt(sheetNumber); //Access the worksheet, so that we can update / modify it.  
          	 rowhead=sheet.createRow(0);
          	   rowhead.createCell(0).setCellValue("Product code");
            count=0;	
          	 }
          	 HSSFRow row = sheet.createRow(rowNumber);
   	            row.createCell(0).setCellValue(ProductModel.getCode());
   	            rowNumber++;
          count++;
          	 }  
             FileOutputStream fileOut = new FileOutputStream(filename);
             wb.write(fileOut);
             fileOut.close();
             LOG.info("Your Product excel file has been generated!");
    }
}

public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	} 
	

}
