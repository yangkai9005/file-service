package org.elsa.filemanager.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author valor
 * @date 2018/9/22 14:17
 */
public class Times {

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE = "yyyy-MM-dd";
    public static final String TIME = "HH:mm:ss";
    public static final String YEAR = "yyyy";
    public static final String MONTH = "MM";
    public static final String DAY = "dd";
    public static final String HOUR = "HH";
    public static final String MINUTE = "mm";
    public static final String SEC = "ss";
    public static final String DATE_TIME_CHINESE = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String DATE_CHINESE = "yyyy年MM月dd日";
    public static final String SIMPLE_DATE_CHINESE = "MM月dd日";

    /**
     * 判断一个字符串日期是否过期
     *
     * @param dateTime 与正则一致的日期格式
     * @param pattern  日期正则 与日期格式一致
     * @return Boolean 过期true 未过期false
     */
    public static Boolean isOutOfDate(String dateTime, String pattern) throws ParseException {
        long now = System.currentTimeMillis();

        SimpleDateFormat df = new SimpleDateFormat(pattern);
        long input = df.parse(dateTime).getTime();

        // 判断是否过期
        return now > input;
    }

    /**
     * 判断是否在一个起止日期内
     *
     * @param start   与正则一致的日期格式
     * @param end     与正则一致的日期格式
     * @param pattern 日期正则 与日期格式一致
     * @return Boolean 在时间段内true
     */
    public static Boolean isBetweenOfDate(String start, String end, String pattern) throws ParseException {
        long now = System.currentTimeMillis();

        SimpleDateFormat df = new SimpleDateFormat(pattern);
        long inputStart = df.parse(start).getTime();
        long inputEnd = df.parse(end).getTime();

        return now > inputStart && now < inputEnd;
    }

    /**
     * 判断一个自定义日期是否在一个起止日期内
     *
     * @param dateTime 自定义日期 与正则一致的日期格式
     * @param start    与正则一致的日期格式
     * @param end      与正则一致的日期格式
     * @param pattern  日期正则 与日期格式一致
     * @return boolean 在时间段内true
     */
    public static Boolean isBetweenOfDate(String dateTime, String start, String end, String pattern) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        long customize = df.parse(dateTime).getTime();
        long inputStart = df.parse(start).getTime();
        long inputEnd = df.parse(end).getTime();

        return customize > inputStart && customize < inputEnd;
    }

    /**
     * 判断当前时间是否在一个时间段内
     * 例如:8:00~10:00
     *
     * @param startTime HH:mm:ss 格式时间
     * @param endTime   HH:mm:ss 格式时间
     * @return boolean 在时间段内true
     */
    public static Boolean isInTime(String startTime, String endTime) throws ParseException {
        // 获取当前日期
        SimpleDateFormat df = new SimpleDateFormat(DATE);
        String nowDate = df.format(new Date());
        return isBetweenOfDate(nowDate + " " + startTime, nowDate + " " + endTime, DATE_TIME);
    }

    /**
     * 判断一个自定义时间是否在一个时间段内
     * 例如:判断02:00是否在08:00~10:00时间段内
     *
     * @param startTime HH:mm:ss 格式时间
     * @param endTime   HH:mm:ss 格式时间
     * @return boolean 在时间段内true
     */
    public static Boolean isInTime(String time, String startTime, String endTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DATE);
        String nowDate = df.format(new Date());
        return isBetweenOfDate(nowDate + " " + time, nowDate + " " + startTime, nowDate + " " + endTime, DATE_TIME);
    }

    /**
     * Date类型日期时间转为字符串类型日期时间
     *
     * @param date    Date类型的日期时间
     * @param pattern 时间格式字符串，如"yyyy-MM-dd HH:mm:ss"
     * @return 字符串类型的时间，如"2016-12-13 08:59:32"
     */
    public static String dateFormat(Date date, String pattern) {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        // new Date()为获取当前系统时间
        return df.format(date);
    }

    /**
     * 字符串日期时间转为Date类型日期时间
     *
     * @param dateTime 字符串类型的时间，如"2016-12-13 08:59:32"
     * @param pattern  时间格式字符串，如"yyyy-MM-dd HH:mm:ss"
     * @return date Date类型的日期时间
     */
    public static Date dateParse(String dateTime, String pattern) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.parse(dateTime);
    }

    /**
     * 将日期时间字符串转为指定格式
     *
     * @param dateTime 日期时间字符串，如"2016-12-13 15:28:00" 或"2016-12-13"
     * @param pattern  指定的日期时间格式
     *                 若"yyyy-MM-dd",则获取传入日期时间的年月日
     *                 若"MM-dd",则获取传入日期时间的月日
     *                 若"dd",则获取传入日期时间的日
     *                 若"HH:mm:ss"，则获取传入日期时间的时分秒，若传入的日期时间字符串为"2016-12-13"，但格式化字符串为"HH:mm:ss"，则返回"00:00:00"
     * @return 字符串类型的日期
     */
    public static String dateFormat(String dateTime, String pattern) throws ParseException {
        String formatStr = DATE_TIME;
        if (dateTime.length() < 12) {
            formatStr = DATE;
        }
        SimpleDateFormat df = new SimpleDateFormat(formatStr);
        Date date = df.parse(dateTime);
        // 设置日期格式
        df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    /**
     * 由Date类型日期时间得到当天的起始时间
     *
     * @param date Date类型的日期时间，如2016-12-13 08:23:55
     * @return 字符串类型的时间，如"2016-12-13 00:00:00"
     */
    public static String getDayByDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DATE);
        return df.format(date) + " 00:00:00";
    }

    /**
     * 由时间戳得到当天的0点时间戳
     */
    public static long getDayByTimeInMillis(long timeInMillis) {
        return getDayByTimeInMillis(timeInMillis, null);
    }

    /**
     * 由时间戳得到当天的0点时间戳
     */
    public static long getDayByTimeInMillis(long timeInMillis, String timeZone) {
        if (StringUtils.isBlank(timeZone)) {
            return getDayByTimestamp(timeInMillis, TimeZone.getDefault());
        }
        return getDayByTimestamp(timeInMillis, TimeZone.getTimeZone(timeZone));
    }

    /**
     * 由时间戳得到当天的0点时间戳
     */
    private static long getDayByTimestamp(long timeInMillis, TimeZone curTimeZone) {
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(timeInMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 由时间戳得到当天的0点时间戳
     */
    public static long getDayByCalculation(long timeInMillis) {
            return getDayByCalculation(timeInMillis, null);
    }
    
    /**
     * 由时间戳得到当天的0点时间戳
     */
    public static long getDayByCalculation(long timeInMillis, String timeZone) {
        if (StringUtils.isBlank(timeZone)) {
            return getDayByCalcu(timeInMillis, TimeZone.getDefault());
        }
        return getDayByCalcu(timeInMillis, TimeZone.getTimeZone(timeZone));
    }

    /**
     * 由时间戳得到当天的0点时间戳
     */
    private static long getDayByCalcu(long timeInMillis, TimeZone curTimeZone) {
        return timeInMillis / (1000 * 3600 * 24) * (1000 * 3600 * 24) - curTimeZone.getRawOffset();
    }

    /**
     * 得到本月的第一天日期
     */
    public static String getMonthFirstDay() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dateFormat(calendar.getTime(), DATE);
    }

    /**
     * 得到本月的最后一天日期
     */
    public static String getMonthLastDay() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormat(calendar.getTime(), DATE);
    }

    /**
     * 根据年、月获取某年某月最后一天
     *
     * @param year  int型
     * @param month int型
     * @return 该月最后一天的dd "31"
     */
    public static int getLastDayOfMonth(int year, int month) {
        Calendar cal = new GregorianCalendar();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据某个日期获取该月最后一天
     *
     * @param dateTime "2016-12-13"
     * @return 该月最后一天的 dd "31"
     */
    public static Integer getLastDayOfMonth(String dateTime) throws ParseException {
        String y = dateFormat(dateTime, YEAR);
        String m = dateFormat(dateTime, MONTH);

        int year = Integer.parseInt(y);
        int month = Integer.parseInt(m);

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据年、月获取某年某月最后一天
     *
     * @param year  int型
     * @param month int型
     * @return 该月最后一天的日期 "2016-12-31
     */
    public static String getLastDateOfMonth(int year, int month) {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormat(cal.getTime(), DATE);
    }

    /**
     * 根据某个日期获取该月最后一天
     *
     * @param dateTime "2016-12-13"
     * @return 该月最后一天的日期 "2016-12-31
     */
    public static String getLastDateOfMonth(String dateTime) throws ParseException {
        String y = dateFormat(dateTime, YEAR);
        String m = dateFormat(dateTime, MONTH);

        int year = Integer.parseInt(y);
        int month = Integer.parseInt(m);

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormat(cal.getTime(), DATE);
    }

    /**
     * 由日期字符串得到该日期为那个月的第几天
     */
    public static Integer getDayOfMonth(String dateTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATE);
        Date date = format.parse(dateTime);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 由日期字符串得到该月有几天
     */
    public static Integer getDaysInMonth(String dateTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = format.parse(dateTime);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得某个日期+days天后的日期
     * days 可正可负
     */
    public static String addDay(String dateTime, int days) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME);
        Date date = format.parse(dateTime);
        return dateFormat(addDay(date, days), DATE_TIME);
    }

    /**
     * 获得某个日期+days天后的日期
     * days 可正可负
     */
    public static Date addDay(Date date, int days) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 计算两个日期之间相差的天数
     */
    public static Long daysBetween(String startDate, String endDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DATE);
        long s = df.parse(startDate).getTime();
        long e = df.parse(endDate).getTime();

        return (e - s) / (1000 * 3600 * 24);
    }

    /**
     * 验证日期字符串是否合法
     *
     * @param str 如传入"2017-01-32"
     * @return 如传入"2017-01-32" 则会报错
     */
    public static boolean isValidDate(String str, String pattern) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 不允许外部实例化
     */
    private Times() { }
}
