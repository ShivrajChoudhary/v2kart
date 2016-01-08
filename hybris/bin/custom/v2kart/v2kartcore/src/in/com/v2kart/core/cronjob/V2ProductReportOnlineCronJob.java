package in.com.v2kart.core.cronjob;

import in.com.v2kart.core.constants.GeneratedV2kartCoreConstants.Enumerations.V2PriceTypeEnum;
import in.com.v2kart.core.enums.SwatchColorEnum;
import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class V2ProductReportOnlineCronJob extends
	AbstractJobPerformable<CronJobModel> {

    private final Logger LOG = Logger
	    .getLogger(V2ProductReportOnlineCronJob.class);
    @Autowired
    protected FlexibleSearchService flexibleSearchService;
    private String fileDir;
    CategoryModel category = new CategoryModel();
    CategoryModel superCategory = new CategoryModel();
    CategoryModel megaCategory = new CategoryModel();
    String switchColor = null;
    SwatchColorEnum primeColor = null;
    boolean galleryAvailability = false;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {

	try {
	    createExcel();
	} catch (Exception ex) {
	    LOG.info("Product excel file has Not been generated.cause :");
	    LOG.error(ex);
	    return new PerformResult(CronJobResult.ERROR,
		    CronJobStatus.FINISHED);
	}
	return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    // this method is used for getting stock level
    public List<V2kartSizeVariantProductModel> getProducts() {
	// final StringBuilder queryBuilder = new
	// StringBuilder("SELECT {%s} FROM {%s} where {variantType} IS NULL ");
	final StringBuilder queryBuilder = new StringBuilder(
		"select {%s} from {%s as p "
			+ "LEFT JOIN CatalogVersion AS cv ON {p:catalogVersion} = {cv.PK} "
			+ "LEFT JOIN Catalog AS c ON {cv.catalog} = {c.PK}} "
			+ "WHERE ( {cv.version} = 'Online' AND {c.id} = 'v2kartProductCatalog' AND {p.variantType} IS NULL )");
	final String formattedQuery = String.format(queryBuilder.toString(),
		V2kartSizeVariantProductModel.PK,
		V2kartSizeVariantProductModel._TYPECODE);
	final FlexibleSearchQuery query = new FlexibleSearchQuery(
		formattedQuery);
	final SearchResult<V2kartSizeVariantProductModel> products = this.flexibleSearchService
		.search(query);
	return products.getResult();
    }

    // this method is used for generating Excel files of Stock level
    // availability
    void createExcel() throws Exception {
	String filename = fileDir + "Product_Report_Online.xls";
	List<V2kartSizeVariantProductModel> products = getProducts();
	HSSFWorkbook wb = new HSSFWorkbook();
	Integer rowNumber = 1;
	Integer sheetNumber = 0;
	Integer count = new Integer(0);
	if (products != null && !products.isEmpty()) {
	    LOG.info("Total product available in Online version ["
		    + products.size() + "]");
	    HSSFSheet sheet = wb.createSheet("Sheet-" + sheetNumber); // Access
	    HSSFRow rowhead = sheet.createRow(0);
	    rowhead.createCell(0).setCellValue("VAR ART");
	    rowhead.createCell(1).setCellValue("VAR ART DESC");
	    rowhead.createCell(2).setCellValue("GEN ART");
	    rowhead.createCell(3).setCellValue("GEN DESC");
	    rowhead.createCell(4).setCellValue("MC CD");
	    rowhead.createCell(5).setCellValue("MC DESC");
	    rowhead.createCell(6).setCellValue("GEN-COL ART");
	    rowhead.createCell(7).setCellValue("SUP CAT");
	    rowhead.createCell(8).setCellValue("SUB CAT");
	    rowhead.createCell(9).setCellValue("CATEGORY");
	    rowhead.createCell(10).setCellValue("SIZE");
	    rowhead.createCell(11).setCellValue("COLOR");
	    rowhead.createCell(12).setCellValue("PRIME COLOR");
	    rowhead.createCell(13).setCellValue("VAR BLOCK INDICATOR");
	    rowhead.createCell(14).setCellValue("BOOSTING FACTOR");
	    rowhead.createCell(15).setCellValue("PROD CR TM");
	    rowhead.createCell(16).setCellValue("PROD MOD TM");
	    rowhead.createCell(17).setCellValue("CSP");
	    rowhead.createCell(18).setCellValue("MRP");
	    rowhead.createCell(19).setCellValue("PRICE ST DT");
	    rowhead.createCell(20).setCellValue("PRICE END DT");
	    rowhead.createCell(21).setCellValue("PRICE CR TM");
	    rowhead.createCell(22).setCellValue("PRICE MOD TM");
	    rowhead.createCell(23).setCellValue("TTL STK");
	    rowhead.createCell(24).setCellValue("RES STK");
	    rowhead.createCell(25).setCellValue("STK CR TM");
	    rowhead.createCell(26).setCellValue("STK MOD TM");
	    // rowhead.createCell(27).setCellValue("IMAGE GALLERY");
	    rowhead.createCell(27).setCellValue("NO OF CONVERTED IMAGES");
	    rowhead.createCell(28).setCellValue("COL-GEN BLOCK INDICATOR");
	    for (final V2kartSizeVariantProductModel product : products) {
		if (count == 50000) {
		    LOG.info("Sheet " + sheetNumber
			    + " written; moving onto to sheet "
			    + (sheetNumber + 1));
		    sheetNumber++;
		    wb.createSheet("Sheet-" + sheetNumber);
		    rowNumber = 1;
		    sheet = wb.getSheetAt(sheetNumber); // Access the worksheet,
		    rowhead = sheet.createRow(0);
		    rowhead.createCell(0).setCellValue("VAR ART");
		    rowhead.createCell(1).setCellValue("VAR ART DESC");
		    rowhead.createCell(2).setCellValue("GEN ART");
		    rowhead.createCell(3).setCellValue("GEN DESC");
		    rowhead.createCell(4).setCellValue("MC CD");
		    rowhead.createCell(5).setCellValue("MC DESC");
		    rowhead.createCell(6).setCellValue("GEN-COL ART");
		    rowhead.createCell(7).setCellValue("SUP CAT");
		    rowhead.createCell(8).setCellValue("SUB CAT");
		    rowhead.createCell(9).setCellValue("CATEGORY");
		    rowhead.createCell(10).setCellValue("SIZE");
		    rowhead.createCell(11).setCellValue("COLOR");
		    rowhead.createCell(12).setCellValue("PRIME COLOR");
		    rowhead.createCell(13).setCellValue("VAR BLOCK INDICATOR");
		    rowhead.createCell(14).setCellValue("BOOSTING FACTOR");
		    rowhead.createCell(15).setCellValue("PROD CR TM");
		    rowhead.createCell(16).setCellValue("PROD MOD TM");
		    rowhead.createCell(17).setCellValue("CSP");
		    rowhead.createCell(18).setCellValue("MRP");
		    rowhead.createCell(19).setCellValue("PRICE ST DT");
		    rowhead.createCell(20).setCellValue("PRICE END DT");
		    rowhead.createCell(21).setCellValue("PRICE CR TM");
		    rowhead.createCell(22).setCellValue("PRICE MOD TM");
		    rowhead.createCell(23).setCellValue("TTL STK");
		    rowhead.createCell(24).setCellValue("RES STK");
		    rowhead.createCell(25).setCellValue("STK CR TM");
		    rowhead.createCell(26).setCellValue("STK MOD TM");
		    // rowhead.createCell(27).setCellValue("IMAGE GALLERY");
		    rowhead.createCell(27).setCellValue(
			    "NO OF CONVERTED IMAGES");
		    rowhead.createCell(28).setCellValue(
			    "COL-GEN BLOCK INDICATOR");
		    count = 0;
		}
		HSSFRow row = sheet.createRow(rowNumber);
		subCategory(product);
		// getSwitchColor(product);
		findGalleryImage(row, product);
		row.createCell(0).setCellValue(product.getCode());
		row.createCell(1).setCellValue(product.getName());
		row.createCell(2).setCellValue(baseProduct(product));
		row.createCell(3).setCellValue(baseProductDesc(product));
		row.createCell(4).setCellValue(product.getMccd());
		row.createCell(5).setCellValue(product.getMcdesc());
		row.createCell(6).setCellValue(colorVariant(product));
		row.createCell(7).setCellValue(
			superCategory.getName() != null ? superCategory
				.getName() : "");
		row.createCell(8).setCellValue(
			megaCategory.getName() != null ? megaCategory.getName()
				: "");
		row.createCell(9).setCellValue(
			category.getName() != null ? category.getName() : "");
		row.createCell(10).setCellValue(product.getSize());
		row.createCell(11).setCellValue(
			switchColor != null ? switchColor : "");
		row.createCell(12).setCellValue(
			primeColor != null ? primeColor.getCode() : "");
		row.createCell(13).setCellValue(
			product.getApprovalStatus() != null ? product
				.getApprovalStatus().toString() : "");
		// color variant boosting factor
		if (product.getBaseProduct() != null) {
		    row.createCell(14)
			    .setCellValue(
				    product.getBaseProduct()
					    .getBoostingFactor() != null ? product
					    .getBaseProduct()
					    .getBoostingFactor().getCode()
					    : "");
		}
		row.createCell(15).setCellValue(product.getCreationtime());
		if (product.getModifiedtime() != null) {
		    row.createCell(16).setCellValue(product.getModifiedtime());
		}

		// price information of the product
		PriceRowModel price = getPrice(product);
		if (price != null) {
		    if (price.getPriceType() != null) {
			if (price.getPriceType().equals(V2PriceTypeEnum.CSP)) {
			    row.createCell(17).setCellValue(price.getPrice());
			} else {
			    row.createCell(18).setCellValue(price.getPrice());
			}
		    }
		    if (price.getStartTime() != null)
			row.createCell(19).setCellValue(price.getStartTime());
		    if (price.getEndTime() != null)
			row.createCell(20).setCellValue(price.getEndTime());
		    row.createCell(21).setCellValue(price.getCreationtime());
		    if (price.getModifiedtime() != null)
			row.createCell(22)
				.setCellValue(price.getModifiedtime());
		}
		// Stock level information of the product
		StockLevelModel stock = getStock(product);
		if (stock != null) {
		    row.createCell(23).setCellValue(stock.getAvailable());
		    row.createCell(24).setCellValue(stock.getReserved());
		    row.createCell(25).setCellValue(stock.getCreationtime());
		    if (stock.getModifiedtime() != null)
			row.createCell(26)
				.setCellValue(stock.getModifiedtime());
		}
		// row.createCell(27).setCellValue(galleryAvailability==true?"YES":"NOT");

		// ----------------------------------------------------------//
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

    public String colorVariant(V2kartSizeVariantProductModel product) {
	return product.getBaseProduct().getCode();
    }

    public String baseProduct(V2kartSizeVariantProductModel product) {
	V2kartStyleVariantProductModel color = (V2kartStyleVariantProductModel) product
		.getBaseProduct();
	return color.getBaseProduct().getCode();
    }

    public String baseProductDesc(V2kartSizeVariantProductModel product) {
	V2kartStyleVariantProductModel color = (V2kartStyleVariantProductModel) product
		.getBaseProduct();
	return color.getSummary();
    }

    public void subCategory(V2kartSizeVariantProductModel product) {
	if (product.getSupercategories() != null) {
	    List<CategoryModel> categories = (List<CategoryModel>) product
		    .getSupercategories();
	    if (categories != null && categories.size() > 0) {
		category = categories.get(0);
		if (category != null
			&& category.getSupercategories().size() > 0) {
		    megaCategory = category.getSupercategories().get(0);
		    if (megaCategory != null
			    && megaCategory.getSupercategories().size() > 0)
			superCategory = megaCategory.getSupercategories()
				.get(0);
		}
	    }
	}
    }

    public PriceRowModel getPrice(ProductModel product) {
	final StringBuilder queryBuilder = new StringBuilder(
		"SELECT {%s} FROM {%s} where {product} =" + product.getPk());
	final String formattedQuery = String.format(queryBuilder.toString(),
		PriceRowModel.PK, PriceRowModel._TYPECODE);
	final FlexibleSearchQuery query = new FlexibleSearchQuery(
		formattedQuery);
	final SearchResult<PriceRowModel> price = this.flexibleSearchService
		.search(query);
	if (price != null) {
	    List<PriceRowModel> pr = price.getResult();
	    if (pr != null && !pr.isEmpty() && pr.size() > 0) {
		return pr.get(0);
	    }
	}
	return null;
    }

    public void findGalleryImage(HSSFRow row,
	    V2kartSizeVariantProductModel productModel) {
	StringBuilder counts = new StringBuilder();
	if (productModel.getBaseProduct() instanceof V2kartStyleVariantProductModel) {
	    V2kartStyleVariantProductModel baseProduct = (V2kartStyleVariantProductModel) productModel
		    .getBaseProduct();
	    if (baseProduct != null) {
		row.createCell(28).setCellValue(
			baseProduct.getApprovalStatus() != null ? baseProduct
				.getApprovalStatus().toString() : "-");
		switchColor = baseProduct.getStyle();
		primeColor = baseProduct.getPrimaryColor();
		final List<MediaContainerModel> mediaContainers = baseProduct
			.getGalleryImages();
		LOG.debug("MediaContainer is :" + mediaContainers);

		if (CollectionUtils.isNotEmpty(mediaContainers)) {
		    for (MediaContainerModel mediaContainer : mediaContainers) {
			if (mediaContainer.getMedias() != null) {
			    counts.append(mediaContainer.getMedias().size());
			    counts.append(",");
			}
		    }
		}
		LOG.debug("Total Medias of [" + productModel.getCode() + "]is:"
			+ counts);
		row.createCell(27).setCellValue(counts.toString());
	    }
	}
	LOG.debug("************END***************");
    }

    public StockLevelModel getStock(V2kartSizeVariantProductModel product) {
	if (product.getStockLevels() != null
		&& !product.getStockLevels().isEmpty()) {
	    return product.getStockLevels().iterator().next();
	}
	return null;
    }
}