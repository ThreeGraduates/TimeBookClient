package cn.edu.hebtu.software.timebookclient.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import javax.xml.transform.dom.DOMResult;

import cn.edu.hebtu.software.timebookclient.Activity.AppActivity;
import cn.edu.hebtu.software.timebookclient.Activity.PersonalCenterActivity;
import cn.edu.hebtu.software.timebookclient.Activity.TaskListManagerActivity;
import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.R;

import static java.security.AccessController.getContext;

public class AppUseTimeAdapter extends BaseAdapter{
    private List<AppActivity.AppInfo> appInfoList;
    private Context context;

    public AppUseTimeAdapter(List<AppActivity.AppInfo> appInfoList, Context context) {
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
        AppActivity.AppInfo app=appInfoList.get(position);
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
