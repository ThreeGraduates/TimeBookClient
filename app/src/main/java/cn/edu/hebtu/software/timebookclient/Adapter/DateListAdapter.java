package cn.edu.hebtu.software.timebookclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.hebtu.software.timebookclient.R;
import cn.edu.hebtu.software.timebookclient.Util.DateTitle;

public class DateListAdapter extends BaseAdapter {

    private List<DateTitle> titleList;//填充的数据
    private Context context;
    private int itemLayout;//子项布局文件

    public DateListAdapter(List<DateTitle> titleList, Context context, int itemLayout) {
        this.titleList = titleList;
        this.context = context;
        this.itemLayout = itemLayout;
    }

    @Override
    public int getCount() {
        return titleList.size();
    }

    @Override
    public Object getItem(int position) {
        return titleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(itemLayout,null);
            viewHolder.iconImage = convertView.findViewById(R.id.iv_icon);
            viewHolder.titleText = convertView.findViewById(R.id.tv_text);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time);
            viewHolder.tvCount = convertView.findViewById(R.id.tv_count);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DateTitle title = titleList.get(position);
        viewHolder.iconImage.setImageResource(title.getIconId());
        viewHolder.titleText.setText(title.getTitle());
        viewHolder.tvTime.setText(title.getSumTime()+"");
        viewHolder.tvCount.setText(title.getTaskCount()+"");

        return convertView;
    }

    public class ViewHolder{

        public ImageView iconImage;
        public TextView titleText;
        public TextView tvTime;
        public TextView tvCount;

    }
}
