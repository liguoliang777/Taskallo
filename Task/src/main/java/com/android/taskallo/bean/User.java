package com.android.taskallo.bean;

import java.io.Serializable;

/**
 * 用户的实体类
 * Created by zeng on 2016/6/12.
 */
public class User implements Serializable {


    /**
     * id : 2
     * userCode : UC1500108412881
     * nickName : 4444
     * loginName : 18621767596
     * password : 60851f6e7795ca38b6c3be80e5dd951d
     * type : 1
     * status : 0
     * phoneNumber : 18621767596
     * gender : 男
     * headPortrait : http://oss.ngame.cn/upload/userHead/1500454650621.png
     * appTypeId : 2
     * age : 0
     * registerTime : 1500108331000
     * qqNumber : 1500364165000
     * email : null
     */

    public int id;
    public String userCode;
    public String nickName;
    public String loginName;
    public String password;
    public String phoneNumber;
    public String gender;
    public String headPortrait;
    public int age;
    public long registerTime;
    public String qqNumber;
    public String weChat;
    public String email;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userCode='" + userCode + '\'' +
                ", nickName='" + nickName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", age=" + age +
                ", registerTime=" + registerTime +
                ", qqNumber=" + qqNumber +
                ", email='" + email + '\'' +
                '}';
    }
}
