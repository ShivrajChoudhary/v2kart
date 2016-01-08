package in.com.v2kart.storefront.forms;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ShowCartForm {
    @NotNull(message = "{basket.error.quantity.notNull}")
    @Min(value = 0, message = "{basket.error.quantity.invalid}")
    @Digits(fraction = 0, integer = 10, message = "{basket.error.quantity.invalid}")
    private long qty = 1L;

    @NotNull(message = "Required")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setQty(final long quantity) {
        this.qty = quantity;
    }

    public long getQty() {
        return qty;
    }
}
