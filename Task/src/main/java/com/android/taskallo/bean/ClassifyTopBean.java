package com.android.taskallo.bean;

/**
 * Created by Administrator on 2017/3/22 0022.
 */

public class ClassifyTopBean {
    long id;
    String icon;
    String name;

    public ClassifyTopBean(String name, long id, String icon) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
