package cn.edu.hebtu.software.timebookclient.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.edu.hebtu.software.timebookclient.R;
import cn.edu.hebtu.software.timebookclient.fragment.AppFragment;

public class AppUseTimeAdapter extends BaseAdapter{
    private List<AppFragment.AppInfo> appInfoList;
    private Context context;

    public AppUseTimeAdapter(List<AppFragment.AppInfo> appInfoList, Context context) {
        this.appInfoList = appInfoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh=null;
        //1.加载布局
        if(null==convertView){
            convertView= LayoutInflater.from(context).inflate(R.layout.app_item_layout,null);//有嵌套则用parent
            vh=new ViewHolder();
            vh.ivAppIcon=convertView.findViewById(R.id.iv_appIcon);
            vh.tvAppName=convertView.findViewById(R.id.tv_appName);
            vh.tvAppTime=convertView.findViewById(R.id.tv_appTime);
            convertView.setTag(vh);
        }else{
            vh= (ViewHolder) convertView.getTag();
        }
        //2.填充数据
        AppFragment.AppInfo app=appInfoList.get(position);
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(20);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
//      RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(context).load(app.getAppIcon()).apply(options).into(vh.ivAppIcon);
        vh.tvAppName.setText(app.getAppName());
        vh.tvAppTime.setText(app.getAppTime());
        return convertView;
    }

    class ViewHolder{
        private ImageView ivAppIcon;
        private TextView tvAppName;
        private TextView tvAppTime;
    }
}
