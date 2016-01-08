package in.com.v2kart.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.OrderHistoryPopulator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;

public class V2OrderHistoryPopulator extends OrderHistoryPopulator{
    private Converter<OrderModel, OrderData> orderConverter;

    /**
     * @return the orderConverter
     */
    public Converter<OrderModel, OrderData> getOrderConverter() {
        return orderConverter;
    }

    /**
     * @param orderConverter
     *        the orderConverter to set
     */
    public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter) {
        this.orderConverter = orderConverter;
    }

    @Override
    public void populate(final OrderModel source, final OrderHistoryData target)
    {
        super.populate(source, target);
        target.setOrderData(getOrderConverter().convert(source));
    }

}
