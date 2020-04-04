package com.destro.linkcalculator.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.destro.linkcalculator.util.Constants.DATE_FORMAT;

public class DateUtil {

    private DateUtil() {}

    public static int getWeekNo(final String startDate, final LocalDate currentDate, final int noOfMembers) {
        final LocalDate parsedStartDate = LocalDate.parse( startDate, DateTimeFormatter.ofPattern( DATE_FORMAT ) );
        final long days = Math.toIntExact( ChronoUnit.DAYS.between( parsedStartDate, currentDate ) );
        int weekNo = (int) (days / 7);
        weekNo = weekNo - (noOfMembers * (weekNo / noOfMembers));
        return weekNo + 1;
    }

    public static int getRelativeWeekNo(final int staffNo, final int weekNo, final int noOfMembers) {
        int staffWeekNo = (staffNo - 1) + weekNo;
        while (staffWeekNo > 16) {
            staffWeekNo = staffWeekNo - noOfMembers;
        }
        return staffWeekNo;
    }

    public static int getOriginalWeekNo(final int staffWeekNo, final int weekNo, final int noOfMembers) {
        int staffNo = (staffWeekNo - weekNo) + 1;
        while (staffNo < 1) {
            staffNo = noOfMembers + staffNo;
        }
        return staffNo;
    }

    public static String getWeekDay(final Date date) {
        return new SimpleDateFormat("EEEE").format(date).toLowerCase();
    }
}
