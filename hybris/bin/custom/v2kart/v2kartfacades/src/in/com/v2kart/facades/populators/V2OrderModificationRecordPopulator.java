package in.com.v2kart.facades.populators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import in.com.v2kart.facades.order.data.OrderModificationRecordData;
import in.com.v2kart.facades.order.data.OrderModificationRecordEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class V2OrderModificationRecordPopulator implements Populator<OrderModificationRecordModel, OrderModificationRecordData> {
	
	private Converter<OrderModificationRecordEntryModel, OrderModificationRecordEntryData> orderModificationRecordEntryConverter;

	public Converter<OrderModificationRecordEntryModel, OrderModificationRecordEntryData> getOrderModificationRecordEntryConverter() {
		return orderModificationRecordEntryConverter;
	}

	public void setOrderModificationRecordEntryConverter(
			Converter<OrderModificationRecordEntryModel, OrderModificationRecordEntryData> orderModificationRecordEntryConverter) {
		this.orderModificationRecordEntryConverter = orderModificationRecordEntryConverter;
	}

	@Override
	public void populate(OrderModificationRecordModel source,
			OrderModificationRecordData target) throws ConversionException {
		target.setIdentifier(source.getIdentifier());
		target.setInProgress(source.isInProgress());

		if (CollectionUtils.isNotEmpty(source.getModificationRecordEntries()))
		{
			final List<OrderModificationRecordEntryModel> recordEntries = new ArrayList<>();
			for (final OrderModificationRecordEntryModel modRecord : source.getModificationRecordEntries())
			{
				if (modRecord instanceof OrderCancelRecordEntryModel)
				{
					recordEntries.add(modRecord);
				}
			}
			target.setModificationRecordEntries(Converters.convertAll(recordEntries, getOrderModificationRecordEntryConverter()));
		}
		
	}

}
