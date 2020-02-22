package cn.edu.hebtu.software.timebookclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.R;

public class TaskListAdapter extends BaseAdapter {

    private List<TaskList> taskLists;
    private Context context;
    private int itemLayout;

    public TaskListAdapter(List<TaskList> taskLists, Context context, int itemLayout) {
        this.taskLists = taskLists;
        this.context = context;
        this.itemLayout = itemLayout;
    }

    @Override
    public int getCount() {
        return taskLists.size();
    }

    @Override
    public Object getItem(int position) {
        return taskLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(itemLayout,null);
            viewHolder.ivColor = convertView.findViewById(R.id.iv_color);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time);
            viewHolder.tvCount = convertView.findViewById(R.id.tv_count);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskList taskList = taskLists.get(position);
        //设置清单的图标颜色
        switch (taskList.getColor()){
            case R.color.color1:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color1));
                break;
            case R.color.color2:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color2));
                break;
            case R.color.color3:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color3));
                break;
            case R.color.color4:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color4));
                break;
            case R.color.color5:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color5));
                break;
            case R.color.color6:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color6));
                break;
            case R.color.color7:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color7));
                break;
            case R.color.color8:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color8));
                break;
            case R.color.color9:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color9));
                break;
            case R.color.color10:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color10));
                break;
            case R.color.color11:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color11));
                break;
            case R.color.color12:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color12));
                break;
            case R.color.color13:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color13));
                break;
            case R.color.color14:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color14));
                break;
            case R.color.color15:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color15));
                break;
            case R.color.color16:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color16));
                break;
            case R.color.color17:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color17));
                break;
            case R.color.color18:
                viewHolder.ivColor.setBackgroundColor(context.getResources().getColor(R.color.color18));
                break;

        }
        viewHolder.tvTitle.setText(taskList.getTitle());
        viewHolder.tvTime.setText(taskList.getSumTime()+"");
        viewHolder.tvCount.setText(taskList.getTaskCount()+"");
        return convertView;
    }

    private class ViewHolder{

        public ImageView ivColor;
        public TextView tvTitle;
        public TextView tvTime;
        public TextView tvCount;
    }
}
