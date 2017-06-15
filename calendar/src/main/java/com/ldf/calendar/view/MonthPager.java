package com.ldf.calendar.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import com.ldf.calendar.MonthPagerBehavior;
import com.ldf.calendar.adpter.CalendarViewAdapter;

@CoordinatorLayout.DefaultBehavior(MonthPagerBehavior.class)
public class MonthPager extends ViewPager {
    public static int CURRENT_DAY_INDEX = 600;

    private int currentPosition = CURRENT_DAY_INDEX;

    private int cellHeight = 0;

    private int rowIndex = 6;

    private int mCellSpace;
    private int totalRow = 6;

    private ViewPager.OnPageChangeListener viewPageChangeListener;
    private OnPageChangeListener monthPageChangeListener;
    private boolean pageChangeByGesture = false;
    private boolean hasPageChangeListener = false;
    private int selectedCell = 10;

    public MonthPager(Context context) {
        this(context, null);
        init();
    }

    public MonthPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        viewPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(monthPageChangeListener != null) {
                    monthPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (pageChangeByGesture) {
                    if(monthPageChangeListener != null) {
                        monthPageChangeListener.onPageSelected(position);
                    }
                    pageChangeByGesture = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                CalendarViewAdapter adapter = (CalendarViewAdapter) getAdapter();
                adapter.updateState();
                if(monthPageChangeListener != null) {
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
        if(hasPageChangeListener) {
            Log.e("ldf","MonthPager Just Can Use Own OnPageChangeListener");
        } else {
            super.addOnPageChangeListener(listener);
        }
    }

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        this.monthPageChangeListener = listener;
        Log.e("ldf","MonthPager Just Can Use Own OnPageChangeListener");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mCellSpace = Math.min(h / 6, w / 7);
        super.onSizeChanged(w, h, oldW, oldH);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mCellSpace > 0){
            super.onMeasure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(mCellSpace * 6,
                    MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        void onPageSelected(int position);
        void onPageScrollStateChanged(int state);
    }

    public void setSelectedCell(int selectedCell) {
        Log.e("ldf","selectedCell = " + selectedCell);
        this.selectedCell = selectedCell;
        rowIndex = selectedCell / 7;
    }

    public int getSelectedCell(){
        return selectedCell;
    }

    public void setSelecteCellOffset(int offset) {
        selectedCell = selectedCell + offset;
        if(selectedCell < 0){
            selectedCell = 42 + selectedCell % 42;
        }
    }

    public int getTopMovableDistance() {
        rowIndex = selectedCell / 7;
        return cellHeight * rowIndex;
    }

    public int getCellHeight() {
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) getAdapter();
        cellHeight = calendarViewAdapter.getPagers().get(currentPosition  % 3).getCellHeight();
        return cellHeight;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
