package cn.edu.hebtu.software.timebookclient.Util;

public class DateTitle {

    private int iconId;
    private String title;
    private  int sumTime;//当前日期或一段时间内 所有任务的总时间
    private int taskCount;//当前日期或一段时间内 任务的数量

    public DateTitle() {
    }

    public DateTitle(int iconId, String title, int sumTime, int taskCount) {
        this.iconId = iconId;
        this.title = title;
        this.sumTime = sumTime;
        this.taskCount = taskCount;
    }

    public DateTitle(String title, int sumTime, int taskCount) {
        this.title = title;
        this.sumTime = sumTime;
        this.taskCount = taskCount;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSumTime() {
        return sumTime;
    }

    public void setSumTime(int sumTime) {
        this.sumTime = sumTime;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    @Override
    public String toString() {
        return "DateTitle{" +
                "iconId=" + iconId +
                ", title='" + title + '\'' +
                ", sumTime=" + sumTime +
                ", taskCount=" + taskCount +
                '}';
    }
}
