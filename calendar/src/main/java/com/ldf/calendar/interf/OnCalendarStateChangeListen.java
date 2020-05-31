package com.ldf.calendar.interf;

public  interface OnCalendarStateChangeListen {
    /**
     *  使日历状态发生该改变的类型
     *   MonthScrollFull 滑动日历视图超出展开日历大小
     *   MonthScrollNotFull 滑动日历视图不满展开日历大小 自动滑动
     *   LinkageScrollFull   滑动联动视图超出展开日历大小
     *    LinkageScrollNotFull  滑动联动视图不满展开日历大小 自动滑动
     */
     int MonthScrollFull = 0;
     int MonthScrollNotFull = 1;
     int LinkageScrollFull = 2;
     int LinkageScrollNotFull = 3;
     void onCalendarStateChange(boolean state,int type);
}
