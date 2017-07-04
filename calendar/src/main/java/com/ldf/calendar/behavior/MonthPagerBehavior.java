package com.ldf.calendar.behavior;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.view.MonthPager;

/**
 * Created by ldf on 17/6/15.
 */

public class MonthPagerBehavior extends CoordinatorLayout.Behavior<MonthPager> {
    private int top;
    private int initRecyclerViewTop;
    private int touchSlop = 24;

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
        if (dependentViewTop != -1) {
            int dy = dependency.getTop() - dependentViewTop;    //dependency对其依赖的view(本例依赖的view是RecycleView)

            int top = child.getTop();

            if( dy > touchSlop){
                calendarViewAdapter.switchToMonth();
            } else if(dy < - touchSlop){
                calendarViewAdapter.switchToWeek(child.getRowIndex());
            }

            if (dy > -top){
                dy = -top;
            }

            if (dy < -top - child.getTopMovableDistance()){
                dy = -top - child.getTopMovableDistance();
            }

            child.offsetTopAndBottom(dy);
        } else {
            initRecyclerViewTop = dependency.getTop();
        }

        dependentViewTop = dependency.getTop();
        top = child.getTop();

        if((initRecyclerViewTop - dependentViewTop) >= child.getCellHeight()) {
            Utils.setScrollToBottom(false);
            calendarViewAdapter.switchToWeek(child.getRowIndex());
            initRecyclerViewTop = dependentViewTop;
        }
        if((dependentViewTop - initRecyclerViewTop) >= child.getCellHeight()) {
            Utils.setScrollToBottom(true);
            calendarViewAdapter.switchToMonth();
            initRecyclerViewTop = dependentViewTop;
        }

        return true;
        // TODO: 16/12/8 dy为负时表示向上滑动，dy为正时表示向下滑动，dy为零时表示滑动停止
    }
}
