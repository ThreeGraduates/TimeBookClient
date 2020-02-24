package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Adapter.ColorListAdapter;
import cn.edu.hebtu.software.timebookclient.Bean.Task;
import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.ColorItem;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TasklistActivity extends AppCompatActivity {

    private Button btnBack;
    private TextView tvFinish;
    private ImageView ivColor;
    private EditText etName;
    private GridView gvColorList;
    private List<ColorItem> colorItemList = new ArrayList<>();
    private ColorListAdapter colorListAdapter;
    private int currentUserId;
    private String serverPath;
    private TaskListHandler handler = new TaskListHandler();
    private int currentColorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        //调用函数，填充布局控件
        findView();

        //获取当前用户的id
        currentUserId = getIntent().getIntExtra("currentUserId",0);
        //获取服务器端连接
        serverPath = getResources().getString(R.string.sever_path);

        //返回按钮绑定点击事件监听器
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回到主页
                Intent intent = new Intent(TasklistActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //完成按钮绑定事件监听器
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //判断输入框中是否有文字
                String taskName = etName.getText().toString();
                if ("".equals(taskName) || taskName == null){
                    //弹出提示框
                    Toast warn = Toast.makeText(TasklistActivity.this,"清单名称不能为空",Toast.LENGTH_SHORT);
                    warn.setGravity(Gravity.TOP,0,400);
                    warn.show();
                }else{
                    TaskList newTaskList = new TaskList();
                    newTaskList.setTitle(taskName);
                    newTaskList.setColorId(currentColorId);
                    newTaskList.setCreateTime(new Date());
                    newTaskList.setSumTime(0);
                    newTaskList.setTaskCount(0);
                    newTaskList.setUserId(currentUserId);
                    //新建的清单对象转换为Gson字符串
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setDateFormat("yyyy-MM-dd")
                            .create();
                    String listJsonStr = gson.toJson(newTaskList);
                    Log.e("TasklistActivity",listJsonStr);
                    //开启异步任务将新建的任务清单传输给服务器端
                    MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
                    final RequestBody requestBody = RequestBody.create(mediaType,listJsonStr);
                    Request request = new Request.Builder()
                            .url(serverPath+"taskList/saveTaskList")
                            .post(requestBody)
                            .build();
                    OkHttpClient taskListClient = new OkHttpClient();
                    Call call = taskListClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //网络响应失败
                            Log.e("TaskListActivity","网络响应失败");
                            Message failMeassage = new Message();
                            failMeassage.what = -1;
                            handler.sendMessage(failMeassage);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            InputStream inputStream = response.body().byteStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            String result = bufferedReader.readLine();
                            Log.e("TaskListActivity",result);
                            Message successMessage = new Message();
                            successMessage.what = 0;
                            handler.sendMessage(successMessage);
                        }
                    });
                }
            }
        });

        //ColorList的数据源
        getData();
        //初识化Adapter
        colorListAdapter = new ColorListAdapter(this,colorItemList,R.layout.colorlist_item_layout);
        gvColorList.setAdapter(colorListAdapter);
        gvColorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0;i<colorItemList.size();i++){
                    if(i == id) {
                        colorItemList.get(i).setSelected(true);
                        currentColorId = colorItemList.get(i).getColorId();
                        ivColor.setImageResource(colorItemList.get(i).getColorId());
                    }
                    else
                        colorItemList.get(i).setSelected(false);
                }
                colorListAdapter.notifyDataSetChanged();
            }
        });


    }

    //获取布局控件
    public void findView(){
        btnBack = findViewById(R.id.btn_back);
        tvFinish = findViewById(R.id.tv_finish);
        ivColor = findViewById(R.id.iv_color);
        etName = findViewById(R.id.et_name);
        gvColorList = findViewById(R.id.gv_colorList);
    }

    //准备ColorList数据
    public void getData(){
        ColorItem colorItem1 = new ColorItem(R.color.color1,R.drawable.circle1,R.drawable.selected1);
        colorItem1.setSelected(true);//初识状态下默认选择第一个颜色
        ColorItem colorItem2 = new ColorItem(R.color.color2,R.drawable.circle2,R.drawable.selected2);
        ColorItem colorItem3 = new ColorItem(R.color.color3,R.drawable.circle3,R.drawable.selected3);
        ColorItem colorItem4 = new ColorItem(R.color.color4,R.drawable.circle4,R.drawable.selected4);
        ColorItem colorItem5 = new ColorItem(R.color.color5,R.drawable.circle5,R.drawable.selected5);
        ColorItem colorItem6 = new ColorItem(R.color.color6,R.drawable.circle6,R.drawable.selected6);
        ColorItem colorItem7 = new ColorItem(R.color.color7,R.drawable.circle7,R.drawable.selected7);
        ColorItem colorItem8 = new ColorItem(R.color.color8,R.drawable.circle8,R.drawable.selected8);
        ColorItem colorItem9 = new ColorItem(R.color.color9,R.drawable.circle9,R.drawable.selected9);
        ColorItem colorItem10 = new ColorItem(R.color.color10,R.drawable.circle10,R.drawable.selected10);
        ColorItem colorItem11 = new ColorItem(R.color.color11,R.drawable.circle11,R.drawable.selected11);
        ColorItem colorItem12 = new ColorItem(R.color.color12,R.drawable.circle12,R.drawable.selected12);
        ColorItem colorItem13 = new ColorItem(R.color.color13,R.drawable.circle13,R.drawable.selected13);
        ColorItem colorItem14 = new ColorItem(R.color.color14,R.drawable.circle14,R.drawable.selected14);
        ColorItem colorItem15 = new ColorItem(R.color.color15,R.drawable.circle15,R.drawable.selected15);
        ColorItem colorItem16 = new ColorItem(R.color.color16,R.drawable.circle16,R.drawable.selected16);
        ColorItem colorItem17 = new ColorItem(R.color.color17,R.drawable.circle17,R.drawable.selected17);
        ColorItem colorItem18 = new ColorItem(R.color.color18,R.drawable.circle18,R.drawable.selected18);

        colorItemList.add(colorItem1);
        colorItemList.add(colorItem2);
        colorItemList.add(colorItem3);
        colorItemList.add(colorItem4);
        colorItemList.add(colorItem5);
        colorItemList.add(colorItem6);
        colorItemList.add(colorItem7);
        colorItemList.add(colorItem8);
        colorItemList.add(colorItem9);
        colorItemList.add(colorItem10);
        colorItemList.add(colorItem11);
        colorItemList.add(colorItem12);
        colorItemList.add(colorItem13);
        colorItemList.add(colorItem14);
        colorItemList.add(colorItem15);
        colorItemList.add(colorItem16);
        colorItemList.add(colorItem17);
        colorItemList.add(colorItem18);
    }

    private class TaskListHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    //网络响应失败
                    Toast warnToast = Toast.makeText(TasklistActivity.this,"网络响应时间过长",Toast.LENGTH_SHORT);
                    warnToast.setGravity(Gravity.TOP,0,400);
                    warnToast.show();
                    break;
                case 0:
                    finish();
                    Intent intent = new Intent(TasklistActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
