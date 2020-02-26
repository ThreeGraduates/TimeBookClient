package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

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

public class SetupActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener,NumberPicker.OnScrollListener,NumberPicker.Formatter {
    private User user;
    private OkHttpClient okHttpClient;
    private Gson gson;
    private NumberPicker numberPicker;
    private NumberPickerListener listener;
    private int userId;
    private String path;
    private TextView tvUsername;
    private TextView tvNum1;
    private TextView tvNum2;
    private TextView tvNum3;
    private TextView tvNum4;
    private ImageView arrow1;
    private ImageView arrow2;
    private ImageView arrow3;
    private ImageView arrow4;
    private ImageView avatar;
    private LinearLayout llUser;
    private LinearLayout llListManager;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

//        userId = getIntent().getIntExtra("currentUserId",0);
//        Log.e("setup-userId",userId+"");

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        Long id = sharedPreferences.getLong("userId",0);
        userId = id.intValue();
        Log.e("setup-userId",userId+"");

        path=getResources().getString(R.string.sever_path);

        llUser=findViewById(R.id.ll_user);
        llListManager=findViewById(R.id.list_manager);

        okHttpClient=new OkHttpClient();
        GsonBuilder builder=new GsonBuilder();
        gson=builder.serializeNulls().create();
        avatar=findViewById(R.id.userImage);
        tvUsername=findViewById(R.id.username);
        tvNum1=findViewById(R.id.tv_num1);
        tvNum2=findViewById(R.id.tv_num2);
        tvNum3=findViewById(R.id.tv_num3);
        tvNum4=findViewById(R.id.tv_num4);
        GetUserTask getUserTask=new GetUserTask();
        getUserTask.execute();

        listener=new NumberPickerListener();
        arrow1=findViewById(R.id.left_arrow1);
        arrow2=findViewById(R.id.left_arrow2);
        arrow3=findViewById(R.id.left_arrow3);
        arrow4=findViewById(R.id.left_arrow4);
        arrow1.setOnClickListener(listener);
        arrow2.setOnClickListener(listener);
        arrow3.setOnClickListener(listener);
        arrow4.setOnClickListener(listener);

        llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到个人中心页面
                Intent intent=new Intent(SetupActivity.this,PersonalCenterActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        //清单管理
        llListManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SetupActivity.this,TaskListManagerActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        //返回主页面
        ImageView ivReturn=findViewById(R.id.setup_return);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //根据用户Id获取用户详情
    class GetUserTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            Request request=new Request.Builder()
                    .url(path+"user/getUserDetailSetup?userId="+userId)
                    .build();
            Call call=okHttpClient.newCall(request);
            try {
                Response response=call.execute();
                String userJson=response.body().string();
                user =gson.fromJson(userJson,User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            tvNum1.setText(user.getTomatoTime()+"");
            tvNum2.setText(user.getShortBreak()+"");
            tvNum3.setText(user.getLongBreak()+"");
            tvNum4.setText(user.getLongRestInterval()+"");
            tvUsername.setText(user.getUsername());
            Glide.with(SetupActivity.this)
                    .load(R.mipmap.loading)
                    .dontAnimate()
                    .load(path+user.getImage()+"?key=" + Math.random())
                    .into(avatar);
            Log.e("user",user.toString());
        }
    }

    //向服务器端发送数据修改番茄时间设置
    class AlterTomatoTimeSetupTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            String jsonStr=gson.toJson(user);
            MediaType type=MediaType.parse("application/json;charset=utf-8");
            RequestBody body=RequestBody.create(type,jsonStr);
            Request request=new Request.Builder()
                    .url(path+"user/alterTomatoTimeSetup")
                    .post(body)
                    .build();
            Call call=okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("result","请求失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("result",response.body().string());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

        }
    }

    //监听器--点击显示数字选择器
    class NumberPickerListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.left_arrow1:
                    numberPicker=findViewById(R.id.numPicker1);
                    if(numberPicker.getVisibility()==View.VISIBLE){
                        user.setTomatoTime(numberPicker.getValue());
                        tvNum1.setText(numberPicker.getValue()+"");
                        arrow1.setImageResource(R.drawable.left_arrow);
                    }else{
                        arrow1.setImageResource(R.drawable.down_arrow);
                    }
                    break;
                case R.id.left_arrow2:
                    numberPicker=findViewById(R.id.numPicker2);
                    if(numberPicker.getVisibility()==View.VISIBLE){
                        user.setShortBreak(numberPicker.getValue());
                        tvNum2.setText(numberPicker.getValue()+"");
                        arrow2.setImageResource(R.drawable.left_arrow);
                    }else{
                        arrow2.setImageResource(R.drawable.down_arrow);
                    }
                    break;
                case R.id.left_arrow3:
                    numberPicker=findViewById(R.id.numPicker3);
                    if(numberPicker.getVisibility()==View.VISIBLE){
                        user.setLongBreak(numberPicker.getValue());
                        tvNum3.setText(numberPicker.getValue()+"");
                        arrow3.setImageResource(R.drawable.left_arrow);
                    }else{
                        arrow3.setImageResource(R.drawable.down_arrow);
                    }
                    break;
                case R.id.left_arrow4:
                    numberPicker=findViewById(R.id.numPicker4);
                    if(numberPicker.getVisibility()==View.VISIBLE){
                        user.setLongRestInterval(numberPicker.getValue());
                        tvNum4.setText(numberPicker.getValue()+"");
                        arrow4.setImageResource(R.drawable.left_arrow);
                    }else{
                        arrow4.setImageResource(R.drawable.down_arrow);
                    }
                    break;
            }
            if(numberPicker.getVisibility()==View.VISIBLE){
                numberPicker.setVisibility(View.GONE);
                //执行Task
                AlterTomatoTimeSetupTask alterTomatoTimeSetupTask=new AlterTomatoTimeSetupTask();
                alterTomatoTimeSetupTask.execute();
            }else{
                NumberPickerUtils.setNumberPickerTextColor(numberPicker, R.color.lightBlack);
                NumberPickerUtils.setNumberPickerDividerColor(numberPicker,R.color.lightBlack);
                init();
                numberPicker.setVisibility(View.VISIBLE);
            }

        }
    }

    //初始化数字选择器
    private void init() {
        numberPicker.setFormatter(this);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setOnScrollListener(this);
        numberPicker.setMaxValue(120);
        numberPicker.setMinValue(0);
        numberPicker.setValue(25);
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
    public void onScrollStateChange(NumberPicker view, int scrollState) {
//        switch (scrollState) {
//            case NumberPicker.OnScrollListener.SCROLL_STATE_FLING:      //手离开之后还在滑动
//                Toast.makeText(this,"后续滑动", Toast.LENGTH_LONG).show();
//                break;
//            case NumberPicker.OnScrollListener.SCROLL_STATE_IDLE:         //不滑动
//                Toast.makeText(this, "不滑动", Toast.LENGTH_LONG).show();
//                break;
//            case NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:   //滑动中
//                Toast.makeText(this, "滑动中", Toast.LENGTH_LONG)
//                        .show();
//                break;
//        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//        Toast.makeText(this,"原来的值：" + oldVal + "--新值: " + newVal, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,"修改成功", Toast.LENGTH_SHORT).show();
    }
}

