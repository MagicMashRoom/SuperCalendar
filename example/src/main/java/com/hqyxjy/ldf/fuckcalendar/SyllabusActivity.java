package com.hqyxjy.ldf.fuckcalendar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ldf.calendar.view.MonthPager;
import com.ldf.calendar.listener.OnSelectDateListener;
import com.ldf.calendar.adpter.CalendarViewAdapter;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;

import java.util.ArrayList;
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
    private ArrayList<Calendar> showCalendars = new ArrayList<>();
    private ArrayList<Calendar> currentCalendars = new ArrayList<>();

    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private Context context;
    private CalendarDate currentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        currentDate = new CalendarDate();
        textViewYearDisplay.setText(currentDate.getYear() + "年");
        textViewMonthDisplay.setText(currentDate.getMonth() + "");
    }

    private void initCalendarView() {
        initListener();
        calendarAdapter = new CalendarViewAdapter(context , onSelectDateListener);
        initMarkData();
        initMonthPager();
    }

    private void initMarkData() {
        HashMap<String , String> markData = new HashMap<>();
        markData.put("2017-8-9" , "1");
        markData.put("2017-7-9" , "0");
        markData.put("2017-6-9" , "1");
        markData.put("2017-6-10" , "0");
        calendarAdapter.setMarkData(markData);
    }

    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {

            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                monthPager.setCurrentItem(mCurrentPage + offset);
            }
        };
    }

    private void refreshClickDate(CalendarDate date) {
        currentDate = date;
        textViewYearDisplay.setText(date.getYear() + "年");
        textViewMonthDisplay.setText(date.getMonth() + "");
    }

    private void initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getAllItems();
                if(currentCalendars.get(position % currentCalendars.size()) instanceof Calendar){
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getShowCurrentDate();
                    currentDate = date;
                    textViewYearDisplay.setText(date.getYear() + "年");
                    textViewMonthDisplay.setText(date.getMonth() + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onClickBackToDayBtn() {
        refreshMonthPager();
    }

    private void refreshMonthPager() {
        calendarAdapter = new CalendarViewAdapter(context , onSelectDateListener);
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        CalendarDate today = new CalendarDate();
        calendarAdapter.updateState(today);
        refreshClickDate(today);
    }
}

