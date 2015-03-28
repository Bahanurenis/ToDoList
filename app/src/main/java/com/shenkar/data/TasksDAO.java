package com.shenkar.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.shenkar.common.Task;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TasksDAO implements TasksIDAO {
    private static final String TAG ="TasksDAO";
    private static TasksDAO instance;
    private Context context;
    private TaskDBHelper dbHelper;
    private String[] tasksColumns = { TasksDBContract.TaskEntry._ID,
            TasksDBContract.TaskEntry.COLUMN_TASK_NAME,
            TasksDBContract.TaskEntry.COLUMN_TASK_NOTE,
            TasksDBContract.TaskEntry.COLUMN_TASK_PRIORITY,
            TasksDBContract.TaskEntry.COLUMN_TASK_CATEGORY,
            TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED,
            TasksDBContract.TaskEntry.COLUMN_TASK_ALARM,
            TasksDBContract.TaskEntry.COLUMN_TASK_LOCATION};
    private SQLiteDatabase database;
    private TasksDAO(Context context) {
        this.context = context;
        dbHelper = new TaskDBHelper(this.context);
    }

    public static TasksDAO getInstatnce(Context context) {
        if (instance == null)
            instance = new TasksDAO(context);
        return instance;
    }

    @Override
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        dbHelper.close();
    }

    @Override
    public Task addTask(Task task, String category) {
        if (task == null)
            return null;
        //build the content values.
        ContentValues values = new ContentValues();
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_NAME, task.getTaskName());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_NOTE, task.getTaskNote());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_PRIORITY, task.getPriority());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_CATEGORY, category);
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED,task.getCompleted());
        String timeToAlarmText = Long.toString(task.getTimeToAlarm()); //put the time in string form
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_ALARM, timeToAlarmText);
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_LOCATION, task.getTaskLocation());

        //do the insert.
        long insertId = database.insert(TasksDBContract.TaskEntry.TABLE_NAME, null, values);
        Log.i(TAG,values.toString());
        if (insertId == -1) {
            System.out.println("insert id = -1");
        }
        //get the entity from the data base - extra validation, entity was insert properly.
        Cursor cursor = database.query(TasksDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                TasksDBContract.TaskEntry._ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();
        return newTask;
    }



    @Override
    public void editTask(Task task) {
        if (task == null)
            return;
        //build the content values.
        ContentValues values = new ContentValues();
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_NAME, task.getTaskName());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_NOTE, task.getTaskNote());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_CATEGORY, task.getCategory());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_PRIORITY,task.getPriority());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED,task.getCompleted());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_ALARM,task.getTimeToAlarm());
        values.put(TasksDBContract.TaskEntry.COLUMN_TASK_LOCATION, task.getTaskLocation());
        int rowsAffected = database.update(TasksDBContract.TaskEntry.TABLE_NAME,values,TasksDBContract.TaskEntry._ID + " = " + task.getId(),null);
        if (rowsAffected == 0) {
            Log.e(TAG,"no affect on the database from editTask");
        }

    }

    private Task cursorToTask(Cursor cursor) {
        Task t = new Task();
        t.setId(cursor.getInt(cursor.getColumnIndex(TasksDBContract.TaskEntry._ID)));
        t.setTaskName(cursor.getString(cursor
                .getColumnIndex(TasksDBContract.TaskEntry.COLUMN_TASK_NAME)));
        t.setTaskNote(cursor.getString(cursor
                .getColumnIndex(TasksDBContract.TaskEntry.COLUMN_TASK_NOTE)));
        t.setPriority(cursor.getInt(cursor
                .getColumnIndex(TasksDBContract.TaskEntry.COLUMN_TASK_PRIORITY)));
        t.setCategory(cursor.getString(cursor
                 .getColumnIndex(TasksDBContract.TaskEntry.COLUMN_TASK_CATEGORY)));
        t.setCompleted(cursor.getInt(cursor
                .getColumnIndex(TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED)));
        String timeToAlarmText = cursor.getString(cursor
                .getColumnIndex(TasksDBContract.TaskEntry.COLUMN_TASK_ALARM));
        long timeToAlarm = Long.parseLong(timeToAlarmText);
        t.setTimeToAlarm(timeToAlarm);
        t.setTaskLocation(cursor.getString(cursor
                .getColumnIndex(TasksDBContract.TaskEntry.COLUMN_TASK_LOCATION)));
        return t;
    }



    @Override
    public void removeTask(Task task) {
        long id = task.getId();
        database.delete(TasksDBContract.TaskEntry.TABLE_NAME, TasksDBContract.TaskEntry._ID + " = " + id,
                null);
    }

    @Override
    public List<Task> getNotCompletedTasks(String category) {
        List<Task> tasks = new ArrayList<>();
        Cursor cursor = database.query(TasksDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                TasksDBContract.TaskEntry.COLUMN_TASK_CATEGORY + " = '" + category+"'"
                +" AND "+ TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED + " = " + 0, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task t = cursorToTask(cursor);
            tasks.add(t);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    @Override
    public List<Task> getCompletedTasks() {
        List<Task> tasks = new ArrayList<>();
        Cursor cursor = database.query(TasksDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED + " = " + 1, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task t = cursorToTask(cursor);
            tasks.add(t);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    @Override
    public void markTaskAsDone(Task task) {
        if (task == null) {
            return;
        }
        ContentValues value = new ContentValues();
        value.put(TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED,1);
        long id = task.getId();
        int rowsAffected = database.update(TasksDBContract.TaskEntry.TABLE_NAME,value,
                TasksDBContract.TaskEntry._ID + " = " + id,null);
        if (rowsAffected == 0) {
            Log.e(TAG,"no affect on the database from markAsDone");
        }

    }

    @Override
    public void getTaskBack(Task task) {
        if (task == null) {
            return;
        }
        ContentValues value = new ContentValues();
        value.put(TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED,0);
        long id = task.getId();
        int rowsAffected = database.update(TasksDBContract.TaskEntry.TABLE_NAME,value,
                TasksDBContract.TaskEntry._ID + " = " + id,null);
        if (rowsAffected == 0) {
            Log.e(TAG,"no affect on the database from markAsDone");
        }
    }
}
