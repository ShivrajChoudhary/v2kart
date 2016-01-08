package in.com.v2kart.dataimport.importengine;

import in.com.v2kart.dataimport.exceptions.FeedProcessingException.FailureCause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReaderSummaryInfo - Class for Reader summary reporting
 * 
 */
public class ReaderSummaryInfo {

    /**
     * Total number of records
     */
    private int totalRecords = 0;

    /**
     * Current cursor position
     */
    private int cursorPosition = 0;

    /**
     * File name
     */
    private final String fileName;

    /**
     * Map to store failure incidences
     */
    private final Map<FailureCause, List<Integer>> failureReport;

    public ReaderSummaryInfo(final String fileName) {
        this.totalRecords = 0;
        this.cursorPosition = 0;
        this.failureReport = new HashMap<>();
        this.fileName = fileName;
    }

    public void addFailure(final FailureCause cause) {
        List<Integer> failureRows = failureReport.get(cause);
        if (failureRows == null) {
            failureRows = new ArrayList<>();
        }
        failureRows.add(Integer.valueOf(this.getCursorPosition()));
        failureReport.put(cause, failureRows);
    }

    /**
     * @return the totalRecords
     */
    public int getTotalRecords() {
        return totalRecords;
    }

    /**
     * @return the cursorPosition
     */
    public int getCursorPosition() {
        return cursorPosition;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    public void incrementCursorPosition() {
        this.setCursorPosition(++cursorPosition);
    }

    /**
     * 
     */
    public void sanitizeSummaryReportForLastNullRecord() {
        this.setCursorPosition(--cursorPosition);
    }

    /**
     * @param cursorPosition
     *        the cursorPosition to set
     */
    private void setCursorPosition(final int cursorPosition) {
        this.cursorPosition = cursorPosition;
        this.setTotalRecords(cursorPosition);
    }

    /**
     * @param totalRecords
     *        the totalRecords to set
     */
    public void setTotalRecords(final int totalRecords) {
        this.totalRecords = totalRecords;
    }

    /**
     * @return the failureReport
     */
    public Map<FailureCause, List<Integer>> getFailureReport() {
        return failureReport;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Report for %2$S%n");
        builder.append("***********************************************");
        builder.append("%n");
        builder.append("Total records : %1$d");
        builder.append("%n");
        // builder.append("Current Cursor Position : %2$d");
        // builder.append("%n");
        builder.append(this.getPrintableFailureReport());
        builder.append("%n");
        builder.append("***********************************************");
        final String returnedString = String.format(builder.toString(), Integer.valueOf(totalRecords), this.fileName);
        return returnedString;
    }

    /**
     * @return String format
     */
    private String getPrintableFailureReport() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Total failure count : %1$d");
        int totalFailureCount = 0;
        for (final FailureCause cause : failureReport.keySet()) {
            totalFailureCount += failureReport.get(cause).size();
            builder.append("%n");
            builder.append(cause);
            builder.append(" : ");
            builder.append(failureReport.get(cause).toString());
        }
        return String.format(builder.toString(), Integer.valueOf(totalFailureCount));

    }

}
