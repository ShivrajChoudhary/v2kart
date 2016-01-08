/**
 *
 */
package in.com.v2kart.commercewebservices.v1.controller;

import in.com.v2kart.commercewebservices.notification.V2NotificationDataList;
import in.com.v2kart.facades.notification.V2NotificationFacade;
import in.com.v2kart.facades.notification.data.V2NotificationData;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main Controller for notifications
 *
 */
@Controller("NotificationControllerV1")
@RequestMapping(value = "/{baseSiteId}/notifications")
public class NotificationController extends BaseController
{
	@Resource(name = "v2NotificationFacade")
	private V2NotificationFacade v2NotificationFacade;

	/**
	 * @return the v2NotificationFacade
	 */
	public V2NotificationFacade getV2NotificationFacade()
	{
		return v2NotificationFacade;
	}

	/**
	 * @param v2NotificationFacade
	 *           the v2NotificationFacade to set
	 */
	public void setV2NotificationFacade(final V2NotificationFacade v2NotificationFacade)
	{
		this.v2NotificationFacade = v2NotificationFacade;
	}

	@ResponseBody
	@RequestMapping(value = "/activeNotifications", method = RequestMethod.GET)
	public V2NotificationDataList getAvailableNotifications()
	{
		final V2NotificationDataList dataList = new V2NotificationDataList();
		final List<V2NotificationData> activeNotifications = getV2NotificationFacade().getActiveNotifications();
		dataList.setNotifications(activeNotifications);
		return dataList;


	}


}
