package cn.edu.hebtu.software.timebookclient.Util;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.AppTime;

public class AppTimeUtils {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<AppTime> getTodayAppTime(Context context,Long userId) throws ParseException {
        //确定时间范围
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar endCal = Calendar.getInstance();
        Calendar beginCal = Calendar.getInstance();  //3.1
        beginCal.add(Calendar.HOUR_OF_DAY,-1);
//        Log.e("执行定时任务",sdf.format(endCal.getTimeInMillis()));

        List<UsageStats> list = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        List<AppTime> appTimes=new ArrayList<>();
//        Log.e("size:",list.size()+"");
        if (list.size() == 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                try {
                    //todo 增加对话框页面，进入打开 “获取应用使用情况权限” 页面
                    Intent intent=new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
//                    Toast.makeText(context, "无法开启允许查看使用情况的应用界面", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            for (UsageStats us : list) {
                PackageManager pm = context.getApplicationContext().getPackageManager();
                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(us.getPackageName(), PackageManager.GET_META_DATA);
                    if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) <= 0) {  //去除系统应用
                        AppTime appTime=new AppTime();
                        appTime.setUserId(userId);
                        appTime.setAppName(pm.getApplicationLabel(applicationInfo) + "");
                        appTime.setTime(us.getTotalTimeInForeground());
                        appTime.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        int number = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
                        int[] arr={7,1,2,3,4,5,6};
                        appTime.setCreateWeek(arr[number]);
                        appTimes.add(appTime);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return appTimes;
    }
}
