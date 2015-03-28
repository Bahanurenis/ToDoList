package com.shenkar.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenkar.common.Category;

import java.util.List;


public class CategoryListBaseAdapter extends BaseAdapter {
    private static final String TAG = "CategoryListBaseAdapter";
    public CategoryListBaseAdapter(Context context, List<Category> items) {
        this.items = items;
        this.context = context;
    }
    static class ViewHolder {
        TextView tv_categoryName;
        ImageView iv_categoryImage;
    }
    private Context context;
    private List<Category> items;

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

    public void UpdateDataSource(List<Category> items) {
        if(items ==null) return;
        this.items= items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.category_list_item, null);
            holder = new ViewHolder();
            holder.tv_categoryName = (TextView) convertView
                    .findViewById(R.id.textViewCategoryName);
            holder.iv_categoryImage = (ImageView)convertView
                    .findViewById(R.id.imageViewCategoryImage);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_categoryName.setText(items.get(position).getCategoryName());
        String categoryName = items.get(position).getCategoryName();
        if (categoryName.equals("home")||categoryName.equals("Home")||categoryName.equals("HOME")){
            holder.iv_categoryImage.setImageResource(R.drawable.home);
        }
        else if (categoryName.equals("music")||categoryName.equals("Music")||categoryName.equals("MUSIC")){
            holder.iv_categoryImage.setImageResource(R.drawable.music);
        }
        else if (categoryName.equals("food")||categoryName.equals("Food")||categoryName.equals("FOOD")){
            holder.iv_categoryImage.setImageResource(R.drawable.food);
        }
        else if (categoryName.equals("shopping")||categoryName.equals("Shopping")||categoryName.equals("SHOPPING")){
            holder.iv_categoryImage.setImageResource(R.drawable.shopping);
        }
        else if (categoryName.equals("clothing")||categoryName.equals("Clothing")||categoryName.equals("CLOTHING")){
            holder.iv_categoryImage.setImageResource(R.drawable.clothe);
        }
        else if (categoryName.equals("School")||categoryName.equals("Studies")||categoryName.equals("school")||categoryName.equals("studies")){
            holder.iv_categoryImage.setImageResource(R.drawable.school);
        }
        else if (categoryName.equals("Sport")||categoryName.equals("sport")||categoryName.equals("SPORT")){
            holder.iv_categoryImage.setImageResource(R.drawable.sport);
        }
        else if (categoryName.equals("travel")||categoryName.equals("Travel")||categoryName.equals("TRAVEL")){
            holder.iv_categoryImage.setImageResource(R.drawable.travel);        }
        else if (categoryName.equals("Work")||categoryName.equals("work")||categoryName.equals("WORK")){
            holder.iv_categoryImage.setImageResource(R.drawable.work);
        }
        else {
            holder.iv_categoryImage.setImageResource(R.drawable.question);
        }
        return convertView;
    }
}
