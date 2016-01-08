/**
 * 
 */
package in.com.v2kart.core.services.impl;

import in.com.v2kart.core.dao.V2NotificationDao;
import in.com.v2kart.core.services.V2NotificationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import  in.com.v2kart.core.model.V2NotificationModel;

/**
 * @author mandeepjolly
 * 
 */
public class V2NotificationServiceImpl implements V2NotificationService {

	@Autowired
	private V2NotificationDao v2NotificationDao;
	
	@Override
	public List<V2NotificationModel> findAllNotifications() {
		return v2NotificationDao.findAllNotifications();
	}
	
	@Override
	public List<V2NotificationModel> findActiveNotfications() {
		return v2NotificationDao.findActiveNotfications();
	}
}
