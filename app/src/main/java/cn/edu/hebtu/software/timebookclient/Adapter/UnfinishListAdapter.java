package cn.edu.hebtu.software.timebookclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.Task;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UnfinishListAdapter extends BaseAdapter {
    private Context context;
    private List<Task> finishTaskList;
    private List<Task> unfinishTaskList;
    private int itemLayout;
    private int tomatoTime;
    private FinishListAdapter finishListAdapter;
    private String serverPath;
    private int flag;

    private TextView tvPlanTime;
    private TextView tvUnfinishCount;
    private TextView tvFinishCount;

    public UnfinishListAdapter(Context context, List<Task> finishTaskList, List<Task> unfinishTaskList, int itemLayout,int tomatoTime,int flag) {
        this.context = context;
        this.finishTaskList = finishTaskList;
        this.unfinishTaskList = unfinishTaskList;
        this.itemLayout = itemLayout;
        this.tomatoTime = tomatoTime;
        this.serverPath = context.getResources().getString(R.string.server_path);
        this.flag = flag;
    }

    public FinishListAdapter getFinishListAdapter() {
        return finishListAdapter;
    }

    public void setFinishListAdapter(FinishListAdapter finishListAdapter) {
        this.finishListAdapter = finishListAdapter;
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

    @Override
    public int getCount() {
        return unfinishTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return unfinishTaskList.get(position);
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
            viewHolder.tvTaskname = convertView.findViewById(R.id.tv_taskname);
            viewHolder.tvFinishCount = convertView.findViewById(R.id.tv_finish_tomato_count);
            viewHolder.tvTotalCount = convertView.findViewById(R.id.tv_total_tomato_count);
            viewHolder.tvExpireDate = convertView.findViewById(R.id.tv_expire_date);
            viewHolder.btnStartTask = convertView.findViewById(R.id.btn_start_task);
            viewHolder.colorIcon = convertView.findViewById(R.id.iv_color_icon);
            viewHolder.tvListname = convertView.findViewById(R.id.tv_listname);

            viewHolder.llList = convertView.findViewById(R.id.ll_list);
            viewHolder.llTomato = convertView.findViewById(R.id.ll_tomato);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //为完成任务按钮绑定事件监听器
        viewHolder.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将该任务从未完成任务列表里去除并添加到已完成任务中
                Task task = unfinishTaskList.get(position);
                task.setCompleteDateTime(new Date());
                task.setFlag(1);
                 finishTaskList.add(task);
                 unfinishTaskList.remove(position);
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
                 finishListAdapter.notifyDataSetChanged();

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

        viewHolder.tvTaskname.setText(unfinishTaskList.get(position).getTitle());
        viewHolder.tvFinishCount.setText(unfinishTaskList.get(position).getUseTime()/(float)tomatoTime+"");
        viewHolder.tvTotalCount.setText(unfinishTaskList.get(position).getCount()+"");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        String date = dateFormat.format(unfinishTaskList.get(position).getExpireDate());
        viewHolder.tvExpireDate.setText(date);

        if(flag == 1){
            //表示从任务清单进入
            viewHolder.llList.setVisibility(View.GONE);
            ((ViewGroup.MarginLayoutParams)viewHolder.llTomato.getLayoutParams()).setMargins(85,6,0,10);
        }else if(flag == 0){
            //表示从日期进入
            viewHolder.llList.setVisibility(View.VISIBLE);
            ((ViewGroup.MarginLayoutParams)viewHolder.llTomato.getLayoutParams()).setMargins(10,6,0,10);
            viewHolder.tvListname.setText(unfinishTaskList.get(position).getList_title());
            switch (unfinishTaskList.get(position).getList_colorId()){
                case R.color.color1:
                    viewHolder.colorIcon.setImageResource(R.color.color1);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color1));
                    break;
                case R.color.color2:
                    viewHolder.colorIcon.setImageResource(R.color.color2);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color2));
                    break;
                case R.color.color3:
                    viewHolder.colorIcon.setImageResource(R.color.color3);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color3));
                    break;
                case R.color.color4:
                    viewHolder.colorIcon.setImageResource(R.color.color4);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color4));
                    break;
                case R.color.color5:
                    viewHolder.colorIcon.setImageResource(R.color.color5);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color5));
                    break;
                case R.color.color6:
                    viewHolder.colorIcon.setImageResource(R.color.color6);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color6));
                    break;
                case R.color.color7:
                    viewHolder.colorIcon.setImageResource(R.color.color7);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color7));
                    break;
                case R.color.color8:
                    viewHolder.colorIcon.setImageResource(R.color.color8);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color8));
                    break;
                case R.color.color9:
                    viewHolder.colorIcon.setImageResource(R.color.color9);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color9));
                    break;
                case R.color.color10:
                    viewHolder.colorIcon.setImageResource(R.color.color10);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color10));
                    break;
                case R.color.color11:
                    viewHolder.colorIcon.setImageResource(R.color.color11);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color11));
                    break;
                case R.color.color12:
                    viewHolder.colorIcon.setImageResource(R.color.color12);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color12));
                    break;
                case R.color.color13:
                    viewHolder.colorIcon.setImageResource(R.color.color13);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color13));
                    break;
                case R.color.color14:
                    viewHolder.colorIcon.setImageResource(R.color.color14);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color14));
                    break;
                case R.color.color15:
                    viewHolder.colorIcon.setImageResource(R.color.color15);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color15));
                    break;
                case R.color.color16:
                    viewHolder.colorIcon.setImageResource(R.color.color16);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color16));
                    break;
                case R.color.color17:
                    viewHolder.colorIcon.setImageResource(R.color.color17);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color17));
                    break;
                case R.color.color18:
                    viewHolder.colorIcon.setImageResource(R.color.color18);
                    viewHolder.tvListname.setTextColor(context.getResources().getColor(R.color.color18));
                    break;
            }
        }

        //为开始任务按钮绑定事件监听器
        viewHolder.btnStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至开始任务页面
            }
        });


        return convertView;
    }

    private class ViewHolder{
        public Button btnFinish;
        public TextView tvTaskname;
        public LinearLayout llList;
        public LinearLayout llTomato;
        public TextView tvFinishCount;
        public TextView tvTotalCount;
        public TextView tvExpireDate;
        public Button btnStartTask;
        public ImageView colorIcon;
        public TextView tvListname;
    }
}
