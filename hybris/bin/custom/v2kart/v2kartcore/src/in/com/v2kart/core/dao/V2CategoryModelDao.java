/**
 * 
 */
package in.com.v2kart.core.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;

import java.util.List;


/**
 * @author mohit2496
 * 
 */
public interface V2CategoryModelDao
{


	/**
	 * @param mcCode
	 * @return List<CategoryModel>
	 */
	List<CategoryModel> findCategoriesByMcCode(final String mcCode, final CatalogVersionModel catalogVersionModel);

}
