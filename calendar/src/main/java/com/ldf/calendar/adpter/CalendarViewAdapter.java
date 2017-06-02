/*
 * Copyright (c) 2016.
 * wb-lijinwei.a@alibaba-inc.com
 */

package com.ldf.calendar.adpter;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ldf.calendar.MonthPager;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.views.Calendar;

import java.util.ArrayList;

public class CalendarViewAdapter extends PagerAdapter {
	private ArrayList<Calendar> calendars;
	private int offset;

	public CalendarViewAdapter(ArrayList<Calendar> calendars, int offset) {
		super();
		this.calendars = calendars;
		this.offset = offset;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(position < 2){
			return null;
		}
		boolean instantiated = false;
		if (container.getChildCount() == calendars.size()) {
			instantiated = true;
			container.removeView(calendars.get(position % calendars.size()));
		}
		Calendar calendar = calendars.get(position % calendars.size());
		CalendarDate date = new CalendarDate();
		date.modifyCurrentDateMonth(position - MonthPager.CURRENT_DAY_INDEX + offset);
		calendar.showDate(date);
		if(position == MonthPager.CURRENT_DAY_INDEX && !instantiated){
			calendar.showToday();
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

	public ArrayList<Calendar> getAllItems() {
		return calendars;
	}

	public void setCalendars(ArrayList<Calendar> calendars) {
		this.calendars = calendars;
	}

	public void updateAllClickState(){
		for(int i = 0; i < calendars.size(); i++){
			Calendar calendar = calendars.get(i);
			calendar.cancelClickState();
			calendar.updateClickDate();
		}
	}
}
