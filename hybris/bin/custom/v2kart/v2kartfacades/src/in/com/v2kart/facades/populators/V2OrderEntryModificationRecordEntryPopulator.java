package in.com.v2kart.facades.populators;

import in.com.v2kart.facades.order.data.OrderEntryModificationRecordEntryData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class V2OrderEntryModificationRecordEntryPopulator implements
Populator<OrderEntryModificationRecordEntryModel, OrderEntryModificationRecordEntryData> {
	
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter() {
		return orderEntryConverter;
	}

	public void setOrderEntryConverter(
			Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter) {
		this.orderEntryConverter = orderEntryConverter;
	}

	@Override
	public void populate(OrderEntryModificationRecordEntryModel source,
			OrderEntryModificationRecordEntryData target)
			throws ConversionException {
		target.setProductInfo(source.getOrderEntry().getProduct().getCode()+"-"+source.getOrderEntry().getProduct().getName());
		target.setNotes(source.getNotes());

		if (source.getOrderEntry() != null)
		{
			target.setOrderEntryData(orderEntryConverter.convert(source.getOrderEntry()));
		}

		if (source instanceof OrderEntryCancelRecordEntryModel)
		{
			//Long qty=new Long(((OrderEntryCancelRecordEntryModel) source).getCancelledQuantity());
			if(((OrderEntryCancelRecordEntryModel) source).getCancelledQuantity()!=null){
			target.setQuantity(((OrderEntryCancelRecordEntryModel) source).getCancelledQuantity().longValue());
			}
			target.setReason(((OrderEntryCancelRecordEntryModel) source).getCancelReason().getCode());
		}
		
	}

}
