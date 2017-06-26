# SuperCalendar
==========================
> 简单使用的日历  [项目链接 求Star](https://github.com/MagicMashRoom/SuperCalendar)
> 
>

# Example 
![Example](http://upload-images.jianshu.io/upload_images/3874191-366d7f0d343989c9.gif?imageMogr2/auto-orient/strip)

Usage
-----

Include `MonthPager` in your layout XML.
MonthPager必须和下方的RecyclerView包裹在同一个CoordinatorLayout内.且RecyclerView的layout_behavior为com.ldf.calendar.behavior.RecyclerViewBehavior

```xml
 <android.support.design.widget.CoordinatorLayout
        android:id="@+id/content"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1">

        <com.ldf.calendar.view.MonthPager
            android:id="@+id/calendar_view"
            android:layout_width="match_parent"
            android:layout_height="300dp"
            android:background="#fff">
        </com.ldf.calendar.view.MonthPager>

        <android.support.v7.widget.RecyclerView
            android:id="@+id/list"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_behavior="com.ldf.calendar.behavior.RecyclerViewBehavior"
            android:background="#c2c2c2"
            android:layout_gravity="bottom"/>

    </android.support.design.widget.CoordinatorLayout>
    
```
目前来看 相比于Dialog选择日历 我的控件更适合于Activity/Fragment在Activity的`onCreate`   或者Fragment的`onCreateView`  你需要实现这两个方法来启动日历并装填进数据

```java
@Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        initCalendarView();
    }
    
    private void initCalendarView() {
        initListener();
        calendarAdapter = new CalendarViewAdapter(context ,
                onSelectDateListener ,
                Calendar.MONTH_TYPE);
        initMarkData();
        initMonthPager();
    } 
```

使用此方法回调日历点击事件
```java
private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                monthPager.setCurrentItem(mCurrentPage + offset);
            }
        };
    }
```
 
 使用此方法初始化日历标记数据
```java
private void initMarkData() {
       HashMap markData = new HashMap<>();
       markData.put("2017-8-9" , "1");
       markData.put("2017-7-9" , "0");
       markData.put("2017-6-9" , "1");
       markData.put("2017-6-10" , "0");
       calendarAdapter.setMarkData(markData);
   }
```
 使用此方法给MonthPager添加上相关监听
```java
monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getAllItems();
                if(currentCalendars.get(position % currentCalendars.size()) instanceof Calendar){
                    //you code
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
```
Download
--------
Gradle:
Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
	...
	maven { url 'https://www.jitpack.io' }
    }
}
```
Step 2. Add the dependency

```groovy
	dependencies {
	        compile 'com.github.MagicMashRoom:SuperCalendar:v1.1.0'
	}

```
[![](https://www.jitpack.io/v/MagicMashRoom/SuperCalendar.svg)](https://www.jitpack.io/#MagicMashRoom/SuperCalendar)

# Licence

      Copyright 2017 MagicMashRoom, Inc.
