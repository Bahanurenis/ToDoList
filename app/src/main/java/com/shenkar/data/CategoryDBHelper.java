package com.shenkar.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategoryDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "categories.db";
    private static final String TAG ="CategoryDBHelper";

    public CategoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "
                + CategoriesDBContract.CategoryEntry.TABLE_NAME + "("
                + CategoriesDBContract.CategoryEntry._ID + " INTEGER PRIMARY KEY, "
                + CategoriesDBContract.CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        Log.i(TAG, SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesDBContract.CategoryEntry.TABLE_NAME);
        onCreate(db);

    }

}
