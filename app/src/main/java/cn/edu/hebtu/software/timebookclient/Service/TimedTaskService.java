package cn.edu.hebtu.software.timebookclient.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.hebtu.software.timebookclient.Bean.AppTime;
import cn.edu.hebtu.software.timebookclient.Bean.PhoneTime;
import cn.edu.hebtu.software.timebookclient.R;
import cn.edu.hebtu.software.timebookclient.Util.AppTimeUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 定时器服务，定时查询用户每天手机应用使用时间
 */
public class TimedTaskService extends Service{
    private Gson gson;
    private OkHttpClient okHttpClient;
    private String path;
    private Long userId;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //修改UI
                    break;
                case 2:
                    //修改UI
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        okHttpClient=new OkHttpClient();
        GsonBuilder builder=new GsonBuilder();
        gson=builder.serializeNulls().create();
        path=getResources().getString(R.string.sever_path);
        //todo 设置userId
        userId=1l;


        new Thread(){
            @Override
            public void run() {
                super.run();
                initTask();
            }
        }.start();
    }
    //测试定时任务是否执行
    public void test(){
        TimerTask task = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                Log.e("执行了定时任务",new SimpleDateFormat("hh:mm:ss").format(new Date()));
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task,strToDateLong("2020-03-03 20:15:00"));
    }

    public void initTask(){
        TimerTask task = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                sendPhoneRequest();

                try {
                    sendAppRequest();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        };
        Timer timer = new Timer(true);
        //todo 设定定时任务每晚12点执行
        timer.schedule(task,strToDateLong(new SimpleDateFormat("yyyy-MM-dd").format(new Date())+" 20:42:00"));
    }

    //发送保存App时间请求
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendAppRequest() throws ParseException {
        Log.e("发送sendAppRequest",new SimpleDateFormat("hh:mm:ss").format(new Date()));
        List<AppTime> appTimes= AppTimeUtils.getTodayAppTime(getApplicationContext(),userId);
        String jsonStr=gson.toJson(appTimes);
        MediaType type=MediaType.parse("application/json;charset=utf-8");
        RequestBody body=RequestBody.create(type,jsonStr);
        Request request=new Request.Builder()
                .url(path+"appTime/saveAppTimes")
                .post(body)
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("sendAppRequest","请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.sendEmptyMessage(1);
                Log.i("sendAppRequest",response.body().string());
            }
        });
    }

    //发送保存手机使用时间请求
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendPhoneRequest(){
        Log.e("执行sendPhoneRequest",new SimpleDateFormat("hh:mm:ss").format(new Date()));
        PhoneTime phoneTime=new PhoneTime();
        phoneTime.setUserId(userId);
        phoneTime.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        int number = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
        int[] arr={7,1,2,3,4,5,6};
        phoneTime.setCreateWeek(arr[number]);
        SharedPreferences sp=getSharedPreferences("actm", Context.MODE_PRIVATE);
        phoneTime.setTime(sp.getLong("sum",0L));

        String jsonStr=gson.toJson(phoneTime);
        MediaType type=MediaType.parse("application/json;charset=utf-8");
        RequestBody body=RequestBody.create(type,jsonStr);
        Request request=new Request.Builder()
                .url(path+"phoneTime/saveOne")
                .post(body)
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("sendPhoneRequest","请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.sendEmptyMessage(2);
                Log.i("sendPhoneRequest",response.body().string());
            }
        });
    }



    /**
     * string类型时间转换为date
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strToDate = formatter.parse(strDate, pos);
        return strToDate;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e("TimeService","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        Log.e("TimeService","onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        Log.e("TimeService","onBind");
        return null;
    }
}
