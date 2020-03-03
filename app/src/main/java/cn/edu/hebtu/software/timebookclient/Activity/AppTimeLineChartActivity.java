package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.AppTime;
import cn.edu.hebtu.software.timebookclient.R;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppTimeLineChartActivity extends AppCompatActivity {
    private Gson gson;
    private OkHttpClient okHttpClient;
    private String path;
    private Long userId;
    private String appName;
    private List<AppTime> appTimes;

    private LineChartView lineChart;
    private TextView lineChartTitle;

    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apptime_linechart);

        appName=getIntent().getStringExtra("appName");


        okHttpClient=new OkHttpClient();
        GsonBuilder builder=new GsonBuilder();
        gson=builder.serializeNulls().create();
        path=getResources().getString(R.string.sever_path);
        //todo 设置userId
        userId=1l;

        lineChart=findViewById(R.id.lineChart);
        lineChartTitle=findViewById(R.id.lineChartTitle);
        lineChartTitle.setText(appName);
        //返回
        ImageView imageView=findViewById(R.id.line_return);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AppTimeLineChartActivity.this,AppActivity.class);
                startActivity(intent);
            }
        });

        GetAppTimesByUserIdTask getAppTimesByUserIdTask=new GetAppTimesByUserIdTask();
        getAppTimesByUserIdTask.execute();
    }



    //根据用户Id获取用户详情
    class GetAppTimesByUserIdTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Request request=new Request.Builder()
                    .url(path+"appTime/getAppTimesByUserId?userId="+userId+"&appName="+appName)
                    .build();
            Call call=okHttpClient.newCall(request);
            try {
                Response response=call.execute();
                String resJson=response.body().string();
                Log.e("resJson",resJson);
                appTimes = gson.fromJson(resJson, new TypeToken<List<AppTime>>(){}.getType());
                Log.e("appTimes",appTimes.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            try {
                initAxisXLables();//获取x轴的标注
            } catch (ParseException e) {
                e.printStackTrace();
            }
            initAxisPoints(); //获取坐标点
            initLineChart();  //初始化
        }
    }

    /**
     * 设置X轴的显示
     */
    private void initAxisXLables() throws ParseException {
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf=new SimpleDateFormat("MM-dd");
        //todo x轴最少有2个
        for(int i=0;i<appTimes.size();i++){
            mAxisXValues.add(new AxisValue(i).setLabel(sdf.format(sdf1.parse(appTimes.get(i).getCreateDate()))));
        }
    }

    /**
     * 图表的每个点的显示 (纵轴以 分钟 为单位)
     */
    private void initAxisPoints() {
        for (int i = 0; i < appTimes.size(); i++) {
            long time=appTimes.get(i).getTime();  //毫秒
            mPointValues.add(new PointValue(i, (float) time/(float)60000));
        }
    }
    private void initLineChart(){
        // 折线对象 传参数为点的集合对象   y轴值的范围自动生成 根据坐标的y值 不必自己准备数据
        Line line = new Line(mPointValues).setColor(getResources().getColor(R.color.color16));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setFormatter(new SimpleLineChartValueFormatter(2));
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
//        axisX.setName("date");  //表格名称
        axisX.setTextSize(12);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部  （顶部底部一旦设置就意味着x轴）
        axisX.setHasLines(false); //x 轴分割线  每个x轴上 面有个虚线 与x轴垂直

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(12);//设置字体大小
        axisY.setTextColor(Color.BLACK);
        data.setAxisYLeft(axisY);  //Y轴设置在左边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setZoomEnabled(true);
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        //设置触摸事件
        lineChart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(AppTimeLineChartActivity.this, ""+value.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });//为图表设置值得触摸事件
        lineChart.setVisibility(View.VISIBLE);
    }
}
