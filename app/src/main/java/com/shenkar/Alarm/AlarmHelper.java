package com.shenkar.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.shenkar.common.Alarm;

/**
 * Created by Owner on 3/17/2015.
 */
public class AlarmHelper {
    private static Intent intent = new Intent();
    private static PendingIntent pendingIntent;
    private static final String TAG = "AlarmHelper";
    public void setAlarm(Context c, Alarm alarm) {

        if (c == null || alarm == null)
            return;
        //add alarm properties
        intent = new Intent(c, alarm.getReceiver());
        intent.setAction(alarm.getAction());
        // set the extras.
        intent.putExtras(alarm.getExtras());
        pendingIntent = PendingIntent.getBroadcast(c, alarm.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getTriggerAtMillis(), pendingIntent);
    }

    public void cancelAlarm(Context c, Alarm alarm) {
        if (c == null || alarm == null)
            return;
        pendingIntent = PendingIntent.getBroadcast(c, alarm.getId(), intent,0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }
}
