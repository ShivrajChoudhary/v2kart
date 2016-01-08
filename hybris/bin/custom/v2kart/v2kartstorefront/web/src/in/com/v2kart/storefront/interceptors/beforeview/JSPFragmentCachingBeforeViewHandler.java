/**
 *
 */
package in.com.v2kart.storefront.interceptors.beforeview;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import in.com.v2kart.core.cache.V2ManualCacheRegion;
import in.com.v2kart.storefront.interceptors.BeforeViewHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author sumit1311
 * 
 */
public class JSPFragmentCachingBeforeViewHandler implements BeforeViewHandler {

    private V2ManualCacheRegion jspFragmentCacheRegion;

    private SiteConfigService siteConfigService;

    private CMSSiteService cmsSiteService;

    /**
     * @return the cmsSiteService
     */
    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    /**
     * @param cmsSiteService
     *        the cmsSiteService to set
     */
    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    protected SiteConfigService getSiteConfigService() {
        return siteConfigService;
    }

    @Required
    public void setSiteConfigService(final SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    /**
     * @param jspFragmentCacheRegion
     *        the jspFragmentCacheRegion to set
     */
    public void setJspFragmentCacheRegion(final V2ManualCacheRegion jspFragmentCacheRegion) {
        this.jspFragmentCacheRegion = jspFragmentCacheRegion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.storefront.interceptors.BeforeViewHandler#beforeView(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
            throws Exception {
        modelAndView.addObject("fragmentCacheEnabled",
                Boolean.valueOf(getSiteConfigService().getBoolean("storefront.jspfragment.cache.enabled", false)));

        modelAndView.addObject("v2kart-site", getCmsSiteService().getCurrentSite().getUid());

        modelAndView.addObject("jspFragmentCacheRegion", jspFragmentCacheRegion);
    }

}
