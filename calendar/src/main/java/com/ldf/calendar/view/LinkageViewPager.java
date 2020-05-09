package com.ldf.calendar.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.ldf.calendar.behavior.ViewPagerBehavior;
import com.ldf.calendar.interf.OnCalendarStateChangeListen;

public class LinkageViewPager extends ViewPager implements OnCalendarStateChangeListen {
    public LinkageViewPager(@NonNull Context context) {
        super(context);
    }
    public LinkageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnCalendarStateChangeListen onCalendarListen;


    public void setOnCalendarListen(OnCalendarStateChangeListen onCalendarListen){
        this.onCalendarListen = onCalendarListen;
    }

    @Override
    public void onCalendarStateChange(boolean state)
        {
            if( onCalendarListen != null){
                onCalendarListen.onCalendarStateChange(state);
            }
        }

}
