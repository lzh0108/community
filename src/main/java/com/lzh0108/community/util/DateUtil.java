package com.lzh0108.community.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: lzh
 * @Date: 2020/11/11 13:42
 * @Description:
 */
public class DateUtil {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final long MILLISECOND_OF_MINUTE = 60000L;
    public static final long MILLISECOND_OF_HOUR = 3600000L;
    public static final long MILLISECOND_OF_DAY = 86400000L;

    public DateUtil() {
    }

    public static Date getDate(int year, int month, int day) {
        return getDate(year, month, day, 0, 0, 0);
    }

    public static Date getDate(int year, int month, int day, int hourOfDay, int minute, int second) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month - 1, day, hourOfDay, minute, second);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static Date getDateMaxTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar.getTime();
    }

    public static Date getDateMinTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static int get(Date date, int field) {
        GregorianCalendar cal = new GregorianCalendar();
        if (date != null) {
            cal.setTime(date);
        }

        return cal.get(field);
    }

    public static int getYear(Date date) {
        return get(date, 1);
    }

    public static int getMonth(Date date) {
        return get(date, 2) + 1;
    }

    public static int getDayOfMonth(Date date) {
        return get(date, 5);
    }

    public static int getHour(Date date) {
        return get(date, 11);
    }

    public static int getMinute(Date date) {
        return get(date, 12);
    }

    public static int getSecond(Date date) {
        return get(date, 13);
    }

    /** @deprecated */
    @Deprecated
    public static int getWeekDay(Date date) {
        return get(date, 7);
    }

    public static int getWeekday(Date date) {
        int day = get(date, 7);
        return day == 1 ? 7 : day - 1;
    }

    public static int getWeekOfYear(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        if (date != null) {
            cal.setTime(date);
        }

        cal.setFirstDayOfWeek(2);
        return cal.get(3);
    }

    public static int getWeekOfMonth(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        if (date != null) {
            cal.setTime(date);
        }

        cal.setFirstDayOfWeek(2);
        return cal.get(4);
    }

    public static Date addDate(Date date, int field, int value) {
        GregorianCalendar start = new GregorianCalendar();
        start.setTime(date);
        start.add(field, value);
        return start.getTime();
    }

    public static Date addYears(Date date, int value) {
        return addDate(date, 1, value);
    }

    public static Date addMonths(Date date, int value) {
        return addDate(date, 2, value);
    }

    public static Date addDays(Date date, int value) {
        return addDate(date, 5, value);
    }

    public static Date addHours(Date date, int value) {
        return addDate(date, 10, value);
    }

    public static Date addHours(Date date, float value) {
        long h = (long)(value * 3600000.0F);
        return new Date(date.getTime() + h);
    }

    public static Date addMinutes(Date date, int value) {
        return addDate(date, 12, value);
    }

    public static Date addSeconds(Date date, int value) {
        return addDate(date, 13, value);
    }

    public static Date removeSecond(Date date) {
        return date == null ? date : new Date(date.getTime() / 60000L * 60000L);
    }

    public static Date getCurrentDateByZone(String zone) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(zone));
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(cal.get(1), cal.get(2), cal.get(5), cal.get(11), cal.get(12), cal.get(13));
        return currentTime.getTime();
    }

    public static Date convertToDate(String str, String format) {
        return convertToDate(str, format, Locale.US);
    }

    public static Date convertToDate(String str, String format, Locale locale) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format, locale == null ? Locale.getDefault() : locale);
            return df.parse(str);
        } catch (ParseException var4) {
            return null;
        }
    }

    public static Date convertToDate(String str, String[] formats) {
        return convertToDate(str, (String[])formats, (Locale)null);
    }

    public static Date convertToDate(String str, String[] formats, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        Date dt = null;
        String[] var5 = formats;
        int var6 = formats.length;
        int var7 = 0;

        while(var7 < var6) {
            String fm = var5[var7];

            try {
                SimpleDateFormat df = new SimpleDateFormat(fm, locale);
                dt = df.parse(str);
                break;
            } catch (ParseException var10) {
                ++var7;
            }
        }

        return dt;
    }

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss", (Locale)null);
    }

    public static String format(Date date, String format) {
        return format(date, format, (Locale)null);
    }

    public static String format(Date date, String format, Locale locale) {
        return format(date, format, locale, (TimeZone)null);
    }

    public static String format(Date date, String format, Locale locale, TimeZone zone) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat dateFormatter = null;
            if (locale != null) {
                dateFormatter = new SimpleDateFormat(format, locale);
            } else {
                dateFormatter = new SimpleDateFormat(format);
            }

            if (zone != null) {
                dateFormatter.setTimeZone(zone);
            }

            return dateFormatter.format(date);
        }
    }

    /** @deprecated */
    @Deprecated
    public static String getDateString(Date date, String format, Locale locale) {
        return format(date, format, locale);
    }

    /** @deprecated */
    @Deprecated
    public static String getDateString(Date date) {
        return format(date);
    }

    /** @deprecated */
    @Deprecated
    public static String getDateString(Date date, String format) {
        return format(date, format, (Locale)null);
    }

    public static Date getUtcDate() {
        return getUtcDate((Date)null);
    }

    public static Date getUtcDate(Date date) {
        Calendar cal = new GregorianCalendar();
        if (date != null) {
            cal.setTime(date);
        }

        int zoneOffset = cal.get(15);
        int dstOffset = cal.get(16);
        cal.add(14, -(zoneOffset + dstOffset));
        return new Date(cal.getTimeInMillis());
    }

    public static long getUtcTime() {
        return getUtcTime((Date)null);
    }

    public static long getUtcTime(Date date) {
        Calendar cal = new GregorianCalendar();
        if (date != null) {
            cal.setTime(date);
        }

        int zoneOffset = cal.get(15);
        int dstOffset = cal.get(16);
        cal.add(14, -(zoneOffset + dstOffset));
        return cal.getTimeInMillis();
    }

    public static boolean between(Date input, Date start, Date end) {
        if (input.compareTo(start) == -1) {
            return false;
        } else {
            return input.compareTo(end) != 1;
        }
    }

    public static int getDaysBetween(Date date1, Date date2) {
        long time = date1.getTime() - date2.getTime();
        if (time == 0L) {
            return 0;
        } else {
            if (time < 0L) {
                time = -time;
            }

            return (int)(time / 86400000L);
        }
    }

    public static int getMonthsBetween(Date date1, Date date2) {
        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(date2);
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(date1);
        int y1 = cal1.get(1);
        int y2 = cal2.get(1);
        int m1 = cal1.get(2);
        int m2 = cal2.get(2);
        if (m1 < m2) {
            m1 += 12;
            --y1;
        }

        return Math.abs((y1 - y2) * 12 + (m1 - m2));
    }

    public static Date getDateBetween(Date input, Date min, Date max) {
        long time = input.getTime();
        if (time <= min.getTime()) {
            return min;
        } else {
            return time > max.getTime() ? max : input;
        }
    }

    public static Date max(Date date1, Date date2) {
        return date1.compareTo(date2) == 0 ? date2 : date1;
    }

    public static Date min(Date date1, Date date2) {
        return date1.compareTo(date2) == 1 ? date2 : date1;
    }

    public static void main(String[] args) {
        Date d2 = new Date();
        System.out.println(getWeekOfYear(d2));
    }
}

