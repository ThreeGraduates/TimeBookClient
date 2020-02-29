package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Adapter.FinishListAdapter;
import cn.edu.hebtu.software.timebookclient.Adapter.UnfinishListAdapter;
import cn.edu.hebtu.software.timebookclient.Bean.Task;
import cn.edu.hebtu.software.timebookclient.Bean.User;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateTaskActivity extends AppCompatActivity {

    private TextView tvDate;
    private Button btnBack;
    private TextView tvPlanTime;
    private TextView tvUnfinishCount;
    private TextView tvUsedTime;
    private TextView tvFinishCount;
    private RelativeLayout rlCreateTask;
    private ListView lvUnfinishTasklist;
    private Button btnShowFinish;
    private ListView lvFinishTasklist;
    private boolean show = false;//默认不展示已完成任务列表
    private LinearLayout llSummary;
    private List<Task> totalTaskList = new ArrayList<Task>();
    private List<Task> unfinishTaskList = new ArrayList<Task>();
    private List<Task> finishTaskList = new ArrayList<Task>();
    private UnfinishListAdapter unfinishListAdapter ;
    private FinishListAdapter finishListAdapter;
    private String serverPath;
    private CreateHandler createHandler = new CreateHandler();

    private int usedTime = 0;//表示今日已使用的时间为 0

    private User currentUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        //获取布局控件
        findView();
        //获取从MainActivity中传递过来的currentUser
        Intent mainIntent = getIntent();
        currentUser = (User) mainIntent.getSerializableExtra("currentUser");
        int flag = mainIntent.getIntExtra("flag",-1);
        Log.e("CreateTaskActivity","tomatoTime-"+currentUser.getTomatoTime());
        switch (flag){
            case 0:
                //从主页点击了"今天"跳转到该页面
                tvDate.setText("今天");
                llSummary.setVisibility(View.VISIBLE);
                break;
            case 1:
                //从主页点击了"明天"跳转到该页面
                tvDate.setText("明天");
                llSummary.setVisibility(View.GONE);
                break;
            case 2:
                //从主页点击了"即将到来"跳转到该页面
                tvDate.setText("即将到来");
                llSummary.setVisibility(View.GONE);
                break;
        }

        //为返回按钮 绑定点击事件监听器
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //为添加任务框的rl绑定事件监听器
        rlCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出添加任务框


            }
        });
        //获取服务器端ip
        serverPath = getResources().getString(R.string.server_path);
        //定义Gson
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd")
                .create();
        //获得当前SharedPreferences内的id
        Long id = getSharedPreferences("data",MODE_PRIVATE).getLong("userId",0);
        currentUser.setId(id);

        //从服务器端获取任务列表
        Request request = new Request.Builder()
                .url(serverPath+"taskList/getTasksTodayAndTomorrowAndSoon?flag="+flag+"&userId="+currentUser.getId())
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CreateTaskActivity","网络响应时间过长");
                Message failMessage = new Message();
                failMessage.what = -1;
                createHandler.sendMessage(failMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String result = bufferedReader.readLine();
                Log.e("CreateTaskActivity","result"+result);
                totalTaskList = gson.fromJson(result,new TypeToken<List<Task>>(){}.getType());
                //将获取到的总的任务按照完成和未完成进行分类
                for(Task task : totalTaskList){
                    if(task.getFlag() == 0)
                        //该任务未开始
                        unfinishTaskList.add(task);
                    else if(task.getFlag() == 1)
                        //该任务已完成
                        finishTaskList.add(task);
                }
                Message message = new Message();
                message.what = 1;
                createHandler.sendMessage(message);
            }
        });


        //为显示已完成任务列表按钮绑定点击事件监听器
        btnShowFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show = !show;
                if(show){
                    btnShowFinish.setText("隐藏已完成任务");
                    lvFinishTasklist.setVisibility(View.VISIBLE);
                }else{
                    btnShowFinish.setText("显示已完成任务");
                    lvFinishTasklist.setVisibility(View.GONE);
                }
            }
        });
    }

    public  void findView(){
        tvDate = findViewById(R.id.tv_date);
        btnBack = findViewById(R.id.btn_back);
        tvPlanTime = findViewById(R.id.tv_plan_time);
        tvUnfinishCount = findViewById(R.id.tv_unfinish_count);
        tvUsedTime = findViewById(R.id.tv_used_time);
        tvFinishCount = findViewById(R.id.tv_finish_count);
        rlCreateTask = findViewById(R.id.rl_create_task);
        lvUnfinishTasklist = findViewById(R.id.lv_unfinish_tasklist);
        lvFinishTasklist = findViewById(R.id.lv_finish_tasklist);
        btnShowFinish = findViewById(R.id.btn_show_finish);
        llSummary = findViewById(R.id.ll_summary);
    }

    public class CreateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == -1){
                Toast warnToast = Toast.makeText(CreateTaskActivity.this,"网络响应时间过长",Toast.LENGTH_SHORT);
                warnToast.setGravity(Gravity.TOP,0,400);
                warnToast.show();
            }else if(msg.what == 1){
                //填充llSummary框中的数据
                int planTime = 0;
                for(Task task : unfinishTaskList)
                    planTime = planTime+task.getCount() * currentUser.getTomatoTime() - task.getUseTime();

                tvPlanTime.setText(planTime+"");
                tvUnfinishCount.setText(unfinishTaskList.size()+"");
                tvUsedTime.setText(usedTime+"");
                tvFinishCount.setText(finishTaskList.size()+"");

                unfinishListAdapter = new UnfinishListAdapter(CreateTaskActivity.this,finishTaskList,unfinishTaskList,R.layout.unfinish_tasks_item_layout,currentUser.getTomatoTime());
                finishListAdapter = new FinishListAdapter(CreateTaskActivity.this,finishTaskList,unfinishTaskList,R.layout.finish_tasks_item_layout,currentUser.getTomatoTime());
                unfinishListAdapter.setFinishListAdapter(finishListAdapter);
                finishListAdapter.setUnfinishListAdapter(unfinishListAdapter);
                //为unfinishListView绑定adapter
                lvUnfinishTasklist.setAdapter(unfinishListAdapter);
                //为finishListView绑定adapter
                lvFinishTasklist.setAdapter(finishListAdapter);
            }

        }
    }
}
