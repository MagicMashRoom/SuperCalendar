package com.hqyxjy.ldf.supercalendar;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldf.calendar.component.State;
import com.ldf.calendar.interf.IDayRenderer;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.DayView;

/**
 * Created by ldf on 17/6/26.
 */

public class CustomDayView extends DayView {

    private TextView dateTv;
    private ImageView marker;
    private View selectedBackground;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomDayView(Context context, int layoutResource) {
        super(context, layoutResource);
        dateTv = (TextView) findViewById(R.id.date);
        marker = (ImageView) findViewById(R.id.maker);
        selectedBackground = findViewById(R.id.selected_background);
    }

    // be used to update the content (user-interface)
    @Override
    public void refreshContent(CalendarDate date, State state) {
        Log.e("ldf","date = " + date.toString());
        if(date != null) {
            dateTv.setText(date.day + "");
        }
        if(state == State.CLICK_DAY || state == State.TODAY) {
            selectedBackground.setVisibility(VISIBLE);
        } else {
            selectedBackground.setVisibility(GONE);
        }
        super.refreshContent(date, state);
    }

    @Override
    public IDayRenderer copy() {
        return new CustomDayView(context , layoutResource);
    }
}
