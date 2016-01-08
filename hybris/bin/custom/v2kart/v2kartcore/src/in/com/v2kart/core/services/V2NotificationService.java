package in.com.v2kart.core.services;

import java.util.List;

import  in.com.v2kart.core.model.V2NotificationModel;

public interface V2NotificationService {
    
	List<V2NotificationModel> findAllNotifications();

	List<V2NotificationModel> findActiveNotfications();
       

}
