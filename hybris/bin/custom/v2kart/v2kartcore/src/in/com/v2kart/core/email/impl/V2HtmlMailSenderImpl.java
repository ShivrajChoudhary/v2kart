/**
 * 
 */
package in.com.v2kart.core.email.impl;

import in.com.v2kart.core.email.V2HtmlMailSender;

import java.io.StringWriter;
import java.util.Collections;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.util.mail.MailUtils;

/**
 * @author shubhammaheshwari
 * custom mail sender use to send mail for notification request 
 */
public class V2HtmlMailSenderImpl implements V2HtmlMailSender {

    private final Logger LOG = LoggerFactory.getLogger(V2HtmlMailSenderImpl.class);

    /**
     * default email address which is used to send mail if site specific not found 
     */
    private static final String FROM_EMAIL_ADDRESS = "smtp.mail.from";

    @Resource
    private RendererService rendererService;

    @Resource
    private CMSSiteService cmsSiteService;

    @Resource
    private SiteConfigService siteConfigService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.email.V2HtmlMailSender#createHtmlEmail(org.apache.velocity.VelocityContext, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public HtmlEmail createHtmlEmail(VelocityContext velocityContext, String templateCode, String subject, String to) throws EmailException {
        try {
            String mailFrom = siteConfigService.getProperty(FROM_EMAIL_ADDRESS); 
            if(null != mailFrom){
                final HtmlEmail htmlEmail = (HtmlEmail) MailUtils.getPreConfiguredEmail();
                final RendererTemplateModel bodyTemplate = rendererService.getRendererTemplateForCode(templateCode);
                final StringWriter renderedText = new StringWriter();
                rendererService.render(bodyTemplate, velocityContext, renderedText);
                htmlEmail.setSubject(subject);
                htmlEmail.setHtmlMsg(renderedText.toString());

                htmlEmail.setFrom(mailFrom);
                final InternetAddress fromAdd = new InternetAddress(mailFrom);
                htmlEmail.setReplyTo(Collections.singletonList(fromAdd));
                final Object[] addresses = to.split(",");
                for (final Object address : addresses) {
                    final String string = ((String) address).trim();
                    if (string != null && !StringUtils.isEmpty(string)) {
                        htmlEmail.addTo(string);
                    }
                }
                return htmlEmail;
            }else{
                return null;
            }
        } catch (final AddressException e) {
            LOG.error("Error in creating mail message", e);
            throw new EmailException("Error in creating  mail message", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.email.V2HtmlMailSender#sendEmail(org.apache.commons.mail.HtmlEmail)
     */
    @Override
    public void sendEmail(HtmlEmail email) throws EmailException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Sending log event email %s", new Object[] { ReflectionToStringBuilder.toString(email) }));
        }
        if(null != email){
            email.send();
        }else{
            LOG.error("Email Setting Not Configured Properly");
        }
    }

}
