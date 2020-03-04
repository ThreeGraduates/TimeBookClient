package cn.edu.hebtu.software.timebookclient.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 *  创建一个服务，该服务主要用来接收广播和创建定时器
 */
public class PhoneTimeService extends Service{
    private static final int NOTIFY_ID=1234;
    private Calendar cal=null;
    //广播接收器
    private final BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e("接收到的Intent的Action为",intent.getAction()+"");
            SharedPreferences sp=getSharedPreferences("actm",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){  //解锁
                editor.putLong("startTime",new Date().getTime());
                int screenUnlock=sp.getInt("unlockCount",0);
                editor.putInt("unlockCount",screenUnlock+1);
                editor.commit();
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){  //锁屏
                long startTime=sp.getLong("startTime",new Date().getTime());
                long sum=sp.getLong("sum",0L);
                sum+=new Date().getTime()-startTime;
                editor.putLong("sum",sum);
                editor.commit();
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {  //屏保
                int screenSaveCount = sp.getInt("lockCount", 0);
                editor.putInt("lockCount", screenSaveCount + 1);
                editor.commit();
            }
//            }else if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){  //每分钟一次
//                //todo 单独提出这个定时器
//                cal=Calendar.getInstance();
//                if(cal.get(Calendar.HOUR_OF_DAY)==0&& cal.get(Calendar.MINUTE)==0){
//                    //每天凌晨自动更新数据
//                    editor.putLong("sum",0L);
//                    editor.putInt("unlockCount",0);
//                    editor.putInt("lockCount",0);
//                    editor.commit();
//                }
//            }
        }
    };

    @Override
    public void onCreate() {
        // 设置BroadcastReceiver能够接收的广播类型
        final IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        //注册广播（动态注册方式）
        registerReceiver(receiver,filter);
//        Log.e("PhoneTimeService","onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e("PhoneTimeService","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        Log.e("PhoneTimeService","onDestroy");
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        Log.e("PhoneTimeService","onBind");
        return null;
    }
}
