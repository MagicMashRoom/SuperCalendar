package com.ldf.calendar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ldf.calendar.model.Cell;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.utils.Utils;

import java.util.HashMap;


public class Calendar extends View {

	private static final int TOTAL_COL = 7;
	private static final int TOTAL_ROW_SIX = 6;
	private static final int TOTAL_ROW_FIVE = 5;
	private static final int PAST_MONTH = -1;
	private static final int NEXT_MONTH = 1;

	private Context context;

	private Paint circlePaint;	// 绘制点击选中圆形的画笔
	private Paint markPaint; //绘制MARK的画笔
	private Paint datePaint;	// 绘制文本的画笔
	private Paint linePaint;	// 绘制分割线的画笔

	private int viewWidth;	// 视图的宽度
	private int viewHeight;	// 视图的高度
	private int cellHeight; // 单元格高度
	private int cellWidth; // 单元格宽度

	private Row rows[] = new Row[TOTAL_ROW_SIX];	// 行数组，每个元素代表一行

	private CalendarDate mShowDate; //自定义的日期  包括year month day
	private CalendarDate clickDate; //被选中的日期  包括year month day
	private OnCellClickListener onCellClickListener;	// 单元格点击回调事件
	private int touchSlop;
	private HashMap<String, String> markData;

	public HashMap<String, String> getMarkData() {
		return markData;
	}

	public void setMarkData(HashMap<String, String> markDateData) {
		this.markData = markDateData;
	}

	public interface OnCellClickListener {

		void onClickDateCell(CalendarDate date);//回调点击的日期

		void refreshDate(CalendarDate date);

		void onClickOtherMonth(int offset);//点击其它月份日期
	}

	public Calendar(Context context, OnCellClickListener onCellClickListener) {
		super(context);
		this.onCellClickListener = onCellClickListener;
		init(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(rows[5].cells[0].state == State.NONE){
			cellHeight = viewHeight / TOTAL_ROW_FIVE;
			Cell.setHeight(cellHeight);
			for (int row = 0; row < TOTAL_ROW_SIX - 1; row++) {
				if (rows[row] != null)
					rows[row].drawRow(canvas);
				drawLine(canvas, row);
			}
		}else{
			cellHeight = viewHeight / TOTAL_ROW_SIX;
			Cell.setHeight(cellHeight);
			for (int row = 0; row < TOTAL_ROW_SIX; row++) {
				if (rows[row] != null)
					rows[row].drawRow(canvas);
				drawLine(canvas, row);
			}
		}
	}

	private void drawLine(Canvas canvas, int row) {
		canvas.drawLine(0, row * cellHeight, viewWidth,row * cellHeight, linePaint);
	}

	private void init(Context context) {
		this.context = context;
		Cell.setContext(context);
		datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		datePaint.setTextSize(Utils.dpi2px(context, 13));
		Cell.setDatePaint(datePaint);
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(Color.parseColor("#FFEBEBEB"));
		Cell.setCirclePaint(circlePaint);
		markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markPaint.setStyle(Paint.Style.FILL);
		Cell.setMarkPaint(markPaint);
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(Color.parseColor("#E8E8E8"));
		linePaint.setStrokeWidth(Utils.dpi2px(context, 0.66f));
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;
		clickDate = Utils.getClickDate(context);
		initDateData();
	}

	private void initDateData() {
		if(mShowDate == null){
			mShowDate = new CalendarDate();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		viewWidth = w;
		viewHeight = h;
		cellHeight = viewHeight / TOTAL_ROW_SIX;
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
			CalendarDate clickDate;
			if(rows[row].cells[col].state == State.CURRENT_MONTH_DAY){
				rows[row].cells[col].state = State.CLICK_DAY;
				clickDate = rows[row].cells[col].cellDate;
				onCellClickListener.onClickDateCell(clickDate);
			} else if (rows[row].cells[col].state == State.PAST_MONTH_DAY){
				clickDate = rows[row].cells[col].cellDate;
				onCellClickListener.onClickOtherMonth(PAST_MONTH);
				onCellClickListener.onClickDateCell(clickDate);
			} else if (rows[row].cells[col].state == State.NEXT_MONTH_DAY){
				clickDate = rows[row].cells[col].cellDate;
				onCellClickListener.onClickOtherMonth(NEXT_MONTH);
				onCellClickListener.onClickDateCell(clickDate);
			} else if (rows[row].cells[col].state == State.TODAY){
				rows[row].cells[col].state = State.CLICK_DAY;
				clickDate = rows[row].cells[col].cellDate;
				onCellClickListener.onClickDateCell(clickDate);
			}
			invalidate();
		}
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
					cells[col].drawCell(canvas, markData);
			}
		}
	}

	public enum State {
		TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, CLICK_DAY, NONE
	}

	private void fillMonthDate() {
		int lastMonthDays = Utils.getMonthDays(mShowDate.year, mShowDate.month - 1);	// 上个月的天数
		int currentMonthDays = Utils.getMonthDays(mShowDate.year, mShowDate.month);	// 当前月的天数
		int firstDayWeek = Utils.getWeekDayFromDate(mShowDate.year, mShowDate.month);
		int day = 0;
		for (int row = 0; row < TOTAL_ROW_SIX; row++) {
			day = fillDayCol(lastMonthDays, currentMonthDays, firstDayWeek, day, row);
		}
	}

	private int fillDayCol(int lastMonthDays, int currentMonthDays, int firstDayWeek, int day, int row) {
		rows[row] = new Row(row);
		for (int col = 0; col < TOTAL_COL; col++) {
			int position = col + row * TOTAL_COL;	// 单元格位置
			if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {	// 这个月的
				day ++;
				fillCurrentMonth(day, row, col);
				if (Utils.isCurrentMonth(mShowDate)
						&& Utils.isCurrentDay(day)
						&& !rows[row].cells[col].cellDate.equals(clickDate)) {
					fillTodayDate(day, row, col);
				}
			} else if (position < firstDayWeek) { //last month
				fillLastMonth(lastMonthDays, firstDayWeek, row, col, position);
			} else if (position >= firstDayWeek + currentMonthDays) {//next month
				fillNextMonth(currentMonthDays, firstDayWeek, row, col, position);
			}
		}
		return day;
	}

	private void fillTodayDate(int day, int row, int col) {
		CalendarDate date = CalendarDate.modifyDay(mShowDate, day);
		mTodayCell = new Cell(date, State.TODAY, col, row);
		rows[row].cells[col] = mTodayCell;
	}

	private void fillCurrentMonth(int day, int row, int col) {
		rows[row].cells[col] = new Cell(CalendarDate.modifyDay(mShowDate, day),
				State.CURRENT_MONTH_DAY, col, row);
		if(rows[row].cells[col].cellDate.equals(clickDate)){
			rows[row].cells[col] = new Cell(CalendarDate.modifyDay(mShowDate, day),
					State.CLICK_DAY, col, row);
		}
	}

	private void fillNextMonth(int currentMonthDays, int firstDayWeek, int row, int col, int position) {

		rows[row].cells[col] = new Cell((new CalendarDate(mShowDate.year,
				mShowDate.month + 1, position - firstDayWeek - currentMonthDays + 1)),
				State.NEXT_MONTH_DAY, col, row);

		if(row == 5 && col == 0){
			rows[row].cells[col] = new Cell((new CalendarDate(mShowDate.year,
					mShowDate.month + 1, position - firstDayWeek - currentMonthDays + 1)),
					State.NONE, col, row);
		}
	}

	private void fillLastMonth(int lastMonthDays, int firstDayWeek, int row, int col, int position) {
		rows[row].cells[col] = new Cell(new CalendarDate(mShowDate.year,
				mShowDate.month - 1, lastMonthDays - (firstDayWeek- position - 1)),
				State.PAST_MONTH_DAY, col, row);
	}

	public void updateCurrentDate() {
		fillMonthDate();
		invalidate();
	}

	public void showDate(CalendarDate mShowDate) {
		if(mShowDate != null){
			this.mShowDate = mShowDate;
		}else {
			this.mShowDate = new CalendarDate();
		}
		updateCurrentDate();
	}

	public CalendarDate getShowCurrentDate() {
		return this.mShowDate;
	}

	public void showToday(){
		if(onCellClickListener != null){
			onCellClickListener.refreshDate(mShowDate);
		}
	}

	public void cancelClickState(){
		for (int i = 0; i < TOTAL_ROW_SIX; i++) {
			if (rows[i] != null){
				for (int j = 0; j < TOTAL_COL; j++){
					if(rows[i].cells[j].state == State.CLICK_DAY){
						rows[i].cells[j].state = State.CURRENT_MONTH_DAY;
					}
				}
			}
		}
	}

	public void updateClickDate(){
		clickDate = Utils.getClickDate(context);
		fillMonthDate();
	}
}