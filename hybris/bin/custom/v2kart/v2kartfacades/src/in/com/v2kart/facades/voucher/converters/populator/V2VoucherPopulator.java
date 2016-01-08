package in.com.v2kart.facades.voucher.converters.populator;

import in.com.v2kart.facades.voucher.data.V2VoucherData;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.voucher.model.PromotionVoucherModel;

/**
 * 
 * @author vikrant2480
 * 
 */
public class V2VoucherPopulator implements Populator<PromotionVoucherModel, V2VoucherData> {
    private AbstractConverter<CurrencyModel, CurrencyData> currencyConverter;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final PromotionVoucherModel source, final V2VoucherData target) throws ConversionException
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setValue(source.getValue());
        target.setValueFormatted(source.getValueString());
        target.setDescription(source.getDescription());
        target.setValueString(source.getValueString());
        target.setFreeShipping(Boolean.TRUE.equals(source.getFreeShipping()));
        if (source.getCurrency() != null)
        {
            target.setCurrency(getCurrencyConverter().convert(source.getCurrency()));
        }

        target.setVoucherCode(source.getVoucherCode());

        target.setEnable(source.getEnable());

        if (source.getDisplayPriority() != null)
        {
            target.setDisplayPriority(source.getDisplayPriority().intValue());
        }

    }

    public AbstractConverter<CurrencyModel, CurrencyData> getCurrencyConverter()
    {
        return currencyConverter;
    }

    @Required
    public void setCurrencyConverter(final AbstractConverter<CurrencyModel, CurrencyData> currencyConverter)
    {
        this.currencyConverter = currencyConverter;
    }
}
