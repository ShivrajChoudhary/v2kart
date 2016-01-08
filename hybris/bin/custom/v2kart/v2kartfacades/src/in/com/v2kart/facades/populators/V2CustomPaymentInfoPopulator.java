/**
 * 
 */
package in.com.v2kart.facades.populators;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2PGPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author Anuj
 * 
 */
public class V2CustomPaymentInfoPopulator implements Populator<PaymentInfoModel, CCPaymentInfoData> {

    private Converter<AddressModel, AddressData> addressConverter;

    /**
     * @return the addressConverter
     */
    public Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    /**
     * @param addressConverter
     *        the addressConverter to set
     */
    @Required
    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final PaymentInfoModel source, final CCPaymentInfoData target) throws ConversionException {

        if (source.getBillingAddress() != null) {
            target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
        }
        
        if( source instanceof V2PGPaymentInfoModel){
            target.setCardNumber(( (V2PGPaymentInfoModel)source).getCardNumber());
        }
    }
      

    }
