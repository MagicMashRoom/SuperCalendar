package com.ldf.calendar.component;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ldf.calendar.Const;
import com.ldf.calendar.interf.OnAdapterSelectListener;
import com.ldf.calendar.interf.IDayRenderer;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.Utils;
import com.ldf.calendar.view.MonthPager;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.Week;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarViewAdapter extends PagerAdapter {
	public static int weekArrayType = 0;//周排列方式 1 express Sunday as the first day of week

	private static CalendarDate date = new CalendarDate();
	private ArrayList<Calendar> calendars = new ArrayList<>();
	private int currentPosition;
	private CalendarAttr.CalendayType calendarType = CalendarAttr.CalendayType.MONTH;
	private int rowCount = 0;
	private CalendarDate seedDate;

	public CalendarViewAdapter(Context context ,
							   OnSelectDateListener onSelectDateListener ,
							   CalendarAttr.CalendayType calendarType ,
							   IDayRenderer dayView) {
		super();
		this.calendarType = calendarType;
		init(context, onSelectDateListener);
		setCustomDayView(dayView);
	}

	private void init(Context context, OnSelectDateListener onSelectDateListener) {
		seedDate = new CalendarDate();//初始化的种子日期为今天
		Log.e("ldf","init saveDate = " + seedDate.toString());
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
		if(calendarType == CalendarAttr.CalendayType.MONTH) {
			CalendarDate current = seedDate.modifyMonth(position - MonthPager.CURRENT_DAY_INDEX);
			calendar.resetSelectedRowIndex();//月切换的时候选择selected row 为第一行
			calendar.showDate(current);
		} else {
			CalendarDate current = seedDate.modifyWeek(position - MonthPager.CURRENT_DAY_INDEX);
			if(weekArrayType == 1) {
				calendar.showDate(Utils.getSaturday(current.year , current.month , current.day));
			} else {
				calendar.showDate(Utils.getSunday(current.year , current.month , current.day));
			}
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
			if(calendar.getCalendarType() == CalendarAttr.CalendayType.WEEK) {
				Log.e("ldf","updateWeek");
				calendar.updateWeek(rowCount);
			}
		}
	}

	public void setMarkData(HashMap<String, String> markData) {
		Utils.setMarkData(markData);
	}

	public void switchToMonth() {
		if(calendars != null && calendars.size() > 0 && calendarType != CalendarAttr.CalendayType.MONTH){
			calendarType = CalendarAttr.CalendayType.MONTH;
			MonthPager.CURRENT_DAY_INDEX = currentPosition;
			Calendar v = calendars.get(currentPosition % 3);//0
			seedDate = v.getSeedDate();

			Calendar v1 =  calendars.get(currentPosition % 3);//0
			v1.switchCalendarType(CalendarAttr.CalendayType.MONTH);
			v1.showDate(seedDate);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);//2
			v2.switchCalendarType(CalendarAttr.CalendayType.MONTH);
			CalendarDate last = seedDate.modifyMonth(-1);
			v2.showDate(last);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);//1
			v3.switchCalendarType(CalendarAttr.CalendayType.MONTH);
			CalendarDate next = seedDate.modifyMonth(1);
			v3.showDate(next);
		}
	}

	public void switchToWeek(int rowIndex) {
		rowCount = rowIndex;
		if(calendars != null && calendars.size() > 0 && calendarType != CalendarAttr.CalendayType.WEEK){
			calendarType = CalendarAttr.CalendayType.WEEK;
			MonthPager.CURRENT_DAY_INDEX = currentPosition;
			Calendar v = calendars.get(currentPosition % 3);
			seedDate = v.getSeedDate();

			Calendar v1 =  calendars.get(currentPosition % 3);
			v1.switchCalendarType(CalendarAttr.CalendayType.WEEK);
			v1.showDate(seedDate);
			v1.updateWeek(rowIndex);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);
			v2.switchCalendarType(CalendarAttr.CalendayType.WEEK);
			CalendarDate last = seedDate.modifyWeek(-1);
			v2.showDate(last);
			v2.updateWeek(rowIndex);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);
			v3.switchCalendarType(CalendarAttr.CalendayType.WEEK);
			CalendarDate next = seedDate.modifyWeek(1);
			v3.showDate(next);
			v3.updateWeek(rowIndex);
		}
	}

	public void notifyDataChanged(CalendarDate date){
		seedDate = date;
		Log.e("ldf","notifyDataChanged saveDate = " + seedDate.toString());
		saveDate(date);
		if(calendarType == CalendarAttr.CalendayType.WEEK) {
			MonthPager.CURRENT_DAY_INDEX = currentPosition;
			Calendar v1 =  calendars.get(currentPosition % 3);
			v1.showDate(date);
			v1.updateWeek(rowCount);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);
			CalendarDate last = date.modifyWeek(-1);
			v2.showDate(last);
			v2.updateWeek(rowCount);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);
			CalendarDate next = date.modifyWeek(1);
			v3.showDate(next);
			v3.updateWeek(rowCount);
		} else {
			MonthPager.CURRENT_DAY_INDEX = currentPosition;

			Calendar v1 =  calendars.get(currentPosition % 3);//0
			v1.showDate(date);

			Calendar v2 = calendars.get((currentPosition - 1) % 3);//2
			CalendarDate last = date.modifyMonth(-1);
			v2.showDate(last);

			Calendar v3 = calendars.get((currentPosition + 1) % 3);//1
			CalendarDate next = date.modifyMonth(1);
			v3.showDate(next);
		}
	}

	public static void saveDate(CalendarDate calendarDate) {
		date = calendarDate;
	}

	public static CalendarDate loadDate() {
		return date;
	}

	public CalendarAttr.CalendayType getCalendarType() {
		return calendarType;
	}

	public void setCustomDayView(IDayRenderer dayRenderer) {
		Week weeks0[] = new Week[Const.TOTAL_ROW];
		for (int j = 0 ; j < 6 ; j ++) {
			Week week = new Week(j);
			IDayRenderer[] days = new IDayRenderer[Const.TOTAL_COL];
			for (int i = 0 ; i < 7 ; i ++) {
				days[i] = dayRenderer.copy();
			}
			week.setDays(days);
			weeks0[j] = week;
		}
		Calendar c0 =  calendars.get(0);
		c0.setWeeks(weeks0);

		Week weeks1[] = new Week[Const.TOTAL_ROW];
		for (int j = 0 ; j < 6 ; j ++) {
			Week week = new Week(j);
			IDayRenderer[] days = new IDayRenderer[Const.TOTAL_COL];
			for (int i = 0 ; i < 7 ; i ++) {
				days[i] = dayRenderer.copy();
			}
			week.setDays(days);
			weeks1[j] = week;
		}
		Calendar c1 = calendars.get(1);
		c1.setWeeks(weeks1);

		Week weeks2[] = new Week[Const.TOTAL_ROW];
		for (int j = 0 ; j < 6 ; j ++) {
			Week week = new Week(j);
			IDayRenderer[] days = new IDayRenderer[Const.TOTAL_COL];
			for (int i = 0 ; i < 7 ; i ++) {
				days[i] = dayRenderer.copy();
			}
			week.setDays(days);
			weeks2[j] = week;
		}

		Calendar c2 = calendars.get(2);
		c2.setWeeks(weeks2);
	}
}