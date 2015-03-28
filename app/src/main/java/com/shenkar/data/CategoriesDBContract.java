package com.shenkar.data;

import android.provider.BaseColumns;

/**
 * Created by Owner on 3/15/2015.
 */
public class CategoriesDBContract {
    public static final class CategoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_CATEGORY_NAME = "category_name";

    }
}
