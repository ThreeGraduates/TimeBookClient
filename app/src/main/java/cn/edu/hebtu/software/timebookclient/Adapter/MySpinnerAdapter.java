package cn.edu.hebtu.software.timebookclient.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.R;


public class MySpinnerAdapter extends BaseAdapter{
    private Context context;
    private String[] listItem ;

    public MySpinnerAdapter(Context context, String[] listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.length;
    }

    @Override
    public Object getItem(int position) {
        return listItem[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh=null;
        //1.加载布局
        if(null==convertView){
            convertView= LayoutInflater.from(context).inflate(R.layout.spinner_item_layout,null);//有嵌套则用parent
            vh=new ViewHolder();
            vh.tvNumber=convertView.findViewById(R.id.tv_number);
            convertView.setTag(vh);
        }else{
            vh= (ViewHolder) convertView.getTag();
        }
        vh.tvNumber.setText(listItem[position]);
        return convertView;
    }

    class ViewHolder{
        private TextView tvNumber;
    }
}
