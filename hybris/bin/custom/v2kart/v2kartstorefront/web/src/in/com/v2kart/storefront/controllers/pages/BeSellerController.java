package in.com.v2kart.storefront.controllers.pages;

import in.com.v2kart.core.email.V2HtmlMailSender;
import in.com.v2kart.core.process.email.context.V2BeSellerEmailContext;
import in.com.v2kart.facades.core.data.V2SellerData;
import in.com.v2kart.facades.seller.SellerFacade;
import in.com.v2kart.storefront.forms.AddSellerForm;
import in.com.v2kart.storefront.forms.validation.V2SellerValidator;

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

@Controller
@Scope("tenant")
@RequestMapping("/beADealer")
public class BeSellerController extends AbstractSearchPageController {


    @Resource(name = "sellerFacade")
    private SellerFacade sellerFacade;

    @Resource(name = "sellerValidator")
    private V2SellerValidator sellerValidator;

    protected V2SellerValidator getV2SellerValidator() {
        return sellerValidator;
    }
    @Resource(name = "htmlMailSender")
    private V2HtmlMailSender v2HtmlMailSender;
    
    @Resource(name = "simpleBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Resource(name = "siteConfigService")
    private SiteConfigService siteConfigService;
    
    public static final String BE_SELLER_PAGE = "beADealer";
    
    private static final String BE_A_SELLER_BODY = "BE_A_SELLER_Body";
    
    private static final String BE_A_SELLER_SUBJECT = "smtp.mail.beaseller.subject";
    private static final String BE_A_SELLER_TO_EMAIL = "be.a.seller.to.email";
    
    @RequestMapping(method = RequestMethod.GET)
    public String beADealer(final Model model, final HttpServletRequest request)
            throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId("beADealer"));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId("beADealer"));
        model.addAttribute("breadcrumbs", simpleBreadcrumbBuilder.getBreadcrumbs("text.beADealer"));
        model.addAttribute("addSellerForm", new AddSellerForm());
        return getViewForPage(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String beASellerSubmitAction(final AddSellerForm addSellerForm,
            final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException {

        getV2SellerValidator().validate(addSellerForm, bindingResult);

        final V2SellerData sellerData = new V2SellerData();

        sellerData.setName(addSellerForm.getName());
        sellerData.setPhone(addSellerForm.getPhone());
        sellerData.setEmail(addSellerForm.getEmail());
        sellerData.setMessage(addSellerForm.getMessage());
        sellerData.setCategory(addSellerForm.getCategory());

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(BE_SELLER_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BE_SELLER_PAGE));
            model.addAttribute("breadcrumbs", simpleBreadcrumbBuilder.getBreadcrumbs("text.beADealer"));
            // model.addAttribute("addSellerForm", new AddSellerForm());
            return getViewForPage(model);
        }
        else {
            try {
                sellerFacade.saveSellerData(sellerData);
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        "text.beADealer.confirmationSubmitted", null);
            } catch (final DuplicateUidException e) {
                bindingResult.rejectValue("email", "text.beADealer.emailid.exists");
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, "text.beADealer.emailid.exists",
                        null);
            }
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(BE_SELLER_PAGE));
        setUpMetaDataForContentPage(model,
                getContentPageForLabelOrId(BE_SELLER_PAGE));
        model.addAttribute("breadcrumbs",
                simpleBreadcrumbBuilder.getBreadcrumbs("text.beADealer"));
       
        final String beASellerSubject = siteConfigService.getProperty(BE_A_SELLER_SUBJECT);
        
        sendMail(BE_A_SELLER_BODY, sellerData, beASellerSubject);
        
        return REDIRECT_PREFIX + "/beADealer";
    }
    
    
    private void sendMail(final String templateCode, final V2SellerData sellerData, final String mailSubject) {

        final VelocityContext beSellerEmailContext = new V2BeSellerEmailContext(sellerData);
        final String to = siteConfigService.getProperty(BE_A_SELLER_TO_EMAIL);
        
        try {
            v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(beSellerEmailContext, templateCode, mailSubject, to));
        } catch (final EmailException e) {
            LOG.info("Failed to send email Be a seller.", e);
        }

    }
}
