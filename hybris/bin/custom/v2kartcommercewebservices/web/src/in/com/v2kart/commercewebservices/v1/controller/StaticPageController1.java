/**
 *
 */
/*
package in.com.v2kart.commercewebservices.v1.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.jalo.contents.components.SimpleCMSComponent;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


 *//**
 * @author himanshumehta
 * 
 */
/*
 * @Controller("StaticPageControllerV1") public class StaticPageController1 {
 * 
 * @Resource private ContactUsFacade fgContactUsFacade;
 * 
 * @Resource(name = "cmsComponentService") private CMSComponentService cmsComponentService;
 * 
 * 
 * 
 * @RequestMapping(value = "/{baseSiteId}/contactUsReasons", method = RequestMethod.POST)
 * 
 * @ResponseBody public ContactUsReasonsData getContactUsReasons() { final ContactUsReasonsData contactUsReasonsData =
 * new ContactUsReasonsData(); contactUsReasonsData.setContactUsReasons(fgContactUsFacade.getContactUsReasonsList());
 * 
 * return contactUsReasonsData; }
 * 
 * 
 * @RequestMapping(value = "/{baseSiteId}/aboutUs", method = RequestMethod.GET)
 * 
 * @ResponseBody public String getAboutUsContent() throws CMSItemNotFoundException { final CMSLinkComponentModel
 * cmsLinkComponent; final SimpleCMSComponent simple = cmsComponentService.getSimpleCMSComponent("aboutUsParagraph");
 * final String content = cmsParagraphComponentModel.getContent(); return content; }
 * 
 * 
 * @RequestMapping(value = "/{baseSiteId}/support", method = RequestMethod.GET)
 * 
 * @ResponseBody public String getSupportContent() throws CMSItemNotFoundException { final CMSParagraphComponentModel
 * cmsParagraphComponentModel = (CMSParagraphComponentModel) cmsComponentService
 * .getSimpleCMSComponent("MobileSupportTextParagraph"); final String content = cmsParagraphComponentModel.getContent();
 * return content; }
 * 
 * @RequestMapping(value = "/{baseSiteId}/paymentOption", method = RequestMethod.GET)
 * 
 * @ResponseBody public String getPaymentOptionsText() throws CMSItemNotFoundException { final
 * CMSParagraphComponentModel cmsParagraphComponentModel = (CMSParagraphComponentModel) cmsComponentService
 * .getSimpleCMSComponent("MobilePaymentOptionsTextParagraph"); final String content =
 * cmsParagraphComponentModel.getContent(); return content; }
 * 
 * @RequestMapping(value = "/{baseSiteId}/shoppingPolicies", method = RequestMethod.GET)
 * 
 * @ResponseBody public String getShoppingPolicies() throws CMSItemNotFoundException { final CMSParagraphComponentModel
 * cmsParagraphComponentModel = (CMSParagraphComponentModel) cmsComponentService
 * .getSimpleCMSComponent("MobilePolicyTextParagraph"); final String content = cmsParagraphComponentModel.getContent();
 * return content; }
 * 
 * @RequestMapping(value = "/{baseSiteId}/faq", method = RequestMethod.GET)
 * 
 * @ResponseBody public FAQListData gefaq() throws CMSItemNotFoundException { final CMSParagraphComponentModel
 * cmsParagraphComponentModel = (CMSParagraphComponentModel) cmsComponentService
 * .getSimpleCMSComponent("MobilefaqTextParagraph"); final String content = cmsParagraphComponentModel.getContent();
 * final String[] subStrings = content.split("<h3>"); final FAQListData faqListData = new FAQListData(); final List list
 * = new ArrayList<FAQData>(); for (int i = 1; i < subStrings.length; i++) { list.add(printStrings(subStrings[i])); }
 * faqListData.setContentList(list);
 * 
 * return faqListData; }
 * 
 * public FAQData printStrings(final String arg) { final FAQData faqData = new FAQData();
 * 
 * int headerLastIndex, contentFirtIndex, contentSecondIndex; headerLastIndex = arg.indexOf("</h3>"); contentFirtIndex =
 * arg.indexOf("<p>"); contentSecondIndex = arg.lastIndexOf("</p>");
 * 
 * final String header = arg.substring(0, headerLastIndex); String content = arg.substring(contentFirtIndex + 3,
 * contentSecondIndex); if (content.contains("<p>")) {
 * 
 * content = content.replaceAll("<p>", ""); content = content.replaceAll("</p>", ""); } faqData.setHeader(header);
 * faqData.setContent(content); return faqData; }
 * 
 * }
 */