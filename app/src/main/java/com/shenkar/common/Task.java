package com.shenkar.common;

/**
 * Created by Owner on 1/26/2015.
 */
public class Task {
    private int id;
    private String taskName;
    private String taskNote;
    private int priority;
    private String category;
    private int completed;
    private long timeToAlarm;
    private String taskLocation;

    public String getTaskLocation() {
        return taskLocation;
    }

    public void setTaskLocation(String taskLocation) {
        this.taskLocation = taskLocation;
    }

    public long getTimeToAlarm() {
        return timeToAlarm;
    }

    public void setTimeToAlarm(long timeToAlarm) {
        this.timeToAlarm = timeToAlarm;
    }

    public int getCompleted() {
        return completed;
    }
    public void setCompleted(int completed) {
        this.completed = completed;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }
    public String getTaskNote() {
        return taskNote;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getPriority() {
        return priority;
    }
    public String getCategory() {
        return category;
    }
}

