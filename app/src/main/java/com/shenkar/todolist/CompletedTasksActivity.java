package com.shenkar.todolist;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.shenkar.common.OnDataSourceChangeListener;
import com.shenkar.common.Task;

import java.util.List;


public class CompletedTasksActivity extends ActionBarActivity implements OnDataSourceChangeListener {

    private MainController controller;
    private TaskListBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4285f4"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("Completed Tasks");
        controller = new MainController(this);
        controller.registerOnDataSourceChanged(this);
        ListView completedTaskLv = (ListView) findViewById(R.id.taskListViewCompleted);
        if (completedTaskLv != null) { //completed tasks list
            adapter = new TaskListBaseAdapter(this,controller.getCompletedTasks());
            List<Task> list = controller.getCompletedTasks();
            completedTaskLv.setAdapter(adapter);
            completedTaskLv.setOnTouchListener(new OnSwipeTouchListener(this,completedTaskLv){
                public void onSwipeRight(int position) { // mark task as done
                    Task t = (Task) adapter.getItem(position);
                    controller.getTaskBackConfirmation(t);
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_completed_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void DataSourceChanged() {
        if (adapter != null) {
            adapter.UpdateDataSource(controller.getCompletedTasks());
            adapter.notifyDataSetChanged();
        }
    }

}
