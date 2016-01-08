/**
 * 
 */
package in.com.v2kart.importlog.email.impl;

import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.util.mail.MailUtils;

import in.com.v2kart.importlog.email.ImportLogEventMailSender;
import in.com.v2kart.importlog.email.context.ImportLogEventRenderContext;
import in.com.v2kart.importlog.model.log.ImportLogEventModel;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

/**
 * @author mohit2496
 * 
 */
public class ImportLogEventVelocityMailSenderImpl implements ImportLogEventMailSender {

    /** Logger instance */
    private final Logger LOG = Logger.getLogger(ImportLogEventVelocityMailSenderImpl.class);

    @Resource
    private RendererService rendererService;

    private String subject;
    private String from;
    private String to;

    @Override
    public HtmlEmail createLogEventsEmail(final List<ImportLogEventModel> logEvents) throws EmailException {
        try {
            final HtmlEmail htmlEmail = (HtmlEmail) MailUtils.getPreConfiguredEmail();
            final RendererTemplateModel bodyTemplate = rendererService.getRendererTemplateForCode("log_event_Body");
            final StringWriter renderedText = new StringWriter();
            rendererService.render(bodyTemplate, new ImportLogEventRenderContext(logEvents), renderedText);
            htmlEmail.setSubject(subject + " - {" + new Date() + "}");
            htmlEmail.setHtmlMsg(renderedText.toString());
            htmlEmail.setFrom(from);
            final InternetAddress fromAdd = new InternetAddress(from);
            htmlEmail.setReplyTo(Collections.singletonList(fromAdd));
            htmlEmail.addTo(to);
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("created log event email %s", new Object[] { ReflectionToStringBuilder.toString(htmlEmail) }));
            }
            return htmlEmail;
        } catch (final AddressException e) {
            LOG.error("Error in creating log event mail message", e);
            throw new EmailException("Error in creating log event mail message", e);
        }
    }

    @Override
    public void sendEmail(final HtmlEmail email) throws EmailException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Sending log event email %s", new Object[] { ReflectionToStringBuilder.toString(email) }));
        }
        email.send();
    }

    /**
     * @param subject
     *        the subject to set
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * @param from
     *        the from to set
     */
    public void setFrom(final String from) {
        this.from = from;
    }

    /**
     * @param to
     *        the to to set
     */
    public void setTo(final String to) {
        this.to = to;
    }
}
