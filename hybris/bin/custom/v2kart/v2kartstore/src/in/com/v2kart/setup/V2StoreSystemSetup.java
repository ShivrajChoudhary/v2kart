/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package in.com.v2kart.setup;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;

import in.com.v2kart.constants.V2kartstoreConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

@SystemSetup(extension = V2kartstoreConstants.EXTENSIONNAME)
public class V2StoreSystemSetup extends AbstractSystemSetup {

    public static final String V2_KART = "v2kart";

    private static final String IMPORT_CORE_DATA = "importCoreData";
    private static final String IMPORT_SAMPLE_DATA = "importSampleData";
    private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";

    private CoreDataImportService coreDataImportService;
    private SampleDataImportService sampleDataImportService;

    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

        params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
        params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));

        return params;
    }

    /**
     * This method will be called during the system initialization.
     * 
     * @param context
     *        the context provides the selected parameters and values
     */
    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
    public void createProjectData(final SystemSetupContext context) {
        final List<ImportData> importData = new ArrayList<>();

        importData.add(importCoreData(V2_KART, false));

        getCoreDataImportService().execute(this, context, importData);
        getEventService().publishEvent(new CoreDataImportedEvent(context, importData));

        getSampleDataImportService().execute(this, context, importData);
        getEventService().publishEvent(new SampleDataImportedEvent(context, importData));
    }

    private ImportData importCoreData(final String siteId, final boolean alreadyImported) {
        final ImportData importData = new ImportData();
        importData.setProductCatalogName(siteId);
        importData.setStoreNames(Arrays.asList(siteId));
        if (alreadyImported) {
            importData.setContentCatalogNames(Collections.EMPTY_LIST);
        } else {
            importData.setContentCatalogNames(Arrays.asList(siteId));
        }
        return importData;
    }

    public CoreDataImportService getCoreDataImportService() {
        return coreDataImportService;
    }

    @Required
    public void setCoreDataImportService(final CoreDataImportService coreDataImportService) {
        this.coreDataImportService = coreDataImportService;
    }

    public SampleDataImportService getSampleDataImportService() {
        return sampleDataImportService;
    }

    @Required
    public void setSampleDataImportService(final SampleDataImportService sampleDataImportService) {
        this.sampleDataImportService = sampleDataImportService;
    }
}
