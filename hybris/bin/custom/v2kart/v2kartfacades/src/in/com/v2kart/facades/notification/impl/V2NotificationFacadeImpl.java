package in.com.v2kart.facades.notification.impl;

import in.com.v2kart.core.services.V2NotificationService;
import in.com.v2kart.facades.notification.V2NotificationFacade;
import in.com.v2kart.facades.notification.data.V2NotificationData;
import in.com.v2kart.facades.populators.V2NotificationPopulator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import  in.com.v2kart.core.model.V2NotificationModel;

public class V2NotificationFacadeImpl implements V2NotificationFacade {

	@Autowired
	private V2NotificationService v2NotificationService;
	
	@Autowired
	private V2NotificationPopulator v2NotificationPopulator;
	
	@Override
	public List<V2NotificationData> getAllNotifications() {
		List<V2NotificationModel>notificationModels = v2NotificationService.findAllNotifications();
		List<V2NotificationData>notifications = new ArrayList<V2NotificationData>();
		for(V2NotificationModel notificationModel:notificationModels){
			V2NotificationData notification = new V2NotificationData();
			v2NotificationPopulator.populate(notificationModel, notification);
			notifications.add(notification);
		}
		return notifications;
	}
	
	@Override
	public List<V2NotificationData> getActiveNotifications() {
		List<V2NotificationModel>notificationModels = v2NotificationService.findActiveNotfications();
		List<V2NotificationData>notifications = new ArrayList<V2NotificationData>();
		for(V2NotificationModel notificationModel:notificationModels){
			V2NotificationData notification = new V2NotificationData();
			v2NotificationPopulator.populate(notificationModel, notification);
			notifications.add(notification);
		}
		return notifications;
	}
}
