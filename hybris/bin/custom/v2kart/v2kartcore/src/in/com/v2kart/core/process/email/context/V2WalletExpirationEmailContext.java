package in.com.v2kart.core.process.email.context;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * @author Rampal Lather
 *
 */
public class V2WalletExpirationEmailContext extends VelocityContext {
    private Converter<UserModel, CustomerData> customerConverter;
    private CustomerData customerData;

    public V2WalletExpirationEmailContext() {
    }

    public V2WalletExpirationEmailContext(final CustomerModel customer) {
        this.customerData = getCustomerConverter().convert(customer);
    }

    public CustomerData getCustomerData() {
        return customerData;
    }

    public Converter<UserModel, CustomerData> getCustomerConverter() {
        return customerConverter;
    }

    @Required
    public void setCustomerConverter(Converter<UserModel, CustomerData> customerConverter) {
        this.customerConverter = customerConverter;
    }
}
