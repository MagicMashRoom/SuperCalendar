# SuperCalendar
## ChangeLog
* 发布1.6 稳定版
* 发布1.5 修改了一些BUG
* 发布1.4稳定版
* 清除了dev污染
* 添加手动切换上一月下一月功能
* 解决神奇的日历尺寸问题，确保日历尺寸正确
## 简介
* [可能是第十好的Android开源日历](http://www.jianshu.com/p/8e3fc46e5a80)
* 博主现在工作在一家教育公司，最近公司的产品狗扔过来一个需求，说要做一个可以周月切换的课表，可以展示用户在某一天的上课安排。接到这个任务之后我研究了很多的日历控件，并且抽出了一个calenderlib。先看一下最后的项目中的效果：

<div style = "float:center">
    <img src="http://osnftsiae.bkt.clouddn.com/syllabus_1.png" width="240">
    <img src="http://osnftsiae.bkt.clouddn.com/syllabus_2.png" width="240">
<div/>
<div style = "float:center">
    <img src="http://osnftsiae.bkt.clouddn.com/supercalendarexample.gif" width="240">
<div/>

* 看到本篇文章的同学估计也是实验课或者项目需求中需要一个日历表，当我接到这个需求的时候，当时脑子压根连想都没想，这么通用的控件，GitHub上一搜一大堆不是嘛。可是等到真正做起来的时候，扎心了老铁，GitHub上的大神居然异常的不给力，都是实现了基本功能，能够滑动切换月份，找实现了周月切换功能的开源库很难。终于我费尽千辛万苦找到一个能够完美切换的项目时，你周月切换之后的数据乱的一塌糊涂啊！！！
* 算了，自己撸一个！！！

## 项目链接 [SuperCalendar][Tags]

[Tags]: https://github.com/MagicMashRoom/SuperCalendar
* 如果你感觉到对你有帮助，欢迎star
* 如果你感觉对代码有疑惑，或者需要修改的地方，欢迎issue

## 主要特性
* 日历样式完全自定义，拓展性强
* 左右滑动切换上下周月，上下滑动切换周月模式
* 抽屉式周月切换效果
* 标记指定日期（marker）
* 跳转到指定日期

## 思路

## 使用方法

#### XML布局
* 新建XML布局

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
#### 自定义日历样式
* 新建CustomDayView继承自DayView并重写refreshContent 和 copy 两个方法

```java
	@Override
    public void refreshContent() {
        //你的代码 你可以在这里定义你的显示规则
        super.refreshContent();
    }

    @Override
    public IDayRenderer copy() {
        return new CustomDayView(context , layoutResource);
    }
```

* 新建CustomDayView实例，并作为参数构建CalendarViewAdapter


```java
	CustomDayView customDayView = new CustomDayView(context, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                context,
                onSelectDateListener,
                CalendarAttr.CalendarType.MONTH,
                CalendarAttr.WeekArrayType.Monday,
                customDayView);
```
#### 初始化View

* 目前来看 相比于Dialog选择日历 我的控件更适合于Activity/Fragment在Activity的`onCreate`   或者Fragment的`onCreateView`  你需要实现这两个方法来启动日历并装填进数据

```java
@Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        initCalendarView();
    }
    
    private void initCalendarView() {
        initListener();
        CustomDayView customDayView = new CustomDayView(context, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                context,
                onSelectDateListener,
                CalendarAttr.CalendarType.MONTH,
                CalendarAttr.WeekArrayType.Monday,
                customDayView);
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

重写onWindowFocusChanged方法，使用此方法得知calendar和day的尺寸

```java
	@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && !initiated) {
            CalendarDate today = new CalendarDate();
        	calendarAdapter.notifyDataChanged(today);
            initiated = true;
        }
    }
```

* 大功告成，如果还不清晰，请下载DEMO

## Download
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
	        compile 'com.github.MagicMashRoom:SuperCalendar:1.6'
	}

```
[![](https://www.jitpack.io/v/MagicMashRoom/SuperCalendar.svg)](https://www.jitpack.io/#MagicMashRoom/SuperCalendar)

## Licence

      Copyright 2017 MagicMashRoom, Inc.
