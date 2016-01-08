/**
 * 
 */
package in.com.v2kart.core.cronjob;

import in.com.v2kart.core.email.V2HtmlMailSender;
import in.com.v2kart.core.enums.V2NotifyCustomerTypeEnum;
import in.com.v2kart.core.model.NotifyCustomerModel;
import in.com.v2kart.core.process.email.context.V2CustomerNotificationEmailContext;
import in.com.v2kart.facades.core.data.V2CustomerNotificationData;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

/**
 * @author shubhammaheshwari Send Email to the Customer When Product Comes inStock or Product price is equals to User Price request.
 */
public class V2NotifyCustomerCronJob extends AbstractJobPerformable<CronJobModel> {

    protected static final Logger LOG = LoggerFactory.getLogger(V2NotifyCustomerCronJob.class);

    private V2HtmlMailSender v2HtmlMailSender;
    private CommercePriceService priceService;
    private UrlResolver<ProductModel> productModelUrlResolver;
    private CommerceStockService commerceStockService;
    private SiteConfigService siteConfigService;
    private String websiteUrl;
    private UserService userService;

    private static final String EMAIL_NOTIFY_ME_BODY_TEMPLATE = "Email_Notify_Me_Body";
    private static final String EMAIL_NOTIFY_MY_PRICE_BODY_TEMPLATE = "Email_Notify_My_Price_Body";
    private static final String EMAIL_NOTIFY_ME_SUBJECT = "smtp.mail.notifyMe.completion.subject";
    private static final String EMAIL_NOTIFY_MY_PRICE_SUBJECT = "smtp.mail.notifyMyPrice.completion.subject";
    private static final String USER = "User";

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final CronJobModel arg0) {
        PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

        final String query = "select {pk} from {NotifyCustomer}";
        final Map<String, String> map = new HashMap<>();
        final SearchResult<NotifyCustomerModel> v2CustomerNotifications = flexibleSearchService.search(query, map);
        try {
            for (final NotifyCustomerModel customerNotifications : v2CustomerNotifications.getResult()) {
                checkProductStatus(customerNotifications);
            }
        } catch (final EmailException e) {
            LOG.error("Failed to send customer notification mail.", e);
            result = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }
        return result;
    }

    /**
     * @param customerNotification
     * @throws EmailException
     *         Checks Product Status inStock or Price Request by Customer
     */
    private void checkProductStatus(final NotifyCustomerModel customerNotification) throws EmailException {
        if (V2NotifyCustomerTypeEnum.NOTIFY_ME.equals(customerNotification.getType())) {
            final ProductModel product = customerNotification.getProduct();
            final StockLevelStatus stockLevelStatus = commerceStockService.getStockLevelStatusForProductAndBaseStore(product,
                    customerNotification.getBaseSite().getStores().get(0));
            if (!StockLevelStatus.OUTOFSTOCK.equals(stockLevelStatus)) {
                createEmail(customerNotification);
                modelService.remove(customerNotification);
            }
        }// else notify my price request
        else {
            final ProductModel product = customerNotification.getProduct();
            PriceInformation priceInformation = priceService.getWebPriceForProduct(product);
            if (priceInformation.getPriceValue().getValue() == customerNotification.getPrice().doubleValue()) {
                createEmail(customerNotification);
                modelService.remove(customerNotification);
            }
        }
    }

    /**
     * @param customerNotification
     * @throws EmailException
     * Create email set body template code and subject
     */
    private void createEmail(final NotifyCustomerModel customerNotification) throws EmailException {
        final V2CustomerNotificationData customerNotificationData = populateFGCustomerNotificationData(customerNotification);
        if (V2NotifyCustomerTypeEnum.NOTIFY_ME.equals(customerNotification.getType())) {
            String notifyMeSubject = null;
            notifyMeSubject = customerNotificationData.getName().concat(siteConfigService.getProperty(EMAIL_NOTIFY_ME_SUBJECT));
            final VelocityContext notifyCustomerEmailContext = new V2CustomerNotificationEmailContext(customerNotificationData);
            final String to = customerNotificationData.getEmailId();

            v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(notifyCustomerEmailContext, EMAIL_NOTIFY_ME_BODY_TEMPLATE,
                    notifyMeSubject, to));
        } else {
            String notifyMyPriceSubject = null;
            notifyMyPriceSubject = customerNotificationData.getName().concat(" ")
                    .concat(siteConfigService.getProperty(EMAIL_NOTIFY_MY_PRICE_SUBJECT))
                    .concat(" ").concat(String.valueOf(customerNotificationData.getNotificationPrice()));
            final VelocityContext notifyCustomerEmailContext = new V2CustomerNotificationEmailContext(customerNotificationData);
            final String to = customerNotificationData.getEmailId();

            v2HtmlMailSender
                    .sendEmail(v2HtmlMailSender.createHtmlEmail(notifyCustomerEmailContext, EMAIL_NOTIFY_MY_PRICE_BODY_TEMPLATE,
                            notifyMyPriceSubject, to));
        }
    }

    /**
     * @param customerNotification
     * @return
     */
    private V2CustomerNotificationData populateFGCustomerNotificationData(final NotifyCustomerModel customerNotification) {
        if (customerNotification.getBaseSite() != null) {
            setWebsiteUrl(customerNotification.getBaseSite().getUid());
        }
        final V2CustomerNotificationData customerNotificationData = new V2CustomerNotificationData();
        customerNotificationData.setEmailId(customerNotification.getEmail());
        try {
            final UserModel user = userService.getUserForUID(customerNotification.getEmail());
            customerNotificationData.setCurrentUserName(user.getDisplayName());
        } catch (final UnknownIdentifierException e) {
            customerNotificationData.setCurrentUserName(USER);
        }

        final ProductModel product = customerNotification.getProduct();
        if (websiteUrl != null) {
            customerNotificationData.setUrl(websiteUrl.concat(productModelUrlResolver.resolve(product)));
        }
        customerNotificationData.setProductCode(product.getCode());
        customerNotificationData.setName(product.getName());
        customerNotificationData.setNotificationPrice(customerNotification.getPrice().doubleValue());
        return customerNotificationData;
    }

    /**
     * @param fgHtmlMailSender
     *        the fgHtmlMailSender to set
     */
    @Required
    public void setV2HtmlMailSender(final V2HtmlMailSender v2HtmlMailSender) {
        this.v2HtmlMailSender = v2HtmlMailSender;
    }

    /**
     * @param priceService
     *        the priceService to set
     */
    @Required
    public void setPriceService(final CommercePriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * @param productModelUrlResolver
     *        the productModelUrlResolver to set
     */
    @Required
    public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    /**
     * @param commerceStockService
     *        the commerceStockService to set
     */
    @Required
    public void setCommerceStockService(final CommerceStockService commerceStockService) {
        this.commerceStockService = commerceStockService;
    }

    /**
     * @param productModel
     *        the websiteUrl to set
     */
    public void setWebsiteUrl(final String siteName) {
        websiteUrl = Config.getParameter("website." + siteName + ".https");
    }

    /**
     * @param userService
     *        the userService to set
     */
    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public SiteConfigService getSiteConfigService() {
        return siteConfigService;
    }

    public void setSiteConfigService(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }
}
