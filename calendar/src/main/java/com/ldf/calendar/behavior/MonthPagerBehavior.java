package com.ldf.calendar.behavior;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnCalendarStateChangeListen;
import com.ldf.calendar.view.MonthPager;
import java.lang.reflect.*;

/**
 * Created by ldf on 17/6/15.
 */

/**
 *  Modify by zzerX on 20/5/10.
 *  <T extends ViewGroup & OnCalendarStateChangeListen>
 *     用于规定与MothPager联动的View的类型{@link  LinkageViewBehavior},
 *     使其可以使用RecyclerView ViewPager 等视作为联动
 *
 */

public class MonthPagerBehavior<T extends View & OnCalendarStateChangeListen> extends CoordinatorLayout.Behavior<MonthPager> {
    private int top = 0;
    private int touchSlop = 1;
    private int offsetY = 0;


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, MonthPager child, View dependency) {
		Type superclass = this.getClass();
		//Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0]; 
		Log.v("type","泛型类型"  + superclass);
        return dependency instanceof View;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, MonthPager child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        child.offsetTopAndBottom(top);
        return true;
    }

    private float downX, downY, lastY, lastTop;
    private boolean isVerticalScroll;
    private boolean directionUpa;

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, MonthPager child, MotionEvent ev) {
        if (downY > lastTop) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isVerticalScroll) {
                    if (ev.getY() > lastY) {
                        Utils.setScrollToBottom(true);
                        directionUpa = false;
                    } else {
                        Utils.setScrollToBottom(false);
                        directionUpa = true;
                    }

                    if (lastTop < child.getViewHeight() / 2 + child.getCellHeight() / 2) {
                        //这里表示本来是收缩状态
                        if (ev.getY() - downY <= 0 || Utils.loadTop() >= child.getViewHeight()) {
                            //向上滑或者已展开了
                            lastY = ev.getY();
                            return true;
                        }
                        if (ev.getY() - downY + child.getCellHeight() >= child.getViewHeight()) {
                            //将要滑过头了
                            saveTop(child.getViewHeight());
                            Utils.scrollTo(parent, (T) parent.getChildAt(1), child.getViewHeight(), 10);
                            isVerticalScroll = false;
                            child.onCalendarStateChange(true,OnCalendarStateChangeListen.MonthScrollFull);//回调日历状态改变
                        } else {
                            //正常下滑
                            saveTop((int) (child.getCellHeight() + ((ev.getY() - downY))));
                            Utils.scroll(parent.getChildAt(1), (int) (lastY - ev.getY()),
                                    child.getCellHeight(), child.getViewHeight());
                        }
                    } else {
                        if (ev.getY() - downY >= 0 || Utils.loadTop() <= child.getCellHeight()) {
                            lastY = ev.getY();
                            return true;
                        }

                        if (ev.getY() - downY + child.getViewHeight() <= child.getCellHeight()) {
                            //将要滑过头了
                            saveTop(child.getCellHeight());
                            Utils.scrollTo(parent, (T) parent.getChildAt(1), child.getCellHeight(), 10);
                            isVerticalScroll = false;
                            child.onCalendarStateChange(false,OnCalendarStateChangeListen.MonthScrollFull);//回调日历状态改变
                        } else {
                            //正常上滑
                            saveTop((int) (child.getViewHeight() + ((ev.getY() - downY))));
                            Utils.scroll(parent.getChildAt(1), (int) (lastY - ev.getY()),
                                    child.getCellHeight(), child.getViewHeight());

                        }
                    }

                    lastY = ev.getY();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isVerticalScroll) {

                    child.setScrollable(true);

                    CalendarViewAdapter calendarViewAdapter =
                            (CalendarViewAdapter) child.getAdapter();
                    if (calendarViewAdapter != null) {
                        if (directionUpa) {
                            Utils.setScrollToBottom(true);
                            calendarViewAdapter.switchToWeek(child.getRowIndex());
                            Utils.scrollTo(parent, (T) parent.getChildAt(1), child.getCellHeight(), 300);
                            child.onCalendarStateChange(false,OnCalendarStateChangeListen.MonthScrollNotFull);//回调日历状态改变
                        } else {
                            Utils.setScrollToBottom(false);
                            calendarViewAdapter.switchToMonth();
                            Utils.scrollTo(parent, (T) parent.getChildAt(1), child.getViewHeight(), 300);
                            child.onCalendarStateChange(true,OnCalendarStateChangeListen.MonthScrollNotFull);//回调日历状态改变
                        }
                    }

                    isVerticalScroll = false;
                    return true;
                }
                break;
        }
        return false;
    }

    private void saveTop(int top) {
        Utils.saveTop(top);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, MonthPager child, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                lastTop = Utils.loadTop();
                lastY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (downY > lastTop) {
                    return false;
                }
                if (Math.abs(ev.getY() - downY) > 25 && Math.abs(ev.getX() - downX) <= 25
                        && !isVerticalScroll) {
                    isVerticalScroll = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isVerticalScroll) {
                    isVerticalScroll = false;
                    return true;
                }
                break;
        }
        return isVerticalScroll;
    }

    private int dependentViewTop = -1;

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, MonthPager child, View dependency) {
        CalendarViewAdapter calendarViewAdapter = (CalendarViewAdapter) child.getAdapter();
        if (dependentViewTop != -1) {
            int dy = dependency.getTop() - dependentViewTop;
            int top = child.getTop();
            if (dy > touchSlop) {
                calendarViewAdapter.switchToMonth();
            } else if (dy < -touchSlop) {
                calendarViewAdapter.switchToWeek(child.getRowIndex());
            }

            if (dy > -top) {
                dy = -top;
            }

            if (dy < -top - child.getTopMovableDistance()) {
                dy = -top - child.getTopMovableDistance();
            }

            child.offsetTopAndBottom(dy);
            Log.e("ldf", "onDependentViewChanged = " + dy);

        }

        dependentViewTop = dependency.getTop();
        top = child.getTop();

        if (offsetY > child.getCellHeight()) {
            calendarViewAdapter.switchToMonth();
        }
        if (offsetY < -child.getCellHeight()) {
            calendarViewAdapter.switchToWeek(child.getRowIndex());
        }

        if (dependentViewTop > child.getCellHeight() - 24
                && dependentViewTop < child.getCellHeight() + 24
                && top > -touchSlop - child.getTopMovableDistance()
                && top < touchSlop - child.getTopMovableDistance()) {
            Utils.setScrollToBottom(true);
            calendarViewAdapter.switchToWeek(child.getRowIndex());
            offsetY = 0;
        }
        if (dependentViewTop > child.getViewHeight() - 24
                && dependentViewTop < child.getViewHeight() + 24
                && top < touchSlop
                && top > -touchSlop) {
            Utils.setScrollToBottom(false);
            calendarViewAdapter.switchToMonth();
            offsetY = 0;
        }

        return true;
        // TODO: 16/12/8 dy为负时表示向上滑动，dy为正时表示向下滑动，dy为零时表示滑动停止
    }
}
