/**
 * 
 */
package in.com.v2kart.facades.populators;

import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.commercefacades.user.converters.populator.CustomerPopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.mobileservices.model.text.UserPhoneNumberModel;

/**
 * @author arunkumar
 * 
 */
public class V2CustomersPopulator extends CustomerPopulator {

    @Override
    public void populate(final CustomerModel source, final CustomerData target) {
        super.populate(source, target);
        List<String> phoneNumbers = new ArrayList<>();
        target.setMobileNumber(source.getMobileNumber());

        for (UserPhoneNumberModel phoneNumberModel : source.getPhoneNumbers()) {
            phoneNumbers.add(phoneNumberModel.getPhoneNumber().getNumber());
        }
        target.setPhoneNumbers(phoneNumbers);
        target.setDateOfBirth(source.getDateOfBirth());
        target.setMaritalStatus(source.getMaritalStatus());

        if (null != source.getGender()) {
            target.setGender(source.getGender().toString().toLowerCase());
        }
        target.setWalletCreditBalance(source.getWalletCreditBalance());
    }
}
