package in.com.v2kart.storefront.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;

public class V2AddressForm extends AddressForm{
    private String phoneNo;

    
    @NotNull(message = "{address.phone.invalid}")
    @Size(min = 1, max = 10, message = "{address.phone.invalid}")
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * @param phoneNo the phoneNo to set
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

}
