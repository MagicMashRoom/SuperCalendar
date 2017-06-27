# SuperCalendar
==========================
> Simple Use Calendar  [project link if you like please star](https://github.com/MagicMashRoom/SuperCalendar)
> [中文介绍](https://magicmashroom.github.io/blog/2017/06/27/SuperCalendar/)
> 
>
# update
* support custom date click effect (if you want to experient new effect please compile v1.2.0)

![new](http://upload-images.jianshu.io/upload_images/3874191-88dc2d783a833c8d.gif =100x)

####      change click effect

* new CustomDayView extends DayView
* override refreshContent and copy method
```java
@Override
    public void refreshContent(CalendarDate date, State state) {
        //you code
        super.refreshContent(date, state);
    }

    @Override
    public IDayRenderer copy() {
        return new CustomDayView(context , layoutResource);
    }
```
* new CustomDayView object，and new CalendarViewAdapter with CustomDayView
```java
CustomDayView customDayView = new CustomDayView(
        context , R.layout.custom_day);
calendarAdapter = new CalendarViewAdapter(
                context ,
                onSelectDateListener ,
                Calendar.MONTH_TYPE ,
                customDayView);
```
* success if not clear please down DEMO
# Example 
![Example](http://upload-images.jianshu.io/upload_images/3874191-a39a2e71699836dd.gif?imageMogr2/auto-orient/strip)

Usage
-----

Include `MonthPager` in your layout XML.
MonthPager must have same CoordinatorLayout with RecyclerView, and RecyclerView's layout_behavioris com.ldf.calendar.behavior.RecyclerViewBehavior

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
so far my lib suit Activity/Fragment Activity的`onCreate`   or Fragment `onCreateView`  you need fill data in you init method 

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

use this method new click callback
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
 
use this method init markdata
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
 use this method add MonthPager some listener
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
