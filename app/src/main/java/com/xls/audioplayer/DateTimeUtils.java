package com.xls.audioplayer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/4/9.
 */

public class DateTimeUtils {


    private static final String TAG = "DateTimeUtils";
    /**
     * 通过年月日值拼接成字符串
     * @param year
     * @param month
     * @return
     */
    public static String getDateStr(int year, int month){
        String monthStr = "";
        if(month<10){
            monthStr = "0"+month;
        }else {
            monthStr = String.valueOf(month);
        }
        return year+"/"+monthStr;
    }

    /**
     * 通过年月日值拼接成字符串
     * @param year
     * @param month
     * @return
     */
    public static String getDateStrForCalendar(int year, int month){
        String monthStr = "";
        if(month<10){
            monthStr = "0"+month;
        }else {
            monthStr = String.valueOf(month);
        }
        return year+"."+monthStr;
    }

    //格式化日期：如 2018/04
    public static String formatDateForHistoryList(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        return sdf.format(date);
    }

    //格式化日期：如 2018-04
    public static String formatDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(date);
    }


    /**
     * 转换成历史课程列表所需要的格式
     * @param lesStartTime
     * @param lesEndTime
     * @return 2018.05.22 21:20-21:45
     */
    public static String getHistoryListDateStr(long lesStartTime, long lesEndTime){

        if(lesStartTime==0 || lesEndTime==0){
            return "";
        }

        SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        SimpleDateFormat sdfEnd = new SimpleDateFormat("HH:mm");

        String start = sdfStart.format(new Date(lesStartTime));
        String end = sdfEnd.format(new Date(lesEndTime));
        return start+"-"+end;


    }

    //判断是否是同一天
    public static boolean isSameDay(long time,int year,int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));

        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH)+1;
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        if(y==year && m==month && d==day){
            return true;
        }else {
            return false;
        }

    }


    //格式化日期为"2018.04.10"
    public static String formatDateCourse(long time){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String str = sdf.format(new Date(time));
        return str;
    }



    //格式化时间为"15:00"
    public static String formatTimeCourse(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(time));
    }

    //格式化时间为"2018-04-10 15:00"
    public static String formatDateTime(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(time));
    }

    //格式化时间为"2018-04-10 15:00:00"
    public static String formatDateTimeSec(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }


    //判断上课时间是否到了（离当前时间20分钟内）
    public static boolean isTimeUp(long lesStartTime,long lesEndTime){
       long now = System.currentTimeMillis();
       if(now >= lesStartTime-20*60*1000 && now <=lesEndTime){
           return true;
       }else {
           return false;
       }
    }

    /**
     * 课程是否已经结束了
     * @param lesEndTime
     * @return
     */
    public static boolean isTimeOver(long lesEndTime){
        long now = System.currentTimeMillis();
        if(now>lesEndTime){
            return true;
        }else {
            return false;
        }
    }


    /**
     * 课时，分转为小时
     * @param minutes
     * @return
     */
    public static String getHourByMinutes(int minutes){

        return new DecimalFormat("0.##").format(minutes/60.0);
    }


    /**
     * 获取当前日期月份的开始时间
     * @return
     */
    public static Calendar getStartCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        return calendar;
    }

    /**
     * 获取当前日期月份的截止时间
     * @return
     */
    public static Calendar getEndCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,11);
        calendar.set(Calendar.DAY_OF_MONTH,31);
        return calendar;
    }

    /**
     * 消息相关时间格式化
     * @param time
     * @return
     */
    public static String getMsgFormatTime(long time){
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH,-1);
        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DAY_OF_MONTH,-7);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(time);
        calendar.setTime(date);
        if(calendar.getTimeInMillis()>weekAgo.getTimeInMillis()){
            if(today.get(Calendar.YEAR)==calendar.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)){
                //是今天
                return "今天 "+new SimpleDateFormat("HH:mm").format(date);
            }else if(yesterday.get(Calendar.YEAR)==calendar.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)){
                //昨天
                return "昨天 "+new SimpleDateFormat("HH:mm").format(date);
            }else {
                int week = calendar.get(Calendar.DAY_OF_WEEK);
                String weekStr = "";
                switch (week){
                    case Calendar.SUNDAY:
                        weekStr = "周日 ";
                        break;
                    case Calendar.MONDAY:
                        weekStr = "周一 ";
                        break;
                    case Calendar.TUESDAY:
                        weekStr = "周二 ";
                        break;
                    case Calendar.WEDNESDAY:
                        weekStr = "周三 ";
                        break;
                    case Calendar.THURSDAY:
                        weekStr = "周四 ";
                        break;
                    case Calendar.FRIDAY:
                        weekStr = "周五 ";
                        break;
                    case Calendar.SATURDAY:
                        weekStr = "周六 ";
                        break;
                }
                return weekStr+new SimpleDateFormat("MM.dd HH:mm").format(date);
            }
        }else {
            return new SimpleDateFormat("MM.dd HH:mm").format(date);
        }

    }

    /**
     * 录音的起止时间格式化,相对于上课开始的时间,如：00:20:32,单位,秒
     * @param time
     * @return
     */
    public static String getRecordTime(int time){
        String hour = "00";
        String minute = "00";
        String second = "00";
        if(time<60){
            //小于60秒
            second = fillUpZero(time);
        }else if(time>=60 && time<3600){
            //大于等于1分钟，小于一个小时
            int min = time/60;
            int sec = time%60;
            minute = fillUpZero(min);
            second = fillUpZero(sec);
        }else {
            //大于一个小时
            int h  = time/3600;
            int min_remain = time%3600;
            int min = min_remain/60;
            int sec = min_remain%60;
            hour = fillUpZero(h);
            minute = fillUpZero(min);
            second = fillUpZero(sec);
        }
        return hour+":"+minute+":"+second;
    }

    public static String formatRecordTime(int time){
        String minute = "00";
        String second = "00";
        if(time<60){
            //小于60秒
            second = fillUpZero(time);
        }else if(time>=60 && time<3600){
            //大于等于1分钟，小于一个小时
            int min = time/60;
            int sec = time%60;
            minute = fillUpZero(min);
            second = fillUpZero(sec);
        }else {
            //大于一个小时

        }
        return minute+":"+second;
    }

    /**
     * 给一个单位的时间数，通过补0，转化为字符串
     * @param time ,单位可能是 时,分,秒
     * @return
     */
    public static String fillUpZero(int time){
        String str = "00";
        if(time>=10){
            str = String.valueOf(time);
        }else {
            str = "0"+time;
        }
        return str;
    }


}
