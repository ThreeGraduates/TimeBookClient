package cn.edu.hebtu.software.timebookclient.fragment;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Activity.AppTimeLineChartActivity;
import cn.edu.hebtu.software.timebookclient.Adapter.AppUseTimeAdapter;
import cn.edu.hebtu.software.timebookclient.R;

public class AppFragment extends Fragment{
    private ListView lvApps;
    private AppUseTimeAdapter appUseTimeAdapter;
    private List<AppInfo> appInfoList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.app_fragment_layout,container,false);
        lvApps = view.findViewById(R.id.lv_apps);
        //todo 注意时间是否修改了
        initData();
        appUseTimeAdapter = new AppUseTimeAdapter(appInfoList, getContext());
        lvApps.setAdapter(appUseTimeAdapter);
        //点击进入app使用时长折线图
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(),AppTimeLineChartActivity.class);
                intent.putExtra("appName",appInfoList.get(position).getAppName());
                startActivity(intent);
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initData() {
        //确定时间范围(统计最近一个星期内手机app的使用情况)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UsageStatsManager usm = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Long endTime = calendar.getTimeInMillis();
//        Log.e("一星期前:", sdf.format(endTime));
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        long startTime = calendar.getTimeInMillis();
//        Log.e("现在:", sdf.format(startTime));

        /*
         * 最近一个星期内启动过所用app的List
         * queryUsageStats第一个参数时间的统计单位，即小时，天，月，年
         */
        List<UsageStats> list = usm.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime);
        if (list.size() == 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                try {
                    new AlertDialog.Builder(getContext())
                            .setTitle("温馨提示")
                            .setMessage("使用此功能，我们需要获取您的“应用使用情况”权限")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //进入打开 “获取应用使用情况权限” 页面
                                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                                }
                            })
                            .setNegativeButton("取消",null)
                            .create()
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "无法开启允许查看使用情况的应用界面", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            for (UsageStats us : list) {
                PackageManager pm = getActivity().getApplicationContext().getPackageManager();
                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(us.getPackageName(), PackageManager.GET_META_DATA);
                    if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) <= 0) {  //去除系统应用
                        AppInfo appInfo = new AppInfo();
                        Drawable drawable = applicationInfo.loadIcon(pm);
                        appInfo.setAppIcon(drawable);
                        appInfo.setAppName(pm.getApplicationLabel(applicationInfo) + "");
                        appInfo.setAppTime(getGapTime(us.getTotalTimeInForeground()));  //获取此程序包在前台花费的总时间（以毫秒为单位）
                        appInfoList.add(appInfo);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getGapTime(long time) {
        long hours = time / (1000*60*60);
        long minutes = (time - hours*(1000*60*60)) / (1000*60);
        long second=(time - hours*(1000*60*60) - minutes*(1000 * 60)) / 1000;
        StringBuffer sb=new StringBuffer();
        sb.append(hours+":");
        if(minutes<10){
            sb.append("0"+minutes+":");
        }else{
            sb.append(minutes+":");
        }
        if(second<10){
            sb.append("0"+second);
        }else{
            sb.append(second);
        }
        return sb.toString();
    }

    public static class AppInfo{
        private Drawable appIcon;
        private String appName;
        private String appTime;
        private String packageName;

        public Drawable getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(Drawable appIcon) {
            this.appIcon = appIcon;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppTime() {
            return appTime;
        }

        public void setAppTime(String appTime) {
            this.appTime = appTime;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public String toString() {
            return "AppInfo{" +
                    "appIcon=" + appIcon +
                    ", appName='" + appName + '\'' +
                    ", appTime='" + appTime + '\'' +
                    '}';
        }
    }
}
