package cn.edu.hebtu.software.timebookclient.Bean;

import java.sql.Timestamp;
import java.util.Date;

public class Task {

    private Long id;
    private String title;
    private Integer count;     //番茄数
    private Integer flag;      //0:未开始  1:已完成  2:中途放弃
    private Integer priority;  //任务优先级
    private Date createDate;   //任务创建日期
    private Timestamp startDateTime;  //任务开始时间
    private Timestamp expireDateTime; //任务到期时间
    private String repeat;    //重复次数
    private String remark;    //备注
    private User user;//创建任务的用户

    public Task() {
    }

    public Task(Long id, String title, Integer count, Integer flag, Integer priority, Date createDate, Timestamp startDateTime, Timestamp expireDateTime, String repeat, String remark, User user) {
        this.id = id;
        this.title = title;
        this.count = count;
        this.flag = flag;
        this.priority = priority;
        this.createDate = createDate;
        this.startDateTime = startDateTime;
        this.expireDateTime = expireDateTime;
        this.repeat = repeat;
        this.remark = remark;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Timestamp getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Timestamp startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Timestamp getExpireDateTime() {
        return expireDateTime;
    }

    public void setExpireDateTime(Timestamp expireDateTime) {
        this.expireDateTime = expireDateTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", count=" + count +
                ", flag=" + flag +
                ", priority=" + priority +
                ", createDate=" + createDate +
                ", startDateTime=" + startDateTime +
                ", expireDateTime=" + expireDateTime +
                ", repeat='" + repeat + '\'' +
                ", remark='" + remark + '\'' +
                ", user=" + user +
                '}';
    }
}
