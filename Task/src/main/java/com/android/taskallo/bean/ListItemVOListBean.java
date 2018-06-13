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
}
