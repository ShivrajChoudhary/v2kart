package in.com.v2kart.core.sms.impl;

import in.com.v2kart.core.dao.BaseDao;
import in.com.v2kart.core.model.V2SmsTemplateModel;
import in.com.v2kart.core.sms.V2SmsService;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.mobileservices.model.text.MobileShortcodeModel;
import de.hybris.platform.mobileservices.text.TextService;

public class V2SmsServiceImpl implements V2SmsService {

    private final Logger LOG = LoggerFactory.getLogger(V2SmsServiceImpl.class);

    @Resource
    private RendererService rendererService;

    @Autowired
    BaseDao<V2SmsTemplateModel> smsTemplateModelDao;

    @Autowired
    private TextService textService;

    private String smsShortCode;

    @Autowired
    private SiteConfigService siteConfigService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.sms.V2SmsService#sendSms(java.util.Map, java.lang.String, java.lang.String)
     */
    @Override
    public void sendSms(final Map<String, String> parameterMap, final String templateCode, final String mobileNumber) {
        if (null == mobileNumber || mobileNumber.isEmpty()) {
            LOG.warn("No Mobile Number Selected");
            return;
        }
        try {
            final V2SmsTemplateModel v2SmsTemplateModel = smsTemplateModelDao.findUniqueModelByCode(templateCode);
            final MobileShortcodeModel paramMobileShortcodeModel = textService.getExclusiveShortCode(smsShortCode);
            final String countryIsoCode = paramMobileShortcodeModel.getCountry().getIsocode();
            for (final String parameter : parameterMap.keySet()) {
                v2SmsTemplateModel.setMessage(StringUtils.replace(v2SmsTemplateModel.getMessage(), "<%=" + parameter + "%>",
                        parameterMap.get(parameter)));
            }
            textService.sendMessage(paramMobileShortcodeModel, countryIsoCode, mobileNumber, v2SmsTemplateModel.getMessage());
        } catch (final Exception e) {
            LOG.error("Error Sending Message !!! " + e);
        }
    }

    /**
     * @param smsShortCode
     *        the smsShortCode to set
     */
    @Value("${mobile.shortcode.v2kart}")
    @Required
    public void setSmsShortCode(final String smsShortCode) {
        this.smsShortCode = smsShortCode;
    }
}
