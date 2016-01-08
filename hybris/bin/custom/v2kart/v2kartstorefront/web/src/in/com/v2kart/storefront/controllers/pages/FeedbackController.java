package in.com.v2kart.storefront.controllers.pages;

import java.util.ArrayList;
import java.util.List;

import in.com.v2kart.core.email.V2HtmlMailSender;
import in.com.v2kart.core.enums.FeedbackCategoryEnum;
import in.com.v2kart.core.enums.FeedbackRatingEnum;
import in.com.v2kart.core.process.email.context.V2FeedbackEmailContext;
import in.com.v2kart.core.services.V2FeedbackService;
import in.com.v2kart.facades.core.data.V2FeedbackData;
import in.com.v2kart.storefront.forms.V2FeedbackForm;
import in.com.v2kart.storefront.forms.validation.V2FeedbackValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.apache.velocity.VelocityContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.enumeration.EnumerationService;

@Controller
@Scope("tenant")
@RequestMapping("/feedback")
public class FeedbackController extends AbstractSearchPageController {
    @Resource(name = "feedbackService")
    private V2FeedbackService feedbackService;

    @Resource(name = "feedbackValidator")
    private V2FeedbackValidator feedbackValidator;

    protected V2FeedbackValidator getV2FeedbackValidator() {
        return feedbackValidator;
    }
    @Resource(name = "htmlMailSender")
    private V2HtmlMailSender v2HtmlMailSender;
    
    @Resource(name="enumerationService")
    private EnumerationService enumeration;
    
    @Resource(name = "simpleBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Resource(name = "siteConfigService")
    private SiteConfigService siteConfigService;
    
    public static final String FEEDBACK_PAGE_LABEL = "/feedback";
    public static final String BREADCRUMB_FEEDBACK_PAGE = "feedback";
    
    private static final String FEEDBACK_BODY = "FEEDBACK_Body";
    
    private static final String FEEDBACK_SUBJECT = "smtp.mail.feedback.subject.v2kart";
    private static final String FEEDBACK_TO_EMAIL = "customercare.v2kart";
    
    @RequestMapping(method = RequestMethod.GET)
    public String beADealer(final Model model, final HttpServletRequest request)
            throws CMSItemNotFoundException {     
         List<String> category = new ArrayList<String>();
      for(FeedbackCategoryEnum eachCategory: FeedbackCategoryEnum.values()){
          category.add(enumeration.getEnumerationName(eachCategory));
      }
      
      List<String> rating = new ArrayList<String>();
      for(FeedbackRatingEnum eachRating: FeedbackRatingEnum.values()){
          rating.add(enumeration.getEnumerationName(eachRating));
        
      }
      model.addAttribute("rating",rating);
      model.addAttribute("category", category);
      V2FeedbackForm v2FeedbackForm = new V2FeedbackForm();
        storeCmsPageInModel(model, getContentPageForLabelOrId(FEEDBACK_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(FEEDBACK_PAGE_LABEL));
        model.addAttribute("breadcrumbs", simpleBreadcrumbBuilder.getBreadcrumbs(BREADCRUMB_FEEDBACK_PAGE));
        model.addAttribute("v2FeedbackForm", v2FeedbackForm);
        return getViewForPage(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String beASellerSubmitAction(final V2FeedbackForm v2FeedbackForm,
            final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException {

        getV2FeedbackValidator().validate(v2FeedbackForm, bindingResult);

        final V2FeedbackData feedbackData = new V2FeedbackData();

        feedbackData.setMessage(v2FeedbackForm.getMessage());
        feedbackData.setMobileNumber(v2FeedbackForm.getMobileNumber());
        feedbackData.setEmail(v2FeedbackForm.getEmail());
        feedbackData.setRating(v2FeedbackForm.getRating());
        feedbackData.setCategory(v2FeedbackForm.getCategory());

        if (bindingResult.hasErrors()) {
            List<String> category = new ArrayList<String>();
            for(FeedbackCategoryEnum eachCategory: FeedbackCategoryEnum.values()){
                category.add(enumeration.getEnumerationName(eachCategory));
            }
            
            List<String> rating = new ArrayList<String>();
            for(FeedbackRatingEnum eachRating: FeedbackRatingEnum.values()){
                rating.add(enumeration.getEnumerationName(eachRating));
            }
            model.addAttribute("rating",rating);
            model.addAttribute("category", category);
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(FEEDBACK_PAGE_LABEL));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(FEEDBACK_PAGE_LABEL));
            model.addAttribute("breadcrumbs", simpleBreadcrumbBuilder.getBreadcrumbs(BREADCRUMB_FEEDBACK_PAGE));
//            model.addAttribute("v2FeedbackForm", v2FeedbackForm);
            return getViewForPage(model);
        }
        else {
            try {
                feedbackService.saveFeedbackData(feedbackData);
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        "text.feedback.confirmationSubmitted", null);
            } catch (final DuplicateUidException e) {
                bindingResult.rejectValue("email", "text.beADealer.emailid.exists");
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, "text.beADealer.emailid.exists",
                        null);
            }
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(FEEDBACK_PAGE_LABEL));
        setUpMetaDataForContentPage(model,
                getContentPageForLabelOrId(FEEDBACK_PAGE_LABEL));
        model.addAttribute("breadcrumbs",
                simpleBreadcrumbBuilder.getBreadcrumbs(BREADCRUMB_FEEDBACK_PAGE));
       
        final String feedbackSubject = siteConfigService.getProperty(FEEDBACK_SUBJECT);
        
        sendMail(FEEDBACK_BODY, feedbackData, feedbackSubject);
        
        return REDIRECT_PREFIX + "/feedback";
    }
    
    
    private void sendMail(final String templateCode, final V2FeedbackData feedbackData, final String mailSubject) {

        final VelocityContext feedbackEmailContext = new V2FeedbackEmailContext(feedbackData);
        final String to = siteConfigService.getProperty(FEEDBACK_TO_EMAIL);       
        try {
            v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(feedbackEmailContext, templateCode, mailSubject, to));
        } catch (final EmailException e) {
            LOG.info("Failed to send email Be a seller.", e);
        }

    }

}
