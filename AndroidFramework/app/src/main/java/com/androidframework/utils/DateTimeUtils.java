package com.androidframework.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date time utils: Its having few static member function to manipulate date and times
 */
public class DateTimeUtils {

    public static String getComparedDateText(String utcTime) {
        try {
            if (utcTime == null)
                return "";

            //removing code after "+"
            if (utcTime.contains("+")) {
                String[] onlyUTCTime = utcTime.split("\\+");
                utcTime = onlyUTCTime[0];
            }

            DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            iso8601.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = iso8601.parse(utcTime);

            return getDifference(new Date(), date, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static String getComparedDateTextForMsg(String utcTime) {
        try {
            if (utcTime == null)
                return "";

            //removing code after "+"
            if (utcTime.contains("+")) {
                String[] onlyUTCTime = utcTime.split("\\+");
                utcTime = onlyUTCTime[0];
            }

            DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            iso8601.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = iso8601.parse(utcTime);

            return getDifferenceForMessage(new Date(), date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static String remainingTime(String utcTime) {
        try {
            if (utcTime == null)
                return "";
            //removing code after "+"
            if (utcTime.contains("+")) {
                String[] onlyUTCTime = utcTime.split("\\+");
                utcTime = onlyUTCTime[0];
            }

            DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            iso8601.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = iso8601.parse(utcTime);
            return getDifference(new Date(), date, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDifference(Date startDate, Date endDate, boolean lessThanAmin) {
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();

        //System.out.println("startDate : " + startDate);
        //System.out.println("endDate : " + endDate);
        //System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        //check if day older
        if (elapsedDays == 1) {
            return elapsedDays + " day ago";
        } else if (elapsedDays > 1 && elapsedDays < 6) {
            return elapsedDays + " days ago";
        } else if (elapsedDays > 5) {
            if (lessThanAmin) {
                return convertMilliSecondsToFormattedDate(endDate.getTime());
            } else {
                return convertMilliSecondsToFormattedDate(startDate.getTime());
            }
        }

        //check if hour older
        if (elapsedHours == 1) {
            return elapsedHours + " hour ago";
        } else if (elapsedHours > 1) {
            return elapsedHours + " hours ago";
        }

        //check mins older
        if (elapsedMinutes > 0) {
            return elapsedMinutes + " min ago";
        } else {
            if (lessThanAmin) {
                //check seconds older
                return "less than one min";
            } else {
                //check seconds older
                return "just now";
            }
        }

    }

    public static String getDifferenceForMessage(Date startDate, Date endDate) {
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        if (elapsedDays >= 1) {
            return convertFormat(endDate.getTime());

        }

        //check if hour older
        if (elapsedHours == 1) {
            return elapsedHours + "h ago";
        } else if (elapsedHours > 1) {
            return elapsedHours + "h ago";
        }

        //check mins older
        if (elapsedMinutes > 0) {
            return elapsedMinutes + "m ago";
        } else {
            //check seconds older
            return "just now";
        }

    }

    public static String convertMilliSecondsToFormattedDate(long milliSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String convertFormat(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return getMonthName(calendar.get(Calendar.MONTH)) + " "
                + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getMonthName(int month) {
        switch (month + 1) {
            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "Mar";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "Jun";

            case 7:
                return "Jul";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            case 12:
                return "Dec";
        }

        return "";
    }
}
