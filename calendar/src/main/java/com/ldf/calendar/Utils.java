/*
 * Copyright (c) 2016.
 * wb-lijinwei.a@alibaba-inc.com
 */

package com.ldf.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.ldf.calendar.model.CalendarDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Utils {
	public static final String[] weekName = {   "周一", "周二", "周三", "周四", "周五", "周六", "周日" };

	private static HashMap<String , String> markData = new HashMap<>();

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

	public static int getWeekDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	public static CalendarDate getNextSunday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7 - getWeekDay() + 1);
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

	public static boolean isToday(CalendarDate date , int day) {
		return (date.year == Utils.getYear() &&
				date.month == Utils.getMonth() && day == Utils.getDay());
	}

	public static int calculateMonthOffset(int year, int month, CalendarDate currentDate) {
		int currentYear = currentDate.getYear();
		int currentMonth = currentDate.getMonth();
		int offset = (year - currentYear) * 12 + (month - currentMonth);
		return offset;
	}

	public static int dpi2px(Context context, float dpi) {
		return (int) (context.getResources().getDisplayMetrics().density * dpi + 0.5f);
	}

	public static HashMap<String, String> getMarkData() {
		return markData;
	}

	public static void setMarkData(HashMap<String, String> imarkData) {
		markData = imarkData;
	}

	private static int offsetMaxAndMin(int value, int min, int max) {
		if (value > max) {
			return max;
		} else if (value < min) {
			return min;
		} else {
			return value;
		}
	}

	public static int scroll(View child, int dy, int minOffset, int maxOffset) {
		final int initOffset = child.getTop();
		int delta = offsetMaxAndMin(initOffset - dy, minOffset, maxOffset) - initOffset;
		child.offsetTopAndBottom(delta);
		return -delta;
	}

	public static int getTouchSlop(Context context) {
		return ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public static CalendarDate getSunday(int year, int month, int day) {// TODO: 16/12/12 得到一个CustomDate对象
		Calendar c = Calendar.getInstance();
		String dateString = year + "-" + month + "-" + day;
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		c.setTime(date);
		if(c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			c.add(Calendar.DAY_OF_MONTH, 7 - c.get(Calendar.DAY_OF_WEEK) + 1);
		}
		return new CalendarDate(c.get(Calendar.YEAR) ,
				c.get(Calendar.MONTH) + 1 ,
				c.get(Calendar.DAY_OF_MONTH));
	}

	private static boolean isGoingUp;
	private static int top;

	public static void scrollTo(final CoordinatorLayout parent, final RecyclerView child, final int y, int duration){
		final Scroller scroller = new Scroller(parent.getContext());
		scroller.startScroll(0, top, 0, y - top, duration);   //设置scroller的滚动偏移量

		ViewCompat.postOnAnimation(child, new Runnable() {
			@Override
			public void run() {
				//返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。
				// 这是一个很重要的方法，通常放在View.computeScroll()中，用来判断是否滚动是否结束。
				if (scroller.computeScrollOffset()) {
					int delta = scroller.getCurrY() - child.getTop();
					child.offsetTopAndBottom(delta);

					saveTop(child.getTop());
					parent.dispatchDependentViewsChanged(child);

					ViewCompat.postOnAnimation(child, this);
				}
			}
		});
	}


	private static void saveTop(int y){
		top = y;

//		if (top == initOffset){
//			isGoingUp = true;
//		} else if (top == minOffset){
//			isGoingUp = false;
//		}
	}
}
