package cn.edu.hebtu.software.timebookclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import cn.edu.hebtu.software.timebookclient.ColorItem;
import cn.edu.hebtu.software.timebookclient.R;

public class ColorListAdapter extends BaseAdapter {

    private Context context;
    private List<ColorItem> colorItemList;
    private int itemLayout;

    public ColorListAdapter(Context context, List<ColorItem> colorItemList, int itemLayout) {
        this.context = context;
        this.colorItemList = colorItemList;
        this.itemLayout = itemLayout;
    }

    @Override
    public int getCount() {
        return colorItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return colorItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(itemLayout,null);
            viewHolder = new ViewHolder();
            viewHolder.ivColor = convertView.findViewById(R.id.iv_color);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (colorItemList.get(position).isSelected())
            viewHolder.ivColor.setImageResource(colorItemList.get(position).getIvSelected());
        else
            viewHolder.ivColor.setImageResource(colorItemList.get(position).getIvColor());
        return convertView;
    }

    private class ViewHolder{
        public ImageView ivColor;
    }
}
