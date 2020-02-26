package cn.edu.hebtu.software.timebookclient.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import cn.edu.hebtu.software.timebookclient.Activity.TaskListManagerActivity;
import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.R;

public class TaskListManagerAdapter extends BaseAdapter{
    private List<TaskList> taskLists;
    private TaskListManagerActivity context;
    private PopupWindow popupWindow;
    private LinearLayout root;

    public TaskListManagerAdapter(List<TaskList> taskLists, TaskListManagerActivity context, LinearLayout root) {
        this.taskLists = taskLists;
        this.context = context;
        this.root=root;
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
        ViewHolder vh=null;
        //1.加载布局
        if(null==convertView){
            convertView= LayoutInflater.from(context).inflate(R.layout.tasklistmanager_item_layout,null);//有嵌套则用parent
            vh=new ViewHolder();
            vh.ivColor=convertView.findViewById(R.id.list_colorId);
            vh.tvTitle=convertView.findViewById(R.id.list_title);
            vh.ivDelete=convertView.findViewById(R.id.iv_delete);
            convertView.setTag(vh);
        }else{
            vh= (ViewHolder) convertView.getTag();
        }
        //2.填充数据
        TaskList taskList=taskLists.get(position);
        vh.ivColor.setImageResource(taskList.getColorId());
        vh.tvTitle.setText(taskList.getTitle());
        //删除清单
        final String title=taskList.getTitle();
        final Long id=taskList.getId();
        vh.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.showDeletePopupWindow(id,title);
            }
        });
        return convertView;
    }

    class ViewHolder{
        private ImageView ivColor;
        private TextView tvTitle;
        private ImageView ivDelete;
    }
}
