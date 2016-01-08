/**
 * 
 */
package in.com.v2kart.commercewebservices.v1.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;

import in.com.v2kart.commercewebservices.component.product.data.StaticPageResponse;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 
 */
@Controller("StaticPageControllerV1")
public class StaticPageController
{

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@RequestMapping(value = "/{baseSiteId}/aboutUs", method = RequestMethod.GET)
	@ResponseBody
	public StaticPageResponse getAboutUsContent() throws CMSItemNotFoundException
	{
		final StaticPageResponse staticPageResponse = new StaticPageResponse();

		final CMSParagraphComponentModel cmsParagraphComponentModel = cmsComponentService.getSimpleCMSComponent("aboutUsParagraph");
		staticPageResponse.setContent(cmsParagraphComponentModel.getContent());
		return staticPageResponse;
	}

	@RequestMapping(value = "/{baseSiteId}/termsAndConditions", method = RequestMethod.GET)
	@ResponseBody
	public StaticPageResponse getTermsAndConditionContent() throws CMSItemNotFoundException
	{
		final StaticPageResponse staticPageResponse = new StaticPageResponse();

		final CMSParagraphComponentModel cmsParagraphComponentModel = cmsComponentService
				.getSimpleCMSComponent("termsAndConditionParagraph");
		staticPageResponse.setContent(cmsParagraphComponentModel.getContent());
		return staticPageResponse;
	}
}
