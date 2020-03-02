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

public class CTTaskListAdapter extends BaseAdapter {

    private Context context;
    private List<TaskList> taskListList;
    private int itemLayout;

    public CTTaskListAdapter(Context context, List<TaskList> taskListList, int itemLayout) {
        this.context = context;
        this.taskListList = taskListList;
        this.itemLayout = itemLayout;
    }

    @Override
    public int getCount() {
        return taskListList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskListList.get(position);
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
            viewHolder.tvTasklistName = convertView.findViewById(R.id.tv_list_title);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();

        switch (taskListList.get(position).getColorId()){
            case R.color.color1:
                viewHolder.ivColor.setImageResource(R.color.color1);
                break;
            case R.color.color2:
                viewHolder.ivColor.setImageResource(R.color.color2);
                break;
            case R.color.color3:
                viewHolder.ivColor.setImageResource(R.color.color3);
                break;
            case R.color.color4:
                viewHolder.ivColor.setImageResource(R.color.color4);
                break;
            case R.color.color5:
                viewHolder.ivColor.setImageResource(R.color.color5);
                break;
            case R.color.color6:
                viewHolder.ivColor.setImageResource(R.color.color6);
                break;
            case R.color.color7:
                viewHolder.ivColor.setImageResource(R.color.color7);
                break;
            case R.color.color8:
                viewHolder.ivColor.setImageResource(R.color.color8);
                break;
            case R.color.color9:
                viewHolder.ivColor.setImageResource(R.color.color9);
                break;
            case R.color.color10:
                viewHolder.ivColor.setImageResource(R.color.color10);
                break;
            case R.color.color11:
                viewHolder.ivColor.setImageResource(R.color.color11);
                break;
            case R.color.color12:
                viewHolder.ivColor.setImageResource(R.color.color12);
                break;
            case R.color.color13:
                viewHolder.ivColor.setImageResource(R.color.color13);
                break;
            case R.color.color14:
                viewHolder.ivColor.setImageResource(R.color.color14);
                break;
            case R.color.color15:
                viewHolder.ivColor.setImageResource(R.color.color15);
                break;
            case R.color.color16:
                viewHolder.ivColor.setImageResource(R.color.color16);
                break;
            case R.color.color17:
                viewHolder.ivColor.setImageResource(R.color.color17);
                break;
            case R.color.color18:
                viewHolder.ivColor.setImageResource(R.color.color18);
                break;
        }
        viewHolder.tvTasklistName.setText(taskListList.get(position).getTitle());

        return convertView;
    }

    private class ViewHolder{

        public ImageView ivColor;
        public TextView tvTasklistName;

    }
}
