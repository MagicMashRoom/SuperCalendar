package com.ldf.calendar.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import com.ldf.calendar.Utils;
import com.ldf.calendar.view.MonthPager;

public class RecyclerViewBehavior extends CoordinatorLayout.Behavior<RecyclerView> {
    private int initOffset = -1;
    private int minOffset = -1;
    private Context context;

    public RecyclerViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        MonthPager monthPager = getMonthPager(parent);
        if (monthPager.getBottom() > 0 && initOffset == -1) {
            initOffset = monthPager.getBottom();
            saveTop(initOffset);
        }
        child.offsetTopAndBottom(Utils.loadTop());
        minOffset = getMonthPager(parent).getCellHeight();
        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) child.getLayoutManager();
        if(linearLayoutManager.findFirstCompletelyVisibleItemPosition() > 0) {
            return false;
        }
        MonthPager monthPager = (MonthPager) coordinatorLayout.getChildAt(0);
        if(monthPager.getPageScrollState() != ViewPager.SCROLL_STATE_IDLE) {
            return false;
        }
        monthPager.setScrollable(false);
        boolean isVertical = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        int firstRowVerticalPosition =
                (child == null || child.getChildCount() == 0) ? 0 : child.getChildAt(0).getTop();
        boolean recycleviewTopStatus = firstRowVerticalPosition >= 0;
        return isVertical && (recycleviewTopStatus || !Utils.isScrollToBottom()) && child == directTargetChild;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, RecyclerView child,
                                  View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (child.getTop() <= initOffset && child.getTop() >= minOffset) {
            consumed[1] = Utils.scroll(child, dy, minOffset, initOffset);
            saveTop(child.getTop());
        }
    }

    @Override
    public void onStopNestedScroll(final CoordinatorLayout parent, final RecyclerView child, View target) {
        Log.e("ldf","onStopNestedScroll");
        super.onStopNestedScroll(parent, child, target);
        MonthPager monthPager = (MonthPager) parent.getChildAt(0);
        monthPager.setScrollable(true);
        if (!Utils.isScrollToBottom()) {
            if (initOffset - Utils.loadTop() > Utils.getTouchSlop(context)){
                scrollTo(parent, child, minOffset, 200);
            } else {
                scrollTo(parent, child, initOffset, 80);
            }
        } else {
            if (Utils.loadTop() - minOffset > Utils.getTouchSlop(context)){
                scrollTo(parent, child, initOffset, 200);
            } else {
                scrollTo(parent, child, minOffset, 80);
            }
        }
    }

    private void scrollTo(final CoordinatorLayout parent, final RecyclerView child, final int y, int duration){
        final Scroller scroller = new Scroller(parent.getContext());
        scroller.startScroll(0, Utils.loadTop(), 0, y - Utils.loadTop(), duration);   //设置scroller的滚动偏移量

        ViewCompat.postOnAnimation(child, new Runnable() {
            @Override
            public void run() {
                //返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。
                // 这是一个很重要的方法，通常放在View.computeScroll()中，用来判断是否滚动是否结束。
                if (scroller.computeScrollOffset()) {
                    int delta = scroller.getCurrY() - child.getTop();
                    child.offsetTopAndBottom(delta);
                    saveTop(child.getTop());
                    parent.dispatchDependentViewsChanged(child);
                    ViewCompat.postOnAnimation(child, this);
                } else {
                    MonthPager monthPager = (MonthPager) parent.getChildAt(0);
                    if(monthPager.getTop() < 0) {
                        if(monthPager.getTop() +  monthPager.getTopMovableDistance() >= 0) {
                            monthPager.offsetTopAndBottom(- monthPager.getTop()
                                    -  monthPager.getTopMovableDistance());
                        } else {
                            monthPager.offsetTopAndBottom(- monthPager.getTop());
                        }
                        parent.dispatchDependentViewsChanged(child);
                    }
                }
            }
        });
    }

    private MonthPager getMonthPager(CoordinatorLayout coordinatorLayout) {
        MonthPager monthPager = (MonthPager) coordinatorLayout.getChildAt(0);
        return monthPager;
    }

    private void saveTop(int top){
        Utils.saveTop(top);
        if (Utils.loadTop() == initOffset){
            Utils.setScrollToBottom(false);
        } else if (Utils.loadTop() == minOffset){
            Utils.setScrollToBottom(true);
        }
    }
}
