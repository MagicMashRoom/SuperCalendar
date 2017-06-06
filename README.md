# FuckCalendar
==========================
> 简单使用的日历
>

![](https://github.com/MagicMashRoom/FuckCalendar/blob/master/raw/examples.gif)

Usage
-----

Include `MonthPager` in your layout XML.

```xml
 <com.ldf.calendar.MonthPager
        android:id="@+id/calendar_view"
        android:layout_width="match_parent"
        android:layout_height="270dp"
        android:background="#fff">
    </com.ldf.calendar.MonthPager>
```

目前来看 相比于Dialog选择日历 我的控件更适合于Activity/Fragment

在Activity的`onCreate`   或者Fragment的`onCreateView`  你需要实现这两个方法来启动日历并装填进数据
```java
  @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        initCurrentDate();
        initCalendarView();

    }
    
    private void initCurrentDate() {
        currentDate = new CalendarDate();
    }
 ```
初始化步骤
 ```java
private void initCalendarView() {
        initListener();
        calendarAdapter = new CalendarViewAdapter(context , onSelectDateListener);
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
新建adapter
```java
calendarAdapter = new CalendarViewAdapter(context , onSelectDateListener);
```
设置有标记的日历的HashMap
```java
private void initMarkData() {
       HashMap<String , String> markData = new HashMap<>();
       markData.put("2017-8-9" , "1");
       markData.put("2017-7-9" , "0");
       markData.put("2017-6-9" , "1");
       markData.put("2017-6-10" , "0");
       calendarAdapter.setMarkData(markData);
   }
```
使用此方法给MonthPager添加上相关监听
 ```
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
    compile 'com.github.MagicMashRoom:FuckCalendar:v1.0.1'
}
```

[![](https://www.jitpack.io/v/MagicMashRoom/FuckCalendar.svg)](https://www.jitpack.io/#MagicMashRoom/FuckCalendar)
