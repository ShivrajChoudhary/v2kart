package in.com.v2kart.dataimport.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import in.com.v2kart.dataimport.dto.V2PriceDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException;
import in.com.v2kart.dataimport.services.BaseImportService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.util.Config;

import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;


public class V2PriceConverter extends DefaultDozerBasedConverter<V2PriceDto, PriceRowModel> {
    /**
 * 
 */
    private static final String DEFAULT_CURRENCY = "INR";

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private UnitService unitService;

    @Autowired
    @Qualifier("baseImportService")
    private BaseImportService baseImportService;

    @Autowired
    private ProductService productService;

    /**
     * @return the commonI18NService
     */
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    /**
     * @param commonI18NService
     *        the commonI18NService to set
     */
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#createTarget()
     */
    @Override
    protected PriceRowModel createTarget() {
        final PriceRowModel priceRow = getModelService().create(PriceRowModel.class);
        priceRow.setCurrency(getCommonI18NService().getCurrency(DEFAULT_CURRENCY));
        return priceRow;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final V2PriceDto source, final PriceRowModel target) {
        target.setPrice(Double.valueOf(source.getPrice()));
        try {
            target.setUnit(unitService.getUnitForCode(Config.getParameter("price.unit")));
        } catch (final UnknownIdentifierException e) {
            throw new FeedPersistanceException(FailureCause.UNIT_NOT_FOUND, source.getRowIndex(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractConverter#convert(java.lang.Object)
     */
    @Override
    @Deprecated
    public PriceRowModel convert(final V2PriceDto source) throws ConversionException {
        final PriceRowModel priceRowModel = createTarget();
        final CatalogVersionModel catalogVersionModel = baseImportService.getCatalogVersionModel();
        if (null == catalogVersionModel) {
            throw new FeedPersistanceException(String.format("No Catalog found for organization unit identifier %1$s for row: %2$s",
                    baseImportService.getCatalogVersionModel(), Integer.valueOf(source.getRowIndex())), FailureCause.CATALOG_VERSION_NOT_FOUND,
                    source.getRowIndex());
        }
        priceRowModel.setProduct(productService.getProductForCode(catalogVersionModel, source.getCode()));
        priceRowModel.setStartTime(source.getValidFrom());
        priceRowModel.setEndTime(source.getValidTo());
        priceRowModel.setPriceType(source.getPriceType());
        priceRowModel.setCatalogVersion(catalogVersionModel);
        populate(source, priceRowModel);
        return priceRowModel;
    }

    /**
     * Function to convert PriceDTO to Price row of product
     * 
     * @param source
     * @param product
     * @return PriceRowModel
     * @throws ConversionException
     */
    public PriceRowModel convert(final V2PriceDto source, final ProductModel product) throws ConversionException {
        final PriceRowModel priceRowModel = createTarget();
        priceRowModel.setProduct(product);
        priceRowModel.setStartTime(source.getValidFrom());
        priceRowModel.setEndTime(source.getValidTo());
        priceRowModel.setPriceType(source.getPriceType());
        priceRowModel.setCatalogVersion(product.getCatalogVersion());
        populate(source, priceRowModel);
        return priceRowModel;
    }

}
