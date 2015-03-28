package com.shenkar.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.shenkar.common.AppConst;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddTaskActivity extends ActionBarActivity {

    private EditText nameEt;
    private EditText noteEt;
    private CheckBox priorityCb;
    private EditText alarmTimeEt;
    private EditText alarmLocationEt;
    private static final String TAG = "AddTaskActivity";
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private long timeToAlarm;
    private boolean hasAlarm;
    private boolean hasGeofence;
    private static final int ADD_LOCATION_REQUEST = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        hasAlarm = false;
        hasGeofence = false;
        timeToAlarm = -1;
        Intent intent = getIntent();
        String categoryName = intent.getStringExtra(AppConst.CATEGORY_TO_ADD_TASK);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4285f4"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("Add Task - " + categoryName);
        nameEt = (EditText) findViewById(R.id.editTextName);
        noteEt = (EditText) findViewById(R.id.editTextNote);
        priorityCb = (CheckBox) findViewById(R.id.priorityCheckBox);
        alarmTimeEt = (EditText)findViewById(R.id.editTextWhenAlarm);
        alarmTimeEt.setVisibility(View.INVISIBLE);
        alarmLocationEt = (EditText)findViewById(R.id.editTextWhereAlarm);
        alarmLocationEt.setVisibility(View.INVISIBLE);
    }

    class DateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
        }
    }

    class TimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            String dateString = mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date myDate = dateFormat.parse(dateString);
                alarmTimeEt.setVisibility(View.VISIBLE);
                alarmTimeEt.setText(myDate.toString());
                alarmTimeEt.setKeyListener(null);
            }
            catch (ParseException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }


public void addLocation(View v) {
    Intent addIntent = new Intent(this, GooglePlacesAutocompleteActivity.class);
    startActivityForResult(addIntent, ADD_LOCATION_REQUEST);
}

    public void showPickers(View v) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        DatePickerDialog dateDialog = new DatePickerDialog(this,
                new DateSetListener(), year, month, day);
        dateDialog.getDatePicker().setCalendarViewShown(false);
        TimePickerDialog timeDialog = new TimePickerDialog(this,
                new TimeSetListener(), hour, minutes, true);
        timeDialog.show();
        dateDialog.show();
        hasAlarm = true;
    }

    public static Date getDateAndTime(int year, int month, int day,int hour,int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ADD_LOCATION_REQUEST) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                String location = extras.getString(AppConst.WHERE_ALARM);
                alarmLocationEt.setVisibility(View.VISIBLE);
                alarmLocationEt.setText(location);
                hasGeofence = true;
            }
        }
    }

    public void okClicked(View v) {
        if(nameEt == null || noteEt == null || priorityCb == null) {
            return;
        }
        String name = nameEt.getText().toString();
        String note = noteEt.getText().toString();
        boolean bPriority = ((CheckBox) findViewById(R.id.priorityCheckBox)).isChecked();
        int iPriority;
        if (bPriority) {
            iPriority = 1;
        }
        else {
            iPriority = 0;
        }
        if (hasAlarm) {
            Date when = getDateAndTime(mYear, mMonth, mDay, mHour, mMinute);
            timeToAlarm = when.getTime();//get the selected date in millie's
        }
        String address = null;
        if (hasGeofence) {
            address = alarmLocationEt.getText().toString();
        }
        Intent data = new Intent();
        data.putExtra(AppConst.TASK_NAME_FOR_INTENT, name);
        data.putExtra(AppConst.TASK_NOTE_FOR_INTENT,note);
        data.putExtra(AppConst.TASK_PRIORITY_FOR_INTENT,iPriority);
        data.putExtra(AppConst.WHEN_ALARM, timeToAlarm);
        data.putExtra(AppConst.WHERE_ALARM, address);
        setResult(RESULT_OK, data);
        finish();
    }

}
