package in.com.v2kart.facades.populators;

import org.apache.commons.collections.CollectionUtils;

import in.com.v2kart.facades.order.data.OrderEntryModificationRecordEntryData;
import in.com.v2kart.facades.order.data.OrderModificationRecordEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class V2OrderModificationRecordEntryPopulator implements
Populator<OrderModificationRecordEntryModel, OrderModificationRecordEntryData> {
	
	private Converter<OrderEntryModificationRecordEntryModel, OrderEntryModificationRecordEntryData> orderEntryModificationRecordEntryConverter;

	public Converter<OrderEntryModificationRecordEntryModel, OrderEntryModificationRecordEntryData> getOrderEntryModificationRecordEntryConverter() {
		return orderEntryModificationRecordEntryConverter;
	}

	public void setOrderEntryModificationRecordEntryConverter(
			Converter<OrderEntryModificationRecordEntryModel, OrderEntryModificationRecordEntryData> orderEntryModificationRecordEntryConverter) {
		this.orderEntryModificationRecordEntryConverter = orderEntryModificationRecordEntryConverter;
	}

	@Override
	public void populate(OrderModificationRecordEntryModel source,
			OrderModificationRecordEntryData target) throws ConversionException {
		target.setCode(source.getCode());
		target.setFailedMessage(source.getFailedMessage());
		target.setNotes(source.getNotes());
		target.setStatus(source.getStatus().getCode());
		if (source instanceof OrderCancelRecordEntryModel)
		{
		    if(null != ((OrderCancelRecordEntryModel) source).getCancelReason()){
		        target.setCancelReason(((OrderCancelRecordEntryModel) source).getCancelReason().getCode());
            }else{
                target.setCancelReason("NA");
            }
			target.setCancelResult(((OrderCancelRecordEntryModel) source).getCancelResult().getCode());
		}
		if (CollectionUtils.isNotEmpty(source.getOrderEntriesModificationEntries()))
		{
			target.setOrderEntryModificationRecordEntries(Converters.convertAll(source.getOrderEntriesModificationEntries(),
					getOrderEntryModificationRecordEntryConverter()));
		}
		
	}

}
