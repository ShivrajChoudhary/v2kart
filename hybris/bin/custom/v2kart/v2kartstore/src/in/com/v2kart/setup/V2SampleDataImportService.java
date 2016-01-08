/**
 *
 */
package in.com.v2kart.setup;

import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;

/**
 * The Class V2SampleDataImportService.
 * 
 * @author atul2455
 */
public class V2SampleDataImportService extends SampleDataImportService {

    @Override
    protected void importProductCatalog(final String extensionName, final String productCatalogName) {
        super.importProductCatalog(extensionName, productCatalogName);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-prices.impex", new Object[] { extensionName,
                        productCatalogName }), false);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-stocklevels.impex", new Object[] {
                        extensionName, productCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-pos-stocklevels.impex", new Object[] {
                        extensionName, productCatalogName }), false);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-tax.impex", new Object[] { extensionName,
                        productCatalogName }), false);
        
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-classifications.impex", new Object[] { extensionName,
                        productCatalogName }), false);
    }

}
