package cn.edu.hebtu.software.timebookclient.Bean;

public class PhoneTime {
    private Long id;
    private Long userId;
    private String createDate;
    private Integer createWeek;
    private Long time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getCreateWeek() {
        return createWeek;
    }

    public void setCreateWeek(Integer createWeek) {
        this.createWeek = createWeek;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "PhoneTime{" +
                "id=" + id +
                ", userId=" + userId +
                ", createDate='" + createDate + '\'' +
                ", createWeek=" + createWeek +
                ", time=" + time +
                '}';
    }
}
