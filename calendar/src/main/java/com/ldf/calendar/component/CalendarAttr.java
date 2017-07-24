package com.ldf.calendar.component;

/**
 * Created by ldf on 17/6/26.
 */

public class CalendarAttr {
    /**
     * 以何种方式排列星期:<br/>
     * {@link CalendarAttr.WeekArrayType} <br/>
     */
    private WeekArrayType weekArrayType;

    /**
     * 日历才去周布局或者月布局:<br/>
     * {@link CalendarAttr.CalendayType} 布局类型<br/>
     */
    private CalendayType calendarType;

    /**
     * 日期格子高度
     */
    private int cellHeight;

    /**
     * 日期格子宽度
     */
    private int cellWidth;

    public WeekArrayType getWeekArrayType() {
        return weekArrayType;
    }

    public void setWeekArrayType(WeekArrayType weekArrayType) {
        this.weekArrayType = weekArrayType;
    }

    public CalendayType getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(CalendayType calendarType) {
        this.calendarType = calendarType;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public enum WeekArrayType {
        Sunday, Monday
    }

    public enum CalendayType {
        WEEK, MONTH
    }
}
