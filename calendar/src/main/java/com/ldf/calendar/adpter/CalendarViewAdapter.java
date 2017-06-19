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

	public CalendarViewAdapter(Context context ,
							   OnSelectDateListener onSelectDateListener ,
							   int calendarType) {
		super();
		this.calendarType = calendarType;
		init(context, onSelectDateListener);
	}

	private void init(Context context, OnSelectDateListener onSelectDateListener) {
		seedDate = new CalendarDate();//初始化的种子日期为今天
        saveDate(seedDate);
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
		Log.e("ldf","setPrimaryItem" + position);
		super.setPrimaryItem(container, position, object);
		this.currentPosition = position;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.e("ldf","instantiateItem");

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
			calendar.updateWeek(rowCount);
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
			calendar.update();
			if(calendar.calendarType == Calendar.WEEK_TYPE) {
				calendar.updateWeek(rowCount);
			}
		}
	}

    public void setMarkData(HashMap<String, String> markData) {
        Utils.setMarkData(markData);
    }

	public void switchToMonth() {
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

	public void switchToWeek(int rowIndex) {
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
			v1.updateWeek(rowIndex);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);
			v2.switchCalendarType(Calendar.WEEK_TYPE);
			CalendarDate last = seedDate.modifyCurrentDateWeek(-1);
			v2.showDate(last);
			v2.updateWeek(rowIndex);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);
			v3.switchCalendarType(Calendar.WEEK_TYPE);
			CalendarDate next = seedDate.modifyCurrentDateWeek(1);
			v3.showDate(next);
			v3.updateWeek(rowIndex);
		}
	}

	public void notifyDataChanged(CalendarDate date){
		saveDate(date);
		if(calendarType == Calendar.WEEK_TYPE) {
			MonthPager.CURRENT_DAY_INDEX = currentPosition;
			Calendar v1 =  calendars.get(currentPosition % 3);
			v1.showDate(date);
			v1.updateWeek(rowCount);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);
			CalendarDate last = date.modifyCurrentDateWeek(-1);
			v2.showDate(last);
			v2.updateWeek(rowCount);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);
			CalendarDate next = date.modifyCurrentDateWeek(1);
			v3.showDate(next);
			v3.updateWeek(rowCount);
		} else {
			MonthPager.CURRENT_DAY_INDEX = currentPosition;

			Calendar v1 =  calendars.get(currentPosition % 3);//0
			v1.showDate(date);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);//2
			CalendarDate last = date.modifyCurrentDateMonth(-1);
			v2.showDate(last);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);//1
			CalendarDate next = date.modifyCurrentDateMonth(1);
			v3.showDate(next);
		}
	}

	public static void saveDate(CalendarDate calendarDate) {
		date = calendarDate;
	}

	public static CalendarDate loadDate() {
		return date;
	}
}
