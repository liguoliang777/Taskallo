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

}
