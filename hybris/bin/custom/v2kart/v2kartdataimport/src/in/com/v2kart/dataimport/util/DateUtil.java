package in.com.v2kart.dataimport.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * This is the common date utility class containing common methods for different operations on dates.
 */
public class DateUtil {

    /** Date Format. */
    public static final String DATE_FORMAT = "ddMMyyyy";

    /**
     * Time format ddMMMyy HH:mm
     */
    private static final String TIME_FORMAT_DDMMMYY_HH_MM = "ddMMMyy HH:mm";

    /**
     * Date formatter to convert Date object to date string for view
     */
    private static final SimpleDateFormat DATE_FORMAT_TO_VIEW = new SimpleDateFormat(DATE_FORMAT);

    /**
     * @param date
     *        - the {@link Date} object
     * 
     * @return - the date in string format
     */
    public static String getDateStringForView(final Date date) {
        return DATE_FORMAT_TO_VIEW.format(date).toUpperCase();
    }

    /**
     * @param calendar
     *        {@link Calendar}
     * @param formatPattern
     *        pattern in which date is to be transformed
     * @return the date in string format
     */
    public static String getFormatedDate(final Calendar calendar, final String formatPattern) {
        final Date date = calendar.getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatPattern);
        return simpleDateFormat.format(date).toUpperCase();
    }

    /**
     * This method converts date string used in REE to Calendar .
     * 
     * @param strDate
     *        -- strDate
     * @param formatPattern
     *        -- formatPattern
     * @return Calendar - Calendar
     * @throws ParseException
     *         - ParseException
     */
    public static Calendar convertReeDtStringToCalendar(final String strDate, final String formatPattern) throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatPattern);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(strDate));
        return calendar;
    }

    /**
     * This method returns current system dateTime as String .
     * 
     * @return String - current dateTime as String.
     */
    public static String getFormattedCurrentDateTimeString() {
        final Calendar calendar = Calendar.getInstance();
        final Date currentDate = calendar.getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_DDMMMYY_HH_MM);
        final String currentDateTime = simpleDateFormat.format(currentDate);
        return currentDateTime;
    }

    /**
     * This method returns dateTime as String in "ddMMMyy HH:mm" format.
     * 
     * @param calendar
     *        - calendar to format.
     * @return String - current dateTime as String.
     */
    public static String getFormattedDateTimeString(final Calendar calendar) {
        final Date currentDate = calendar.getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_DDMMMYY_HH_MM);
        final String currentDateTime = simpleDateFormat.format(currentDate);
        return currentDateTime.toUpperCase();
    }

    /**
     * Change Time in Date object to 00:00:00
     * 
     * @param date
     *        Date
     * @return Date
     */
    public static Calendar setDefaultTime(final Calendar date) {
        if (date != null) {
            date.set(Calendar.MILLISECOND, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.HOUR_OF_DAY, 0);
        }
        return date;
    }

    /**
     * @param miliseconds
     *        - time in millisecond
     * @param showSeconds
     *        - to show second or not
     * @return formated time string as hh:mm:ss
     */
    public static String convertMilliseconds(final long miliseconds, final boolean showSeconds) {
        final StringBuffer convertedTime = new StringBuffer();
        long hh = 0;
        long mm = 0;
        long ss = 0;
        long time = miliseconds / 1000L;

        ss = time % 60L;
        time = time / 60L;
        mm = time % 60L;
        time = time / 60L;
        hh = time;

        if (hh == 0) {
            convertedTime.append("00");
        } else if (hh < 10) {
            convertedTime.append("0").append(hh);
        } else {
            convertedTime.append(hh);
        }

        convertedTime.append(":");

        if (mm == 0) {
            convertedTime.append("00");
        } else if (mm < 10) {
            convertedTime.append("0").append(mm);
        } else {
            convertedTime.append(mm);
        }

        if (showSeconds) {
            convertedTime.append(":");

            if (ss == 0) {
                convertedTime.append("00");
            } else if (ss < 10) {
                convertedTime.append("0").append(ss);
            } else {
                convertedTime.append(ss);
            }
        }
        return convertedTime.toString();
    }

    /**
     * Convert Time in date object to String.
     * 
     * @param date
     *        date
     * @return String with time
     * 
     */
    public static String getTimeString(final Calendar date) {
        String hr = String.valueOf(date.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(date.get(Calendar.MINUTE));
        if (hr.length() == 1) {
            hr = "0" + hr;
        }
        if (min.length() == 1) {
            min = "0" + min;
        }
        return hr + min;
    }

    /**
     * This method returns updated DOW of schedule that signifies on which days within scenario date range it flies.
     * 
     * @param scenStartDate
     *        - scenario date range start
     * @param scenEndDate
     *        - scenario date range end
     * @param scheduleStartDate
     *        - schedule date range start
     * @param scheduleEndDate
     *        - schedule date range end
     * @param scenDOW
     *        - scenario DOW
     * @param scheduleDOW
     *        - schedule DOW
     * @return updated DOW of schedule
     */
    public static String getDOWForScenDateRange(final Calendar scenStartDate, final Calendar scenEndDate, final Calendar scheduleStartDate,
            final Calendar scheduleEndDate, final String scenDOW, final String scheduleDOW) {
        final Calendar scheduleStartDateTmp = (Calendar) scheduleStartDate.clone();
        final Calendar scenStartDateTmp = (Calendar) scenStartDate.clone();
        final StringBuffer updatedDOW = new StringBuffer();
        Calendar updatedStartDate = scenStartDateTmp;
        if (updatedStartDate.before(scheduleStartDateTmp)) {
            updatedStartDate = scheduleStartDateTmp;
        }
        Calendar updatedEndDate = scheduleEndDate;
        if (updatedEndDate.after(scenEndDate)) {
            updatedEndDate = scenEndDate;
        }
        int i = 0;
        while (i < 7 && updatedStartDate.compareTo(updatedEndDate) <= 0) {
            int day = updatedStartDate.get(Calendar.DAY_OF_WEEK) - 1;
            if (day == 0) {
                day = 7;
            }
            if (scenDOW.indexOf(String.valueOf(day)) != -1 && scheduleDOW.indexOf(String.valueOf(day)) != -1) {
                updatedDOW.append(day);
                i++;
            }
            updatedStartDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return updatedDOW.toString();
    }

    /**
     * This method returns DOW for a scenario date range.
     * 
     * @param scenStartDate
     *        - scenario date range start
     * @param scenEndDate
     *        - scenario date range end
     * @return updated DOW for a scenario date range
     */
    public static String getDOWForScenDateRange(final Calendar scenStartDate, final Calendar scenEndDate) {
        final Calendar scenStartDateTmp = (Calendar) scenStartDate.clone();
        final StringBuilder scenDows = new StringBuilder();
        int i = 0;
        while (i < 7 && scenStartDateTmp.compareTo(scenEndDate) <= 0) {
            int day = scenStartDateTmp.get(Calendar.DAY_OF_WEEK) - 1;
            if (day == 0) {
                day = 7;
            }
            if (scenDows.indexOf(String.valueOf(day)) == -1) {
                scenDows.append(day);
                i++;
            }
            scenStartDateTmp.add(Calendar.DAY_OF_MONTH, 1);
        }
        return scenDows.toString();
    }

    /**
     * This method returns updated set of DOW for a scenario date range.
     * 
     * @param scenStartDate
     *        - scenario date range start
     * @param scenEndDate
     *        - scenario date range end
     * @param dowOfScenarios
     *        - original DOW to update
     * @return updated DOW for a scenario date range
     */
    public static Set<String> getDOWForScenDateRange(final Calendar scenStartDate, final Calendar scenEndDate,
            final Set<String> dowOfScenarios) {
        final Calendar scenStartDateTmp = (Calendar) scenStartDate.clone();
        int i = 0;
        while (i < 7 && scenStartDateTmp.compareTo(scenEndDate) <= 0) {
            int day = scenStartDateTmp.get(Calendar.DAY_OF_WEEK) - 1;
            if (day == 0) {
                day = 7;
            }
            if (!dowOfScenarios.contains(String.valueOf(day))) {
                dowOfScenarios.add(String.valueOf(day));
                i++;
            }
            scenStartDateTmp.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dowOfScenarios;
    }

    /**
     * Get the converted date.
     * 
     * @param date
     *        {@link Date}
     * @return <code>Calendar</code>
     */
    public static Calendar getConvertedDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        return cal;
    }
}