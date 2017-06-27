package com.ldf.calendar.interf;

import android.graphics.Canvas;

import com.ldf.calendar.component.State;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.DayView;

/**
 * Created by ldf on 17/6/26.
 */

public interface IDayRenderer {

    void refreshDate(CalendarDate date);

    void refreshState(State state);

    void refreshContent(CalendarDate date , State state);

    State getState();

    CalendarDate getDate();

    IDayRenderer copy();

    void drawDay(Canvas canvas, float posX, float posY);

}
