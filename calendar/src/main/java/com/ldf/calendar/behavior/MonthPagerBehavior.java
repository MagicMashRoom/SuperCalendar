package com.ldf.calendar.behavior;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ldf.calendar.adpter.CalendarViewAdapter;
import com.ldf.calendar.view.MonthPager;

/**
 * Created by ldf on 17/6/15.
 */

public class MonthPagerBehavior extends CoordinatorLayout.Behavior<MonthPager> {
    private int top;
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
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) child.getAdapter();
        if (dependentViewTop != -1) {
            int dy = dependency.getTop() - dependentViewTop;    //dependency对其依赖的view(本例依赖的view是RecycleView)

            int top = child.getTop();

            if( dy > touchSlop){
                Log.e("ldf","switchToMonth");
                calendarViewAdapter.switchToMonth();
            } else if(dy < - touchSlop){
                Log.e("ldf","switchToWeek");
                calendarViewAdapter.switchToWeek(child.getRowIndex());
            }

            if (dy > -top){
                dy = -top;
            }

            if (dy < -top - child.getTopMovableDistance()){
                dy = -top - child.getTopMovableDistance();
            }

            child.offsetTopAndBottom(dy);
        }
        dependentViewTop = dependency.getTop();
        top = child.getTop();

        return true;
        // TODO: 16/12/8 dy为负时表示向上滑动，dy为正时表示向下滑动，dy为零时表示滑动停止
    }
}
