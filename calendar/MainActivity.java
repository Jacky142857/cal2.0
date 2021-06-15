package com.example.calendar;


import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The MainActivity class encapsulates the user interface that displays the calendar.
 * */

public class MainActivity extends AppCompatActivity  {
    public static final int ADD_NOTE_REQUEST = 2;
    protected static final float FLIP_DISTANCE = 50;

    private TextView currentDate;
    private GridView gv;
    private GestureDetector detector;


    /**
     * Initialize the calendar and buttons to navigate through different activities and different
     * months of the calendar.
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonViewAll = findViewById(R.id.button_view_all);
        buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListReminderActivity.class);
                startActivity(intent);
            }
        });

        currentDate = findViewById(R.id.current_date);
        gv = findViewById(R.id.gv);

        final List<CalendarUnit> dataList = new ArrayList<>();
        final CalendarAdapter adapter = new CalendarAdapter(dataList, this);
        gv.setAdapter(adapter);

        final Calendar calendar = Calendar.getInstance();
        setCurrentData(calendar);
        updateAdapter(calendar, dataList, adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                AddEditNoteActivity.isFromMain = true;
                startActivityForResult(intent,ADD_NOTE_REQUEST);
            }
        });


        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    updateAdapter(calendar, dataList, adapter);
                    return true;
                }
                if (e2.getX() - e1.getX() > FLIP_DISTANCE) {
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                    updateAdapter(calendar, dataList, adapter);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    /**
     * Update the adapter when navigating to different months.
     * */
    private void updateAdapter(Calendar calendar, List<CalendarUnit> dataList, CalendarAdapter adapter) {
        dataList.clear();
        setCurrentData(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);

        int index = 0;
        int preMonthDays = getMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        for (int i = 0; i < weekIndex; i++) {
            CalendarUnit Unit = new CalendarUnit();
            Unit.setYear(calendar.get(Calendar.YEAR));
            Unit.setMonth(calendar.get(Calendar.MONTH) + 1);
            Unit.setDay(preMonthDays - weekIndex + i + 1);
            Unit.setId(index);
            index++;
            Unit.setCurrentDay(false);
            Unit.setCurrentMonth(false);
            dataList.add(Unit);
        }

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        int currentDays = getMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

        for (int i = 0; i < currentDays; i++) {
            CalendarUnit Unit = new CalendarUnit();
            Unit.setYear(calendar.get(Calendar.YEAR));
            Unit.setMonth(calendar.get(Calendar.MONTH) + 1);
            Unit.setDay(i + 1);
            Unit.setId(index);
            index++;
            String nowDate = getFormatTime("yyyy-M-d", Calendar.getInstance().getTime());
            String selectDate = getFormatTime("yyyy-M-", calendar.getTime()) + (i + 1);
            if (nowDate.contentEquals(selectDate)) {
                Unit.setCurrentDay(true);
            } else {
                Unit.setCurrentDay(false);
            }
            Unit.setCurrentMonth(true);
            dataList.add(Unit);
        }

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < 7 - weekIndex; i++) {
            CalendarUnit Unit = new CalendarUnit();
            Unit.setYear(calendar.get(Calendar.YEAR));
            Unit.setMonth(calendar.get(Calendar.MONTH) + 1);
            Unit.setDay(i + 1);
            Unit.setId(index);
            index++;
            Unit.setCurrentDay(false);
            Unit.setCurrentMonth(false);
            dataList.add(Unit);
        }

        adapter.notifyDataSetChanged();

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            data.setClass(MainActivity.this,ListReminderActivity.class);
            startActivity(data);
        }
    }

    private static String[] monthRep = new String[] {"January","February","March","April",
            "May","June","July","August","September","October","November","December"};

    public static String getMonthRep(Calendar calendar) {
        return monthRep[calendar.get(Calendar.MONTH)] + "," + calendar.get(Calendar.YEAR);
    }

    private void setCurrentData(Calendar calendar) {
        currentDate.setText(getMonthRep(calendar));
    }

    public static String getRep(Calendar calendar) {
        return monthRep[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.DATE)+ "," +
                calendar.get(Calendar.YEAR);
    }

    /**
     * Check if an input year is a leap year.
     * */
    public boolean isLeapYear(int y) {
        return y % 4 == 0 && y % 100 != 0 || y % 400 == 0;
    }

    public static String getFormatTime(String p, Date t) {
        return new SimpleDateFormat(p, Locale.ENGLISH).format(t);
    }

    public int getMonth(int m, int y) {
        if(m == 2) {
            if(isLeapYear(y)) {
                return 29;
            }
            return 28;
        } else if (m == 4 || m == 6 || m == 9 || m == 11) {
            return 30;
        }
        return 31;
    }
}