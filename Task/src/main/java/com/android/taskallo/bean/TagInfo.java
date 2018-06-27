package com.android.taskallo.bean;

import java.io.Serializable;

/**
 * 游戏软件的实体类
 * Created by zeng on 2016/5/16.
 */
public class TagInfo implements Serializable {
    public String labelId;
    public String labelName;
    public String labelColour;

    public TagInfo(String labelId, String labelName, String labelColour) {
        this.labelId = labelId;
        this.labelName = labelName;
        this.labelColour = labelColour;
    }
}
