package in.com.v2kart.core.cronjob;

import in.com.v2kart.core.dao.V2CustomerDao;
import in.com.v2kart.core.email.V2HtmlMailSender;
import in.com.v2kart.core.model.V2CustomerWalletTransactionModel;
import in.com.v2kart.core.process.email.context.V2WalletExpirationEmailContext;
import in.com.v2kart.fulfilmentprocess.dao.V2WalletCreditDao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;

/**
 * Job to expire wallet credit of user if not modified from last 364 days, and send the mail to the customer.
 * 
 * @author shailja gupta
 *
 */
public class V2ExpireWaletAmountCronJob extends AbstractJobPerformable<CronJobModel> {

    protected static final Logger LOG = Logger.getLogger(V2ExpireWaletAmountCronJob.class);

    private static final String WALLET_EXPIRATION_KEY = "customer.wallet.credit.expiration.days";
    private static final String WALLET_EXPIRED_SUBJECT = "customer.wallet.credit.expired.email.subject";
    private static final String WALLET_CREDIT_EXPIRED_BODY = "Wallet_Credit_Expired_Body";

    private V2WalletCreditDao v2WalletCreditDao;
    
    private V2CustomerDao v2CustomerDao;
    
    private V2HtmlMailSender htmlMailSender;

    @Override
    public PerformResult perform(CronJobModel arg0) {
        PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        String description = "Wallet Credit expired";
        List<CustomerModel> customers = getV2CustomerDao().findAllCustomers();
        if (customers != null && customers.size() != 0) {
            for (CustomerModel customer : customers) {
                List<V2CustomerWalletTransactionModel> transactions = getV2WalletCreditDao().getTransactionsForCustomer(customer, null);
                if (transactions != null && transactions.size() != 0) {
                    V2CustomerWalletTransactionModel latestTransaction = transactions.get(0);
                    final Calendar date = Calendar.getInstance();
                    date.setTime(new Date());
                    date.add(Calendar.DAY_OF_YEAR, Integer.parseInt(Config.getParameter(WALLET_EXPIRATION_KEY)));
                    if (latestTransaction.getPostedDate().getTime() < date.getTime().getTime()) {
                        Double amount = getV2WalletCreditDao().getBalanceForCustomer(customer);
                        getV2WalletCreditDao().addTransaction(customer, description, -amount, null);

                        // send mail to the customer notifying that wallet amount has been expired.
                        final String walletExpiredSubject = Config.getParameter(WALLET_EXPIRED_SUBJECT);
                        sendMail(WALLET_CREDIT_EXPIRED_BODY, customer, walletExpiredSubject);
                    }
                }
            }
        }
        return result;
    }

    private void sendMail(final String templateCode, final CustomerModel customer, final String mailSubject) {

        final V2WalletExpirationEmailContext walletExpirationEmailContext = new V2WalletExpirationEmailContext(customer);
        final String to = walletExpirationEmailContext.getCustomerData().getDisplayUid();

        try {
            getHtmlMailSender().sendEmail(getHtmlMailSender().createHtmlEmail(walletExpirationEmailContext, templateCode, mailSubject, to));
        } catch (final EmailException e) {
            LOG.info("Failed to send wallet credit expired mail to customer.", e);
        }
    }

    public V2CustomerDao getV2CustomerDao() {
        return v2CustomerDao;
    }

    public void setV2CustomerDao(V2CustomerDao v2CustomerDao) {
        this.v2CustomerDao = v2CustomerDao;
    }

    public V2WalletCreditDao getV2WalletCreditDao() {
        return v2WalletCreditDao;
    }

    public void setV2WalletCreditDao(V2WalletCreditDao v2WalletCreditDao) {
        this.v2WalletCreditDao = v2WalletCreditDao;
    }

    public V2HtmlMailSender getHtmlMailSender() {
        return htmlMailSender;
    }

    public void setHtmlMailSender(V2HtmlMailSender mailSender) {
        this.htmlMailSender = mailSender;
    }
}
