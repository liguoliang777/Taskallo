package com.android.taskallo.bean;

import java.io.Serializable;

/**
 * Created by zeng on 2016/6/15.
 */
public class SubtaskItemInfo implements Serializable {

    public String termId;
    public String termDesc;

    public SubtaskItemInfo(String termId, String termDesc) {
        this.termId = termId;
        this.termDesc = termDesc;
    }
}
