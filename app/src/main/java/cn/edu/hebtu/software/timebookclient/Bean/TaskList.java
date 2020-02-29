package cn.edu.hebtu.software.timebookclient.Bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TaskList implements Serializable {

    private Long id;
    private String title;
    private Date createTime;
    private int colorId;
    private int sumTime;
    private int taskCount;
    private List<Task> taskLists;//任务清单中包含的各种任务
    private int userId;//创建任务清单的用户

    public TaskList() {
    }

    public TaskList(Long id, String title, Date createTime, int colorId, int sumTime, int taskCount, List<Task> taskLists, int userId) {
        this.id = id;
        this.title = title;
        this.createTime = createTime;
        this.colorId = colorId;
        this.sumTime = sumTime;
        this.taskCount = taskCount;
        this.taskLists = taskLists;
        this.userId = userId;
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

    public List<Task> getTaskLists() {
        return taskLists;
    }

    public void setTaskLists(List<Task> taskLists) {
        this.taskLists = taskLists;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
                ", userId=" + userId +
                '}';
    }
}
