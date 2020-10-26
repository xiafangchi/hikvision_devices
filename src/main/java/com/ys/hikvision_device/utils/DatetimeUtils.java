package com.ys.hikvision_device.utils;

import com.google.common.base.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DatetimeUtils {

    private static Log logger = LogFactory.getLog(DatetimeUtils.class);

    /**
     * 线程的哈希表
     */
    private static ThreadLocal<Map> hashMapThreadLocal = new ThreadLocal<>();

    //private static Map<String, SimpleDateFormat> formatterMap = new Hashtable<String, SimpleDateFormat>();

    /**
     * 获取格式化对象， 线程安全
     *
     * @param formatPattern 格式字符串
     * @return 格式对象
     */
    public static SimpleDateFormat getFormatter(String formatPattern) {
        Map<String, SimpleDateFormat> formatterMap = hashMapThreadLocal.get();
        if (formatterMap == null) {
            formatterMap = new ConcurrentHashMap<>();
            hashMapThreadLocal.set(formatterMap);
        }
        if (formatterMap.containsKey(formatPattern)) {
            return formatterMap.get(formatPattern);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);
            TimeZone tz = TimeZone.getDefault();
            formatter.setTimeZone(tz);
            formatterMap.put(formatPattern, formatter);
            return formatter;
        }
    }

    /**
     * String格式时间转换我UnixTime，如2014-8-12 转换结果不包括毫秒
     */
    public static long convertDateToTimestamp(String datetime, String format)
            throws ParseException {
        return getFormatter(format.trim()).parse(datetime.trim()).getTime() / 1000;
    }

    /**
     * String格式时间转换我UnixTime，起始时间为00：00：00
     */
    public static long convertStartDateToTimestamp(String datetime,
                                                   String format) throws ParseException {
        datetime = datetime + " 00:00:00";
        return getFormatter(format.trim()).parse(datetime.trim()).getTime() / 1000;
    }

    /**
     * String格式时间转换我UnixTime，起始时间为00：00：00
     */
    public static Long convertStartDateToTimestamp(String datetime) {
        datetime = datetime + " 00:00:00";
        try {
            return getFormatter("yyyy-MM-dd HH:mm:ss").parse(datetime.trim()).getTime() / 1000;
        } catch (ParseException e) {
            logger.error("ERROR:", e);
            return null;
        }

    }

    /**
     * String格式时间转换我UnixTime，结束时间为23:59:59
     */
    public static long convertEndDateToTimestamp(String datetime, String format)
            throws ParseException {
        datetime = datetime + " 23:59:59";
        return getFormatter(format.trim()).parse(datetime.trim()).getTime() / 1000;
    }

    /**
     * String格式时间转换我UnixTime，结束时间为23:59:59
     */
    public static Long convertEndDateToTimestamp(String datetime) {

        datetime = datetime + " 23:59:59";
        try {
            return getFormatter("yyyy-MM-dd HH:mm:ss").parse(datetime.trim()).getTime() / 1000;
        } catch (ParseException e) {
            logger.error("ERROR:", e);
            return null;
        }
    }

    /**
     * Date转时间戳 转换结果不包括毫秒
     */
    public static long convertDateToTimestamp(Date datetime)
            throws ParseException {
        // SimpleDateFormat formater=getFormatter(format.trim());
        // return formater.parse(formater.format(datetime)).getTime();
        return datetime.getTime() / 1000;
    }

    /*
     * String格式时间转换我UnixTime，如2014-8-12 此方法暂不使用
     */
    @SuppressWarnings("unused")
    private static long convertDateToLong(String datetime, String format)
            throws ParseException {
        return getFormatter(format.trim()).parse(datetime.trim()).getTime();
    }

    /*
     * Date转时间戳 此方法暂不使用
     */
    @SuppressWarnings("unused")
    private static long convertDateToLong(Date datetime) throws ParseException {
        // SimpleDateFormat formater=getFormatter(format.trim());
        // return formater.parse(formater.format(datetime)).getTime();
        return datetime.getTime();
    }

    /*
     * UnixTime转换为String
     */
    public static String convertTimestampToDate(long time, String format) {
        return getFormatter(format.trim()).format(new Date(time * 1000)).trim();
    }

    /*
     * UnixTime转换为Date
     */
    public static Date convertTimestampToDate(long time) {
        // SimpleDateFormat formater=getFormatter(format.trim());
        // Date date=null;
        // try {
        // date= formater.parse(formater.format(time));
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        return new Date(time * 1000);
    }

    /*
     * UnixTime转换为String
     */
    public static String convertLongToDate(long time, String format) {
        return getFormatter(format.trim()).format(new Date(time)).trim();
    }

    /*
     * UnixTime转换为String
     */
    public static String convertDateToDateString(Date date, String format) {
        return getFormatter(format.trim()).format(date).trim();
    }

    /*
     * UnixTime转换为Date
     */
    @SuppressWarnings("unused")
    public static Date convertLongToDate(long time) {
        // SimpleDateFormat formater=getFormatter(format.trim());
        // Date date=null;
        // try {
        // date= formater.parse(formater.format(time));
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        return new Date(time);
    }

    /*
     * String 转换 Date
     */
    public static Date ConvertDate(String time, String format) {
        try {
            SimpleDateFormat formater = getFormatter(format.trim());
            return formater.parse(time);
        } catch (ParseException e) {
            logger.error("ERROR:", e);
            return null;
        }
    }

    /**
     * 取得当前时间的时间戳 不带毫秒
     *
     * @return 时间戳
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getTodayDateOfTimestamp() {
        long t = System.currentTimeMillis() / 1000 / 60 / 60 / 24;
        return t * 24 * 3600;

    }

    // public static long getNextWeekDateOfLong() {
    // calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    // calendar.add(Calendar.WEEK_OF_YEAR, 1);
    // Date d = calendar.getTime();
    // return calendar.getTimeInMillis() / 1000;
    //
    // }

    public static long getNextMonthDateOfTimestamp(long thisMonth) {
        Calendar calendar = Calendar.getInstance();
        Date thisMonthDate = new Date(thisMonth * 1000);
        calendar.setTime(thisMonthDate);
        calendar.add(Calendar.MONTH, 1);
        // Date d = calendar.getTime();
        return calendar.getTimeInMillis() / 1000;

    }

    public static long getNextIntervalTimeOfTimestamp(long thisDay, int days) {
        Calendar calendar = Calendar.getInstance();
        Date thisDayDate = new Date(thisDay * 1000);
        calendar.setTime(thisDayDate);
        calendar.add(Calendar.DATE, days);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;

    }

    public static long getSpecifiedMonthDateOfTimestamp(long thisMonth,
                                                        int amount) {
        Calendar calendar = Calendar.getInstance();
        Date thisMonthDate = new Date(thisMonth * 1000);
        calendar.setTime(thisMonthDate);
        calendar.add(Calendar.MONTH, amount);
        // Date d = calendar.getTime();
        return calendar.getTimeInMillis() / 1000;

    }

    public static long getLastDayOfThisYear() {
        Calendar calendar = Calendar.getInstance();
        Date d = new Date();
        calendar.setTime(d);

        calendar.set(calendar.get(Calendar.YEAR), 12, 31, 23, 59, 59);
        return calendar.getTimeInMillis() / 1000;
    }

    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 描述：得到当月的第一天的00时
     *
     * @return
     * @data 2016年3月2日 下午6:50:38
     * @author 杨建 yang.jian@mobcb.com
     */
    public static long getFirstDayOfThisMonth() throws ParseException {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        SimpleDateFormat first = DatetimeUtils.getFormatter("yyyy-MM-dd 00:00:00");
        Date date = first.parse(year + "-" + month + "-01" + " 00:00:00");

        return date.getTime() / 1000;
    }


    /**
     * 描述：得到当月的最后一天的23:59:59
     */
    public static long getLastDayOfThisMonth() throws ParseException {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        //把日期设置为当月第一天
        calendar.set(Calendar.DATE, 1);
        //日期回滚一天，也就是最后一天
        calendar.roll(Calendar.DATE, -1);
        int MaxDate = calendar.get(Calendar.DATE);
        System.out.println("该月最大天数:" + MaxDate);
        SimpleDateFormat first = DatetimeUtils.getFormatter("yyyy-MM-dd 23:59:59");
        Date date = first.parse(year + "-" + month + "-" + MaxDate + " 23:59:59");
        return date.getTime() / 1000;
    }

    /**
     * 根据日期取得星期几
     */
    public static String[] getWeek(Date date) {

        String[] args = {"1", "星期一"};
        String[] weeks = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(date);
        int weekId = 0;
        for (int i = 0; i < weeks.length; i++) {
            if (week.equals(weeks[i])) {
                weekId = i + 1;
                args[0] = String.valueOf(weekId);
                args[1] = week;
                // System.out.println(args[0] + args[1]);
            }
        }
        return args;
    }

    /**
     * 获取明天的日期
     */
    public static Date getTomorrow(Date date) {

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // String dateString = formatter.format(date);
        return date;
    }

    /**
     * （yyyy-MM-dd）格式字符串转时间戳
     * <p>
     * by lvmenghui
     *
     * @param datetime
     * @return
     */
    public static long convertDateToTimestamp(String datetime) {

        try {
            return getFormatter("yyyy-MM-dd").parse(datetime.trim()).getTime() / 1000;
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * （yyyy-MM）格式字符串转时间戳
     * <p>
     * by lvmenghui
     *
     * @param datetime
     * @return
     */
    public static long convertMonthToTimestamp(String datetime) {

        try {
            return getFormatter("yyyy-MM").parse(datetime.trim()).getTime() / 1000;
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * （yyyy-MM-dd HH:mm:ss）格式字符串转时间戳
     * <p>
     * by lvmenghui
     *
     * @param datetime
     * @return
     */
    public static long convertDatetimeToTimestamp(String datetime) {

        try {
            return getFormatter("yyyy-MM-dd HH:mm:ss").parse(datetime.trim())
                    .getTime() / 1000;
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * ] 根据年龄返回出生日期字符串
     * <p>
     * by lvmenghui
     *
     * @param age
     * @return
     */
    public static String getBirthDate(int age) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.YEAR, 0 - age);
        Date birthDate = c.getTime();
        return format.format(birthDate);
    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd）
     * <p>
     * by yw
     *
     * @return
     */
    public static Date getSystemDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            return sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd）
     * <p>
     * by yw
     *
     * @return
     */
    public static String getSystemDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return sdf.format(date);

    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd HH:mm:ss）
     * <p>
     * by yw
     *
     * @return
     */
    public static String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd HH:mm:ss）
     *
     * @return
     */
    public static String getNowDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获得系统当前时间（yyyyMMddHHmmss）
     */
    public static String getNowDateSmsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获得 当前系统时间（yy-MM-dd）
     * <p>
     * by yw
     *
     * @return
     */
    public static String getSystemTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Date date = new Date();
        return sdf.format(date);

    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd）
     * <p>
     * by yw
     *
     * @return
     */
    public static String getSystemMonthStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        return sdf.format(date);

    }

    /**
     * 获得 当前系统时间（yyyyMMddHHmmssSSS）
     * 精确到毫秒
     * <p>
     * by zonghaibo
     *
     * @return
     */
    public static String getSystemMilliSecondStr() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd）
     * <p>
     * by yw
     *
     * @return
     */
    public static String getSystemDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return sdf.format(date);

    }

    /**
     * 获得 当前系统时间（HHmmss）
     * <p>
     * by yw
     *
     * @return
     */
    public static String getSystemTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获得 当前系统时间（HHmmss）
     * <p>
     * by yw
     *
     * @return
     */
    public static String getSystemTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd）获取该日期前n天，后n天
     * <p>
     * by yw
     *
     * @return
     */
    public static String getAssignDateString(Integer n) {
        //modify by gonghongxing  at 2017/08/16 一天的截至应该要算到23：59：59
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, n);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        return sdf.format(date);

    }

    /**
     * 描述：获得某年第一天
     *
     * @return
     * @data 2016年3月2日 下午6:50:38
     * @author 杨建 yang.jian@mobcb.com
     */
    public static String getYearFirstDay(Integer n) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - n;
        /*   SimpleDateFormat first = DatetimeUtils.getFormatter("yyyy-MM-dd");*/
        String date = year + "-01" + "-01";
        return date;
    }

    /**
     * 获取今天0时的时间戳
     *
     * @return
     */
    public static Long getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 比较时间戳日期是否相等
     *
     * @param t1 时间戳1
     * @param t2 时间戳2
     * @return 是（true）/否（false）
     */
    public static boolean equalDate(Long t1, Long t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return convertTimestampToDate(t1, "yyyy-MM-dd").equals(convertTimestampToDate(t2, "yyyy-MM-dd"));
    }

    /**
     * 获取时间间隔单位为天的下一个时间
     *
     * @param thisTime 当时
     * @param days     天数
     * @return 下一个时间
     */
    public static long getNextIntervalTime(long thisTime, int days) {
        Calendar calendar = Calendar.getInstance();
        Date thisDayDate = new Date(thisTime * 1000);
        calendar.setTime(thisDayDate);
        calendar.add(Calendar.DATE, days);
//        calendar.set(Calendar.HOUR_OF_DAY, 24);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;

    }

    /**
     * 获取当前时间的上个月第一天
     * ccc
     * 2017/09/28
     *
     * @return 上个月第一天 00:00:00
     */
    public static long getFirstDayLastMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //获取前月的第一天
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String firstDay = format.format(cal_1.getTime());
        return DatetimeUtils.convertStartDateToTimestamp(firstDay);
    }

    /**
     * 获取当前时间的上个月最后一天
     * ccc
     * 2017/09/28
     *
     * @return 上个月最后一天 23:59:59
     */
    public static long getEndDayLastMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取前月的最后一天
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH, 0);
        String lastDay = format.format(cale.getTime());
        return DatetimeUtils.convertEndDateToTimestamp(lastDay);
    }

    /*
     * 获取当月最后一天的时间戳
     *
     * @return
     */
    public static long getLastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 最后一天
        String lastDayOfMonth = sdf.format(cal.getTime());
        return convertEndDateToTimestamp(lastDayOfMonth);

    }

    /**
     * 获取参数月份的第一天
     * ccc
     * 2017/09/29
     *
     * @return 参数月份第一天 00:00:00 时间戳
     */
    public static long getFirstDayOnMonth(String time) {
        int year = Integer.parseInt(time.split("-")[0].toString());
        int month = Integer.parseInt(time.split("-")[1].toString());
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //设置日历中月份的第1天
        cal.set(Calendar.DAY_OF_MONTH, 1);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());

        return DatetimeUtils.convertStartDateToTimestamp(firstDayOfMonth);
    }

    /*
     * 获取参数月份的最后一天
     * @param 2018-08 或 2018-08-22
     *
     * @return 参数月份最后一天 23:59:59 时间戳
     */
    public static long getLastDayOnMonth(String time) {
        int year = Integer.parseInt(time.split("-")[0].toString());
        int month = Integer.parseInt(time.split("-")[1].toString());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 某年某月的最后一天
        String lastDayOfMonth = sdf.format(cal.getTime());
        return DatetimeUtils.convertEndDateToTimestamp(lastDayOfMonth);

    }

    /**
     * 字符串计算两个日期之间相差的天数
     *
     * @param
     * @return 天数
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 某个日期前后几天日期
     *
     * @param
     * @return 天数
     */
    public static String getAfterNdayDate(String str, int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(str, new ParsePosition(0));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, n);
        Date date1 = calendar.getTime();
        String out = sdf.format(date1);
        return out;
    }

    /**
     * 获取今天23时59点59分的时间戳
     *
     * @return
     */
    public static Long getTodayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 地图0点的时间戳
     *
     * @return
     */
    public static long getTodayBeginDateOfTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }

    public static Long getTodayHourAndMinTime(String format) {
        Calendar calendar = Calendar.getInstance();
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        try {
            return convertDateToTimestamp(time, format);
        } catch (ParseException e) {

        }
        return null;
    }

    /**
     * 获得当前系统时间的前n天或后n天
     *
     * @param offset 日期偏移量
     * @return 查询结果
     */
    public static String getDateStringWithOffset(Integer offset) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, offset);    // 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();
        return sdf.format(date);
    }

    public static String subDateStr(String date) {
        if (Strings.isNullOrEmpty(date)) {
            return date;
        }

        //yyyy-MM-dd HH:mm:ss
        if (date.length() > 10) {
            date = date.substring(0, 10);
        }

        return date;

    }


    /**
     * @param currentTime 时间到秒
     * @return
     */
    public static String formatDate(long currentTime, String format) {
        return DatetimeUtils.convertLongToDate(currentTime * 1000, format);
    }

    /**
     * 获得 当前系统时间（yyyy-MM-dd HH:mm:ss）
     *
     * @return
     */
    public static String getSystemNowTimeStr() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 比较日期相隔天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else {
            return day2 - day1;
        }
    }

    /**
     * @Author：
     * @Description：更加输入日期，获取输入日期的前一天
     * @Date：
     * @strData：参数格式：yyyy-MM-dd
     * @return：返回格式：yyyy-MM-dd
     */
    public static String getPreDateByDate(String strData) {
        String preDate = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strData);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.setTime(date);
        int day1 = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day1 - 1);
        preDate = sdf.format(c.getTime());
        return preDate;
    }
}
