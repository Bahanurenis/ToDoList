package com.shenkar.common;

import android.os.Bundle;

/**
 * Created by Owner on 3/9/2015.
 */
public class Alarm {
    //The alarm ID
    private int id;
    //The intent filter action.
    private String action;
    //When
    private long triggerAtMillis;
    //Frequency
    private long intervalMillis;
    //Extras for the alarm
    private Bundle extras;
    //The receiver
    private Class<?> receiver;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTriggerAtMillis() {
        return triggerAtMillis;
    }

    public void setTriggerAtMillis(long triggerAtMillis) {
        this.triggerAtMillis = triggerAtMillis;
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Bundle getExtras() {
        return extras;
    }

    public void setExtras(Bundle extras) {
        this.extras = extras;
    }

    public Class<?> getReceiver() {
        return receiver;
    }

    public void setReceiver(Class<?> receiver) {
        this.receiver = receiver;
    }
}
