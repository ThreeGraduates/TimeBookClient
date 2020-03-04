package cn.edu.hebtu.software.timebookclient.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Activity.ChartActivity;
import cn.edu.hebtu.software.timebookclient.Activity.PhoneTimeLineChartActivity;
import cn.edu.hebtu.software.timebookclient.Adapter.MySpinnerAdapter;
import cn.edu.hebtu.software.timebookclient.R;
import cn.edu.hebtu.software.timebookclient.Service.PhoneTimeService;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * 番茄任务情况统计
 */
public class DataFragment extends Fragment {
    private PieChartView pieChart;
    private PieChartData data;         //存放数据
    private int userId;
    private SharedPreferences sharedPreferences;
    private String serverPath;
    private OkHttpClient okHttpClient;
    private Gson gson;
    private Spinner spYear;
    private Spinner spMonth;
    private MySpinnerAdapter adapter;
    private String year;
    private String month;

    private TextView tvPhoneUseTime;
    private TextView tvScreenSaveCount;
    private TextView tvUnlockCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.data_fragment_layout,container,false);
        sharedPreferences = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        Long id = sharedPreferences.getLong("userId",0);
        userId = id.intValue();
//        userId=1;
        serverPath = getResources().getString(R.string.server_path);
        okHttpClient=new OkHttpClient();
        GsonBuilder builder=new GsonBuilder();
        gson=builder.serializeNulls().create();
        spYear=view.findViewById(R.id.sp_year);
        spMonth=view.findViewById(R.id.sp_month);
        tvPhoneUseTime=view.findViewById(R.id.tv_phoneUseTime);
        tvScreenSaveCount=view.findViewById(R.id.tv_screenSaveCount);
        tvUnlockCount=view.findViewById(R.id.tv_unlockCount);

        getPhoneUseTime();

        initSpinner();
        //默认统计本月
        new GetTaskStatusPieChartTask().execute();
        //点击查询统计
        TextView tvSelect=view.findViewById(R.id.tv_select);
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetTaskStatusPieChartTask().execute();
            }
        });

        LinearLayout linearLayout=view.findViewById(R.id.ll_phone);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), PhoneTimeLineChartActivity.class));
            }
        });

        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, SliceValue sliceValue) {
                Toast.makeText(getContext(), "选中值"+sliceValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });
        return view;
    }

    public void getPhoneUseTime(){
        SharedPreferences sp=getContext().getSharedPreferences("actm", MODE_PRIVATE);
        int sum=(int)sp.getLong("sum",0L)/1000;
        int hour=sum/3600;
        int minute=(sum-hour*3600)/60;
        int second=sum%60;
        tvPhoneUseTime.setText(hour+"时"+minute+"分"+second+"秒");

        tvScreenSaveCount.setText(sp.getInt("lockCount",0)+"次");

        tvUnlockCount.setText(sp.getInt("unlockCount",0)+"次");
    }

    public void initSpinner(){
        //设置年份下拉框
        Calendar now = Calendar.getInstance();
        int currentYear =  now.get(Calendar.YEAR); //当前年
        year=currentYear+"";
        String[] years=new String[5];
        for(int i=0;i<5;i++){
            years[i]=currentYear+"";
            currentYear--;
        }
        month=now.get(Calendar.MONTH)+1+"";
        if(month.length()==1){
            month="0"+month;
        }
        this.setSpinner("year",spYear,years);
        spYear.setSelection(0);
        //设置月份下拉框
        String[] months={"01","02","03","04","05","06","07","08","09","10","11","12"};
        this.setSpinner("month",spMonth,months);
        spMonth.setSelection(now.get(Calendar.MONTH));  //设置默认的选择项
    }

    private void setSpinner(final String flag, final Spinner spinner, String[] data){
        adapter=new MySpinnerAdapter(getContext(),data);
        spinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if("year".equals(flag)){
                    year = (String) spinner.getItemAtPosition(position);
                }else if("month".equals(flag)){
                    month = (String) spinner.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //设置默认值
        spinner.setVisibility(View.VISIBLE);
    }

    private void initDatas(float a,float b,float c){
        //初始化数据
        List<SliceValue> values = new ArrayList<SliceValue>();
        SliceValue completedValue=new SliceValue(a, Color.rgb(86,188,166));  //已完成
        completedValue.setLabel("已完成:"+a*100+"%");
        SliceValue unfinishedValue=new SliceValue(b,Color.rgb(234,97,90)); //未完成
        unfinishedValue.setLabel("未完成:"+b*100+"%");
        SliceValue abandonedValue=new SliceValue(c,Color.rgb(248,214,81));  //中途放弃
        NumberFormat ddf1= NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(2);
        c=Float.parseFloat(ddf1.format(1-a-b));
        abandonedValue.setLabel("中途放弃:"+c*100+"%");
        values.add(completedValue);
        values.add(unfinishedValue);
        values.add(abandonedValue);
        data = new PieChartData(values);
        data.setHasLabels(true);  //是否有标签
        data.setHasLabelsOutside(true);//标签是否在扇形外面
        data.setHasCenterCircle(false);  //是否有中心圆
        data.setHasLabelsOnlyForSelected(true);   //选中的扇形显示标签

        /*****************************饼中文字设置************************************/
        data.setHasLabels(true);//是否显示文本内容(默认为false)
        data.setHasLabelsOutside(true); //文本内容是否显示在饼图外侧(默认为false)
        data.setValueLabelTextSize(18);  //文本字体大小
        data.setValueLabelsTextColor(Color.WHITE); //文本文字颜色
        data.setValueLabelBackgroundColor(Color.WHITE);  //设置文本背景颜色

        /*****************************中心圆设置************************************/
        data.setHasCenterCircle(true);   //饼图是空心圆环还是实心饼状(默认false,饼状)
        data.setCenterCircleColor(Color.WHITE);//中心圆的颜色(需setHasCenterCircle(true),因为只有圆环才能看到中心圆)
        data.setCenterCircleScale(0.5f); //中心圆所占饼图比例(0-1)
       /******************************中心圆文本*****************************/
        int m=Integer.parseInt(month);
        int y=Integer.parseInt(year);
        data.setCenterText1(y+"年"+m+"月");
        data.setCenterText1Color(Color.GRAY);
        data.setCenterText1FontSize(18);

        data.setSlicesSpacing(5);//饼图各模块的间隔(默认为0)
        pieChart.setPieChartData(data);
        pieChart.setCircleFillRatio(0.7f);//整个饼图所占视图比例(0-1)
        pieChart.setChartRotationEnabled(true);//饼图是否可以转动(默认为true)
    }

    /**
     * 以月为单位统计，统计番茄任务完成情况
     */
    class GetTaskStatusPieChartTask extends AsyncTask {
        private float a;
        private float b;
        private float c;
        private String result;
        @Override
        protected Object doInBackground(Object[] objects) {
            Request request = new Request.Builder()
                    .url(serverPath + "task/getTaskStatusPieChart?userId=" + userId+"&year="+year+"&month="+month)
                    .build();
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                String jsonStr = response.body().string();
                result=jsonStr;
                if(!"empty".equals(jsonStr)){
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    a=Float.parseFloat((String) jsonObject.get("finish"));
                    b=Float.parseFloat((String) jsonObject.get("unFinish"));
                    c=1-a-b;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(!"empty".equals(result)){
                initDatas(a,b,c);
            }else{
                spYear.setSelection(0);
                Calendar now = Calendar.getInstance();
                spMonth.setSelection(now.get(Calendar.MONTH));
                Toast.makeText(getContext(),year+"年"+month+"月数据为空", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
