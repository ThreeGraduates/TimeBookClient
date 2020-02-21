package cn.edu.hebtu.software.timebookclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.R;
import cn.edu.hebtu.software.timebookclient.Util.CircleImageView;

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
        viewHolder.ivColor.setBackgroundColor(taskList.getColor());
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
