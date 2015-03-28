package com.shenkar.data;

/**
 * Created by Owner on 1/26/2015.
 */

import android.provider.BaseColumns;


/**
 * Defines table and column names for the friends database.
 */
public class TasksDBContract {

    /* Inner class that defines the table contents of the friends */
    public static final class TaskEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_TASK_NAME = "task_name";

        public static final String COLUMN_TASK_NOTE = "task_note";

        public static final String COLUMN_TASK_PRIORITY = "task_priority";

        public static final String COLUMN_TASK_CATEGORY = "task_category";

        public static final String COLUMN_TASK_COMPLETED = "task_completed";

        public static final String COLUMN_TASK_ALARM = "task_alarm";

        public static final String COLUMN_TASK_LOCATION = "task_location";


    }
}
