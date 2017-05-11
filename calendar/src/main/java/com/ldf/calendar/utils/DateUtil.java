/*
 * Copyright (c) 2016.
 * wb-lijinwei.a@alibaba-inc.com
 */

package com.ldf.calendar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.ldf.calendar.model.CalendarDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	private static final String CLICK_DATE= "CLICK_DATE";

	public static final String[] weekName = {   "周一", "周二", "周三", "周四", "周五", "周六", "周日" };

	public static int getMonthDays(int year, int month) {
		if (month > 12) {
			month = 1;
			year += 1;
		} else if (month < 1) {
			month = 12;
			year -= 1;
		}
		int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int days = 0;

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			arr[1] = 29; // 闰年2月29天
		}

		try {
			days = arr[month - 1];
		} catch (Exception e) {
			e.getStackTrace();
		}

		return days;
	}
	
	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static int getDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static boolean isCurrentDay(int day) {
		if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == day){
			return true;
		}
		return false;
	}

	public static int getWeekDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}
	public static int getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}
	public static CalendarDate getNextSunday() {
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7 - getWeekDay()+1);
		CalendarDate date = new CalendarDate(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
		return date;
	}

	public static int[] getWeekSunday(int year, int month, int day, int pervious) {
		int[] time = new int[3];
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.add(Calendar.DAY_OF_MONTH, pervious);
		time[0] = c.get(Calendar.YEAR);
		time[1] = c.get(Calendar.MONTH )+1;
		time[2] = c.get(Calendar.DAY_OF_MONTH);
		return time;

	}

	public static String getWeekDayFromDate(CalendarDate date) {
		Calendar cal = Calendar.getInstance();
		cal.set(date.getYear(), date.getMonth() - 1, date.getDay());
		int week_index = cal.get(Calendar.DAY_OF_WEEK) + 5;
		if (week_index >= 7) {
			week_index -= 7;
		}
		return weekName[week_index];
	}

	public static int getWeekDayFromDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFromString(year, month));
		int week_index = cal.get(Calendar.DAY_OF_WEEK) + 5;
		if (week_index >= 7) {
			week_index -= 7;
		}
		return week_index;
	}

	public static int getWeekDayFromDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day);
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 2;
		if (week_index < 0) {
			week_index = 0;
		}
		return week_index;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(int year, int month) {
		String dateString = year + "-" + (month > 9 ? month : ("0" + month))
				+ "-01";
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return date;
	}
	
	public static boolean isCurrentMonth(CalendarDate date){
		return(date.year == DateUtil.getYear() &&
				date.month == DateUtil.getMonth());
	}

	public static void saveClickDate(Context context, CalendarDate calendarDate) {
		SharedPreferences sp = context.getSharedPreferences(CLICK_DATE, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt(CLICK_DATE + "YEAR", calendarDate.getYear());
		edit.putInt(CLICK_DATE + "MONTH", calendarDate.getMonth());
		edit.putInt(CLICK_DATE + "DAY", calendarDate.getDay());
		edit.commit();
	}

	public static CalendarDate getClickDate(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CLICK_DATE, Context.MODE_PRIVATE);
		CalendarDate defaultDate = new CalendarDate();
		int defaultYear = defaultDate.getYear();
		int defaultMonth = defaultDate.getMonth();
		int defaultDay = defaultDate.getDay();
		int year = sp.getInt(CLICK_DATE + "YEAR", defaultYear);
		int month = sp.getInt(CLICK_DATE + "MONTH", defaultMonth);
		int day = sp.getInt(CLICK_DATE + "DAY", defaultDay);
		return new CalendarDate(year, month, day);
	}

	public static int calculateMonthOffset(int year, int month, CalendarDate currentDate) {
		int currentYear = currentDate.getYear();
		int currentMonth = currentDate.getMonth();
		int offset = (year - currentYear) * 12 + (month - currentMonth);
		return offset;
	}
}
