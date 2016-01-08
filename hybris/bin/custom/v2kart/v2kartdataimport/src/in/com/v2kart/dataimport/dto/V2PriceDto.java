package in.com.v2kart.dataimport.dto;

import in.com.v2kart.core.enums.V2PriceTypeEnum;

import java.util.Calendar;
import java.util.Date;

/**
 * @author arunkumar
 * 
 */
public class V2PriceDto extends BaseDto {

    private static final int END_OF_TIME = 9999;

    private V2PriceTypeEnum priceType;

    private String code;

    private double price;

    private Date validFrom;

    private Date validTo;

    /**
     * @return priceType
     */
    public V2PriceTypeEnum getPriceType() {
        return priceType;
    }

    /**
     * @param priceType
     */
    public void setPriceType(V2PriceTypeEnum priceType) {
        this.priceType = priceType;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *        the code to set
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price
     *        the price to set
     */
    public void setPrice(final double price) {
        this.price = price;
    }

    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom
     *        the validFrom to set
     */
    public void setValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * SAP includes the end date while calculation and hybris doesn't so for end date 15.01.2014 in SAP, hybris needs the end date to be
     * 16.01.2014.
     * 
     * @param validTo
     *        the validTo to set
     */
    public void setValidTo(final Date validTo) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(validTo);
        if (calendar.get(Calendar.YEAR) != END_OF_TIME) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            this.validTo = calendar.getTime();
        } else {
            this.validTo = validTo;
        }
    }

}
