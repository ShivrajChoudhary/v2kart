package in.com.v2kart.core.cronjob;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import in.com.v2kart.sapinboundintegration.order.daos.V2OrderDao;
/**
 * <V2OrderFailureReportJob> fetches SAP FAILURE orders which are not much older than 5 days and then send/post again then to SAP
 * 
 * @author Nagarro_Devraj
 * @Version 1.0
 * 
 */
public class V2OrderFailureReportJob extends AbstractJobPerformable<CronJobModel> {

    /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(V2OrderFailureReportJob.class);

    /** V2OrderDao bean injection */
    private V2OrderDao v2OrderDao;
private String fileDir;
    /** {@inheritDoc} */
    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
      	LOG.info("Order Failure Report Generating in data/report/OrderFailureReport<currentDate>.txt file");             
        Integer count=new Integer(1);
	    	   SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");//dd/MM/yyyy
 	         Date date=new Date();
 	         String fileWithDate=sdf.format(date);
        // fetch Orders that are FAILURE and less than 15 days old
        final List<OrderModel> failureOrders = v2OrderDao.findAllTypeOfFailureOrders();
        try {
            String filename = fileDir+"OrderFailureReport_"+fileWithDate+".xls" ;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("FirstSheet");  
            
            HSSFRow rowhead = sheet.createRow(0);
            rowhead.createCell(0).setCellValue("Nr");
            rowhead.createCell(1).setCellValue("Order Nr.");
            rowhead.createCell(2).setCellValue("Email");       
            rowhead.createCell(3).setCellValue("City");       
            rowhead.createCell(4).setCellValue("Date");
            rowhead.createCell(5).setCellValue("Total Product no");
            rowhead.createCell(6).setCellValue("Total Price");
            rowhead.createCell(7).setCellValue("Status");
            rowhead.createCell(8).setCellValue("Payment Mode");  
      if (failureOrders != null && !failureOrders.isEmpty()) {           	
         LOG.info("Total Failure Order :"+failureOrders.size());
    	  for (final OrderModel orderModel : failureOrders) {
            		String city=null;
					if (orderModel.getDeliveryAddress() != null)
            {
            	city=orderModel.getDeliveryAddress().getTown();
            }
            HSSFRow row = sheet.createRow(count++);
            row.createCell(0).setCellValue(count-1);
            row.createCell(1).setCellValue(orderModel.getCode());
            if(orderModel.getUser()!=null)
            row.createCell(2).setCellValue(orderModel.getUser().getUid());
         if(city!=null){
            row.createCell(3).setCellValue(city);
         }else{
        	    row.createCell(3).setCellValue("-");
         }
            if(orderModel.getCreationtime()!=null)
            row.createCell(4).setCellValue(getDateTimeStamp(orderModel.getCreationtime()));
           if(orderModel.getEntries()!=null)
            row.createCell(5).setCellValue(orderModel.getEntries().size()); 
           if(orderModel.getTotalPrice()!=null)
           row.createCell(6).setCellValue(orderModel.getTotalPrice());
           if(orderModel.getStatus()!=null)
            row.createCell(7).setCellValue(orderModel.getStatus().toString());
           if(orderModel.getDeliveryMode()!=null && orderModel.getDeliveryMode().getCode().contains("pickup"))
           {
           	 if(orderModel.getPaymentInfo()!=null)
           	 {
           		 if(orderModel.getPaymentInfo().getPaymentMode()!=null)
           		 {
           			 if(orderModel.getPaymentInfo().getPaymentMode().getCode().contains("ZECC"))
           			 {
           				 row.createCell(8).setCellValue("pickup COD");
           			 }
           			 else{
           				 row.createCell(8).setCellValue("pickup Prepaid");	 
           			 }
           		 }
           		 else{
           			 row.createCell(8).setCellValue("");	 
           		 }
           	 }
           	 else{
           		 row.createCell(8).setCellValue("");	 
               	 }
           	 }
           	else {
          if(orderModel.getCodCharges()!=null){
                      row.createCell(8).setCellValue(orderModel.getCodCharges()>0?"COD":"Prepaid");        
          } 	 
          else{
      		row.createCell(8).setCellValue("");   
      		}
     	}

    	  }
			}
            	FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            LOG.info("Your failure Order excel file has been generated!");

        } catch ( Exception ex ) {
        	LOG.info("Your failure Order excel file has been Not generated!");
        	LOG.info(ex);
        	   return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);        	   
        }
     return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
        
   public String getDateTimeStamp(Date date) {
	   if(date!=null){
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy
          String strDate = sdfDate.format(date);
        return strDate;
	   }
	   return null;
	   }
    
    /**
     * @param v2OrderDao
     *        the v2OrderDao to set
     */
    @Required
    public void setV2OrderDao(V2OrderDao v2OrderDao) {
        this.v2OrderDao = v2OrderDao;
    }
	public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
}
