package com.ldf.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ldf.calendar.listener.OnSelectDateListener;
import com.ldf.calendar.adpter.CalendarViewAdapter;
import com.ldf.calendar.model.Cell;
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
	public static int calendarType = MONTH_TYPE;

	private Context context;

	private Paint circlePaint;	// 绘制点击选中圆形的画笔
	private int selectedColor = Color.BLACK;

	private Paint markPaint; //绘制MARK的画笔
	private int markColor = Color.RED;

	private Paint datePaint;	// 绘制文本的画笔
	private int dateTextColor = Color.BLACK;
	private float dateTextSize = 13f;

	private Paint linePaint;	// 绘制分割线的画笔

	private int lineColor = Color.BLACK;
	private float lineSize = 0.66f;

	private int viewWidth;	// 视图的宽度
	private int viewHeight;	// 视图的高度

	private int cellHeight; // 单元格高度
	private int cellWidth; // 单元格宽度

	private int currentMonthWeeks = TOTAL_ROW_SIX;

	private Row rows[] = new Row[TOTAL_ROW_SIX];	// 行数组，每个元素代表一行

	private CalendarDate showDate; //自定义的日期  包括year month day
	private CalendarDate selectedDate; //被选中的日期  包括year month day
	private OnSelectDateListener onCellClickListener;	// 单元格点击回调事件
	private int touchSlop;
	private int selectedRow = 0;


	public Calendar(Context context, OnSelectDateListener onCellClickListener) {
		super(context);
		this.onCellClickListener = onCellClickListener;
		init(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		cellHeight = viewHeight / currentMonthWeeks;
		Cell.setHeight(cellHeight);
		for (int row = 0; row < currentMonthWeeks; row++) {
			if (rows[row] != null)
				rows[row].drawRow(canvas);
			drawLine(canvas, row);
		}
	}

	private void drawLine(Canvas canvas, int row) {
		canvas.drawLine(0, row * cellHeight, viewWidth , row * cellHeight, linePaint);
	}

	private void init(Context context) {
		this.context = context;
		Cell.setContext(context);
		datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		datePaint.setTextSize(Utils.dpi2px(context, dateTextSize));
		Cell.setDatePaint(datePaint);
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(selectedColor);
		Cell.setCirclePaint(circlePaint);
		markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markPaint.setStyle(Paint.Style.FILL);
		Cell.setMarkPaint(markPaint);
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(lineColor);
		linePaint.setStrokeWidth(Utils.dpi2px(context, lineSize));
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;
		selectedDate = CalendarViewAdapter.getDate();
		initDateData();
	}

	private void initDateData() {
		if(showDate == null){
			showDate = new CalendarDate();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		viewWidth = w;
		viewHeight = h;
		cellHeight = viewHeight / currentMonthWeeks;
		cellWidth = viewWidth / TOTAL_COL;
		Cell.setWidth(cellWidth);
	}

	private Cell mTodayCell;
	private float mDownX;
	private float mDownY;
	/*
     *
     * 触摸事件为了确定点击的位置日期
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getX();
				mDownY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				float disX = event.getX() - mDownX;
				float disY = event.getY() - mDownY;
				if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
					int col = (int) (mDownX / cellWidth);
					int row = (int) (mDownY / cellHeight);
					measureClickCell(col, row);
				}
				break;
		}
		return true;
	}


	private void measureClickCell(int col, int row) {

		if (col >= TOTAL_COL || row >= TOTAL_ROW_SIX)
			return;
		if (rows[row] != null) {
			if(rows[row].cells[col].state == State.CURRENT_MONTH_DAY){
				cancelClickState();
				rows[row].cells[col].state = State.CLICK_DAY;
				selectedDate = rows[row].cells[col].date;
				CalendarViewAdapter.setDate(selectedDate);
				onCellClickListener.onSelectDate(selectedDate);
			} else if (rows[row].cells[col].state == State.PAST_MONTH_DAY){
				selectedDate = rows[row].cells[col].date;
				CalendarViewAdapter.setDate(selectedDate);
				onCellClickListener.onSelectOtherMonth(PAST_MONTH);
				onCellClickListener.onSelectDate(selectedDate);
			} else if (rows[row].cells[col].state == State.NEXT_MONTH_DAY){
				selectedDate = rows[row].cells[col].date;
				CalendarViewAdapter.setDate(selectedDate);
				onCellClickListener.onSelectOtherMonth(NEXT_MONTH);
				onCellClickListener.onSelectDate(selectedDate);
			} else if (rows[row].cells[col].state == State.TODAY){
				cancelClickState();
				rows[row].cells[col].state = State.CLICK_DAY;
				selectedDate = rows[row].cells[col].date;
				CalendarViewAdapter.setDate(selectedDate);
				onCellClickListener.onSelectDate(selectedDate);
			}
			invalidate();
		}
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	// 组
	class Row {
		public int row;
		Row(int row) {
			this.row = row;
		}

		public Cell[] cells = new Cell[TOTAL_COL];

		public void drawRow(Canvas canvas) {
			for (int col = 0; col < cells.length; col++) {
				if (cells[col] != null)
					cells[col].drawCell(canvas);
			}
		}
	}

	public enum State {
		TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, CLICK_DAY
	}

	private void instantiateCalendar(){
		instantiateMonth();
		if (calendarType == WEEK_TYPE) {
			instantiateWeek();
		}
	}

	private void instantiateWeek() {
		CalendarDate sunday = Utils.getSunday(showDate.year , showDate.month , showDate.day);
		showDate = sunday;
		int lastMonthDays = Utils.getMonthDays(showDate.year, showDate.month - 1);
		rows[selectedRow] = new Row(selectedRow);
		int day = sunday.day;
		for (int i = TOTAL_COL - 1; i >= 0 ; i --) {
			CalendarDate date = sunday.modifyDay(day);
			if (Utils.isToday(date , day)) {
				mTodayCell = new Cell(date, State.TODAY, i, selectedRow);
				fillToday(day , selectedRow , i);
			} else {
				rows[selectedRow].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY,i, selectedRow);
			}
			day -- ;
			if (day < 1) {
				day = lastMonthDays + day;
			}
		}
	}

	private void instantiateMonth() {
		int lastMonthDays = Utils.getMonthDays(showDate.year, showDate.month - 1);	// 上个月的天数
		int currentMonthDays = Utils.getMonthDays(showDate.year, showDate.month);	// 当前月的天数
		int firstDayPosition = Utils.getWeekDayFromDate(showDate.year, showDate.month);//本月第一天是本月第一周的周几
		int day = 0;
		for (int row = 0; row < TOTAL_ROW_SIX; row++) {
			day = fillWeek(lastMonthDays, currentMonthDays, firstDayPosition, day, row);
		}
	}

	private int fillWeek(int lastMonthDays, int currentMonthDays, int firstDayWeek, int day, int row) {
		rows[row] = new Row(row);
		for (int col = 0; col < TOTAL_COL; col++) {
			int position = col + row * TOTAL_COL;	// 单元格位置
			if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {	// 本月的
				day ++;
				fillCurrentMonthDay(day, row, col);
				if (Utils.isToday(showDate, day)
						&& !rows[row].cells[col].date.equals(CalendarViewAdapter.getDate())) {
					fillToday(day, row, col);
				}
			} else if (position < firstDayWeek) { //last month
				instantiateLastMonth(lastMonthDays, firstDayWeek, row, col, position);
			} else if (position >= firstDayWeek + currentMonthDays) {//next month
				instantiateNextMonth(currentMonthDays, firstDayWeek, row, col, position);
			}
		}
		return day;
	}

	private void fillToday(int day, int row, int col) {
		CalendarDate date = showDate.modifyDay(day);
		mTodayCell = new Cell(date, State.TODAY, col, row);
		rows[row].cells[col] = mTodayCell;
	}

	private void fillCurrentMonthDay(int day, int row, int col) {
		rows[row].cells[col] = new Cell(showDate.modifyDay(day),
				State.CURRENT_MONTH_DAY, col, row);
		if(rows[row].cells[col].date.equals(CalendarViewAdapter.getDate())){
			rows[row].cells[col] = new Cell(showDate.modifyDay(day),
					State.CLICK_DAY, col, row);
		}
	}

	private void instantiateNextMonth(int currentMonthDays, int firstDayWeek, int row, int col, int position) {

		rows[row].cells[col] = new Cell((new CalendarDate(showDate.year,
				showDate.month + 1, position - firstDayWeek - currentMonthDays + 1)),
				State.NEXT_MONTH_DAY, col, row);
		if(position - firstDayWeek - currentMonthDays + 1 >= 7) {
			currentMonthWeeks = TOTAL_ROW_FIVE;
			cellHeight = viewHeight / currentMonthWeeks;
		} else {
			currentMonthWeeks = TOTAL_ROW_SIX;
			cellHeight = viewHeight / currentMonthWeeks;
		}
	}

	private void instantiateLastMonth(int lastMonthDays, int firstDayWeek, int row, int col, int position) {
		rows[row].cells[col] = new Cell(new CalendarDate(showDate.year,
				showDate.month - 1, lastMonthDays - (firstDayWeek- position - 1)),
				State.PAST_MONTH_DAY, col, row);
	}

	public void updateCurrentDate() {
		instantiateCalendar();
		invalidate();
	}

	public void showDate(CalendarDate mShowDate) {
		if(mShowDate != null){
			this.showDate = mShowDate;
		}else {
			this.showDate = new CalendarDate();
		}
		updateCurrentDate();
	}

	public CalendarDate getShowCurrentDate() {
		return this.showDate;
	}

	public void cancelClickState(){
		for (int i = 0; i < TOTAL_ROW_SIX; i++) {
			if (rows[i] != null){
				for (int j = 0; j < TOTAL_COL; j++){
					if(rows[i].cells[j].state == State.CLICK_DAY){
						rows[i].cells[j].state = State.CURRENT_MONTH_DAY;
					}
					if(rows[i].cells[j].date.equals(new CalendarDate())) {
						rows[i].cells[j].state = State.TODAY;
					}
				}
			}
		}
	}

	public void updateClickDate(){
		instantiateCalendar();
	}

	public void updateClickDate(CalendarDate date){
		CalendarViewAdapter.setDate(date);
		instantiateCalendar();
		invalidate();
	}

	public void setMarkColor(int markColor) {
		this.markColor = markColor;
	}

	public void setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
	}

	public int getDateTextColor() {
		return dateTextColor;
	}

	public void setDateTextColor(int dateTextColor) {
		this.dateTextColor = dateTextColor;
	}

	// 传入参数为Dp
	public void setDateTextSize(int dateTextSize) {
		this.dateTextSize = dateTextSize;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	// 传入参数为Dp
	public void setLineSize(float lineSize) {
		this.lineSize = lineSize;
	}

	public int getCurrentMonthWeeks() {
		return currentMonthWeeks;
	}

	public int getCellHeight() {
		return cellHeight;
	}
}