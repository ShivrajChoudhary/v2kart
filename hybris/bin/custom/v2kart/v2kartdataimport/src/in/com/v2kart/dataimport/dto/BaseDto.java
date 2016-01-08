/**
 * 
 */
package in.com.v2kart.dataimport.dto;

import in.com.v2kart.dataimport.constants.V2kartdataimportConstants;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.dozer.Mapping;

/**
 * BaseDto for all dtos which will be populated out of CSV import process
 * 
 */
public class BaseDto {

    private int rowIndex;

    /**
     * unTokenizedRowString to maintain csv row as is
     */
    private String unTokenizedRowString;

    /**
     * @return the unTokenizedRowString
     */
    public String getUnTokenizedRowString() {
        return unTokenizedRowString;
    }

    /**
     * @param unTokenizedRowString
     *        the unTokenizedRowString to set
     */
    public void setUnTokenizedRowString(final String unTokenizedRowString) {
        this.unTokenizedRowString = unTokenizedRowString;
    }

    /**
     * @return the rowIndex
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     *        the rowIndex to set
     */
    public void setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    @Mapping(V2kartdataimportConstants.MAPPING_FIELD_HASH)
    public int hashCode() {
        return new HashCodeBuilder().append(this.unTokenizedRowString).toHashCode();
    }

    public Integer getHash() {
        return new Integer(this.hashCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "hash#" + this.hashCode();
    }

    /**
     * Returns the verbose details
     * 
     * @return String
     */
    public String getVerboseToString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Type : [%3$s], ");
        // builder.append("String output : [%1$s], ");
        builder.append("Row index : [%1$d], ");
        builder.append("Untokenized row : [%2$s] ");
        return String
                .format(builder.toString(), Integer.valueOf(this.rowIndex), this.unTokenizedRowString, this.getClass().getSimpleName());
    }

    /**
     * @param o
     * @return
     */
    public boolean equal(final Object o) {
        return this.getHash().intValue() == o.hashCode();
    }
}
