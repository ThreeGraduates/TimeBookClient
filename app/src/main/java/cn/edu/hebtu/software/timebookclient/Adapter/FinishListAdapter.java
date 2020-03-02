package cn.edu.hebtu.software.timebookclient.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.Task;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FinishListAdapter extends BaseAdapter {

    private Context context;
    private List<Task> finishTaskList = new ArrayList<Task>();
    private List<Task> unfinishTaskList = new ArrayList<Task>();
    private int itemLayout;
    private int tomatoTime;
    private UnfinishListAdapter unfinishListAdapter;
    private  String serverPath;

    public FinishListAdapter(Context context, List<Task> finishTaskList, List<Task> unfinishTaskList, int itemLayout, int tomatoTime) {
        this.context = context;
        this.finishTaskList = finishTaskList;
        this.unfinishTaskList = unfinishTaskList;
        this.itemLayout = itemLayout;
        this.tomatoTime = tomatoTime;
        this.serverPath = context.getResources().getString(R.string.server_path);
    }

    public UnfinishListAdapter getUnfinishListAdapter() {
        return unfinishListAdapter;
    }

    public void setUnfinishListAdapter(UnfinishListAdapter unfinishListAdapter) {
        this.unfinishListAdapter = unfinishListAdapter;
    }

    @Override
    public int getCount() {
        return finishTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return finishTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(itemLayout,null);
            viewHolder = new ViewHolder();
            viewHolder.btnFinish = convertView.findViewById(R.id.btn_isfinish);
            viewHolder.tvTaskName = convertView.findViewById(R.id.tv_taskname);
            viewHolder.tvListName = convertView.findViewById(R.id.tv_listname);
            viewHolder.tvFinishTomatoCount = convertView.findViewById(R.id.tv_finish_tomato_count);
            viewHolder.tvCompleteDate = convertView.findViewById(R.id.tv_complete_date);
            viewHolder.btnStartTask = convertView.findViewById(R.id.btn_start_task);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为完成按钮绑定点击事件监听器
                //将任务从完成列表去除 加入未完成列表
                Task task =finishTaskList.get(position);
                task.setFlag(0);
                task.setCompleteDateTime(null);
                unfinishTaskList.add(task);
                finishTaskList.remove(task);
                //将未完成任务列表中的任务按照到期日期排序
                Collections.sort(unfinishTaskList, new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        int flag = o1.getExpireDate().compareTo(o2.getExpireDate());
                        return flag;
                    }
                });
                //对已完成任务列表中的任务按照完成日期排序
                Collections.sort(finishTaskList, new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        int flag = o1.getCompleteDateTime().compareTo(o2.getCompleteDateTime());
                        return flag;
                    }
                });
                notifyDataSetChanged();
                unfinishListAdapter.notifyDataSetChanged();

                //开启异步任务 改变数据库中的数据
                //开启异步任务改变数据库中的数据
                Request request = new Request.Builder()
                        .url(serverPath+"transFormTaskStatus?taskId=+"+task.getId()+"&flag="+task.getFlag())
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();
                Call call = okHttpClient.newCall(request);
                try {
                    call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        viewHolder.tvTaskName.setText(finishTaskList.get(position).getTitle());
        //添加删除线
        viewHolder.tvTaskName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.tvListName.setText(finishTaskList.get(position).getList_title());
        viewHolder.tvFinishTomatoCount.setText(finishTaskList.get(position).getUseTime()/(float)tomatoTime+"");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String date = dateFormat.format(finishTaskList.get(position).getCompleteDateTime());
        viewHolder.tvCompleteDate.setText(date);
        viewHolder.btnStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为重新开启任务绑定事件监听器
            }
        });
        return convertView;
    }

    private class ViewHolder{
        public Button btnFinish;
        public TextView tvTaskName;
        public TextView tvListName;
        public TextView tvFinishTomatoCount;
        public TextView tvCompleteDate;
        public Button btnStartTask;
    }
}
