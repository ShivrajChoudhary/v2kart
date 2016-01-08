/**
 * 
 */
package in.com.v2kart.importlog.email;

import in.com.v2kart.importlog.model.log.ImportLogEventModel;

import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * Simple <ImportLogEventMailSender> interface which helps to create HTML e-mails with pre-configured to/from fields.<br>
 * Contains methods for building up a text/HTML message and sending that mail message.
 * 
 * @author Nagarro-Dev.
 * 
 */
public interface ImportLogEventMailSender
{

    /**
     * creates log events mail.
     */
    public HtmlEmail createLogEventsEmail(final List<ImportLogEventModel> logEvents) throws EmailException;

    /**
     * sends log events mail.
     */
    public void sendEmail(final HtmlEmail email) throws EmailException;

}