package com.android.taskallo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏键位操作描述列表
 * Created by zeng on 2016/10/12.
 */
public class ProjDetailInfo implements Serializable {
    public String projectId;
    public String projectImg;
    public long updateTime;
    public String name;
    public String desc;
    public List<ListItemVOListBean> listItemVOList;

    public static class ListItemVOListBean {

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
}
