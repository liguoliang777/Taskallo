package com.android.taskallo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 用于保存ListView 的下载状态
 * Created by zeng on 2016/6/4.
 */
public class ListItemVOListBean implements Serializable {

    public String listItemId;
    public String listItemName;
    public Object updateTime;
    public String projectId;
    public List<BoardVOListBean> boardVOList;

    public static class BoardVOListBean {
        public String boardId;
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
}
