/**
 * 
 */
package in.com.v2kart.core.services.impl;

import in.com.v2kart.core.dao.V2NotifyCustomerDao;
import in.com.v2kart.core.enums.V2NotifyCustomerTypeEnum;
import in.com.v2kart.core.model.NotifyCustomerModel;
import in.com.v2kart.core.services.V2NotifyCustomerService;
import in.com.v2kart.facades.core.data.V2CustomerNotificationData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

/**
 * @author shubhammaheshwari
 *
 */
public class V2NotifyCustomerServiceImpl implements V2NotifyCustomerService {
    
    /**
     * modelService  bean Injection
     */
    private ModelService modelService;
    /**
     * productService bean injection
     */
    private ProductService productService;
    /**
     * customerNotificationDao bean injection
     */
    private V2NotifyCustomerDao customerNotificationDao;
    /**
     * baseSiteService bean injection
     */
    private BaseSiteService baseSiteService;

    /* (non-Javadoc)
     * @see in.com.v2kart.core.services.V2CustomerNotificationService#notifyCustomer(in.com.v2kart.facades.core.data.V2CustomerNotificationData)
     */
    @Override
    public void notifyCustomer(V2CustomerNotificationData customerNotificationData) {
        final NotifyCustomerModel notifyCustomerModel = modelService.create(NotifyCustomerModel.class);
        populateNotifyCustomerModel(customerNotificationData, notifyCustomerModel);
        customerNotificationDao.saveNotification(notifyCustomerModel);

    }

    /**
     * @param customerNotificationData
     * @param notifyCustomerModel
     * 
     * populate notify customer model
     */
    private void populateNotifyCustomerModel(V2CustomerNotificationData customerNotificationData,
            final NotifyCustomerModel notifyCustomerModel) {
        final ProductModel product = productService.getProductForCode(customerNotificationData.getProductCode());
        if (customerNotificationData.getSite() != null) {
            final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(customerNotificationData.getSite());
            notifyCustomerModel.setBaseSite(baseSite);
        }
        notifyCustomerModel.setProduct(product);

        if (customerNotificationData.getType().equals(V2NotifyCustomerTypeEnum.NOTIFY_ME.getCode())) {
            notifyCustomerModel.setType(V2NotifyCustomerTypeEnum.NOTIFY_ME);
        } else {
            notifyCustomerModel.setType(V2NotifyCustomerTypeEnum.NOTIFY_MY_PRICE);
        }
        notifyCustomerModel.setPrice(Double.valueOf(customerNotificationData.getNotificationPrice()));
        notifyCustomerModel.setEmail(customerNotificationData.getEmailId());
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setCustomerNotificationDao(V2NotifyCustomerDao customerNotificationDao) {
        this.customerNotificationDao = customerNotificationDao;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

}
