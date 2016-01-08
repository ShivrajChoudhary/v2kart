/**
 * 
 */
package in.com.v2kart.core.payment.services.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import in.com.v2kart.core.payment.data.response.V2PaymentResponse;
import in.com.v2kart.core.payment.services.V2PaymentResponseStrategy;

import java.util.Map;

/**
 * The Class V2PaymentResponseStrategyImpl.
 * 
 * @author Anuj
 * @param <T>
 *        the generic type
 */
public class V2PaymentResponseStrategyImpl<T extends V2PaymentResponse> implements V2PaymentResponseStrategy {

    private Converter<Map<String, String>, T> createPaymentResultConverter;

    @Override
    public T interpretResponse(final Map responseParameters) {
        return createPaymentResultConverter.convert(responseParameters);
    }

    /**
     * @return the createPaymentResultConverter
     */
    public Converter<Map<String, String>, T> getCreatePaymentResultConverter() {
        return createPaymentResultConverter;
    }

    /**
     * @param createPaymentResultConverter
     *        the createPaymentResultConverter to set
     */
    public void setCreatePaymentResultConverter(final Converter<Map<String, String>, T> createPaymentResultConverter) {
        this.createPaymentResultConverter = createPaymentResultConverter;
    }

}
