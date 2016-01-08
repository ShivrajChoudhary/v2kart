/**
 * 
 */
package in.com.v2kart.core.email;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;

/**
 * @author shubhammaheshwari
 * custom mail sender use to send mail for notification request 
 */
public interface V2HtmlMailSender {
    
    HtmlEmail createHtmlEmail(final VelocityContext velocityContext, String templateCode, final String subject, final String to)
            throws EmailException;


    /**
     * sends log events mail.
     * 
     * @param email
     * @throws EmailException
     */
    void sendEmail(final HtmlEmail email) throws EmailException;

}
