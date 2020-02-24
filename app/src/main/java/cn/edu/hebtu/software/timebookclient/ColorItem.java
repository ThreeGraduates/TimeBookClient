package cn.edu.hebtu.software.timebookclient;

import android.widget.ImageView;

public class ColorItem {

    private int colorId;
    private int ivColor;
    private int ivSelected;
    private boolean isSelected = false;//标志当前颜色时候被选中 默认为未被选中

    public ColorItem(int colorId, int ivColor, int ivSelected) {
        this.colorId = colorId;
        this.ivColor = ivColor;
        this.ivSelected = ivSelected;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getIvColor() {
        return ivColor;
    }

    public void setIvColor(int ivColor) {
        this.ivColor = ivColor;
    }

    public int getIvSelected() {
        return ivSelected;
    }

    public void setIvSelected(int ivSelected) {
        this.ivSelected = ivSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
