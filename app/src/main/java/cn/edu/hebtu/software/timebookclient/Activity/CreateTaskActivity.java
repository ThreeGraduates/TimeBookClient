package cn.edu.hebtu.software.timebookclient.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


import cn.edu.hebtu.software.timebookclient.Adapter.CTTaskListAdapter;
import cn.edu.hebtu.software.timebookclient.Adapter.FinishListAdapter;
import cn.edu.hebtu.software.timebookclient.Adapter.UnfinishListAdapter;
import cn.edu.hebtu.software.timebookclient.Bean.Task;
import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.Bean.User;
import cn.edu.hebtu.software.timebookclient.R;
import cn.edu.hebtu.software.timebookclient.Util.NumberPickerUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateTaskActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, NumberPicker.Formatter, NumberPicker.OnScrollListener,DatePicker.OnDateChangedListener {

    private RelativeLayout rlRoot;
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
    private ArrayList<Task> totalTaskList = new ArrayList<Task>();
    private ArrayList<Task> unfinishTaskList = new ArrayList<Task>();
    private ArrayList<Task> finishTaskList = new ArrayList<Task>();
    private UnfinishListAdapter unfinishListAdapter ;
    private FinishListAdapter finishListAdapter;
    private String serverPath;
    private CreateHandler createHandler = new CreateHandler();
    //createTaskWindow的布局中的各个控件元素
    private PopupWindow createTaskWindow;
    private Button btnCloseWindow;
    private TextView tvFinish;
    private EditText etTaskTitle;
    private RelativeLayout rlSettingCount;
    private TextView tomatoCount;
    private NumberPicker numberPicker;
    private RelativeLayout rlSettingDate;
    private TextView expireDate;
    private DatePicker datePicker;
    private RelativeLayout rlSettingList;
    private TextView tvListTitle;
    private ListView lvTaskList;
    private ArrayList<TaskList> taskLists = new ArrayList<TaskList>();
    private CTTaskListAdapter taskListAdapter;

    private int usedTime = 0;//表示今日已使用的时间为 0
    private int index = 0;
    private int flag = 0;
    private int listPosition = -1;
    private  String listResult;

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
        taskLists = (ArrayList<TaskList>) mainIntent.getSerializableExtra("taskList");
        flag = mainIntent.getIntExtra("flag",-1);
        listPosition = mainIntent.getIntExtra("listPosition",-1);
        Log.e("CreateTaskActivity",flag+"");
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
            case 3:
                //从主页点击某项清单跳转到该页面
                tvDate.setText(taskLists.get(listPosition).getTitle());
                llSummary.setVisibility(View.VISIBLE);
                break;
        }



        //为返回按钮 绑定点击事件监听器
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTaskActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //为添加任务框的rl绑定事件监听器
        rlCreateTask.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //弹出添加任务框 createTaskWindow弹出
                showCreateTaskPopupWindow();
            }
        });
        //获取服务器端ip
        serverPath = getResources().getString(R.string.server_path);
        //定义Gson
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        //获得当前SharedPreferences内的id
        Long id = getSharedPreferences("data",MODE_PRIVATE).getLong("userId",0);
        currentUser.setId(id);
        if(flag == 3) {
            Log.e("CreateTaskActivity","当前任务列表id:"+taskLists.get(listPosition).getId());
            //从服务器端 根据任务清单id获取该清单的任务列表
            final Request listRequest = new Request.Builder()
                    .url(serverPath+"taskList/getTasksByListId?taskListId="+taskLists.get(listPosition).getId())
                    .build();
            OkHttpClient listOkHttpClient = new OkHttpClient();
            Call listCall = listOkHttpClient.newCall(listRequest);
            listCall.enqueue(new Callback() {
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
                    listResult = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                    Log.e("CreateTaskActivity","根据清单id获取到的数据:"+listResult);
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    totalTaskList = gson.fromJson(listResult,new TypeToken<List<Task>>(){}.getType());
                    //将获取到的总的任务按照完成和未完成进行分类
                    for(Task task : totalTaskList){
                        if(task.getFlag() == 0)
                            //该任务未开始
                            unfinishTaskList.add(task);
                        else if(task.getFlag() == 1)
                            //该任务已完成
                            finishTaskList.add(task);
                        usedTime +=task.getUseTime();
                    }
                    Message message = new Message();
                    message.what = 1;
                    createHandler.sendMessage(message);
                }
            });

        }else{

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
                    Log.e("CreateTaskActivity","根据日期获取到的数据:"+result);
                    totalTaskList = gson.fromJson(result,new TypeToken<List<Task>>(){}.getType());
                    //将获取到的总的任务按照完成和未完成进行分类
                    for(Task task : totalTaskList){
                        if(task.getFlag() == 0)
                            //该任务未开始
                            unfinishTaskList.add(task);
                        else if(task.getFlag() == 1)
                            //该任务已完成
                            finishTaskList.add(task);
                        usedTime +=task.getUseTime();
                    }
                    Message message = new Message();
                    message.what = 1;
                    createHandler.sendMessage(message);
                }
            });

        }



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
        rlRoot = findViewById(R.id.rl_root);
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

                if(flag == 3){
                    //从任务清单点击进入
                    unfinishListAdapter = new UnfinishListAdapter(CreateTaskActivity.this,finishTaskList,unfinishTaskList,R.layout.unfinish_tasks_item_layout,currentUser.getTomatoTime(),1);
                    finishListAdapter = new FinishListAdapter(CreateTaskActivity.this,finishTaskList,unfinishTaskList,R.layout.finish_tasks_item_layout,currentUser.getTomatoTime(),1);
                }else{
                    //从日期点击进入
                    unfinishListAdapter = new UnfinishListAdapter(CreateTaskActivity.this,finishTaskList,unfinishTaskList,R.layout.unfinish_tasks_item_layout,currentUser.getTomatoTime(),0);
                    finishListAdapter = new FinishListAdapter(CreateTaskActivity.this,finishTaskList,unfinishTaskList,R.layout.finish_tasks_item_layout,currentUser.getTomatoTime(),0);
                }

                unfinishListAdapter.setTvPlanTime(tvPlanTime);
                finishListAdapter.setTvPlanTime(tvPlanTime);
                unfinishListAdapter.setTvUnfinishCount(tvUnfinishCount);
                finishListAdapter.setTvUnfinishCount(tvUnfinishCount);
                unfinishListAdapter.setTvFinishCount(tvFinishCount);
                finishListAdapter.setTvFinishCount(tvFinishCount);
                unfinishListAdapter.setTvUsedTime(tvUsedTime);
                finishListAdapter.setTvUsedTime(tvUsedTime);
                unfinishListAdapter.setBreakTime(currentUser.getShortBreak());

                unfinishListAdapter.setFinishListAdapter(finishListAdapter);
                finishListAdapter.setUnfinishListAdapter(unfinishListAdapter);

                unfinishListAdapter.setCurrentUser(currentUser);
                unfinishListAdapter.setTaskLists(taskLists);
                unfinishListAdapter.setListPosition(listPosition);
                unfinishListAdapter.setEnterFlag(flag);

                //为unfinishListView绑定adapter
                lvUnfinishTasklist.setAdapter(unfinishListAdapter);
                //为finishListView绑定adapter
                lvFinishTasklist.setAdapter(finishListAdapter);
            }

        }
    }

    //创建任务的popupwindow的获取和相关设置
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showCreateTaskPopupWindow(){
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_create_task,null,false);
        createTaskWindow = new PopupWindow(this);
        createTaskWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        createTaskWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        createTaskWindow.setContentView(contentView);
        createTaskWindow.setFocusable(true);
        createTaskWindow.setOutsideTouchable(true);
        createTaskWindow.setBackgroundDrawable(getDrawable(R.drawable.create_task_window_style));

        findWindowView(contentView);
        settingViews();
        backgroundAlpha(0.7f);
        createTaskWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        createTaskWindow.showAtLocation(rlRoot,Gravity.CENTER,0,0);


    }

    //获取createTaskWindow中的布局控件
    public void findWindowView(View view){
        btnCloseWindow = view.findViewById(R.id.btn_close);
        tvFinish = view.findViewById(R.id.tv_finish);
        etTaskTitle = view.findViewById(R.id.et_task_title);
        rlSettingCount = view.findViewById(R.id.rl_setting_count);
        tomatoCount = view.findViewById(R.id.tv_tomato_count);
        numberPicker = view.findViewById(R.id.select_count);
        rlSettingDate = view.findViewById(R.id.rl_setting_date);
        expireDate = view.findViewById(R.id.expire_date);
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        expireDate.setText(simpleDateFormat.format(today));
        datePicker = view.findViewById(R.id.pick_expire_date);
        rlSettingList = view.findViewById(R.id.rl_setting_list);
        tvListTitle = view.findViewById(R.id.list_name);
        lvTaskList = view.findViewById(R.id.lv_taskList);

    }

    //为createTaskWindow中布局控件进行设置
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void settingViews(){
        btnCloseWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击关闭按钮 createTaskWindow消失 并且背景透明度恢复为1
                createTaskWindow.dismiss();
            }
        });

        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task;
                String taskTitle = etTaskTitle.getText().toString();
                int taskTomatoCount = Integer.parseInt(tomatoCount.getText().toString());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = new Date();
                    date = simpleDateFormat.parse(expireDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String listName =  tvListTitle.getText().toString();
                if(taskTitle != null && !"".equals(taskTitle) && taskTomatoCount!=0 && date!=null && listName!=null && !"".equals(listName)){
                    //点击完成 开启异步任务
                    task = new Task();
                    task.setTitle(taskTitle);
                    task.setCount(taskTomatoCount);
                    //判断是从今天 明天 还是即将到来进入
                    Date createDate;
                    Calendar rightNow = Calendar.getInstance();
                    switch (flag){
                        case 1:
                            //从明天处进入
                            rightNow.add(Calendar.DAY_OF_YEAR,1);
                            createDate = rightNow.getTime();
                            break;
                        case 2:
                            //从即将到来进入
                            rightNow.add(Calendar.DAY_OF_YEAR,7);
                            createDate = rightNow.getTime();
                            break;
                            default:
                                //从今天或者任务清单进入
                                createDate = new Date();
                                break;
                    }
                    task.setCreateDate(createDate);
                    task.setExpireDate(date);
                    task.setPriority(0);
                    task.setFlag(0);
                    task.setUserId(currentUser.getId());
                    task.setList_title(listName);
                    task.setUseTime(0);
                    task.setCheckListId(taskLists.get(index).getId());
                    task.setList_colorId(taskLists.get(index).getColorId());
                    unfinishTaskList.add(task);
                    Collections.sort(unfinishTaskList, new Comparator<Task>() {
                        @Override
                        public int compare(Task o1, Task o2) {
                            //根据任务的到期时间进行排序
                            return o1.getExpireDate().compareTo(o2.getExpireDate());
                        }
                    });
                    unfinishListAdapter.notifyDataSetChanged();

                    //对总览框做出相应的更改
                    tvUnfinishCount.setText(unfinishTaskList.size()+"");
                    int minute = 0;
                    for(Task item : unfinishTaskList){
                        minute += item.getCount()*currentUser.getTomatoTime();
                    }
                    tvPlanTime.setText(minute+"");


                    createTaskWindow.dismiss();
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setDateFormat("yyyy-MM-dd hh:mm:ss")
                            .create();
                    String taskJsonStr = gson.toJson(task);
                    Log.e("CreateTaskActivity",taskJsonStr);
                    MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
                    RequestBody requestBody = RequestBody.create(mediaType,taskJsonStr);
                    Request request = new Request.Builder()
                            .url(serverPath+"task/saveTask")
                            .post(requestBody)
                            .build();
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("CreateTaskActivity","创建任务：网络响应时间过长");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //do nothing
                        }
                    });
                }
            }
        });
        initPicker(Integer.parseInt(tomatoCount.getText().toString()));
        NumberPickerUtils.setNumberPickerTextColor(numberPicker, R.color.inkGray);
        NumberPickerUtils.setNumberPickerDividerColor(numberPicker,R.color.lightBlack);
        //点击 选择番茄数
        rlSettingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberPicker.getVisibility() == View.VISIBLE){
                    //日期选择器当前为显示的状态
                   tomatoCount.setText(numberPicker.getValue()+"");
                   numberPicker.setVisibility(View.GONE);

                }else
                    //日期选择器当前为隐藏的状态
                    numberPicker.setVisibility(View.VISIBLE);
            }
        });
        //点击 选择到期日期
        Calendar calendar = Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int monthOfYear=calendar.get(Calendar.MONTH);
        int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year,monthOfYear,dayOfMonth,this);
        datePicker.setFocusableInTouchMode(true);
        rlSettingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePicker.getVisibility() == View.VISIBLE){
                    //日期选择器显示
                    int month = datePicker.getMonth()+1;
                    expireDate.setText(datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth());
                    datePicker.setVisibility(View.GONE);
                }else
                    datePicker.setVisibility(View.VISIBLE);
            }
        });

        if(flag == 3){
            //表示从清单处进入创建任务页面
            tvListTitle.setText(taskLists.get(listPosition).getTitle());
            taskListAdapter = new CTTaskListAdapter(this,taskLists,R.layout.create_task_tasklist_item_layout,listPosition);
        }else{
            //表示从日期处进入创建任务页面
            tvListTitle.setText("");
            taskListAdapter = new CTTaskListAdapter(this,taskLists,R.layout.create_task_tasklist_item_layout,0);
        }

        lvTaskList.setAdapter(taskListAdapter);
        lvTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskList taskList = taskLists.get(position);
                tvListTitle.setText(taskList.getTitle());
                lvTaskList.setVisibility(View.GONE);
                index = position;
                taskListAdapter.changeSelected(position);
                taskListAdapter.notifyDataSetChanged();
            }
        });
        //点击 选择任务清单
        rlSettingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lvTaskList.getVisibility() == View.VISIBLE)
                    lvTaskList.setVisibility(View.GONE);
                else
                    lvTaskList.setVisibility(View.VISIBLE);
            }
        });

    }

    //初始化NumberPicker DatePicker
    public void initPicker(int defaultValue){
        //初始化NumberPicker
        numberPicker.setFormatter(this);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setOnScrollListener(this);
        numberPicker.setMaxValue(120);
        numberPicker.setMinValue(0);
        numberPicker.setValue(defaultValue);
    }


    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }


    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        //监听NumberPicker滑动事件 滑动的过程中 tomatoCount也随着改变
        tomatoCount.setText(newVal+"");
    }

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //日期选择器 当日起发生改变的事件监听器
        int month = monthOfYear+1;
        expireDate.setText(year+"-"+month+"-"+dayOfMonth);
    }
}
