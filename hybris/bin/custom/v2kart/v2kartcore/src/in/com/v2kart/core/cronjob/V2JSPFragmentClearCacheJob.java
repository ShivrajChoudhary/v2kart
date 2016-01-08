/**
 * 
 */
package in.com.v2kart.core.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import in.com.v2kart.core.cache.impl.DefaultV2ManualCacheRegion;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The class <Code> V2JSPFragmentClearCacheJob clears the jsp fragment cache.
 * 
 * @author gaurav2007
 * 
 */
public class V2JSPFragmentClearCacheJob extends AbstractJobPerformable<CronJobModel> {

    /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(V2JSPFragmentClearCacheJob.class);

    @Resource(name = "jspFragmentCacheRegion")
    private DefaultV2ManualCacheRegion jspFragmentCacheRegion;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel )
     */
    @Override
    public PerformResult perform(final CronJobModel paramT) {
        LOG.info("Clear jsp fragemnt cache...");
        PerformResult result = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        jspFragmentCacheRegion.clear();
        result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        return result;
    }

    /**
     * @param jspFragmentCacheRegion
     *        the jspFragmentCacheRegion to set
     */
    @Required
    public void setJspFragmentCacheRegion(final DefaultV2ManualCacheRegion jspFragmentCacheRegion) {
        this.jspFragmentCacheRegion = jspFragmentCacheRegion;
    }

}
