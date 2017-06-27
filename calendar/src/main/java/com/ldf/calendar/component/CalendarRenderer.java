package com.ldf.calendar.component;

import android.content.Context;
import android.graphics.Canvas;

import com.ldf.calendar.Utils;
import com.ldf.calendar.adpter.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.CustomDayView;
import com.ldf.calendar.view.DayView;
import com.ldf.calendar.view.Calendar;
import com.ldf.mi.calendar.R;

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

    private class Week {
        public int row;
        Week(int row) {
            this.row = row;
        }

        public DayView[] cells = new DayView[TOTAL_COL];

        public void drawRow(Canvas canvas) {
            for (int col = 0; col < cells.length; col++) {
                if (cells[col] != null)
                    cells[col].draw(canvas);
            }
        }
    }

    public void onClickDate(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW_SIX)
            return;
        if (weeks[row] != null) {
            if(attr.getCalendarType() == CalendarAttr.CalendayType.MONTH) {
                if(weeks[row].cells[col].state == DayView.State.CURRENT_MONTH_DAY){
                    weeks[row].cells[col].state = DayView.State.CLICK_DAY;
                    selectedDate = weeks[row].cells[col].date;
                    seedDate = weeks[row].cells[col].date;
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectDate(selectedDate);
                } else if (weeks[row].cells[col].state == DayView.State.PAST_MONTH_DAY){
                    selectedDate = weeks[row].cells[col].date;
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectOtherMonth(-1);
                    onCellClickListener.onSelectDate(selectedDate);
                } else if (weeks[row].cells[col].state == DayView.State.NEXT_MONTH_DAY){
                    selectedDate = weeks[row].cells[col].date;
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectOtherMonth(1);
                    onCellClickListener.onSelectDate(selectedDate);
                } else if (weeks[row].cells[col].state == DayView.State.TODAY){
                    weeks[row].cells[col].state = DayView.State.CLICK_DAY;
                    selectedDate = weeks[row].cells[col].date;
                    seedDate = weeks[row].cells[col].date;
                    CalendarViewAdapter.saveDate(selectedDate);
                    onCellClickListener.onSelectDate(selectedDate);
                }
            } else {
                weeks[row].cells[col].state = DayView.State.CLICK_DAY;
                selectedDate = weeks[row].cells[col].date;
                seedDate = weeks[row].cells[col].date;
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
                weeks[rowIndex].cells[i] = new CustomDayView(context , R.layout.);
                // TODO: 17/6/26 使用接口 
            }

            if(weeks[rowIndex].cells[i].date.equals(CalendarViewAdapter.loadDate())){
                weeks[rowIndex].cells[i] = new DayView(date, DayView.State.CLICK_DAY, i, rowIndex);
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
        weeks[row] = new Week(row);
        for (int col = 0; col < TOTAL_COL; col++) {
            int position = col + row * TOTAL_COL;	// 单元格位置
            if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {	// 本月的
                day ++;
                fillCurrentMonthDate(day, row, col);
                if (Utils.isToday(seedDate, day)
                        && !weeks[row].cells[col].date.equals(CalendarViewAdapter.loadDate())) {
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
        CalendarDate date = seedDate.modifyDay(day);
        weeks[row].cells[col] = new DayView(date, DayView.State.TODAY, col, row);
    }

    private void fillCurrentMonthDate(int day, int row, int col) {
        weeks[row].cells[col] = new DayView(seedDate.modifyDay(day), DayView.State.CURRENT_MONTH_DAY, col, row);
        if(weeks[row].cells[col].date.equals(CalendarViewAdapter.loadDate())){
            weeks[row].cells[col] = new DayView(seedDate.modifyDay(day), DayView.State.CLICK_DAY, col, row);
        }
        if(weeks[row].cells[col].date.equals(seedDate)){
            selectedRowIndex = row;
        }
    }

    private void instantiateNextMonth(int currentMonthDays, int firstDayWeek, int row, int col, int position) {
        weeks[row].cells[col] = new DayView((new CalendarDate(seedDate.year,
                seedDate.month + 1, position - firstDayWeek - currentMonthDays + 1)),
                DayView.State.NEXT_MONTH_DAY, col, row);
        if(position - firstDayWeek - currentMonthDays + 1 >= 7) { //当下一个月的天数大于七时，说明该月有六周
//            cellHeight = cellHeight * currentMonthWeeks / TOTAL_ROW_FIVE;
        }
    }

    private void instantiateLastMonth(int lastMonthDays, int firstDayWeek, int row, int col, int position) {
        weeks[row].cells[col] = new DayView(new CalendarDate(seedDate.year,
                seedDate.month - 1, lastMonthDays - (firstDayWeek- position - 1)),
                DayView.State.PAST_MONTH_DAY, col, row);
    }

    public void showDate(CalendarDate seedDate) {
        if(seedDate != null){
            this.seedDate = seedDate;
        }else {
            this.seedDate = new CalendarDate();
        }
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
                    if(weeks[i].cells[j].state == DayView.State.CLICK_DAY){
                        weeks[i].cells[j].state = DayView.State.CURRENT_MONTH_DAY;
                        resetSelectedRowIndex();
                    }
                    if(weeks[i].cells[j].date.equals(new CalendarDate())) {
                        weeks[i].cells[j].state = DayView.State.TODAY;
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
