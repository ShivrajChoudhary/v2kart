/**
 *
 */
package in.com.v2kart.commercewebservices.search.solrfacetsearch.querybuilder;

import de.hybris.platform.commerceservices.search.solrfacetsearch.querybuilder.impl.DefaultFreeTextQueryBuilder;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import org.apache.log4j.Logger;


/**
 * @author Shivraj
 *
 */
public class V2FreeTextQueryBuilder extends DefaultFreeTextQueryBuilder
{
	//
	private static final Logger LOG = Logger.getLogger(V2FreeTextQueryBuilder.class);

	@Override
	protected void addFreeTextQuery(final SearchQuery searchQuery, final IndexedProperty indexedProperty, final String value,
			final double boost)
	{
		final String field = indexedProperty.getName();
		if (!(indexedProperty.isFacet()))
		{
			if ("text".equalsIgnoreCase(indexedProperty.getType()))
			{
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "", boost);
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "*", boost / 2.0D);
				/*
				 * addFreeTextQuery(searchQuery, field, value.toLowerCase(), "~", boost / 4.0D);
				 */}
			else
			{
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "", boost);
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "*", boost / 2.0D);
			}

		}
		else
		{
			LOG.warn("Not searching " + indexedProperty
					+ ". Free text search not available in facet property. Configure an additional text property for searching.");
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("FIELDS : " + field);
		}
	}
}
