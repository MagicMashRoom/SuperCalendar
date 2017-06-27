package com.ldf.calendar.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import com.ldf.calendar.Utils;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.Week;

/**
 * Created by ldf on 17/6/26.
 */

public class CalendarRenderer {
    private static final int TOTAL_COL = 7;
    private static final int TOTAL_ROW_SIX = 6;

    private Week weeks[] = new Week[TOTAL_ROW_SIX];	// 行数组，每个元素代表一行

    private Calendar calendar;
    private CalendarAttr attr;
    private Context context;
    private OnSelectDateListener onCellClickListener;	// 单元格点击回调事件
    private CalendarDate seedDate; //种子日期  包括year month day
    private CalendarDate selectedDate; //被选中的日期  包括year month day
    private int selectedRowIndex = 0;

    public CalendarRenderer(Calendar calendar , CalendarAttr attr , Context context) {
        this.calendar = calendar;
        this.attr = attr;
        this.context = context;
        seedDate = new CalendarDate();
    }

    public void draw(Canvas canvas) {
        for (int row = 0; row < TOTAL_ROW_SIX; row++) {
			if (weeks[row] != null)
				weeks[row].drawRow(canvas);
		}
    }

    public void setWeeks(Week[] weeks) {
        this.weeks = weeks;
    }

    public Week[] getWeeks() {
        return weeks;
    }

    public void onClickDate(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW_SIX)
            return;
        if (weeks[row] != null) {
            if(attr.getCalendarType() == CalendarAttr.CalendayType.MONTH) {
                if(weeks[row].days[col].getState() == State.CURRENT_MONTH_DAY){
                    weeks[row].days[col].refreshState(State.CLICK_DAY);
                    selectedDate = weeks[row].days[col].getDate();
                    seedDate = weeks[row].days[col].getDate();
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectDate(selectedDate);
                } else if (weeks[row].days[col].getState() == State.PAST_MONTH_DAY){
                    selectedDate = weeks[row].days[col].getDate();
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectOtherMonth(-1);
                    onCellClickListener.onSelectDate(selectedDate);
                } else if (weeks[row].days[col].getState() == State.NEXT_MONTH_DAY){
                    selectedDate = weeks[row].days[col].getDate();
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectOtherMonth(1);
                    onCellClickListener.onSelectDate(selectedDate);
                } else if (weeks[row].days[col].getState() == State.TODAY){
                    weeks[row].days[col].refreshState(State.CLICK_DAY);
                    selectedDate = weeks[row].days[col].getDate();
                    seedDate = weeks[row].days[col].getDate();
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectDate(selectedDate);
                }
            } else {
                weeks[row].days[col].refreshState(State.CLICK_DAY);
                selectedDate = weeks[row].days[col].getDate();
                seedDate = weeks[row].days[col].getDate();
                CalendarViewAdapter.saveDate(selectedDate);
                onCellClickListener.onSelectDate(selectedDate);
            }
        }
    }

    public void updateWeek(int rowIndex) {
        CalendarDate weekLastDay;
        if(CalendarViewAdapter.weekArrayType == 1) {
            weekLastDay = Utils.getSaturday(seedDate.year , seedDate.month , seedDate.day);
        } else {
            weekLastDay = Utils.getSunday(seedDate.year , seedDate.month , seedDate.day);
        }
        weeks[rowIndex] = new Week(rowIndex);
        int day = weekLastDay.day;
        for (int i = TOTAL_COL - 1; i >= 0 ; i --) {
            CalendarDate date = weekLastDay.modifyDay(day);

            if (Utils.isToday(date , day)) {
                fillToday(day , rowIndex, i);
            } else {
                weeks[rowIndex].days[i].refreshContent(date , State.CURRENT_MONTH_DAY);
            }

            if(weeks[rowIndex].days[i].getState().equals(CalendarViewAdapter.loadDate())){
                weeks[rowIndex].days[i].refreshState(State.CLICK_DAY);
            }
            day -- ;
        }
        calendar.invalidate();
    }

    private void instantiateMonth() {
        int lastMonthDays = Utils.getMonthDays(seedDate.year, seedDate.month - 1);	// 上个月的天数
        int currentMonthDays = Utils.getMonthDays(seedDate.year, seedDate.month);	// 当前月的天数
        int firstDayPosition = Utils.getFirstDayWeekPosition(seedDate.year, seedDate.month , CalendarViewAdapter.weekArrayType);

        int day = 0;
        for (int row = 0; row < TOTAL_ROW_SIX; row++) {
            day = fillWeek(lastMonthDays, currentMonthDays, firstDayPosition, day, row);
        }
    }

    private int fillWeek(int lastMonthDays, int currentMonthDays, int firstDayWeek, int day, int row) {
        for (int col = 0; col < TOTAL_COL; col++) {
            int position = col + row * TOTAL_COL;	// 单元格位置
            if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {	// 本月的
                day ++;
                fillCurrentMonthDate(day, row, col);
            } else if (position < firstDayWeek) { //last month
                instantiateLastMonth(lastMonthDays, firstDayWeek, row, col, position);
            } else if (position >= firstDayWeek + currentMonthDays) {//next month
                instantiateNextMonth(currentMonthDays, firstDayWeek, row, col, position);
            }
        }
        return day;
    }

    private void fillToday(int day, int row, int col) {
        if(weeks[row].days[col] != null) {
            weeks[row].days[col].refreshContent(seedDate.modifyDay(day), State.TODAY);
        }
    }

    private void fillCurrentMonthDate(int day, int row, int col) {
        CalendarDate date = seedDate.modifyDay(day);
        if(weeks[row].days[col] != null) {
            if(date.equals(CalendarViewAdapter.loadDate())) {
                weeks[row].days[col].refreshContent(seedDate.modifyDay(day) , State.CLICK_DAY);
            } else {
                weeks[row].days[col].refreshContent(seedDate.modifyDay(day) , State.CURRENT_MONTH_DAY);
            }
        }
        if(date.equals(seedDate)){
            selectedRowIndex = row;
        }
    }

    private void instantiateNextMonth(int currentMonthDays, int firstDayWeek, int row, int col, int position) {
        CalendarDate date = new CalendarDate(
                seedDate.year,
                seedDate.month + 1,
                position - firstDayWeek - currentMonthDays + 1);
        if(weeks[row].days[col] != null) {
            Log.e("ldf","next month date = " + date.toString());
            weeks[row].days[col].refreshContent(date, State.NEXT_MONTH_DAY);
        }
        if(position - firstDayWeek - currentMonthDays + 1 >= 7) { //当下一个月的天数大于七时，说明该月有六周
//            cellHeight = cellHeight * currentMonthWeeks / TOTAL_ROW_FIVE;
        }
    }

    private void instantiateLastMonth(int lastMonthDays, int firstDayWeek, int row, int col, int position) {
        CalendarDate date = new CalendarDate(
                seedDate.year,
                seedDate.month - 1,
                lastMonthDays - (firstDayWeek- position - 1));
        if(weeks[row].days[col] != null) {
            weeks[row].days[col].refreshContent(date , State.NEXT_MONTH_DAY);
        }
    }

    public void showDate(CalendarDate seedDate) {
        if(seedDate != null){
            this.seedDate = seedDate;
        }else {
            this.seedDate = new CalendarDate();
        }
        Log.e("ldf","showDate");
        update();
    }

    public void update() {
        instantiateMonth();
        calendar.invalidate();
    }

    public CalendarDate getShowCurrentDate() {
        return this.seedDate;
    }

    public void cancelSelectState(){
        for (int i = 0; i < TOTAL_ROW_SIX; i++) {
            if (weeks[i] != null){
                for (int j = 0; j < TOTAL_COL; j++){
                    if(weeks[i].days[j].getState() == State.CLICK_DAY){
                        weeks[i].days[j].refreshState(State.CURRENT_MONTH_DAY);
                        resetSelectedRowIndex();
                    }
                    if(weeks[i].days[j].getDate().equals(new CalendarDate())) {
                        weeks[i].days[j].refreshState(State.TODAY);
                    }
                }
            }
        }
    }

    public void resetSelectedRowIndex(){
        selectedRowIndex = 0;
    }

    public int getSelectedRowIndex() {
        return selectedRowIndex;
    }

    public void setSelectedRowIndex(int selectedRowIndex) {
        this.selectedRowIndex = selectedRowIndex;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public CalendarAttr getAttr() {
        return attr;
    }

    public void setAttr(CalendarAttr attr) {
        this.attr = attr;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
