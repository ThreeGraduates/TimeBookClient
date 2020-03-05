package cn.edu.hebtu.software.timebookclient.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Task implements Serializable {

    private Long id;
    private String title;
    private Integer count;     //番茄数
    private Integer flag;      //0:未开始  1:已完成  2:中途放弃
    private Integer priority;  //任务优先级
    private Date createDate;   //任务创建日期
    private Date expireDate; //任务截止日期
    private Date startDatetime;  //任务开始时间
   private Date completeDatetime;//任务完成时间
    private String repeat;    //重复次数
    private String remark;    //备注
    private Long userId;//创建任务的用户
    private Long checkListId; //所属任务的任务清单
    private String list_title;
    private Integer list_colorId;
    private int useTime; //任务已用过的时间

    public Task() {
    }

    public Task(Long id, String title, Integer count, Integer flag, Integer priority, Date createDate, Date expireDate, Date startDateTime, Date completeDateTime, String repeat, String remark, Long userId, Long checkListId, String list_title, Integer list_colorId, int usedTime) {
        this.id = id;
        this.title = title;
        this.count = count;
        this.flag = flag;
        this.priority = priority;
        this.createDate = createDate;
        this.expireDate = expireDate;
        this.startDatetime = startDateTime;
        this.completeDatetime = completeDateTime;
        this.repeat = repeat;
        this.remark = remark;
        this.userId = userId;
        this.checkListId = checkListId;
        this.list_title = list_title;
        this.list_colorId = list_colorId;
        this.useTime = usedTime;
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

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Date getStartDateTime() {
        return startDatetime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDatetime = startDateTime;
    }

    public Date getCompleteDateTime() {
        return completeDatetime;
    }

    public void setCompleteDateTime(Date completeDateTime) {
        this.completeDatetime = completeDateTime;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCheckListId() {
        return checkListId;
    }

    public void setCheckListId(Long checkListId) {
        this.checkListId = checkListId;
    }

    public String getList_title() {
        return list_title;
    }

    public void setList_title(String list_title) {
        this.list_title = list_title;
    }

    public Integer getList_colorId() {
        return list_colorId;
    }

    public void setList_colorId(Integer list_colorId) {
        this.list_colorId = list_colorId;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int usedTime) {
        this.useTime = usedTime;
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
                ", expireDate=" + expireDate +
                ", startDatetime=" + startDatetime +
                ", completeDatetime=" + completeDatetime +
                ", repeat='" + repeat + '\'' +
                ", remark='" + remark + '\'' +
                ", userId=" + userId +
                ", checkListId=" + checkListId +
                ", list_title='" + list_title + '\'' +
                ", list_colorId=" + list_colorId +
                ", useTime=" + useTime +
                '}';
    }
}
