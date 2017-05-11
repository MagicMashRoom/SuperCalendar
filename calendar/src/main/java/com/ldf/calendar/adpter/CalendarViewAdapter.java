/*
 * Copyright (c) 2016.
 * wb-lijinwei.a@alibaba-inc.com
 */

package com.ldf.calendar.adpter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ldf.calendar.MonthPager;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.views.Calendar;

public class CalendarViewAdapter<V extends View> extends PagerAdapter {
	private V[] views;
	private int offset;

	public CalendarViewAdapter(V[] views, int offset) {
		super();
		this.views = views;
		this.offset = offset;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(position < 2){
			return null;
		}
		boolean isInitToday = true;
		if (container.getChildCount() == views.length) {
			isInitToday = false;
			container.removeView(views[position % views.length]);
		}
		View view = views[position % views.length];
		if(view instanceof Calendar){
			((Calendar) view).setShowCurrentDate(CalendarDate.modifyCurrentDateMonth(new CalendarDate(),
					position - MonthPager.CURRENT_DAY_INDEX + offset));
			if(position == MonthPager.CURRENT_DAY_INDEX && isInitToday){
				((Calendar) view).setInitCurrentDate();
			}
		}
		container.addView(view, 0);
		return view;
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

	public V[] getAllItems() {
		return views;
	}

	public void setViews(V[] views) {
		this.views = views;
	}

	public void updateAllClickState(){
		for(int i = 0; i < views.length; i++){
			Calendar calendar = (Calendar) views[i];
			calendar.cancelClickState();
			calendar.updateClickDate();
		}
	}
}
