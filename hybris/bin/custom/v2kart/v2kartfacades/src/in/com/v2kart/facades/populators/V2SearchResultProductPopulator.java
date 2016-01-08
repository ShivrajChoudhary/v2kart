package in.com.v2kart.facades.populators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.converters.populator.SearchResultProductPopulator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

public class V2SearchResultProductPopulator extends SearchResultProductPopulator {

    @Override
    public void populate(final SearchResultValueData source, final ProductData target)
    {
	super.populate(source, target);

	if (target.getName() != null && !target.getName().isEmpty())
	{
	    target.setName(target.getName().replaceAll("\'", "`"));
	}
	if (target.getDescription() != null && !target.getDescription().isEmpty())
	{
	    target.setDescription(target.getDescription().replaceAll("\'", "`"));
	}
	if (target.getSummary() != null && !target.getSummary().isEmpty())
	{
	    target.setSummary(target.getSummary().replaceAll("\'", "`"));
	}
	populatePrices(source, target);
	populateSize(source, target);
    }

    private void populateSize(SearchResultValueData source, ProductData target) {

	final List<String> sizeValue = this.<List> getValue(source, "size");
	target.setSize(sizeValue);
    }

    @Override
    protected void populatePrices(final SearchResultValueData source, final ProductData target) {

	// Pull the price value for the current currency
	final Double mrpValue = this.<Double> getValue(source, "priceValue");
	final Double discountedValue = this.<Double> getValue(source, "discountedPrice");
	// final String plpImageFormat = this.<String> getValue(source,
	// "img-Product-157Wx235H");
	// final List<ImageData> result = new ArrayList<ImageData>();
	createImageData(source);
	if (mrpValue != null) {
	    final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
		    BigDecimal.valueOf(mrpValue.doubleValue()),
		    getCommonI18NService().getCurrentCurrency());
	    target.setPrice(priceData);
	    ;
	}
	if (discountedValue != null) {
	    final PriceData discountedPriceData = getPriceDataFactory().create(PriceDataType.BUY,
		    BigDecimal.valueOf(discountedValue.doubleValue()),
		    getCommonI18NService().getCurrentCurrency());
	    target.setDiscountedPrice(discountedPriceData);
	}
	if (mrpValue != null && discountedValue != null) {
	    int discount = calculateDiscount(mrpValue, discountedValue);
	    target.setPercentageDiscount(discount);
	}
    }

    private int calculateDiscount(Double mrpValue, Double discountedValue) {
	int discount = 0;
	if (null != mrpValue && null != discountedValue) {
	    discount = (int) (((mrpValue - discountedValue) * 100) / mrpValue);
	}
	return discount;
    }

    protected List<ImageData> createImageData(final SearchResultValueData source)
    {
	final List<ImageData> result = new ArrayList<ImageData>();

	addImageData(source, "productListing", result);
	addImageData(source, "thumbnail", result);
	addImageData(source, "product", result);
	addImageData(source, "mobileProductListing", result);
	addImageData(source, "newProductListing", result);

	return result;
    }

}
