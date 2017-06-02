/*
 * Copyright (c) 2016.
 * wb-lijinwei.a@alibaba-inc.com
 */

package com.ldf.calendar.model;

import com.ldf.calendar.utils.Utils;

import java.io.Serializable;

public class CalendarDate implements Serializable{
	private static final long serialVersionUID = 1L;
	public int year;
	public int month;  //1~12
	public int day;

	public CalendarDate(int year, int month, int day){
		if(month > 12){
			month = 1;
			year ++;
		}else if(month < 1){
			month = 12;
			year --;
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public CalendarDate(){
		this.year = Utils.getYear();
		this.month = Utils.getMonth();
		this.day = Utils.getDay();
	}

	public static CalendarDate modifyDay(CalendarDate date, int day){
		CalendarDate modifyDate = new CalendarDate(date.year, date.month, day);
		return modifyDate;
	}

	public void modifyCurrentDateMonth(int offset){
		int addToMonth = this.month + offset;
		if(offset > 0){
			if(addToMonth > 12){
				setYear(this.year + (addToMonth - 1) / 12);
				setMonth(addToMonth % 12 == 0 ? 12: addToMonth % 12);
			}else {
				setMonth(addToMonth);
			}
		}else{
			if(addToMonth == 0){
				setYear(this.year - 1);
				setMonth(12);
			}else if(addToMonth < 0){
				setYear(this.year + addToMonth / 12 - 1);
				int month = 12 - Math.abs(addToMonth) % 12;
				setMonth(month == 0 ? 12 : month);
			}else {
				setMonth(addToMonth == 0 ? 12 : addToMonth);
			}
		}
	}

	@Override
	public String toString() {
		return year + "-" + month + "-" + day;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean equals(CalendarDate date) {
		if(date == null){
			return false;
		}
		if(this.getYear() == date.getYear()
				&& this.getMonth() == date.getMonth()
				&& this.getDay() == date.getDay()){
			return true;
		}
		return false;
	}

	public static boolean isSameMonth(CalendarDate con, CalendarDate com){
		if(con.month == com.month && com.year == com.year){
			return true;
		}
		return false;
	}
}