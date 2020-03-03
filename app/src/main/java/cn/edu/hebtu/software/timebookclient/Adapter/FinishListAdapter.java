package cn.edu.hebtu.software.timebookclient.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.Task;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FinishListAdapter extends BaseAdapter {

    private Context context;
    private List<Task> finishTaskList = new ArrayList<Task>();
    private List<Task> unfinishTaskList = new ArrayList<Task>();
    private int itemLayout;
    private int tomatoTime;
    private UnfinishListAdapter unfinishListAdapter;
    private  String serverPath;
    private  int flag;

    private TextView tvPlanTime;
    private TextView tvUnfinishCount;
    private TextView tvFinishCount;
    private TextView tvUsedTime;

    public FinishListAdapter(Context context, List<Task> finishTaskList, List<Task> unfinishTaskList, int itemLayout, int tomatoTime,int flag) {
        this.context = context;
        this.finishTaskList = finishTaskList;
        this.unfinishTaskList = unfinishTaskList;
        this.itemLayout = itemLayout;
        this.tomatoTime = tomatoTime;
        this.serverPath = context.getResources().getString(R.string.server_path);
        this.flag= flag;
    }

    public UnfinishListAdapter getUnfinishListAdapter() {
        return unfinishListAdapter;
    }

    public void setUnfinishListAdapter(UnfinishListAdapter unfinishListAdapter) {
        this.unfinishListAdapter = unfinishListAdapter;
    }

    public TextView getTvPlanTime() {
        return tvPlanTime;
    }

    public void setTvPlanTime(TextView tvPlanTime) {
        this.tvPlanTime = tvPlanTime;
    }

    public TextView getTvUnfinishCount() {
        return tvUnfinishCount;
    }

    public void setTvUnfinishCount(TextView tvUnfinishCount) {
        this.tvUnfinishCount = tvUnfinishCount;
    }


    public TextView getTvFinishCount() {
        return tvFinishCount;
    }

    public void setTvFinishCount(TextView tvFinishCount) {
        this.tvFinishCount = tvFinishCount;
    }

    public TextView getTvUsedTime() {
        return tvUsedTime;
    }

    public void setTvUsedTime(TextView tvUsedTime) {
        this.tvUsedTime = tvUsedTime;
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
            viewHolder.colorIcon = convertView.findViewById(R.id.iv_color_icon);
            viewHolder.btnDelete = convertView.findViewById(R.id.btn_delete);
            viewHolder.btnStartTask = convertView.findViewById(R.id.btn_start_task);
            viewHolder.llList = convertView.findViewById(R.id.ll_list);
            viewHolder.llTomato = convertView.findViewById(R.id.ll_tomato);
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
                        return o1.getCompleteDateTime().compareTo(o2.getCompleteDateTime());
                    }
                });

                notifyDataSetChanged();
                unfinishListAdapter.notifyDataSetChanged();
                //改变总览处的几个数据
                int planTime = 0;
                for(Task item : unfinishTaskList)
                    planTime = planTime+item.getCount() * tomatoTime- task.getUseTime();

                tvPlanTime.setText(planTime+"");
                tvUnfinishCount.setText(unfinishTaskList.size()+"");
                tvFinishCount.setText(finishTaskList.size()+"");


                //开启异步任务改变数据库中的数据
                Request request = new Request.Builder()
                        .url(serverPath+"transFormTaskStatus?taskId=+"+task.getId()+"&flag="+task.getFlag())
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });

            }
        });
        viewHolder.tvTaskName.setText(finishTaskList.get(position).getTitle());
        //添加删除线
        viewHolder.tvTaskName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (flag == 0){
            //表示从日期进入
            viewHolder.llList.setVisibility(View.VISIBLE);
            ((ViewGroup.MarginLayoutParams)viewHolder.llTomato.getLayoutParams()).setMargins(10,6,0,10);
            viewHolder.tvListName.setText(finishTaskList.get(position).getList_title());
        }else if (flag == 1){
            //表示从任务清单进入
            viewHolder.llList.setVisibility(View.GONE);
            ((ViewGroup.MarginLayoutParams)viewHolder.llTomato.getLayoutParams()).setMargins(85,6,0,10);
        }

        viewHolder.tvFinishTomatoCount.setText(finishTaskList.get(position).getUseTime()/(float)tomatoTime+"");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String date = dateFormat.format(finishTaskList.get(position).getCompleteDateTime());
        viewHolder.tvCompleteDate.setText(date);

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为删除任务绑定事件监听器

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //刷新数据框
                        Task temp = finishTaskList.get(position);
                        finishTaskList.remove(position);
                        notifyDataSetChanged();
                        int planTime = 0;
                        int usedTime = 0;
                        for(Task item : finishTaskList){
                            planTime = item.getCount()*tomatoTime - item.getUseTime();
                            usedTime += item.getUseTime();
                        }

                        tvPlanTime.setText(planTime+"");
                        tvFinishCount.setText(finishTaskList.size()+"");
                        tvUsedTime.setText(usedTime+"");

                        //开启异步任务
                        Request deleteRequest = new Request.Builder()
                                .url(serverPath+"task/deleteTask?taskId="+temp.getId())
                                .build();
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Call call = okHttpClient.newCall(deleteRequest);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                InputStream inputStream = response.body().byteStream();
                                String result = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                                Log.e("FinishListAdapter","result:"+result);
                            }
                        });
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                builder.setMessage("确认删除该任务？");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

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
        public ImageView colorIcon;
        public LinearLayout llList;
        public LinearLayout llTomato;
        public Button btnDelete;
        public Button btnStartTask;
    }
}
