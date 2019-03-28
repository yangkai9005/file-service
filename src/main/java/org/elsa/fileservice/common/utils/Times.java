package org.elsa.fileservice.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

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

    /**
     * 一天的毫秒数  24 * 60 * 60 * 1000
     */
    private static final long ONE_DAY_IN_MILLISECOND = 86400000;

    /**
     * 不允许外部实例化
     */
    private Times() { }

    /**
     * 指定日期字符串 按指定格式转换成时间戳
     */
    public static long toTimestamp(String str, String pattern) throws Exception {
        return DateUtils.parseDate(str, pattern).getTime();
    }

    /**
     * 指定timestamp 按指定格式格式化时间
     */
    public static String format(long time, String pattern) {
        return DateFormatUtils.format(time, pattern);
    }

    /**
     * 指定Date 按指定格式格式化时间
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 判断指定timestamp 是否是过去的时间
     */
    public static boolean isPast(long time) {
        return !isFuture(time);
    }

    /**
     * 判断指定timestamp 是否是未来的时间
     */
    public static boolean isFuture(long time) {
        return System.currentTimeMillis() < time;
    }

    /**
     * 判断当前timestamp 是否在某个时间段内
     */
    public static boolean isInTime(long start, long end) {
        return isInTime(System.currentTimeMillis(), start, end);
    }

    /**
     * 判断指定timestamp 是否在某个时间段内
     */
    public static boolean isInTime(long time, long start, long end) {
        return start <= time && time <= end;
    }

    /**
     * 计算两个日期相差的天数
     * 不足一天 <p>按一天计算</p>
     */
    public static long getDaysInCeil(Date date, Date another) {
        return getDaysInCeil(date.getTime(), another.getTime());
    }

    /**
     * 计算两个日期相差的天数
     * 不足一天 <p>按一天计算</p>
     */
    public static long getDaysInCeil(long time, long another) {
        return getDaysInFloor(time, another) + 1;
    }

    /**
     * 计算两个日期相差的天数
     * 不足一天 <p>不计算在内</p>
     */
    public static long getDaysInFloor(Date date, Date another) {
        return getDaysInFloor(date.getTime(), another.getTime());
    }

    /**
     * 计算两个日期相差的天数
     * 不足一天 <p>不计算在内</p>
     */
    public static long getDaysInFloor(long time, long another) {
        long a = time - another;
        if (a < 0) {
            a = 0 - a;
        }
        return a / ONE_DAY_IN_MILLISECOND;
    }

    /**
     * 获取今日零时timestamp
     */
    public static long getZeroPoint() {
        return getZeroDate().getTime();
    }

    /**
     * 获取今日零时Date
     */
    public static Date getZeroDate() {
        return getZeroDate(0);
    }

    /**
     * 指定与今日相差的天数 获取零时timestamp
     */
    public static long getZeroPoint(int amount) {
        return getZeroDate(amount).getTime();
    }

    /**
     * 指定与今日相差的天数 获取零时Date
     */
    public static Date getZeroDate(int amount) {
        if (amount == 0) {
            return DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        }
        return DateUtils.truncate(DateUtils.addDays(new Date(), amount), Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当月第一天的timestamp (零时)
     */
    public static long getMonthFirstPoint() {
        return getMonthFirstDate().getTime();
    }

    /**
     * 获取当月第一天的Date
     */
    public static Date getMonthFirstDate() {
        return getMonthFirstDate(0);
    }

    /**
     * 指定与当月相差的月数 获取指定月份第一天的timestamp (零时)
     */
    public static long getMonthFirstPoint(int amount) {
        return getMonthFirstDate(amount).getTime();
    }

    /**
     * 指定与当月相差的月数 获取指定月份第一天的Date
     */
    public static Date getMonthFirstDate(int amount) {
        return getMonthDate(amount, false);
    }

    /**
     * 获取本月最后一天的timestamp (零时)
     */
    public static long getMonthLastPoint() {
        return getMonthLastDay().getTime();
    }

    /**
     * 获取本月最后一天的Date
     */
    public static Date getMonthLastDay() {
        return getMonthLastDay(0);
    }

    /**
     * 指定与当月相差的月数 获取指定月份最后一天的timestamp (零时)
     */
    public static long getMonthLastPoint(int amount) {
        return getMonthLastDay(amount).getTime();
    }

    /**
     * 指定与当月相差的月数 获取指定月份最后一天的Date
     */
    public static Date getMonthLastDay(int amount) {
        return getMonthDate(amount, true);
    }

    private static Date getMonthDate(int amount, boolean isEnd) {
        Calendar c = Calendar.getInstance();
        if (amount != 0) {
            c.add(Calendar.MONTH, amount);
        }
        if (isEnd) {
            // 设置月末时间
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            // 设置月初时间
            c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

}
