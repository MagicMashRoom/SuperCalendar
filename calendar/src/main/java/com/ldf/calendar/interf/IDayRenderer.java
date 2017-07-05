package com.ldf.calendar.interf;

import android.graphics.Canvas;

import com.ldf.calendar.component.State;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Day;
import com.ldf.calendar.view.DayView;

/**
 * Created by ldf on 17/6/26.
 */

public interface IDayRenderer {

    void refreshContent();

    void drawDay(Canvas canvas, Day day);

    IDayRenderer copy();

}
