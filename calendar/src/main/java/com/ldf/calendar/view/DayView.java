package com.ldf.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ldf.calendar.Utils;
import com.ldf.calendar.interf.ICeller;
import com.ldf.calendar.model.CalendarDate;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by ldf on 16/10/19.
 */

public class DayView extends RelativeLayout implements ICeller{

    private WeakReference<Calendar> calendar;
    public State state;
    public CalendarDate date;
    public Context context;

    /**
     * Constructor. Sets up the DayView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the DayView
     */
    public DayView(Context context, int layoutResource) {
        super(context);
        setupLayoutResource(layoutResource);
    }

    /**
     * Sets the layout resource for a custom DayView.
     *
     * @param layoutResource
     */
    private void setupLayoutResource(int layoutResource) {

        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);

        inflated.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        inflated.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
    }

    private void drawMarker(Canvas canvas, CalendarDate date, HashMap<String, String> markDateData) {
        if(markDateData != null){
            if(!date.toString().equals(new CalendarDate().toString())){//今天时不绘制MARK
                if(markDateData.containsKey(date.toString())){
                    if(markDateData.get(date.toString()).equals("0")){
                    }else{
                    }
                }
            }
        }
    }

    // be used to update the content (user-interface)
    @Override
    public void refreshContent(CalendarDate date, State state) {
        this.state = state;
        this.date = date;
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    public void drawDay(Canvas canvas , float posX , float posY) {
        int saveId = canvas.save();
        canvas.translate(posX, posY);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    public void drawData(){

    }

    public void drawHighlight(){

    }

    public void drawExtras(){
        drawLines();
    }

    public void drawLines(){

    }

    public enum State {
        TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, CLICK_DAY
    }
}