/**
 *
 */
package io.github.treeyw.crud.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DateTool {


    /**
     * @Description //日期相减
     * @Author treeyw
     * @Date 2019/10/18 11:17
     **/
    public static String dateAdd2(String yDate, String clazz, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(str2Date(yDate));
        if (clazz.equals("Y")) calendar.add(Calendar.YEAR, num);
        if (clazz.equals("M")) calendar.add(Calendar.MONTH, num);
        if (clazz.equals("D")) calendar.add(Calendar.DAY_OF_MONTH, num);
        if (clazz.equals("W")) calendar.add(Calendar.DAY_OF_MONTH, num * 7);
        return dateTimeSec2Str(calendar.getTime());
    }

    /**
     * 获取当天的开始时间
     *
     * @Author treeyw
     * @Date 2021/1/27 18:15
     * @Parameter --
     **/
    public static Date dayStart(Date date) {
        return str2DateTimeSec(date2Str(date) + " 00:00:00");
    }

    /**
     * 获取当天的结束时间
     *
     * @Author treeyw
     * @Date 2021/1/27 18:15
     * @Parameter --
     **/
    public static Date dayEnd(Date date) {
        return str2DateTimeSec(date2Str(date) + " 23:59:59");
    }

    public static Date dateAdd2(Date yDate, String clazz, int num) {
        return str2DateTimeSec(dateAdd2(dateTimeSec2Str(yDate), clazz, num));
    }

    /**
     * @Description //日期相减
     * @Author treeyw
     * @Date 2019/10/18 11:17
     **/
    public static long dateSub2days(String yDate, String jDAte) {
        long y = str2Date(yDate).getTime();
        long j = str2Date(jDAte).getTime();

        return (y - j) / 1000 / 60 / 60 / 24;
    }

    public static long dateSub2days(Date yDate, Date jDAte) {
        return (yDate.getTime() - jDAte.getTime()) / 1000 / 60 / 60 / 24;
    }

    public static long dateSub2hours(String yDate, String jDAte) {
        long y = str2DateTimeSec(yDate).getTime();
        long j = str2DateTimeSec(jDAte).getTime();
        return (y - j) / 1000 / 60 / 60;
    }

    public static long dateSub2hours(Date yDate, Date jDAte) {
        return (yDate.getTime() - jDAte.getTime()) / 1000 / 60 / 60;
    }





    public DateTool() {
    }

    public static String longDate2Str(Long l) {
        return dateTimeSec2Str(new Date(l));
    }

    public static Date str2Date(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(date);
        } catch (Exception err) {
            return null;
        }
    }

    /**
     * 将日期格式化为指定日期格式
     * @param date - 要格式化的日期
     * @param formatStr - 格式化后的日期格式，如：yyyy-MM-dd HH:mm:ss
     * @return 格式化后的日期
     */
    public static String dateToString(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static Date str2DateTime(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.parse(date);
        } catch (Exception err) {
            return null;
        }
    }

    public static Date str2DateTimeSec(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(date);
        } catch (Exception err) {
            return null;
        }
    }


    public static String date2Str(Date date) {
        if ( StringUtils.isEmpty(date) ) return null;
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd");
        return simpledate.format(date);
    }


    public static String dateTime2Str(Date date) {
        if ( StringUtils.isEmpty(date) ) return null;
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpledate.format(date);
    }

    public static String dateTime2String(Date date) {
        if ( StringUtils.isEmpty(date) ) return null;
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpledate.format(date);
    }

    public static String dateTimeSec2Str(Date date) {
        if ( StringUtils.isEmpty(date) ) return null;
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpledate.format(date);
    }

    public static String stampToTime(String time) {
        String res;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long lt = Long.parseLong(time);

        //将时间戳转换为时间

        Date date = new Date(lt);

        //将时间调整为yyyy-MM-dd HH:mm:ss时间样式

        res = simpleDateFormat.format(date);

        return res;
    }

    public static int getYear() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        int year = ca.get(Calendar.YEAR);
        return year;
    }

    public static int getMonth() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        int month = ca.get(Calendar.MONTH);
        return month;
    }

    public static int getDay() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        int day = ca.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public static int getYear(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int year = ca.get(Calendar.YEAR);
        return year;
    }

    public static int getMonth(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int month = ca.get(Calendar.MONTH);
        return month;
    }

    public static int getDay(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int day = ca.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public static int getHourOfDay() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getDistanceDay(Date date1, Date date2) {
        int distanceDay = 0;
        if (date1 != null && date2 != null) {
            Calendar ca1 = Calendar.getInstance();
            Calendar ca2 = Calendar.getInstance();
            ca1.setTime(date1);
            ca2.setTime(date2);
            int year1 = ca1.get(Calendar.YEAR);
            int year2 = ca2.get(Calendar.YEAR);
            if (year1 != year2) {
                if ((year2 - year1) >= 2) {
                    for (int i = year1 + 1; i < year2; i++) {
                        if (isLeapYear(i)) {
                            distanceDay = distanceDay + 366;
                        } else {
                            distanceDay = distanceDay + 365;
                        }
                    }
                }
                if (isLeapYear(year1)) {
                    distanceDay = distanceDay + 366 - ca1.get(Calendar.DAY_OF_YEAR) + ca2.get(Calendar.DAY_OF_YEAR);
                } else {
                    distanceDay = distanceDay + 365 - ca1.get(Calendar.DAY_OF_YEAR) + ca2.get(Calendar.DAY_OF_YEAR);
                }
            } else {
                distanceDay = ca2.get(Calendar.DAY_OF_YEAR) - ca1.get(Calendar.DAY_OF_YEAR);
            }
        }
        return distanceDay;
    }


    public static long getDistanceMinute(Date date1, Date date2) throws Exception {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date begin = dfs.parse(dateTime2Str(date1));
        Date end = dfs.parse(dateTime2Str(date2));
        long between = end.getTime() - begin.getTime();
        between = (between / 1000) / 60;
        return between;
    }

    public static long getDistanceSecond(Date date1, Date date2) throws Exception {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = dfs.parse(dateTimeSec2Str(date1));
        Date end = dfs.parse(dateTimeSec2Str(date2));
        long between = end.getTime() - begin.getTime();
        between = between / 1000;
        return between;
    }


    public static boolean isLeapYear(int year) {
        boolean isLeapYear = true;
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            isLeapYear = true;
        } else {
            isLeapYear = false;
        }
        return isLeapYear;
    }

    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public static Date getHourOfDay(Date date, int hour) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.HOUR_OF_DAY, hour);
        return ca.getTime();
    }


    public static Date getComputeDate(int days) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_YEAR, days);
        return ca.getTime();
    }


    public static Date getComputeDate(Date date, int days) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DAY_OF_YEAR, days);
        return ca.getTime();
    }


    public static Date getComputeYear(int years) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.YEAR, years);
        return ca.getTime();
    }


    public static Date getComputeMonth(int months) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.MONTH, months);
        return ca.getTime();
    }

    public static Date getComputeMonth(Date date, int months) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, months);
        return ca.getTime();
    }

    public static int getDayOfWeek(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取一段时间内的时间
     * @param dateFormStr	日期格式
     * @param startDateStr	开始日期
     * @return
     */
    public static List<Map<String,Object>> getDuringDate(String dateFormStr ,String startDateStr){
        List<Map<String,Object>> result = new ArrayList<>();
        try{
            SimpleDateFormat format = new SimpleDateFormat(dateFormStr);
            String nowdate=format.format(new Date());//当前月份
            Date d1 = new SimpleDateFormat(dateFormStr).parse(startDateStr);//定义起始日期
            Date d2 = new SimpleDateFormat(dateFormStr).parse(nowdate);//定义结束日期  可以去当前月也可以手动写日期。
            Calendar dd = Calendar.getInstance();//定义日期实例
            dd.setTime(d1);//设置日期起始时间
            while (dd.getTime().before(d2)) {//判断是否到结束日期
                Map<String,Object> mapInner = new HashMap<>();
                SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");
                String strY = sdfY.format(dd.getTime());
                SimpleDateFormat sdfM = new SimpleDateFormat("MM");
                String strM = sdfM.format(dd.getTime());
                mapInner.put("submitYear",strY);
                mapInner.put("submitMonth",strM);
                result.add(mapInner);
                dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
            }
        }catch (Exception e){
            System.out.println("异常"+e.getMessage());
        }
        return result;
    }

    /**
     * 将日期格式字符串转换为Date对象
     *
     * @param dateStr - 日期格式字符串，如：2017-07-07 15:36:43
     * @param pattern - 日期格式，如：yyyy-MM-dd HH:mm:ss
     * @return 转换后的Date对象
     */
    public static Date stringToDate(String dateStr, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date date = format.parse(dateStr);
            return date;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析时间
     * @param date
     * @return
     */
    public static Date extractDate(String date) {
        if (StringUtils.isEmpty(date)) return null;

        try{
            //判断是否有多个
            if (date.contains(",")) {
                String[] s = date.split(",");
                date = s[0];
            }
            if (date.contains(";")) {
                String[] s = date.split(";");
                date = s[0];
            }
            //判断是否有空格
            date = date.trim();
            if (date.length() == 8){
                if (date.contains("."))  return DateTool.stringToDate(date, "yyyy.M.d");
                else if (date.contains("-"))  return DateTool.stringToDate(date, "yyyy-M-d");
                else if (date.contains("/"))  return DateTool.stringToDate(date, "yyyy/M/d");
                else return DateTool.stringToDate(date, "yyyyMMdd");
            }

            if (date.length() == 9) {
                if (date.contains("-")) {
                    Date d9 = DateTool.stringToDate(date, "yyyy-M-dd");
                    if (null != d9) return d9;
                    return DateTool.stringToDate(date, "yyyy-MM-d");
                }else if (date.contains(".")) {
                    Date d9 = DateTool.stringToDate(date, "yyyy.M.dd");
                    if (null != d9) return d9;
                    return DateTool.stringToDate(date, "yyyy.MM.d");
                }else if (date.contains("/")) {
                    Date d9 = DateTool.stringToDate(date, "yyyy/M/dd");
                    if (null != d9) return d9;
                    return DateTool.stringToDate(date, "yyyy/MM/d");
                }
            }

            if (date.length() == 10){
                if (date.contains("-") )  return DateTool.stringToDate(date, "yyyy-MM-dd");
                else if (date.contains(".") )  return DateTool.stringToDate(date, "yyyy.MM.dd");
                else if (date.contains("/"))  return DateTool.stringToDate(date, "yyyy/MM/dd");
            }

            if (date.length() == 17){
                if (date.contains("-") )  return DateTool.stringToDate(date, "yyyy-M-d HH:mm:ss");
                else if (date.contains(".") )  return DateTool.stringToDate(date, "yyyy.M.d HH:mm:ss");
                else if (date.contains("/"))  return DateTool.stringToDate(date, "yyyy/M/d HH:mm:ss");
            }
            if (date.length() == 18){
                if (date.contains("-") ) return DateTool.stringToDate(date, "yyyy-M-dd HH:mm:ss");
                else if (date.contains(".") )  return DateTool.stringToDate(date, "yyyy.M.dd HH:mm:ss");
                else if (date.contains("/"))  return DateTool.stringToDate(date, "yyyy/M/dd HH:mm:ss");
            }

            if (date.length() == 19 ){
                if ( date.contains("-") )  return DateTool.stringToDate(date, "yyyy-MM-dd HH:mm:ss");
                else if ( date.contains("/") )  return DateTool.stringToDate(date, "yyyy/MM/dd HH:mm:ss");
                else if ( date.contains(".") )  return DateTool.stringToDate(date, "yyyy.MM.dd HH:mm:ss");
            }
        }catch (Exception e) {
            System.out.println("DateTool.extractDate方法解析时间失败，解析内容【" + date + "】，异常信息：" + e.getMessage());
        }
        return null;
    }


    /**
     * 获取某年某月的第一天
     * @param year
     * @param month
     * @return
     */
    public static String getActualMinimum(int year, int month) {
        if(year < 0 || month < 1) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        //Calendar月份从0开始
        cal.set(Calendar.MONTH, month - 1);
        //方便查看
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取year年month月的开始一天
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置Calendar为指定月份的第一天
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //输出日期--指定月份第一天
        return sdf.format(cal.getTime());
    }

    /**
     * 获取某年某月的最后一天
     * @param year
     * @param month
     * @return
     */
    public static String getActualMaximum(int year, int month) {
        if(year < 0 || month < 1) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        //Calendar月份从0开始
        cal.set(Calendar.MONTH, month - 1);
        //方便查看
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //输出日期--指定月份最后一天
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return sdf.format(cal.getTime());
    }


    /**
     * 获取几天前的时间
     * @param date
     * @param num
     * @return
     */
    public static String getFewDaysAgo(Date date,int num){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -num);
        date = calendar.getTime();
        System.out.println(sdf.format(date));
        return sdf.format(date);
    }




    public static void main(String[] args) {

        long l = dateSub2hours("2021-10-25 12:00:00", "2021-10-24 00:00:00");
        System.out.println(l);

    }
}
