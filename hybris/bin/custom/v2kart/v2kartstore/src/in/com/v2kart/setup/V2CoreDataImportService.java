/**
 *
 */
package in.com.v2kart.setup;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.validation.services.ValidationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * The Class V2CoreDataImportService.
 */
public class V2CoreDataImportService extends CoreDataImportService {

    /** Solr Environment */
    private String envSolr;

    @Override
    public void execute(final AbstractSystemSetup systemSetup, final SystemSetupContext context, final List<ImportData> importData) {
        final boolean importCoreData = systemSetup.getBooleanSystemSetupParameter(context, "importCoreData");

        if (!(importCoreData)) {
            return;
        }
        for (final ImportData data : importData) {
            importAllData(systemSetup, context, data, true);
        }
        this.importCatalogUserPermissions(context.getExtensionName());
        final ValidationService validation = (ValidationService) getBeanForName("validationService");
        validation.reloadValidationEngine();
    }

    private void importCatalogUserPermissions(final String extensionName) {
        if (isExtensionLoaded("cmscockpit")) {
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/cockpits/cmscockpit/cmscockpit-users.impex", new Object[] { extensionName }), false);
        }
        if (isExtensionLoaded("productcockpit")) {
            getSetupImpexService()
                    .importImpexFile(
                            String.format("/%s/import/coredata/cockpits/productcockpit/productcockpit-users.impex",
                                    new Object[] { extensionName }), false);
        }
        if (isExtensionLoaded("cscockpit")) {
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/cockpits/cscockpit/cscockpit-users.impex", new Object[] { extensionName }), false);
        }
    }

    @Override
    protected void importCommonData(final String extensionName) {
        super.importCommonData(extensionName);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/v2kart_masterdata.impex", new Object[]
        { extensionName }), true);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/jobs.impex", new Object[]
        { extensionName }), true);

    }

    @Override
    protected void importProductCatalog(final String extensionName, final String productCatalogName) {
        super.importProductCatalog(extensionName, productCatalogName);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/categories-mccode.impex", new Object[]
                { extensionName, productCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/categories.impex", new Object[]
                { extensionName, productCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/categories_en.impex", new Object[]
                { extensionName, productCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/categories-classifications.impex", new Object[]
                { extensionName, productCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/categories-classifications_en.impex",
                        new Object[]
                        { extensionName, productCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/mediaconversion_formats.impex", new Object[]
                { extensionName, productCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/suppliers.impex",
                        new Object[]
                        { extensionName, productCatalogName }), false);
    }

    @Override
    protected void importSolrIndex(final String extensionName, final String storeName) {
        if ("standalone_dev".equalsIgnoreCase(this.envSolr)) {
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/stores/%s/solr_standalone_dev.impex", new Object[] { extensionName, storeName }),
                    false);
        }
        if ("standalone_uat".equalsIgnoreCase(this.envSolr)) {
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/stores/%s/solr_standalone_uat.impex", new Object[] { extensionName, storeName }),
                    false);
        }
        if ("standalone_prod".equalsIgnoreCase(this.envSolr)) {
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/stores/%s/solr_standalone_prod.impex", new Object[] { extensionName, storeName }),
                    false);
        }
        if ("standalone_stagingprod".equalsIgnoreCase(this.envSolr)) {
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/stores/%s/solr_standalone_stagingprod.impex", new Object[] { extensionName, storeName }),
                    false);
        }

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/stores/%s/solr.impex", new Object[] { extensionName, storeName }), false);

        getSetupSolrIndexerService().createSolrIndexerCronJobs(String.format("%sIndex", new Object[] { storeName }));

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/stores/%s/solr-classification.impex", new Object[]
                { extensionName, storeName }), false);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/stores/%s/solr-boost-rules.impex", new Object[] { extensionName, storeName }), false);
        
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/stores/%s/solrtrigger.impex", new Object[] { extensionName, storeName }), false);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/stores/%s/solr-CSVariant.impex", new Object[]
                { extensionName, storeName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/stores/%s/solr-CSclassification.impex", new Object[]
                { extensionName, storeName }), false);
    }

    /**
     * @param envSolr
     *        the envSolr to set
     */
    @Required
    public void setEnvSolr(final String envSolr) {
        this.envSolr = envSolr;
    }

    @Override
    protected void importContentCatalog(String extensionName, String contentCatalogName) {
        super.importContentCatalog(extensionName, contentCatalogName);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/v2kart-cms-content.impex", new Object[] {
                        extensionName,
                        contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/v2kart-cms-content_en.impex", new Object[] {
                        extensionName,
                        contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/termsOfUse.impex", new Object[]
                { extensionName, contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/privacyPolicy.impex", new Object[]
                { extensionName, contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/safeAndSecureShopping.impex", new Object[]
                { extensionName, contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/careers.impex", new Object[]
                { extensionName, contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/corporateBlog.impex", new Object[]
                { extensionName, contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/ResponsibleDisclosurePolicy.impex", new Object[]
                { extensionName, contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/shipping.impex", new Object[]
                { extensionName, contentCatalogName }), false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/v2kart-cms-mobile-content.impex", new Object[] {
                        extensionName,
                        contentCatalogName }), false);
       
    }
    
    @Override
    protected void importStore(String extensionName, String storeName, String productCatalogName) {
        super.importStore(extensionName, storeName, productCatalogName);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/stores/%s/warehouses.impex", new Object[] { extensionName, storeName }), false);
    }

}
