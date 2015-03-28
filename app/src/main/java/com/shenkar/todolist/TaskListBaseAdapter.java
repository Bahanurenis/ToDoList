package com.shenkar.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.shenkar.common.Task;
import java.util.Calendar;
import java.util.List;

public class TaskListBaseAdapter extends BaseAdapter {
    private static final String TAG = "NotCompletedTaskListBaseAdapter";

    public TaskListBaseAdapter(Context context, List<Task> items) {
        this.items = items;
        this.context = context;
    }

    static class ViewHolder {
        TextView tv_taskName;
        TextView tv_taskNote;
        ImageView iv_taskPriority;
        TextView tv_taskCategory;
        ImageView iv_taskAlarm;
        ImageView iv_taskLocation;
    }

    private Context context;
    private List<Task> items;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        if (this.items != null && items.size() > position)
            return this.items.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void UpdateDataSource(List<Task> items) {
        if(items ==null) return; //TODO Decide how to deal with it (Maybe an exception??)
        this.items= items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.task_list_item, null);
            holder = new ViewHolder();
            holder.tv_taskName = (TextView) convertView
                    .findViewById(R.id.textViewTaskName);
            holder.tv_taskNote = (TextView) convertView
                    .findViewById(R.id.textViewTaskNote);
            holder.iv_taskPriority = (ImageView)convertView
                    .findViewById(R.id.imageViewTaskPriority);
            holder.tv_taskCategory = (TextView) convertView
                    .findViewById(R.id.textViewTaskCategory);
            holder.iv_taskAlarm = (ImageView)convertView
                    .findViewById(R.id.imageViewTaskDueDate);
            holder.iv_taskLocation = (ImageView)convertView
                    .findViewById(R.id.imageViewTaskLocation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_taskName.setText(items.get(position).getTaskName());
        holder.tv_taskNote.setText(items.get(position).getTaskNote());
        if (items.get(position).getPriority() == 1) {
            holder.iv_taskPriority.setImageResource(R.drawable.ex_point);
        }
        else {
            holder.iv_taskPriority.setImageResource(R.drawable.ex_point_disable);
        }
        holder.tv_taskCategory.setText(items.get(position).getCategory());
        long timeToAlarm = items.get(position).getTimeToAlarm();
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        if (timeToAlarm > currentTime) { // means that to due date time hasn't passed yet
            holder.iv_taskAlarm.setImageResource(R.drawable.clock_icon);
        }
        else {
            holder.iv_taskAlarm.setImageResource(R.drawable.clock_icon_disable);
        }
        String location  = items.get(position).getTaskLocation();
        if (location == null || location.equals("")) {
            holder.iv_taskLocation.setImageResource(R.drawable.location_icon_disable);
        }
        else {
            holder.iv_taskLocation.setImageResource(R.drawable.location_icon);
        }
        return convertView;
    }

}
