package in.com.v2kart.facades.notification; 

import in.com.v2kart.facades.notification.data.V2NotificationData;

import java.util.List;

public interface V2NotificationFacade {

	public List<V2NotificationData>getAllNotifications();
	
	public List<V2NotificationData>getActiveNotifications();
    
}
