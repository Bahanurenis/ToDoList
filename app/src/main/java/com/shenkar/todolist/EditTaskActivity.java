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


public class EditTaskActivity extends ActionBarActivity {
    private EditText nameEt;
    private EditText noteEt;
    private CheckBox priorityCb;
    private EditText locationEt;
    private EditText whenEt;
    private int id;
    private static final String TAG = "EditTaskActivity";
    private long oldTimeToAlarm;
    private String oldTaskLocation;
    private boolean hasAlarm;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private static final int EDIT_LOCATION_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4285f4"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_edit_task);
        nameEt = (EditText) findViewById(R.id.newEditTextName);
        noteEt = (EditText) findViewById(R.id.newEditTextNote);
        whenEt = (EditText) findViewById(R.id.editTextNewWhenAlarm);
        whenEt.setVisibility(View.INVISIBLE);
        locationEt = (EditText)findViewById(R.id.editTextNewWhereAlarm);
        priorityCb = (CheckBox) findViewById(R.id.newPriorityCheckBox);
        hasAlarm = false;
        Intent intent = getIntent();
        id = intent.getIntExtra(AppConst.TASK_ID_FOR_INTENT,0);
        String oldTaskName = intent.getStringExtra(AppConst.TASK_NAME_FOR_INTENT);
        getSupportActionBar().setTitle("Edit "+oldTaskName);
        String oldTaskNote = intent.getStringExtra(AppConst.TASK_NOTE_FOR_INTENT);
        int iOldTaskPriority = intent.getIntExtra(AppConst.TASK_PRIORITY_FOR_INTENT,0);
        oldTimeToAlarm = intent.getLongExtra(AppConst.WHEN_ALARM,-1);
        oldTaskLocation = intent.getStringExtra(AppConst.WHERE_ALARM);
        boolean bOldTaskPriority;
        if (iOldTaskPriority == 1) {
            bOldTaskPriority = true;
        }
        else {
            bOldTaskPriority = false;
        }
        //put the old values first
        nameEt.setText(oldTaskName);
        noteEt.setText(oldTaskNote);
        priorityCb.setChecked(bOldTaskPriority);
        if (oldTimeToAlarm != -1) {
            Date date = fromMilliesToDate(oldTimeToAlarm);
            whenEt.setVisibility(View.VISIBLE);
            whenEt.setText(date.toString());
        }
        if (oldTaskLocation != null && !oldTaskLocation.equals("")){
            locationEt.setVisibility(View.VISIBLE);
            locationEt.setText(oldTaskLocation);
        }

    }

    private Date fromMilliesToDate(long millie) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millie);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinutes = c.get(Calendar.MINUTE);
        String dateString = mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinutes;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date myDate = dateFormat.parse(dateString);
            return myDate;
        }
        catch (ParseException e){
            Log.e(TAG, e.getMessage());
            return null;
        }
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
                whenEt.setVisibility(View.VISIBLE);
                whenEt.setText(myDate.toString());
                whenEt.setKeyListener(null);
            }
            catch (ParseException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void changeAlarmTime(View v) {
        Calendar c = Calendar.getInstance();
        if (oldTimeToAlarm != -1) {
            c.setTimeInMillis(oldTimeToAlarm); // set the old date and time first
        }
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinutes = c.get(Calendar.MINUTE);
        DatePickerDialog dateDialog = new DatePickerDialog(this,
                new DateSetListener(), mYear, mMonth, mDay);
        dateDialog.getDatePicker().setCalendarViewShown(false);
        TimePickerDialog timeDialog = new TimePickerDialog(this,
                new TimeSetListener(), mHour, mMinutes, true);
        timeDialog.show();
        dateDialog.show();
        hasAlarm = true;
    }

    public void changeAlarmLocation(View v) {
        Intent addIntent = new Intent(this, GooglePlacesAutocompleteActivity.class);
        startActivityForResult(addIntent, EDIT_LOCATION_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_LOCATION_REQUEST) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                String location = extras.getString(AppConst.WHERE_ALARM);
                locationEt.setVisibility(View.VISIBLE);
                locationEt.setText(location);
            }
        }
    }

    public void okClicked(View v) {
        long newTimeToAlarm = -1;
        if(nameEt == null || noteEt == null || priorityCb == null) {
            return;
        }
        String name = nameEt.getText().toString();
        String note = noteEt.getText().toString();
        boolean bPriority = priorityCb.isChecked();
        String address = locationEt.getText().toString();
        int iPriority;
        if (bPriority) {
            iPriority = 1;
        }
        else {
            iPriority = 0;
        }
        if (hasAlarm) {
            Date when = AddTaskActivity.getDateAndTime(mYear, mMonth, mDay, mHour, mMinute);
            newTimeToAlarm = when.getTime();
        }
        Intent data = new Intent();
        data.putExtra(AppConst.TASK_ID_FOR_INTENT, id);
        data.putExtra(AppConst.TASK_NAME_FOR_INTENT, name);
        data.putExtra(AppConst.TASK_NOTE_FOR_INTENT,note);
        data.putExtra(AppConst.TASK_PRIORITY_FOR_INTENT,iPriority);
        data.putExtra(AppConst.WHEN_ALARM,newTimeToAlarm);
        data.putExtra(AppConst.WHERE_ALARM,address);
        setResult(RESULT_OK, data);
        finish();
    }
}
