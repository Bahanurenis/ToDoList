package com.shenkar.Alarm;

import android.content.Context;
import android.os.Bundle;

import com.shenkar.common.Alarm;
import com.shenkar.common.AppConst;

/**
 * Created by Owner on 3/17/2015.
 */
public class AlarmController {
    private Context context;
    private AlarmHelper alarmHelper;

    public AlarmController(Context context) {
        this.context = context;
        alarmHelper = new AlarmHelper();
    }

    public void CreateAlarm(String msg,long when,int alarmId) {
        Alarm alarm = new Alarm();
        alarm.setId(alarmId);
        Bundle extras = new Bundle();
        extras.putString(AppConst.Extra_Message,msg);
        alarm.setExtras(extras);
        alarm.setAction(AppConst.ACTION_ALARM);
        alarm.setIntervalMillis(0);
        alarm.setReceiver(NotificationBroadCastReciever.class);
        alarm.setTriggerAtMillis(when);
        alarmHelper.setAlarm(context,alarm);

    }

    public void cancelAlarm(int alarmId) {
        Alarm alarm = new Alarm();
        alarm.setId(alarmId);
        alarmHelper.cancelAlarm(context,alarm);

    }
}
