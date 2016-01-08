package in.com.v2kart.ecomlspintegration.dao.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import in.com.v2kart.core.email.impl.V2DeliveryConfirmationNotificationSender;
import in.com.v2kart.ecomlspintegration.dao.EcomTrackerDao;
import in.com.v2kart.ecomlspintegration.data.response.EcomexpressObjectsType;
import in.com.v2kart.ecomlspintegration.data.response.FieldType;
import in.com.v2kart.ecomlspintegration.data.response.ObjectType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 *
 */

/**
 * @author arunkumar
 *
 */
public class EcomTrackerDaoImpl implements EcomTrackerDao {
	private final Logger LOG = Logger.getLogger(EcomTrackerDaoImpl.class);

	private FlexibleSearchService flexibleSearchService;

	private ModelService modelService;

	private V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender;
	private final String AWB_NO = "awb_number";
	private final String STATUS = "status";
	private final String CUR_LOC = "current_location_name";
	private final String STATUS_CODE = "reason_code_number";
	private final String Delivery_Date = "delivery_date";
	private final String DELIVERED = "999";
	private final SimpleDateFormat formatter = new SimpleDateFormat(
			"dd/MM/yyyy");

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.ecomlspintegration.dao.EcomTrackerDao#
	 * updateOrderConsignmentStatus(in.com.v2kart.ecomlspintegration
	 * .data.response. EcomOrderTrackerResponse)
	 */
	@Override
	public void updateOrderConsignmentStatus(
			final EcomexpressObjectsType ecomOrderTrackerResponse) {

		if (null != ecomOrderTrackerResponse) {
			for (final ObjectType objectType : ecomOrderTrackerResponse
					.getObject()) {
				String awb = null;
				String status = null;
				String statusLocation = null;
				String reasonCodeNumber = null;
				String deliveryDate = null;

				for (final FieldType fieldType : objectType.getField()) {
					String fieldName = null;

					if (null != fieldType) {
						fieldName = fieldType.getName();
						if (fieldName.equals(AWB_NO)) {
							awb = fieldType.getValue();
						}
						fieldName = fieldType.getName();
						if (fieldName.equals(STATUS)) {
							status = fieldType.getValue().trim();
						}
						fieldName = fieldType.getName();
						if (fieldName.equals(CUR_LOC)) {
							statusLocation = fieldType.getValue();
						}
						fieldName = fieldType.getName();
						if (fieldName.equals(STATUS_CODE)) {
							reasonCodeNumber = fieldType.getValue();
						}

						fieldName = fieldType.getName();
						if (fieldName.equals(Delivery_Date)) {
							deliveryDate = fieldType.getValue();
						}

					}

				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Ecom LSP Response of awb[" + awb + "],status["
							+ status + "],statusLocation[" + statusLocation
							+ "],reasonCodeNumber[" + reasonCodeNumber
							+ "],deliveryDate[" + deliveryDate + "]");
				}

				if ((null != awb) || (null != status)) {

					final String query = "select {c.pk} from {Consignment as c join V2LogisticServiceProvider as lspsp on {c.lsp}={lspsp.pk}} where {trackingID}=?trackingID and {lspsp.code}='ECOM EXPRESS'";
					final Map<String, String> map = new HashMap<>();
					map.put("trackingID", awb);
					final FlexibleSearchQuery flexiblesearchquery = new FlexibleSearchQuery(
							query, map);
					final ConsignmentModel consignment = flexibleSearchService
							.searchUnique(flexiblesearchquery);

					consignment.setStatus(ConsignmentStatus.valueOf(status));
					consignment.setCurrentLocation(statusLocation);
					// consignment.setCompletionDate(convertStringToDate(deliveryDate));
					modelService.save(consignment);

					// trigger delivery confirmation email and sms if reason
					// Code Number is 999
					if (reasonCodeNumber.equals(DELIVERED)) {
						consignment.setCompletionDate(new Date());
						modelService.save(consignment);

						v2DeliveryConfirmationNotificationSender
								.triggerDeliveryConfirmationNotification(consignment);
					}

				}
			}
		}
	}

	/**
	 * @param deliveryDate
	 * @return delivery date of consignment
	 */
	Date convertStringToDate(final String deliveryDate) {
		try {
			final Date date = formatter.parse(deliveryDate);
			return date;
		} catch (final ParseException | NullPointerException e) {
			final Date currentDate = new Date();
			return currentDate;
		}
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(
			final FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService) {
		this.modelService = modelService;
	}

	public V2DeliveryConfirmationNotificationSender getV2DeliveryConfirmationNotificationSender() {
		return v2DeliveryConfirmationNotificationSender;
	}

	@Required
	public void setV2DeliveryConfirmationNotificationSender(
			final V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender) {
		this.v2DeliveryConfirmationNotificationSender = v2DeliveryConfirmationNotificationSender;
	}

}
