package com.ldf.calendar.view;

import android.graphics.Canvas;

import com.ldf.calendar.Const;
import com.ldf.calendar.interf.IDayRenderer;

/**
 * Created by ldf on 17/6/27.
 */

public class Week {
    public int row;

    public Week(int row) {
        this.row = row;
    }

    public IDayRenderer[] days = new IDayRenderer[Const.TOTAL_COL];

    public void setDays(IDayRenderer[] days) {
        this.days = days;
    }

    public void drawRow(Canvas canvas) {
        for (int col = 0; col < days.length; col++) {
            if (days[col] != null)
                days[col].drawDay(canvas , col , row );
        }
    }
}
