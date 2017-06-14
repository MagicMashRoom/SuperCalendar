package com.ldf.calendar.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ldf.calendar.adpter.CalendarViewAdapter;
import com.ldf.calendar.Utils;

@CoordinatorLayout.DefaultBehavior(MonthPager.Behavior.class)
public class MonthPager extends ViewPager {
    public static int CURRENT_DAY_INDEX = 600;
    private static Context context;
    private int mCellSpace;
    private static int rowCount = 6;
    private int totalRow = 6;
    private int cellHeight = 0;

    private ViewPager.OnPageChangeListener viewPageChangeListener;
    private OnPageChangeListener monthPageChangeListener;
    private boolean pageChangeByGesture = false;
    private boolean hasPageChangeListener = false;
    private int selectedCell = 10;

    public MonthPager(Context context) {
        this(context, null);
        this.context = context;
        init();
    }

    public MonthPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
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
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) adapter;
        cellHeight = calendarViewAdapter.getPagers().get(0).getCellHeight();
        Log.e("ldf","cellHeight = " + cellHeight);
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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
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

    public static class Behavior extends CoordinatorLayout.Behavior<MonthPager> {
        private int top;

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, MonthPager child, View dependency) {
            return dependency instanceof RecyclerView;
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, MonthPager child, int layoutDirection) {
            parent.onLayoutChild(child, layoutDirection);
            child.offsetTopAndBottom(top);
            return true;
        }

        private int dependentViewTop = -1;

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, MonthPager child, View dependency) {
            Log.e("ldf","onDependentViewChanged");
            CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) child.getAdapter();
            int touchSlop = Utils.getTouchSlop(context);
            if (dependentViewTop != -1) {
                int dy = dependency.getTop() - dependentViewTop;    //dependency对其依赖的view(本例依赖的view是RecycleView)
                Log.e("ldf","dy1 = " + dy);
                int top = child.getTop();
                Log.e("ldf","top = " + top);

                if(dy > touchSlop){
                    calendarViewAdapter.switchToMonthType();
                } else if(dy < - touchSlop){
                    calendarViewAdapter.switchToWeekType(rowCount);
                }

                if (dy > -top){
                    dy = -top;
                }
                Log.e("ldf","dy2 = " + dy);

                if (dy < -top - child.getTopMovableDistance()){
                    dy = -top - child.getTopMovableDistance();
                }
                Log.e("ldf","dy3 = " + dy);

                Log.e("ldf","monthPager dy = " + dy);
                child.offsetTopAndBottom(dy);
            }
            dependentViewTop = dependency.getTop();
            top = child.getTop();

            return true;
            // TODO: 16/12/8 dy为负时表示向上滑动，dy为正时表示向下滑动，dy为零时表示滑动停止
        }
    }

    public void setSelectedCell(int selectedCell) {
        Log.e("ldf","selectedCell = " + selectedCell);
        this.selectedCell = selectedCell;
        rowCount = selectedCell / 7;
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
        rowCount = selectedCell / 7;
        Log.e("ldf","cellHeight = " + cellHeight + "" + rowCount);
        return cellHeight * rowCount;
    }
    public int getMaxMovableDistance() {
        Log.e("ldf","cellHeight = " + cellHeight);
        return getHeight() - cellHeight; //getHeight为本控件的高度
    }
}
