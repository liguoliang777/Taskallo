package com.android.taskallo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 用户登录的Token信息
 * Created by zeng on 2016/6/12.
 */
public class BoardVOListBean implements Serializable {
    public String boardId;
    public String id;
    public String listItemId;
    public String boardName;
    public String boardDesc;
    public int expiryTime;
    public Object updateTime;
    public Object boardFileVOList;
    public List<ProjectLabelVOListBean> projectLabelVOList;
    public List<SubtaskVOListBean> subtaskVOList;
    public List<UserBasicVOListBean> userBasicVOList;

    public static class ProjectLabelVOListBean {

        public String labelId;
        public String labelName;
        public String labelColour;
    }

    public static class SubtaskVOListBean {
        public String subtaskId;
        public String subtaskName;
        public long updateTime;
        public Object termVOList;
    }

    public static class UserBasicVOListBean {
        public int id;
        public String nickName;
        public String headPortrait;
    }
}
