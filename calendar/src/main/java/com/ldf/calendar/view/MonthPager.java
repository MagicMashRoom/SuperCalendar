package com.ldf.calendar.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ldf.calendar.behavior.MonthPagerBehavior;
import com.ldf.calendar.component.CalendarViewAdapter;

@CoordinatorLayout.DefaultBehavior(MonthPagerBehavior.class)
public class MonthPager extends ViewPager {
    public static int CURRENT_DAY_INDEX = 1000;

    private int currentPosition = CURRENT_DAY_INDEX;
    private int cellHeight;
    private int viewHeight;
    private int rowIndex = 6;

    private OnPageChangeListener monthPageChangeListener;
    private boolean pageChangeByGesture = false;
    private boolean hasPageChangeListener = false;
    private boolean scrollable = true;
    private int pageScrollState = ViewPager.SCROLL_STATE_IDLE;

    public MonthPager(Context context) {
        this(context, null);
    }

    public MonthPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ViewPager.OnPageChangeListener viewPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (monthPageChangeListener != null) {
                    monthPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (pageChangeByGesture) {
                    if (monthPageChangeListener != null) {
                        monthPageChangeListener.onPageSelected(position);
                    }
                    pageChangeByGesture = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                pageScrollState = state;
                if (monthPageChangeListener != null) {
                    monthPageChangeListener.onPageScrollStateChanged(state);
                }
                pageChangeByGesture = true;
            }
        };
        addOnPageChangeListener(viewPageChangeListener);
        hasPageChangeListener = true;
    }

    @Override
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (hasPageChangeListener) {
            Log.e("ldf", "MonthPager Just Can Use Own OnPageChangeListener");
        } else {
            super.addOnPageChangeListener(listener);
        }
    }

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        this.monthPageChangeListener = listener;
        Log.e("ldf", "MonthPager Just Can Use Own OnPageChangeListener");
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (!scrollable)
            return false;
        else
            return super.onTouchEvent(me);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent me) {
        if (!scrollable)
            return false;
        else
            return super.onInterceptTouchEvent(me);
    }

    public void selectOtherMonth(int offset) {
        setCurrentItem(currentPosition + offset);
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) getAdapter();
        calendarViewAdapter.notifyDataChanged(CalendarViewAdapter.loadSelectedDate());
    }

    public int getPageScrollState() {
        return pageScrollState;
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public int getTopMovableDistance() {
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) getAdapter();
        if(calendarViewAdapter == null) {
            return cellHeight;
        }
        rowIndex = calendarViewAdapter.getPagers().get(currentPosition % 3).getSelectedRowIndex();
        return cellHeight * rowIndex;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setViewHeight(int viewHeight) {
        cellHeight = viewHeight / 6;
        this.viewHeight = viewHeight;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getRowIndex() {
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) getAdapter();
        rowIndex = calendarViewAdapter.getPagers().get(currentPosition % 3).getSelectedRowIndex();
        Log.e("ldf", "getRowIndex = " + rowIndex);
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
