/**
 * 
 */
package in.com.v2kart.dataimport;

import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.exceptions.FeedPersistanceException.FailureCause;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ImportDataSummaryInfo - Class to store import data summary report.
 */
public class ImportDataSummaryInfo {

    /**
     * Total number of records to be processed
     */
    private int totalRecords = 0;

    /**
     * Current cursor position
     */
    private int cursorPosition = 0;

    /**
     * Row index reference.
     */
    private int rowIndexRef = 0;

    private boolean deltaRecordingSupport = false;

    /**
     * Map to store failure incidences.
     */
    private final Map<FailureCause, Set<Integer>> failureReport;

    private final Set<String> updatedProductsUPCs;

    /**
     * Default constructor.
     */
    public ImportDataSummaryInfo() {
        this.totalRecords = 0;
        this.cursorPosition = 0;
        this.rowIndexRef = 0;
        this.failureReport = new HashMap<>();
        this.updatedProductsUPCs = new HashSet<>();
    }

    /**
     * Gets the total records.
     * 
     * @return the totalRecords
     */
    public final int getTotalRecords() {
        return totalRecords;
    }

    /**
     * Sets the total records.
     * 
     * @param totalRecords
     *        the totalRecords to set
     */
    public final void setTotalRecords(final int totalRecords) {
        this.totalRecords = totalRecords;
    }

    /**
     * Gets the cursor position.
     * 
     * @return the cursorPosition
     */
    public final int getCursorPosition() {
        return cursorPosition;
    }

    /**
     * Record dto processing.
     * 
     * @param baseDto
     *        the base dto
     */
    public final void recordDtoProcessing(final BaseDto baseDto) {
        this.setRowIndexRef(baseDto.getRowIndex());
        this.incrementCursorPosition();
    }

    /**
     * Function to increment cursor position.
     */
    protected final void incrementCursorPosition() {
        cursorPosition += 1;
    }

    /**
     * Function to add failure incidence.
     * 
     * @param cause
     *        the cause
     */
    public final void addFailureIncidence(final FailureCause cause) {
        Set<Integer> failureRows = failureReport.get(cause);
        if (failureRows == null) {
            failureRows = new HashSet<>();
        }
        failureRows.add(Integer.valueOf(this.rowIndexRef));
        failureReport.put(cause, failureRows);
    }

    /**
     * Function to add failure incidence.
     * 
     * @param cause
     *        the cause
     * @param referenceNumber
     *        the reference number
     */
    public final void addFailureIncidence(final FailureCause cause, final Integer referenceNumber) {
        Set<Integer> failureRows = failureReport.get(cause);
        if (failureRows == null) {
            failureRows = new HashSet<>();
        }
        failureRows.add(referenceNumber);
        failureReport.put(cause, failureRows);
    }

    /**
     * Function to add failure incidence.
     * 
     * @param cause
     *        the cause
     * @param dto
     *        the dto
     */
    public final void addFailureIncidence(final FailureCause cause, final BaseDto dto) {
        Set<Integer> failureRows = failureReport.get(cause);
        if (failureRows == null) {
            failureRows = new HashSet<>();
        }
        failureRows.add(Integer.valueOf(dto.getRowIndex()));
        failureReport.put(cause, failureRows);
    }

    /**
     * Function to add Updated Product UPC.
     * 
     * @param upc
     *        of updated product
     */
    public final void addUpdatedProduct(final String upc) {
        updatedProductsUPCs.add(upc);
    }

    /**
     * Gets the row index ref.
     * 
     * @return the rowIndexRef
     */
    public final int getRowIndexRef() {
        return rowIndexRef;
    }

    /**
     * Sets the row index ref.
     * 
     * @param rowIndexRef
     *        the rowIndexRef to set
     */
    public final void setRowIndexRef(final int rowIndexRef) {
        this.rowIndexRef = rowIndexRef;
    }

    /**
     * Checks if is delta recording support.
     * 
     * @return the deltaRecordingSupported
     */
    public final boolean isDeltaRecordingSupport() {
        return deltaRecordingSupport;
    }

    /**
     * Sets the delta recording support.
     * 
     * @param deltaRecordingSupport
     *        the deltaRecordingSupport to set
     */
    public final void setDeltaRecordingSupport(final boolean deltaRecordingSupport) {
        this.deltaRecordingSupport = deltaRecordingSupport;
    }

    /**
     * Function returning status if there is any update performed by the import engine.
     * 
     * @return status
     */
    public final boolean isUpdatePerformed() {
        return (!this.deltaRecordingSupport) || (this.deltaRecordingSupport && this.updatedProductsUPCs.size() > 0);
    }

    /**
     * Checks if is success.
     * 
     * @return true, if is success
     */
    public final boolean isSuccess() {
        return failureReport.size() == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Import data summary report %n");
        builder.append("***********************************************");
        builder.append("%n");
        builder.append("Total records : %1$d");
        builder.append("%n");
        if (this.deltaRecordingSupport) {
            builder.append("No of updated products : %2$d");
            builder.append("%n");
            builder.append("Updated Product's  : %3$s");
            builder.append("%n");
        }
        builder.append(this.getPrintableFailureReport());
        builder.append("%n");
        builder.append("***********************************************");
        final String returnedString = String.format(builder.toString(), Integer.valueOf(totalRecords),
                Integer.valueOf(updatedProductsUPCs.size()), updatedProductsUPCs.toString());
        return returnedString;
    }

    /**
     * Gets the printable failure report.
     * 
     * @return String format
     */
    private String getPrintableFailureReport() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Total failure count : %1$d");
        int totalFailureCount = 0;
        for (final FailureCause cause : failureReport.keySet()) {
            totalFailureCount += failureReport.get(cause).size();
            builder.append("%n");
            builder.append(String.format(cause.getMessage(), failureReport.get(cause)));
        }
        return String.format(builder.toString(), Integer.valueOf(totalFailureCount));

    }

}
