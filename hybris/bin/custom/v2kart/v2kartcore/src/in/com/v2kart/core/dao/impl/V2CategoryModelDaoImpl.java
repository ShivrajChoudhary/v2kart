/**
 * 
 */
package in.com.v2kart.core.dao.impl;

import in.com.v2kart.core.dao.V2CategoryModelDao;

import java.util.Collections;
import java.util.List;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class V2CategoryModelDaoImpl extends DefaultCatalogAwareModelDaoImpl<CategoryModel> implements V2CategoryModelDao
{
	/**
	 * @param typecode
	 * @param codePropertyName
	 * @param catalogVersionPropertyName
	 */
	public V2CategoryModelDaoImpl(final String typecode, final String codePropertyName, final String catalogVersionPropertyName)
	{
		super(typecode, codePropertyName, catalogVersionPropertyName);
	}

	@Override
	public List<CategoryModel> findCategoriesByMcCode(final String mcCode, final CatalogVersionModel catalogVersionModel)
	{
		final FlexibleSearchQuery q = new FlexibleSearchQuery(
				"SELECT {c:PK} FROM { Category as c JOIN V2CategoryMcCodeRel as rel ON {c:PK} = {rel:source} JOIN V2McCode AS mcc ON {rel:target} = {mcc:PK} } where {mcc.code}=?mccode AND {c.catalogVersion} = ?catalogVersionModel");
		q.addQueryParameter("mccode", mcCode);
		q.addQueryParameter("catalogVersionModel", catalogVersionModel);

		q.setResultClassList(Collections.singletonList(ProductModel.class));
		final SearchResult<CategoryModel> results = getFlexibleSearchService().search(q);
		return results.getResult();
	}
}
