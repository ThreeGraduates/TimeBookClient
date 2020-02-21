package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Adapter.DateListAdapter;
import cn.edu.hebtu.software.timebookclient.Adapter.TaskListAdapter;
import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.Bean.User;
import cn.edu.hebtu.software.timebookclient.DateTitle;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String serverPath;//服务器地址
    private User currentUser = new User();
    private List<DateTitle> dateTitleList = new ArrayList<DateTitle>();
    private List<TaskList> taskListList = new ArrayList<TaskList>();
    private ImageView ivAvatar;//填充用户头像的控件
    private TextView tvUsername;//填充用户名的控件
    private TextView tvUserSignature;//填充用户个性签名的控件
    private ListView lvDateTitles;
    private ListView lvTaskList;
    private RelativeLayout rlCreate;//创建清单框
    private DateListAdapter dateListAdapter;
    private TaskListAdapter taskListAdapter;
    private UserHandler userHandler = new UserHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取布局控件
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvUserSignature = findViewById(R.id.tv_signature);
        lvDateTitles = findViewById(R.id.lv_datelist);
        lvTaskList = findViewById(R.id.lv_tasklist);
        rlCreate = findViewById(R.id.rl_create);

        serverPath = getResources().getString(R.string.sever_path);

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        Long id = sharedPreferences.getLong("userId",0);
        Integer userId = id.intValue();

        //开启异步请求
        Request request = new Request.Builder()
                .url(serverPath+"user/getUserDetail?userId="+userId)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call userCall = okHttpClient.newCall(request);
        userCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity","网络响应失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String result = bufferedReader.readLine();
                Log.e("MainActivity",result);
                //将返回的结果字符串解析为JSONArray
                JsonParser parser = new JsonParser();
                JsonArray userArray = parser.parse(result).getAsJsonArray();
                JsonObject userObject = (JsonObject) userArray.get(0);

                //将JSONArray中的第一个JSONObject转化为User对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                currentUser = gson.fromJson(userObject.toString(),User.class);
                //将JSONArray中的dateTitle(1~3的JSONObject)转化为DateTitle对象
                for(int i=1;i<4;i++)
                    dateTitleList = gson.fromJson(userArray.get(i).toString(),new TypeToken<ArrayList<DateTitle>>(){}.getType());
                dateTitleList.get(1).setIconId(R.drawable.today);
                dateTitleList.get(2).setIconId(R.drawable.tomarrow);
                dateTitleList.get(3).setIconId(R.drawable.plan);
                //将JSONArray中的taskList转换为TaskList对象
                for(int i=4;i<userArray.size();i++)
                    taskListList = gson.fromJson(userArray.get(i).toString(),new TypeToken<ArrayList<TaskList>>(){}.getType());
                Message message = new Message();
                userHandler.sendMessage(message);

            }
        });

        //为创建清单 绑定点击事件监听器
        rlCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private class UserHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //填充用户的信息
            RequestOptions options = new RequestOptions().circleCrop();
            Glide.with(MainActivity.this)
                    .load(serverPath+currentUser.getImage())
                    .apply(options)
                    .into(ivAvatar);
            tvUsername.setText(currentUser.getUsername());
            tvUserSignature.setText(currentUser.getSignature());

            //初始化 DateListAdapter 并绑定适配器
            dateListAdapter = new DateListAdapter(dateTitleList,MainActivity.this,R.layout.datelist_item_layout);
            lvDateTitles.setAdapter(dateListAdapter);
            lvDateTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //为各个子项绑定点击事件监听
                    Log.e("MainActivity","lvDateTitles:点击各个子项");
                }
            });

            //初始化TaskListAdpater 并绑定适配器
            taskListAdapter = new TaskListAdapter(taskListList,MainActivity.this,R.layout.tasklist_item_layout);
            lvTaskList.setAdapter(taskListAdapter);
            lvTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //为各个子项绑定点击事件监听
                    Log.e("MainActivity","lvTaskList:点击各个子项");
                }
            });
        }
    }
}
