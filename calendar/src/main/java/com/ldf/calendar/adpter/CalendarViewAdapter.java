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
		for (int i = 0; i < 3; i++) {
			Calendar calendar = new Calendar(context , onSelectDateListener);
			calendars.add(calendar);
		}
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		this.currentPosition = position;
		Log.e("ldf","currentPosition = " + currentPosition);
		super.setPrimaryItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(position < 2){
			return null;
		}
		if (container.getChildCount() == calendars.size()) {
			container.removeView(calendars.get(position % calendars.size()));
		}
		Calendar calendar = calendars.get(position % calendars.size());
		CalendarDate date = new CalendarDate();
		if(calendarType == Calendar.MONTH_TYPE) {
			date.modifyCurrentDateMonth(position - MonthPager.CURRENT_DAY_INDEX);
			calendar.setSelectedRow(0);//月切换的时候选择selected row 为第一行
			calendar.showDate(date);
		} else {
			date.modifyCurrentDateWeek(position - MonthPager.CURRENT_DAY_INDEX);
			Log.e("ldf","week date = " + date.toString());
			calendar.setSelectedRow(rowCount);
			calendar.showDate(Utils.getSunday(date.year , date.month , date.day));
		}

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

	public void updateState(){
		for(int i = 0; i < calendars.size(); i++){
			Calendar calendar = calendars.get(i);
			calendar.cancelClickState();
			calendar.updateClickDate();
		}
	}

	public void updateState(CalendarDate date){
		for(int i = 0; i < calendars.size(); i++){
			Calendar calendar = calendars.get(i);
			calendar.cancelClickState();
			calendar.updateClickDate(date);
		}
	}

    public void setMarkData(HashMap<String, String> markData) {
        Utils.setMarkData(markData);
    }

	public void switchToMonthType() {

	}

	public void switchToWeekType(int rowCount) {

	}
}
