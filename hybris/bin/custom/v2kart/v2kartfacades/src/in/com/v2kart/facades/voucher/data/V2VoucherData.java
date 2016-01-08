package in.com.v2kart.facades.voucher.data;

import de.hybris.platform.commercefacades.voucher.data.VoucherData;

public class V2VoucherData extends VoucherData implements Comparable<V2VoucherData> {

    private static final long serialVersionUID = 1L;
    private Boolean enable;
    private int displayPriority;

    /**
     * @return the enable
     */
    public Boolean isEnable() {
        return enable;
    }

    public Boolean getEnable() {
        return enable;
    }

    /**
     * @param enable
     *        the enable to set
     */
    public void setEnable(final Boolean enable) {
        this.enable = enable;
    }

    /**
     * @return the displayPriority
     */
    public int getDisplayPriority() {
        return displayPriority;
    }

    /**
     * @param displayPriority
     *        the displayPriority to set
     */
    public void setDisplayPriority(final int displayPriority) {
        this.displayPriority = displayPriority;
    }

    @Override
    public int compareTo(final V2VoucherData vd) {
        return (this.displayPriority > vd.displayPriority) ? -1 : (this.displayPriority < vd.displayPriority) ? 1 : 0;
    }
}
