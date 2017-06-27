package com.ldf.calendar.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldf.calendar.model.CalendarDate;
import com.ldf.mi.calendar.R;

/**
 * Created by ldf on 17/6/26.
 */

public class CustomDayView extends DayView{

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
        if(date != null) {
            dateTv.setText(date.day);
        }
        if(state == State.CLICK_DAY || state == State.TODAY) {
            selectedBackground.setVisibility(VISIBLE);
        }
        super.refreshContent(date, state);
    }
}
