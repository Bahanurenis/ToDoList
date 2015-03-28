package com.shenkar.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Owner on 1/26/2015.
 */
public class TaskDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database
    // version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tasks.db";
    private static final String TAG ="TaskDBHelper";

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "
                + TasksDBContract.TaskEntry.TABLE_NAME + "("
                + TasksDBContract.TaskEntry._ID + " INTEGER PRIMARY KEY, "
                + TasksDBContract.TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, "
                + TasksDBContract.TaskEntry.COLUMN_TASK_NOTE + " TEXT NOT NULL, "
                + TasksDBContract.TaskEntry.COLUMN_TASK_PRIORITY + " INTEGER, "
                + TasksDBContract.TaskEntry.COLUMN_TASK_CATEGORY + " TEXT NOT NULL, "
                + TasksDBContract.TaskEntry.COLUMN_TASK_COMPLETED + " INTEGER, "
                + TasksDBContract.TaskEntry.COLUMN_TASK_ALARM + " TEXT, "
                + TasksDBContract.TaskEntry.COLUMN_TASK_LOCATION + " TEXT)";
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        Log.i(TAG,SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TasksDBContract.TaskEntry.TABLE_NAME);
        onCreate(db);

    }

}
