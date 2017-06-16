/*
 * Copyright (c) 2016.
 * wb-lijinwei.a@alibaba-inc.com
 */

package com.ldf.calendar.adpter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ldf.calendar.listener.OnSelectDateListener;
import com.ldf.calendar.Utils;
import com.ldf.calendar.view.MonthPager;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarViewAdapter extends PagerAdapter {
	private static CalendarDate date = new CalendarDate();
	private ArrayList<Calendar> calendars = new ArrayList<>();
	private int currentPosition;
	private int calendarType = Calendar.MONTH_TYPE;
	private int rowCount = 0;
	private CalendarDate seedDate;

	public static void setDate(CalendarDate calendarDate) {
		date = calendarDate;
	}

	public static CalendarDate getDate() {
		return date;
	}

	public CalendarViewAdapter(Context context , OnSelectDateListener onSelectDateListener) {
		super();
		init(context, onSelectDateListener);
	}

	private void init(Context context, OnSelectDateListener onSelectDateListener) {
		seedDate = new CalendarDate();//初始化的种子日期为今天
		for (int i = 0; i < 3; i++) {
			Calendar calendar = new Calendar(context , onSelectDateListener);
			calendar.setOnAdapterSelectListener(new OnAdapterSelectListener() {
				@Override
				public void cancelSelectState() {
					cancelAllSelectState();
				}

				@Override
				public void updateSelectState() {
					updateAllSelectState();
				}
			});
			calendars.add(calendar);
		}
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		this.currentPosition = position;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(position < 2){
			return null;
		}
		if (container.getChildCount() == calendars.size()) {
			container.removeView(calendars.get(position % 3));
		}
		Calendar calendar = calendars.get(position % calendars.size());
		if(calendarType == Calendar.MONTH_TYPE) {
			CalendarDate current = seedDate.modifyCurrentDateMonth(position - MonthPager.CURRENT_DAY_INDEX);
			calendar.resetSelectedRowIndex();//月切换的时候选择selected row 为第一行
			calendar.showDate(current);
		} else {
			CalendarDate current = seedDate.modifyCurrentDateWeek(position - MonthPager.CURRENT_DAY_INDEX);
			calendar.showDate(Utils.getSunday(current.year , current.month , current.day));
			calendar.instantiateWeek(rowCount);
		}
		calendar.getCellHeight();

		container.addView(calendar, 0);
		return calendar;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(container);
	}

	public ArrayList<Calendar> getPagers() {
		return calendars;
	}

	public void cancelAllSelectState(){
		for(int i = 0; i < calendars.size(); i++){
			Calendar calendar = calendars.get(i);
			calendar.cancelSelectState();
		}
	}

	public void updateAllSelectState(){
		for(int i = 0; i < calendars.size(); i++){
			Calendar calendar = calendars.get(i);
			if(i != (currentPosition % 3)) {
				calendar.updateSelectDate();//不刷新正在展示月的状态
				Log.e("ldf","update show date = " + calendar.getShowCurrentDate().toString());
			} else {
				Log.e("ldf","show date = " + calendar.getShowCurrentDate().toString());
			}
		}
	}

	public void updateDate(CalendarDate date){
		for(int i = 0; i < calendars.size(); i++){
			Calendar calendar = calendars.get(i);
			calendar.cancelSelectState();
			calendar.updateSelectDate(date);
		}
	}

    public void setMarkData(HashMap<String, String> markData) {
        Utils.setMarkData(markData);
    }

	public void switchToMonthType() {
		if(calendars != null && calendars.size() > 0 && calendarType != Calendar.MONTH_TYPE){
			calendarType = Calendar.MONTH_TYPE;
			MonthPager.CURRENT_DAY_INDEX = currentPosition;
			Calendar v = calendars.get(currentPosition % 3);//0
			seedDate = v.getShowCurrentDate();

			Calendar v1 =  calendars.get(currentPosition % 3);//0
			v1.switchCalendarType(Calendar.MONTH_TYPE);
			v1.showDate(seedDate);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);//2
			v2.switchCalendarType(Calendar.MONTH_TYPE);
			CalendarDate last = seedDate.modifyCurrentDateMonth(-1);
			v2.showDate(last);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);//1
			v3.switchCalendarType(Calendar.MONTH_TYPE);
			CalendarDate next = seedDate.modifyCurrentDateMonth(1);
			v3.showDate(next);
		}
	}

	public void switchToWeekType(int rowIndex) {
		rowCount = rowIndex;
		Log.e("ldf","rowIndex = " + rowIndex);
		if(calendars != null && calendars.size() > 0 && calendarType != Calendar.WEEK_TYPE){
			calendarType = Calendar.WEEK_TYPE;
			MonthPager.CURRENT_DAY_INDEX = currentPosition;
			Calendar v = calendars.get(currentPosition % 3);
			seedDate = v.getShowCurrentDate();

			Calendar v1 =  calendars.get(currentPosition % 3);
			v1.switchCalendarType(Calendar.WEEK_TYPE);
			v1.showDate(seedDate);
			v1.instantiateWeek(rowIndex);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);
			v2.switchCalendarType(Calendar.WEEK_TYPE);
			CalendarDate last = seedDate.modifyCurrentDateWeek(-1);
			v2.showDate(last);
			v2.instantiateWeek(rowIndex);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);
			v3.switchCalendarType(Calendar.WEEK_TYPE);
			CalendarDate next = seedDate.modifyCurrentDateWeek(1);
			v3.showDate(next);
			v3.instantiateWeek(rowIndex);

		}
	}
}
