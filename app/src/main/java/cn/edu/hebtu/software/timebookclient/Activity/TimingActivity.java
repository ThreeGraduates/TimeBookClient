package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.hebtu.software.timebookclient.Adapter.FinishListAdapter;
import cn.edu.hebtu.software.timebookclient.Adapter.UnfinishListAdapter;
import cn.edu.hebtu.software.timebookclient.Bean.Task;
import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.Bean.User;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TimingActivity extends AppCompatActivity {

    private RelativeLayout rlRoot;
    private TextView tvTaskName;
    private TextView tvFinishTomatoCount;
    private TextView tvTotalTomatoCount;
    private TextView tvExpireDate;
    private TextView tvMinute;
    private TextView tvSecond;
    private Button btnGoAhead;
    private Button btnPause;
    private Button btnStop;

    private int currentTomatoTime;//当前用户所设置的任务番茄时间
    private Task task;
    private ArrayList<Task> unfinishTaskList = new ArrayList<Task>();
    private ArrayList<Task> finishTaskList = new ArrayList<Task>();

    private int breakTime;

    private boolean isTask = true;//用以判断是任务倒计时还是休息倒计时 true:任务倒计时 false:休息倒计时

    private TimerHandler timerHandler = new TimerHandler();
    private ActivityHandler activityHandler = new ActivityHandler();
    private long mMin;
    private long mSecond;
    private TimerTask mTimerTask;

    private int usedCount;//当前已用的番茄数
    private Timer timer = new Timer();
    private String serverPath;

    private User currentUser;
    private int listPosition;
    private int flag;
    private ArrayList<TaskList> taskLists = new ArrayList<TaskList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);

        //调用 获取布局控件方法
        findViews();
        //获取从创建任务页面的ListView中传输的信息
        Intent timingIntent = getIntent();

        currentTomatoTime = timingIntent.getIntExtra("tomatoTime",0);
        breakTime = timingIntent.getIntExtra("breakTime",0);
        task = (Task) timingIntent.getSerializableExtra("task");
        unfinishTaskList = (ArrayList<Task>) timingIntent.getSerializableExtra("unfinishTaskList");
        finishTaskList  = (ArrayList<Task>) timingIntent.getSerializableExtra("finishTaskList");

        currentUser = (User) timingIntent.getSerializableExtra("currentUser");
        taskLists = (ArrayList<TaskList>) timingIntent.getSerializableExtra("taskList");
        listPosition = timingIntent.getIntExtra("listPosition",-1);
        flag = timingIntent.getIntExtra("flag",-1);
        serverPath = getResources().getString(R.string.server_path);

        //填充初始界面基本数据
        tvTaskName.setText(task.getTitle());
        tvFinishTomatoCount.setText(""+task.getUseTime()/(float)currentTomatoTime);
        usedCount = task.getUseTime()/currentTomatoTime;
        tvTotalTomatoCount.setText(""+task.getCount());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        tvExpireDate.setText(simpleDateFormat.format(task.getExpireDate()));
        mMin =currentTomatoTime;
        mSecond = 0;
        tvMinute.setText(getTv(mMin));
        tvSecond.setText(getTv(mSecond));

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                btnPause.setVisibility(View.GONE);
                btnGoAhead.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
            }
        });
        btnGoAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRun();
                btnGoAhead.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                int useTime = usedCount*currentTomatoTime + Integer.parseInt(tvMinute.getText().toString());
                task.setUseTime(task.getUseTime()+useTime);

                AlertDialog.Builder builder = new AlertDialog.Builder(TimingActivity.this);
                builder.setMessage("是否中途放弃该任务?");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        timer.cancel();
                        unfinishTaskList.remove(task);
                        task.setFlag(2);
                        task.setUseTime(usedCount*currentTomatoTime+Integer.parseInt(tvMinute.getText().toString()));

                        //开启异步任务 从服务器端更改用户状态放弃任务
                        Request request = new Request.Builder()
                                .url(serverPath+"task/giveUpTask?taskId="+task.getId()+"&useTime="+task.getUseTime())
                                .build();
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Call call = okHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Message message = new Message();
                                message.what = 0;
                                activityHandler.sendMessage(message);
                            }
                        });
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //不放弃该任务 继续计时
                        startRun();
                        btnPause.setVisibility(View.VISIBLE);
                        btnGoAhead.setVisibility(View.GONE);
                        btnStop.setVisibility(View.GONE);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        startRun();

    }

    //获取布局控件
    private void findViews(){
        rlRoot = findViewById(R.id.rl_startTaskRoot);
        tvTaskName = findViewById(R.id.tv_taskname);
        tvFinishTomatoCount = findViewById(R.id.tv_finish_tomato_count);
        tvTotalTomatoCount = findViewById(R.id.tv_total_tomato_count);
        tvExpireDate = findViewById(R.id.tv_expire_date);
        tvMinute = findViewById(R.id.tv_minute);
        tvSecond = findViewById(R.id.tv_second);
        btnGoAhead = findViewById(R.id.btn_gohead);
        btnPause = findViewById(R.id.btn_pause);
        btnStop = findViewById(R.id.btn_stop);
    }

    //处理数据显示问题 当倒计时的分和秒只剩个位 用0补位
    private String getTv(long l){
        if(l>=10){
            return l+"";
        }else{
            return "0"+l;//小于10,,前面补位一个"0"
        }
    }

    private void startRun() {
        timer = new Timer();
         mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 1;
                timerHandler.sendMessage(message);
            }
        };
        timer.schedule(mTimerTask,0,1000);
    }

    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mMin--;
            mSecond = 59;
            if (mMin < 0) {
                //该轮倒计时结束
                mMin = 0;
                mSecond = 0;
            }
        }
    }

    private class TimerHandler extends Handler{
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                computeTime();
                tvMinute.setText(getTv(mMin));
                tvSecond.setText(getTv(mSecond));
                //表明该轮倒计时结束 判断是任务倒计时结束还是休息倒计时结束
                if(mMin == 0 && mSecond == 0&&isTask){
                    //表明 任务倒计时结束
                    isTask = false;
                    task.setUseTime(task.getUseTime()+currentTomatoTime);
                    usedCount ++;
                    tvFinishTomatoCount.setText(usedCount+"");
                    timer.cancel();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TimingActivity.this);
                    dialogBuilder.setMessage("一个番茄时钟已完成！");
                    dialogBuilder.setPositiveButton("开始休息", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //开始休息倒计时
                            isTask = false;
                            rlRoot.setBackgroundResource(R.drawable.break_background);
                            mMin = breakTime;
                            mSecond = 0;
                            startRun();
                            btnPause.setVisibility(View.VISIBLE);
                            btnGoAhead.setVisibility(View.GONE);
                            btnStop.setVisibility(View.GONE);
                            btnPause.setText("结束");
                            btnPause.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //点击结束休息后
                                    startTask();
                                }
                            });
                        }
                    });
                    dialogBuilder.setNegativeButton("已完成该任务", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //通知adapter数据发生变化 转到创建任务页面
                            finishTaskList.add(task);
                            unfinishTaskList.remove(task);
                            task.setCompleteDateTime(new Date());
                            task.setFlag(1);
                            timer.cancel();

                            //开启异步任务 服务器端更改用户状态
                            Request request = new Request.Builder()
                                    .url(serverPath+"task/completeTask?taskId="+task.getId()+"&useTime="+task.getUseTime())
                                    .build();
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Call call = okHttpClient.newCall(request);
                            call.enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Message message = new Message();
                                    message.what = 0;
                                    activityHandler.sendMessage(message);
                                }
                            });
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }else if(mMin == 0 && mSecond == 0&& !isTask){
                    //休息倒计时结束
                    startTask();
                }

            }
        }
    }

    //任务开始倒计时 执行的操作
    private void startTask(){
        timer.cancel();
        isTask = true;
        rlRoot.setBackgroundResource(R.drawable.start_task_background);
        mMin = currentTomatoTime;
        mSecond = 0;
        tvMinute.setText(getTv(mMin));
        tvSecond.setText(getTv(mSecond));
        startRun();
        btnPause.setVisibility(View.VISIBLE);
        btnPause.setText("暂停");
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                btnPause.setVisibility(View.GONE);
                btnGoAhead.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
            }
        });

    }

    private class ActivityHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                Intent intent = new Intent(TimingActivity.this,CreateTaskActivity.class);
                intent.putExtra("currentUser",currentUser);
                intent.putExtra("flag",flag);
                intent.putExtra("listPosition",listPosition);
                intent.putExtra("taskList",taskLists);
                startActivity(intent);
            }
        }
    }
}
