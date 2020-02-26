package cn.edu.hebtu.software.timebookclient.Bean;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String image;
    private String email;
    //用户设置的番茄时长 单位：分钟
    private Integer tomatoTime;
    //用户设置的短时休息 单位：分钟
    private Integer shortBreak;
    //用户设置的长时休息 单位：分钟
    private Integer longBreak;
    //用户设置的长时休息间隔 单位：分钟
    private Integer longRestInterval;
    private Date createTime;
    private String signature;//用户个性签名

    public User() {
    }

    public User(Long id, String username, String password, String image, String email, Integer tomatoTime, Integer shortBreak, Integer longBreak, Integer longRestInterval, Date createTime, String signature) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.image = image;
        this.email = email;
        this.tomatoTime = tomatoTime;
        this.shortBreak = shortBreak;
        this.longBreak = longBreak;
        this.longRestInterval = longRestInterval;
        this.createTime = createTime;
        this.signature = signature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTomatoTime() {
        return tomatoTime;
    }

    public void setTomatoTime(Integer tomatoTime) {
        this.tomatoTime = tomatoTime;
    }

    public Integer getShortBreak() {
        return shortBreak;
    }

    public void setShortBreak(Integer shortBreak) {
        this.shortBreak = shortBreak;
    }

    public Integer getLongBreak() {
        return longBreak;
    }

    public void setLongBreak(Integer longBreak) {
        this.longBreak = longBreak;
    }

    public Integer getLongRestInterval() {
        return longRestInterval;
    }

    public void setLongRestInterval(Integer longRestInterval) {
        this.longRestInterval = longRestInterval;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", email='" + email + '\'' +
                ", tomatoTime=" + tomatoTime +
                ", shortBreak=" + shortBreak +
                ", longBreak=" + longBreak +
                ", longRestInterval=" + longRestInterval +
                ", createTime=" + createTime +
                ", signature='" + signature + '\'' +
                '}';
    }
}
