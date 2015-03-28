package com.shenkar.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.shenkar.common.Category;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesDAO implements CategoriesIDAO{
    private static final String TAG ="CategoriesDAO";
    private static CategoriesDAO instance;
    private Context context;
    private CategoryDBHelper dbHelper;
    private String[] categoryColumns = {CategoriesDBContract.CategoryEntry._ID,
            CategoriesDBContract.CategoryEntry.COLUMN_CATEGORY_NAME};
    private SQLiteDatabase database;
    private CategoriesDAO(Context context) {
        this.context = context;
        dbHelper = new CategoryDBHelper(this.context);
    }

    public static CategoriesDAO getInstatnce(Context context) {
        if (instance == null)
            instance = new CategoriesDAO(context);
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
    public Category addCategory(Category category) {
        if (category == null)
            return null;
        //build the content values.
        ContentValues values = new ContentValues();
        values.put(CategoriesDBContract.CategoryEntry.COLUMN_CATEGORY_NAME, category.getCategoryName());
        //do the insert.
        long insertId = database.insert(CategoriesDBContract.CategoryEntry.TABLE_NAME, null, values);
        if (insertId == -1) {
            System.out.println("insert id = -1");
        }
        //get the entity from the data base - extra validation, entity was insert properly.
        Cursor cursor = database.query(CategoriesDBContract.CategoryEntry.TABLE_NAME, categoryColumns,
                CategoriesDBContract.CategoryEntry._ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Category newCategory = cursorToCategory(cursor);
        cursor.close();
        return newCategory;
    }

    private Category cursorToCategory(Cursor cursor) {
        Category c = new Category();
        c.setId(cursor.getInt(cursor.getColumnIndex(CategoriesDBContract.CategoryEntry._ID)));
        c.setCategoryName(cursor.getString(cursor
                .getColumnIndex(CategoriesDBContract.CategoryEntry.COLUMN_CATEGORY_NAME)));
        return c;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<Category>();
        Cursor cursor = database.query(CategoriesDBContract.CategoryEntry.TABLE_NAME, categoryColumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category c = cursorToCategory(cursor);
            categories.add(c);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return categories;
    }

    @Override
    public void removeCategory(Category category) {
        long id = category.getId();
        database.delete(CategoriesDBContract.CategoryEntry.TABLE_NAME, CategoriesDBContract.CategoryEntry._ID + " = " + id,
                null);
    }
}
