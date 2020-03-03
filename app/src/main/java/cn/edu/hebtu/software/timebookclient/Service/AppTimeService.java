package cn.edu.hebtu.software.timebookclient.Service;

import android.app.Service;
import android.content.Intent;
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
public class AppTimeService extends Service{
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
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TimeService","onCreate");
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
                //执行定时任务
                initTimeTask();
            }
        }.start();
    }

    /**
     * 初始化定时任务
     */
    public void initTimeTask(){
        TimerTask task = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                try {
                    List<AppTime> appTimes= AppTimeUtils.getTodayAppTime(getApplicationContext(),userId);
                    Log.e("TimeActivity-appTimes",appTimes.toString());
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
                            Log.i("result","请求失败");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i("result",response.body().string());
                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }
        };
        Timer timer = new Timer(true);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long time = Calendar.getInstance().getTimeInMillis();
        timer.schedule(task,strToDateLong("2020-03-02 19:16:00"));
//        timer.schedule(task,strToDateLong(sdf.format(time)+" 23:55:00"));  //设定定时任务每晚12点执行
    }

    /**
     * string类型时间转换为date
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TimeService","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("TimeService","onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TimeService","onBind");
        return null;
    }
}
