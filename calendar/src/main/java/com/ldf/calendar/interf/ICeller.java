package com.ldf.calendar.interf;

import android.graphics.Canvas;

import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.DayView;

/**
 * Created by ldf on 17/6/26.
 */

public interface ICeller {

    void refreshContent(CalendarDate date, DayView.State state);

    void drawDay(Canvas canvas, float posX, float posY);
}
