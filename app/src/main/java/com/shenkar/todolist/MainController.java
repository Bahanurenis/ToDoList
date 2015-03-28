package com.shenkar.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.shenkar.Alarm.AlarmController;
import com.shenkar.common.Category;
import com.shenkar.common.OnDataSourceChangeListener;
import com.shenkar.common.Task;
import com.shenkar.data.CategoriesDAO;
import com.shenkar.data.CategoriesIDAO;
import com.shenkar.data.TasksDAO;
import com.shenkar.data.TasksIDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainController {
    // The data model, act as a cache
    private HashMap<Integer,Task> tasks;
    private List<Category> allCategories;
    private Context context;
    private TasksIDAO tDao;
    private CategoriesIDAO cDao;
    private AlarmController alarmController;
    private final static String TAG = "MainController";

    //observers list.
    private List<OnDataSourceChangeListener> dataSourceChangedListenrs = new ArrayList<>();

    public MainController(Context context) {
        this.context = context;
        cDao = CategoriesDAO.getInstatnce(context.getApplicationContext());
        tDao = TasksDAO.getInstatnce(context.getApplicationContext());
        alarmController = new AlarmController(context);
    }

    public List<Task> getNotCompletedTasks(String categoryName) {
        try {
            if (tasks != null) {
                ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
                ArrayList<Task> categoryTasks = new ArrayList<>();
                for (Task task : allTasks) {
                    if (task.getCategory().equals(categoryName) && task.getCompleted()==0){
                        categoryTasks.add(task);
                    }
                }
                return categoryTasks;
            }
            tDao.open();
            List<Task> tl = tDao.getNotCompletedTasks(categoryName);
            tDao.close();
            PopulatetasksCache(tl);
            return tl;
        }
        catch (Exception e) {
            // in case of error, return empty list.
            Log.e(TAG,e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Task> getCompletedTasks() {
        try {
            if (tasks != null) {
                ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
                ArrayList<Task> categoryTasks = new ArrayList<>();
                for (Task task : allTasks) {
                    if (task.getCompleted()==1){
                        categoryTasks.add(task);
                    }
                }
                return categoryTasks;
            }
            tDao.open();
            List<Task> tl = tDao.getCompletedTasks();
            tDao.close();
            PopulatetasksCache(tl);
            return tl;
        }
        catch (Exception e) {
            // in case of error, return empty list.
            Log.e(TAG,e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Category> getAllCategories() {
        try {
            if (allCategories != null) {
                return new ArrayList<>(allCategories);
            }
            cDao.open();
            List<Category> cl = cDao.getCategories();
            cDao.close();
            populateCategoriesCache(cl);
            return cl;
        }
        catch (Exception e) {
            // in case of error, return empty list.
            return new ArrayList<>();
        }
    }

    private void PopulatetasksCache(List<Task> tasksList){
        tasks =  new HashMap<Integer, Task>();
        for (Task task : tasksList) {
            tasks.put(task.getId(), task);
        }
    }

    private void populateCategoriesCache (List<Category> categories) {
        allCategories = new ArrayList<>();
        for (Category category : categories) {
            allCategories.add(category);
        }
    }

    public void refreshData(String categoryName) {
        tasks = null;
        getCompletedTasks();
        getNotCompletedTasks(categoryName);
    }

    public int addTask(Task t, String categoryName) {
        try {
            tDao.open();
            Task retTask = tDao.addTask(t, categoryName);
            tDao.close();
            if(retTask == null) return -1;
            if(tasks.containsKey(retTask.getId())) {
                return -1;
            }
            tasks.put(retTask.getId(), retTask);
            invokeDataSourceChanged();
            return  retTask.getId();
        }
        catch (Exception e) {
            Log.e("MainController",e.getMessage());
            return -1;
        }
    }

    public void addCategory(Category c) {
        try {
            cDao.open();
            Category retCat = cDao.addCategory(c);
            cDao.close();
            if (retCat == null) return;
            if (allCategories.contains(retCat.getCategoryName())) {
                return;
            }
            allCategories.add(retCat);
            invokeDataSourceChanged();
        }
        catch (Exception e) {
            Log.e("MainController",e.getMessage());
        }
    }

    public void removeTask(Task t) {
        try {
            //open the database connection
            tDao.open();
            tDao.removeTask(t);
            //remove from the local cache.
            removeFronCache(t);
            //close the connection.
            tDao.close();
            invokeDataSourceChanged();

        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void removeCategory(Category c) {
        try {
            List<Task> tasksToRemove = getNotCompletedTasks(c.getCategoryName());
            tDao.open();
            for (Task t : tasksToRemove) {
                if (t.getTimeToAlarm() > 0) {
                    alarmController.cancelAlarm(t.getId());
                }
                tDao.removeTask(t);
            }
            tDao.close();
            removeCategoryFromCache(c);
            cDao.open();
            cDao.removeCategory(c);
            cDao.close();
            invokeDataSourceChanged();
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void markTaskAsDone(Task t) {
        try {
            Task task = tasks.get(t.getId());
            task.setCompleted(1);
            tDao.open();
            tDao.markTaskAsDone(task);
            tDao.editTask(task);
            tDao.close();
            invokeDataSourceChanged();
        }
        catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void getTaskBack(Task t) {
        try {
            Task task = tasks.get(t.getId());
            task.setCompleted(0);
            tDao.open();
            tDao.getTaskBack(task);
            tDao.editTask(task);
            tDao.close();
            invokeDataSourceChanged();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public void editTask(Task t) {
        try {
            tDao.open();
            Log.i(TAG,"inside the editTask");
            tDao.editTask(t);
            updateCache(t);
            tDao.close();
            invokeDataSourceChanged();
        }
        catch (Exception e) {
            Log.e("MainController",e.getMessage());
        }
    }

    public void removeFronCache(Task t){
        if(tasks.containsKey(t.getId()))
            tasks.remove(t.getId());
    }

    public void updateCache(Task t) {
        if(tasks.containsKey(t.getId())) {
            Task task = tasks.get(t.getId());
            task.setTaskName(t.getTaskName());
            task.setTaskNote(t.getTaskNote());
            task.setPriority(t.getPriority());
            task.setCategory(t.getCategory());
            task.setTimeToAlarm(t.getTimeToAlarm());
            task.setCompleted(t.getCompleted());
            task.setTaskLocation(t.getTaskLocation());
            tasks.put(task.getId(),task);
        }
    }

    public void taskDeleteConfirmation(final Task task) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (task.getTimeToAlarm() > 0) {
                        alarmController.cancelAlarm(task.getId());
                    }
                    removeTask(task);
                    Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete "+ task.getTaskName()+"?") .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    public void categoryDeleteConfirmation (final Category category) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    removeCategory(category);
                    Toast.makeText(context, "Category Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete "+ category.getCategoryName()+"?").setMessage("All tasks inside will be deleted too")
                .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    public void taskCompleteConfirmation(final Task task) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (task.getTimeToAlarm() > 0) {
                        alarmController.cancelAlarm(task.getId());
                        task.setTimeToAlarm(-1);
                    }
                    task.setCompleted(1);
                    markTaskAsDone(task);
                    Toast.makeText(context, "Task Completed", Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(task.getTaskName()+" completed?") .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

    }

    public void getTaskBackConfirmation(final Task task) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    getTaskBack(task);
                    Toast.makeText(context, "Task Completed", Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Return "+task.getTaskName()+" back to incomplete?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }




    public void removeCategoryFromCache(Category category) {
        if (allCategories.contains(category)) {
            allCategories.remove(category);
        }
    }


    public void registerOnDataSourceChanged(OnDataSourceChangeListener listener)
    {
        if(listener!=null)
            dataSourceChangedListenrs.add(listener);
    }
    public void unRegisterOnDataSourceChanged(OnDataSourceChangeListener listener)
    {
        if(listener!=null)
            dataSourceChangedListenrs.remove(listener);
    }
    public void invokeDataSourceChanged()
    {
        for (OnDataSourceChangeListener listener : dataSourceChangedListenrs) {
            listener.DataSourceChanged();
        }
    }

}
