# 简介

*        由于项目的需求，研究了众多日历软件。本软件是一款高仿小米的自定义日历>控件，周月视图平滑滚动，平滑切换，可以在xml文件中进行属性的配置定制，加入你自己的RecyclerView后，实现日历与列表的无缝连接。
*       解释一下为什么认为他是第十好的Android开源日历呢，看到本篇文章的同学估计也是实验课或者项目需求中需要一个日历表，当我接到这个需求的时候，当时脑子压根连想都没想，这么通用的控件，GitHub上一搜一大堆不是嘛。可是等到真正做起来的时候，扎心了老铁，GitHub上的大神居然异常的不给力，都是实现了基本功能，能够滑动切换月份，找实现了周月切换功能的开源库很难。终于我费尽千辛万苦找到一个能够完美切换的项目时，扎铁了老心。。。你周月切换之后的数据乱的一塌糊涂啊！！！
*        算了，自己撸一个！！！

# 主要的优点：
* 完全自定义，原理简单，扩展性强
* 左右滑动切换上下周月，上下滑动切换周月模式
* 抽屉式周月切换效果
* 标记指定日期（marker）
* 跳转到指定日期

不多废话，直接看：

# SuperCalendar
==========================
> 简单使用的日历  [项目链接 求Star](https://github.com/MagicMashRoom/SuperCalendar)
> 
>

# Example 

![example](http://upload-images.jianshu.io/upload_images/3874191-366d7f0d343989c9.gif?imageMogr2/auto-orient/strip)

RecyclerView的layout_behavior为com.ldf.calendar.behavior.RecyclerViewBehavior

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
                //your code
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                //偏移量 -1表示上一个月 ， 1表示下一个月
                monthPager.selectOtherMonth(offset);
            }
        };
    }
```
 
 使用此方法初始化日历标记数据
```java
private void initMarkData() {
       HashMap markData = new HashMap<>();
        //1表示红点，0表示灰点
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
