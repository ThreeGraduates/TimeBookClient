package cn.edu.hebtu.software.timebookclient.Bean;

import java.util.Date;
import java.util.List;

public class TaskList {

    private Long id;
    private String title;
    private Date createTime;
    private int colorId;
    private int sumTime;
    private int taskCount;
    private List<TaskList> taskLists;//任务清单中包含的各种任务
    private User user;//创建任务清单的用户

    public TaskList() {
    }

    public TaskList(Long id, String title, Date createTime, int color, int sumTime, int taskCount, List<TaskList> taskLists, User user) {
        this.id = id;
        this.title = title;
        this.createTime = createTime;
        this.colorId = color;
        this.sumTime = sumTime;
        this.taskCount = taskCount;
        this.taskLists = taskLists;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<TaskList> getTaskLists() {
        return taskLists;
    }

    public void setTaskLists(List<TaskList> taskLists) {
        this.taskLists = taskLists;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getColor() {
        return colorId;
    }

    public void setColor(int color) {
        this.colorId = color;
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
        return "TaskList{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", createTime=" + createTime +
                ", colorId=" + colorId +
                ", sumTime=" + sumTime +
                ", taskCount=" + taskCount +
                ", taskLists=" + taskLists +
                ", user=" + user +
                '}';
    }
}
