package com.shenkar.Alarm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.shenkar.common.AppConst;
import com.shenkar.todolist.MainActivity;
import com.shenkar.todolist.R;

/**
 * Created by Owner on 3/17/2015.
 */
public class NotificationBroadCastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String msg = extras.getString(AppConst.Extra_Message);
        createNotification(context,msg);

    }
    @TargetApi(16)
    public void createNotification(Context context, String msg) {
        NotificationManager notificationManager = (NotificationManager)context
                .getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(msg).setSmallIcon(R.drawable.app_icon).
                setContentIntent(pIntent).setContentText(msg).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0,notification);
    }
}
