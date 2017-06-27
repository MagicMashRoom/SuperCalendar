package com.ldf.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ldf.calendar.interf.OnAdapterSelectListener;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarRenderer;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.Utils;

public class Calendar extends View {
	/**
	 * 日历列数
	 */
	private static final int TOTAL_COL = 7;

	private static final int TOTAL_ROW_SIX = 6;
	private static final int TOTAL_ROW_FIVE = 6;
	private static final int PAST_MONTH = -1;
	private static final int NEXT_MONTH = 1;

	public static final int MONTH_TYPE = 0;
	public static final int WEEK_TYPE = 1;

	public int calendarType = MONTH_TYPE;

	private int cellHeight; // 单元格高度
	private int cellWidth; // 单元格宽度

	private int currentMonthWeeks = TOTAL_ROW_SIX;

	private CalendarDate seedDate; //种子日期  包括year month day
	private CalendarDate selectedDate; //被选中的日期  包括year month day

	private OnSelectDateListener onSelectDateListener;	// 单元格点击回调事件
	private Context context;
	private CalendarAttr calendarAttr;
	private CalendarRenderer renderer;

	private OnAdapterSelectListener onAdapterSelectListener;
	private float touchSlop;

	public Calendar(Context context, OnSelectDateListener onSelectDateListener) {
		super(context);
		this.onSelectDateListener = onSelectDateListener;
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		touchSlop = Utils.getTouchSlop(context);
		initAttr();
	}

	private void initAttr() {
		calendarAttr = new CalendarAttr();
		calendarAttr.setWeekArrayType(CalendarAttr.WeekArrayType.Monday);
		calendarAttr.setCalendarType(CalendarAttr.CalendayType.MONTH);
		renderer = new CalendarRenderer(this , calendarAttr , context);
		renderer.setOnSelectDateListener(onSelectDateListener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		renderer.draw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		cellHeight = w / TOTAL_ROW_SIX;
		cellWidth = w / TOTAL_COL;
		calendarAttr.setCellHeight(cellHeight);
		calendarAttr.setCellWidth(cellWidth);
		renderer.setAttr(calendarAttr);
	}

	private float posX = 0;
	private float posY = 0;
	/*
     * 触摸事件为了确定点击的位置日期
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				posX = event.getX();
				posY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				float disX = event.getX() - posX;
				float disY = event.getY() - posY;
				if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
					Log.e("ldf","onClickDate");
					int col = (int) (posX / cellWidth);
					int row = (int) (posY / cellHeight);
					onAdapterSelectListener.cancelSelectState();
					renderer.onClickDate(col, row);
					onAdapterSelectListener.updateSelectState();
					this.invalidate();
				}
				break;
		}
		return true;
	}

	public int getCalendarType() {
		return calendarType;
	}

	public void switchCalendarType(int calendarType) {
		this.calendarType = calendarType;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void resetSelectedRowIndex(){
		renderer.resetSelectedRowIndex();
	}

	public int getSelectedRowIndex() {
		return renderer.getSelectedRowIndex();
	}

	public void setSelectedRowIndex(int selectedRowIndex) {
		renderer.setSelectedRowIndex(selectedRowIndex);
	}

	public void setOnAdapterSelectListener(OnAdapterSelectListener onAdapterSelectListener) {
		this.onAdapterSelectListener = onAdapterSelectListener;
	}

	public void showDate(CalendarDate current) {
		renderer.showDate(current);
	}

	public void updateWeek(int rowCount) {
		renderer.updateWeek(rowCount);
	}

	public void update() {
		renderer.update();
	}

	public void cancelSelectState() {
		renderer.cancelSelectState();
	}

	public CalendarDate getShowCurrentDate() {
		return renderer.getShowCurrentDate();
	}

	public void setWeeks(Week[] weeks){
		renderer.setWeeks(weeks);
	}


}