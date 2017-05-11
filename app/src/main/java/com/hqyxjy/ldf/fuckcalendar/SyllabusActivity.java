package com.hqyxjy.ldf.fuckcalendar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ldf.calendar.MonthPager;
import com.ldf.calendar.adpter.CalendarViewAdapter;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.utils.DateUtil;
import com.ldf.calendar.views.Calendar;

import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * Created by ldf on 16/11/4.
 */

public class SyllabusActivity extends AppCompatActivity{
    MonthPager monthPager;
    TextView textViewYearDisplay;
    TextView textViewMonthDisplay;
    TextView backToday;

    public static int CURRENT_OFFSET = 0;
    private Calendar[] showCalendars;
    private Calendar[] currentCalendars;

    private CalendarViewAdapter<Calendar> calendarAdapter;
    private Calendar.OnCellClickListener onCellClickListener;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private Context context;
    private boolean pageChangeByGestureSlide = false;
    private CalendarDate currentDate;
    private HashMap<String, String> markDateData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ldf","setContentView()");
        setContentView(R.layout.activity_syllabus);
        ButterKnife.bind(this);
        context = this;
        monthPager = (MonthPager) findViewById(R.id.calendar_view);
        textViewYearDisplay = (TextView) findViewById(R.id.show_year_view);
        textViewMonthDisplay = (TextView) findViewById(R.id.show_month_view);
        backToday = (TextView) findViewById(R.id.back_today_button);
        initCurrentDate();
        initCalendarView();
        initBackTodayClickListener();
    }

    private void initBackTodayClickListener() {
        backToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBackToDayBtn();
            }
        });
    }

    private void initCurrentDate() {
        DateUtil.saveClickDate(context, new CalendarDate());
        currentDate = new CalendarDate();
    }

    private void initCalendarView() {
        onCellClickListener = new Calendar.OnCellClickListener() {
            @Override
            public void onClickDateCell(CalendarDate date) {
                refreshCurrentDate(date);
                refreshClickDate(date);
                calendarAdapter.updateAllClickState();
            }

            @Override
            public void refreshDate(CalendarDate date) {
                refreshClickDate(date);
            }

            @Override
            public void onClickOtherMonth(int offset) {
                monthPager.setCurrentItem(mCurrentPage + offset);
            }
        };
        initMonthPage();
        calendarAdapter = new CalendarViewAdapter<>(showCalendars, CURRENT_OFFSET);
        initMonthPager();
    }

    private void refreshCurrentDate(CalendarDate date) {
        DateUtil.saveClickDate(context, date);
        currentDate = date;
    }

    private void refreshClickDate(CalendarDate date) {
        textViewYearDisplay.setText(date.getYear() + "年");
        textViewMonthDisplay.setText(date.getMonth() + "");
    }

    private void initMonthPage() {
        showCalendars = new Calendar[3];
        for (int i = 0; i < 3; i++) {
            showCalendars[i] = new Calendar(context, onCellClickListener);
        }
    }

    private void  initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(pageChangeByGestureSlide){
                    mCurrentPage = position;
                    currentCalendars = calendarAdapter.getAllItems();
                    if(currentCalendars[position % currentCalendars.length] instanceof Calendar){
                        CalendarDate date = currentCalendars[position % currentCalendars.length].getShowCurrentDate();
                        currentDate = date;
                        textViewYearDisplay.setText(date.getYear() + "年");
                        textViewMonthDisplay.setText(date.getMonth() + "");
                    }
                    pageChangeByGestureSlide = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                pageChangeByGestureSlide = true;
            }
        });
    }

    public void onClickBackToDayBtn() {
        CalendarDate today = new CalendarDate();
        refreshCurrentDate(today);
        refreshClickDate(today);
        calendarAdapter.updateAllClickState();
        refreshMonthPager(CURRENT_OFFSET);
    }

    private void refreshMonthPager(int offset) {
        calendarAdapter = new CalendarViewAdapter<>(showCalendars, offset);
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
    }
}

