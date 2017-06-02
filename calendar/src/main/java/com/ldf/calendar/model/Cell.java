package com.ldf.calendar.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.ldf.calendar.utils.Utils;
import com.ldf.calendar.views.Calendar;

import java.util.HashMap;

/**
 * Created by ldf on 16/10/19.
 */

public class Cell {

    public CalendarDate cellDate;
    public Calendar.State state;
    public int col;
    public int row;
    public static int width;
    public static int height;
    public static Paint datePaint;
    public static Paint circlePaint;
    public static Paint markPaint;
    public static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Cell.context = context;
    }

    public static void setDatePaint(Paint datePaint) {
        Cell.datePaint = datePaint;
    }

    public static void setCirclePaint(Paint circlePaint) {
        Cell.circlePaint = circlePaint;
    }

    public static void setMarkPaint(Paint markPaint) {
        Cell.markPaint = markPaint;
    }

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        Cell.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        Cell.height = height;
    }

    public Cell(CalendarDate date, Calendar.State state, int col, int row) {
        super();
        this.cellDate = date;
        this.state = state;
        this.col = col;
        this.row = row;
    }

    // 绘制一个单元格 如果颜色需要自定义可以修改
    public void drawCell(Canvas canvas, HashMap<String, String> markDateData) {
        drawMark(canvas, cellDate, markDateData);
        switch (state) {
            case CURRENT_MONTH_DAY:
                datePaint.setColor(Color.parseColor("#FF333333"));
                break;
            case NEXT_MONTH_DAY:
            case PAST_MONTH_DAY:
                datePaint.setColor(Color.parseColor("#FFE3E3E3"));
                break;
            case TODAY:
                datePaint.setColor(Color.parseColor("#FF25ADFF"));
                circlePaint.setColor(Color.parseColor("#FF25ADFF"));
                circlePaint.setStyle(Paint.Style.STROKE);
                circlePaint.setStrokeWidth(3f);
                canvas.drawCircle((float) (width * (col + 0.5)),
                        (float) ((row + 0.5) * height), Utils.dpi2px(context, 33) / 2,
                        circlePaint);
                circlePaint.setStyle(Paint.Style.FILL);
                break;
            case CLICK_DAY:
                datePaint.setColor(Color.parseColor("#FFFFFFFF"));
                circlePaint.setColor(Color.parseColor("#FF25ADFF"));
                canvas.drawCircle((float) (width * (col + 0.5)),
                        (float) ((row + 0.5) * height), Utils.dpi2px(context, 33) / 2,
                        circlePaint);
                break;
        }
        drawDateText(canvas);
    }



    private void drawMark(Canvas canvas, CalendarDate date, HashMap<String, String> markDateData) {
        if(markDateData != null){
            if(!date.toString().equals(new CalendarDate().toString())){//今天时不绘制MARK
                if(markDateData.containsKey(date.toString())){
                    if(markDateData.get(date.toString()).equals("0")){
                        markPaint.setColor(Color.parseColor("#FFFE6D6D"));
                        canvas.drawCircle((float) (width * (col + 0.5)),
                                (float) ((row + 0.5) * height + Utils.dpi2px(context, 12)) ,
                                Utils.dpi2px(context, 3),
                                markPaint);
                    }else{
                        markPaint.setColor(Color.parseColor("#FFCCCCCC"));
                        canvas.drawCircle((float) (width * (col + 0.5)),
                                (float) ((row + 0.5) * height + Utils.dpi2px(context, 12)) ,
                                Utils.dpi2px(context, 3),
                                markPaint);
                    }
                }
            }
        }
    }

    private void drawDateText(Canvas canvas) {
        // 绘制文字
        String date = this.cellDate.day+"";
        CalendarDate today = new CalendarDate();
        if(this.cellDate.toString().equals(today.toString())){
            date = "今";
        }
        if(CalendarDate.isSameMonth(today , this.cellDate)
                && this.cellDate.day < today.day
                && state == Calendar.State.CURRENT_MONTH_DAY){
            datePaint.setColor(Color.parseColor("#888888"));
        }
        canvas.drawText(date,
                (float) ((col + 0.5) * width - datePaint.measureText(date) / 2),
                (float) ((row + 0.5) * height + Utils.dpi2px(context, 4)),
                datePaint);

    }

    private void setState(Calendar.State state){
        this.state = state;
    }
}