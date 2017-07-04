package com.ldf.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ldf.calendar.component.State;
import com.ldf.calendar.interf.IDayRenderer;
import com.ldf.calendar.model.CalendarDate;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by ldf on 16/10/19.
 */

public abstract class DayView extends RelativeLayout implements IDayRenderer {

    protected State state;
    protected CalendarDate date;
    protected Context context;
    protected int layoutResource;

    /**
     * Constructor. Sets up the DayView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the DayView
     */
    public DayView(Context context, int layoutResource) {
        super(context);
        setupLayoutResource(layoutResource);
        this.context = context;
        this.layoutResource = layoutResource;
    }

    /**
     * Sets the layout resource for a custom DayView.
     *
     * @param layoutResource
     */
    private void setupLayoutResource(int layoutResource) {
        LayoutInflater.from(getContext()).inflate(layoutResource, this);
    }

    @Override
    public void refreshDate(CalendarDate date) {
        this.date = date;
        refreshContent(date , state);
    }

    @Override
    public void refreshState(State state) {
        this.state = state;
        refreshContent(date , state);
    }

    @Override
    public void refreshContent(CalendarDate date, State state) {
        this.state = state;
        this.date = date;
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public CalendarDate getDate() {
        return date;
    }

    @Override
    public void drawDay(Canvas canvas , float posX , float posY) {
        int saveId = canvas.save();
        canvas.translate(posX * getMeasuredWidth(), posY * getMeasuredHeight());
        draw(canvas);
        canvas.restoreToCount(saveId);
    }
}