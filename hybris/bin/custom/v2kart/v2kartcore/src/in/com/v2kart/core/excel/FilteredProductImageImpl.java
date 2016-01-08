package in.com.v2kart.core.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;

public class FilteredProductImageImpl implements FilteredProductImage {

	  private final Logger LOG = Logger.getLogger(FilteredProductImageImpl.class);

	  //Filter All product Media for category page
	  @Override
	public ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> filterAllProductMedia( ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> allProduct){
	    	ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> allProducts = allProduct;
	        List<ProductData> allProductwithImage = new ArrayList<ProductData>();
	        List<ProductData> products=allProducts.getResults();
	         for(ProductData product : products){
	    if(product.getImages()!=null && !product.getImages().isEmpty())   	   
	    {
	    	allProductwithImage.add(product);
	    }
	   }
	         allProducts.setResults(allProductwithImage);  
//	         createNewPagination(allProducts.getPagination(),allProducts.getResults()!=null?allProducts.getResults().size():0);
	         return allProducts;
	    }

	//Filter All product Media for search page 
	@Override
	public ProductSearchPageData<SearchStateData, ProductData> filterAllProductMedia(
			ProductSearchPageData<SearchStateData, ProductData> searchPageData) {
		ProductSearchPageData<SearchStateData, ProductData> searchData=searchPageData;
		
	      List<ProductData> allProductwithImage = new ArrayList<ProductData>();
	        List<ProductData> products=searchData.getResults();
	         for(ProductData product : products){
	    if(product.getImages()!=null && !product.getImages().isEmpty())   	   
	    {
	    	allProductwithImage.add(product);
	    }
	   }
	         searchData.setResults(allProductwithImage);  
	//         createNewPagination(searchData.getPagination(),searchData.getResults()!=null?searchData.getResults().size():0);
	         return searchData;
	} 
	  
	 public PaginationData createNewPagination(PaginationData pagination ,Integer size) {
	        final PaginationData paginationData =pagination;
	        paginationData.setTotalNumberOfResults(size);
	        return paginationData;
	    }

}
